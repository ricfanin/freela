package com.freela.app.domain.usecase

import com.freela.app.domain.model.OrigineTask
import com.freela.app.domain.model.StatoPreventivo
import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.FinanzeRepository
import com.freela.app.domain.repository.InterazioneRepository
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Genera task suggeriti (origine SUGGERITO) in base a regole sullo stato dei record (PRD FR-12, US-10):
 *  - preventivo INVIATO da più di N giorni senza interazioni successive → follow-up;
 *  - cliente senza contatto da più di N giorni → ricontatto;
 *  - fattura scaduta e non pagata → sollecito.
 *
 * Le soglie N vengono da [SettingsRepository]. Evita duplicati: non ricrea un suggerito
 * aperto con lo stesso titolo.
 */
class GeneraSuggerimentiFollowUp @Inject constructor(
    private val clienteRepo: ClienteRepository,
    private val interazioneRepo: InterazioneRepository,
    private val finanzeRepo: FinanzeRepository,
    private val taskRepo: TaskRepository,
    private val settingsRepo: SettingsRepository,
) {

    suspend operator fun invoke() {
        val now = System.currentTimeMillis()
        val clienti = clienteRepo.osservaTutti().first()
        if (clienti.isEmpty()) return

        val nomePerId = clienti.associateBy({ it.id }, { it.nome })
        val sogliaSenzaContatto = settingsRepo.giorniSenzaContatto.first()
        val sogliaPreventivo = settingsRepo.giorniFollowUpPreventivo.first()

        // Titoli dei suggeriti aperti già presenti → dedup.
        val titoliEsistenti = taskRepo.osservaAperti().first()
            .filter { it.origine == OrigineTask.SUGGERITO }
            .map { it.titolo }
            .toHashSet()

        val daCreare = mutableListOf<Task>()

        // Regola 1: preventivo inviato senza interazioni da N giorni.
        finanzeRepo.osservaPreventivi().first()
            .filter { it.stato == StatoPreventivo.INVIATO }
            .forEach { p ->
                val giorni = giorniDa(p.dataInvio, now)
                if (giorni < sogliaPreventivo) return@forEach
                val ultima = interazioneRepo.osservaUltimaData(p.clienteId).first()
                if (ultima == null || ultima < p.dataInvio) {
                    val nome = nomePerId[p.clienteId] ?: return@forEach
                    daCreare += suggerito(
                        titolo = "Follow-up preventivo a $nome",
                        descrizione = "Preventivo inviato $giorni giorni fa, nessuna risposta.",
                        clienteId = p.clienteId,
                        now = now,
                    )
                }
            }

        // Regola 2: cliente senza contatto da N giorni.
        clienti.forEach { c ->
            val ultima = interazioneRepo.osservaUltimaData(c.id).first() ?: c.dataCreazione
            val giorni = giorniDa(ultima, now)
            if (giorni >= sogliaSenzaContatto) {
                daCreare += suggerito(
                    titolo = "Ricontatta ${c.nome}",
                    descrizione = "Nessun contatto da $giorni giorni.",
                    clienteId = c.id,
                    now = now,
                )
            }
        }

        // Regola 3: fattura scaduta non pagata.
        finanzeRepo.osservaFattureInRitardo(now).first().forEach { f ->
            val nome = nomePerId[f.clienteId] ?: return@forEach
            val giorni = giorniDa(f.dataScadenza, now)
            daCreare += suggerito(
                titolo = "Sollecito pagamento ${f.numero} a $nome",
                descrizione = "Fattura scaduta da $giorni giorni.",
                clienteId = f.clienteId,
                now = now,
            )
        }

        daCreare
            .distinctBy { it.titolo }
            .filter { it.titolo !in titoliEsistenti }
            .forEach { taskRepo.crea(it) }
    }

    private fun suggerito(titolo: String, descrizione: String, clienteId: Long, now: Long) = Task(
        titolo = titolo,
        descrizione = descrizione,
        clienteId = clienteId,
        scadenza = now,
        origine = OrigineTask.SUGGERITO,
    )

    private fun giorniDa(millis: Long, now: Long): Int = ((now - millis) / 86_400_000L).toInt()
}
