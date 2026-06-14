package com.freela.app.data.seed

import androidx.room.withTransaction
import com.freela.app.data.local.FreelaDatabase
import com.freela.app.data.local.entity.ClienteEntity
import com.freela.app.data.local.entity.ClienteTagCrossRef
import com.freela.app.data.local.entity.FatturaEntity
import com.freela.app.data.local.entity.InterazioneEntity
import com.freela.app.data.local.entity.ProgettoEntity
import com.freela.app.data.local.entity.SessioneLavoroEntity
import com.freela.app.data.local.entity.TagEntity
import com.freela.app.data.local.entity.TaskEntity
import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.model.Priorita
import com.freela.app.domain.model.StatoFattura
import com.freela.app.domain.model.StatoProgetto
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// carica i dati demo nel db: wipe completo + insert atomico in transazione
@Singleton
class SeedDataSource @Inject constructor(
    private val db: FreelaDatabase,
) {

    // svuota tutte le tabelle, lo usano sia il seed che il logout
    suspend fun clear() = withContext(Dispatchers.IO) {
        db.withTransaction { wipe() }
    }

    // ripopola i dati demo solo se il db è vuoto.
    // serve dopo una migrazione distruttiva: il db si azzera ma l'onboarding risulta già fatto,
    // quindi seed() non verrebbe più richiamato
    suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        if (db.clienteDao().count() == 0) seed()
    }

    suspend fun seed() = withContext(Dispatchers.IO) {
        val payload = DemoSeed.payload

        db.withTransaction {
            wipe()

            val tagNomi = payload.clienti.map { it.tag }.toSet()
            val tagIds = tagNomi.associateWith { db.tagDao().upsert(it) }

            val clientiIds = HashMap<Int, Long>()
            payload.clienti.forEach { c ->
                val entity = ClienteEntity(
                    nome = c.nome,
                    telefono = null,
                    email = null,
                    fonteAcquisizione = c.fonte,
                    faseCorrente = c.stage,
                    dataCreazione = now() - daysToMillis(30),
                    note = c.note,
                    orePreventivate = c.orePreventivate,
                    importoPreventivato = c.budget,
                    avatarColor = c.avatarColor,
                )
                val id = db.clienteDao().insert(entity)
                clientiIds[c.localId] = id
                tagIds[c.tag]?.let { tagId ->
                    db.clienteDao().insertTagCrossRef(ClienteTagCrossRef(id, tagId))
                }
            }

            payload.clienti.forEach { c ->
                val cid = clientiIds[c.localId] ?: return@forEach
                val daysAgo = c.giorniDallUltimaInterazione
                val tipo = c.tipoUltimaInterazione
                db.interazioneDao().insert(
                    InterazioneEntity(
                        clienteId = cid,
                        tipo = tipo,
                        data = now() - daysToMillis(daysAgo),
                        durataMinuti = 25,
                        descrizione = "Interazione recente · ${tipo.name.lowercase()}",
                    )
                )
            }

            payload.taskOggi.forEach { t ->
                val cid = clientiIds[t.clienteLocalId]
                db.taskDao().insert(
                    TaskEntity(
                        titolo = t.titolo,
                        clienteId = cid,
                        scadenza = startOfTodayPlusHours(t.oraOpzionale ?: 18),
                        priorita = if (t.urgente) Priorita.ALTA else Priorita.MEDIA,
                    )
                )
            }
            payload.taskSettimana.forEach { t ->
                val cid = clientiIds[t.clienteLocalId]
                db.taskDao().insert(
                    TaskEntity(
                        titolo = t.titolo,
                        clienteId = cid,
                        scadenza = now() + daysToMillis(t.giorniInAvanti),
                        priorita = Priorita.MEDIA,
                    )
                )
            }

            // numero fattura sull'anno corrente, tipo "2026-024"
            val anno = currentYear()
            payload.fatture.forEach { f ->
                val cid = clientiIds[f.clienteLocalId] ?: return@forEach
                val scadenza = if (f.giorniDallaScadenza >= 0) {
                    now() - daysToMillis(f.giorniDallaScadenza)
                } else {
                    now() + daysToMillis(-f.giorniDallaScadenza)
                }
                db.fatturaDao().insert(
                    FatturaEntity(
                        numero = "%d-%03d".format(anno, f.progressivo),
                        clienteId = cid,
                        importo = f.importo,
                        dataEmissione = scadenza - daysToMillis(30),
                        dataScadenza = scadenza,
                        dataPagamento = if (f.pagata) scadenza - daysToMillis(5) else null,
                        stato = if (f.pagata) StatoFattura.PAGATA else StatoFattura.EMESSA,
                    )
                )
            }

            payload.clienti.filter { it.budget != null }.forEach { c ->
                val cid = clientiIds[c.localId] ?: return@forEach
                val stato = when (c.stage) {
                    FasePipeline.CONSEGNATO, FasePipeline.IN_ATTESA_PAGAMENTO,
                    FasePipeline.CHIUSO, FasePipeline.CLIENTE_RICORRENTE -> StatoProgetto.COMPLETATO
                    FasePipeline.IN_CORSO -> StatoProgetto.IN_CORSO
                    else -> StatoProgetto.DA_INIZIARE
                }
                db.progettoDao().insert(
                    ProgettoEntity(
                        clienteId = cid,
                        nome = c.progettoNome ?: "Progetto ${c.nome}",
                        deadline = now() + daysToMillis(15),
                        oreStimate = (c.orePreventivate ?: 0f).toInt(),
                        stato = stato,
                        dataCreazione = now() - daysToMillis(20),
                    )
                )
            }

            payload.clienti.filter { it.oreReali > 0f }.forEach { c ->
                val cid = clientiIds[c.localId] ?: return@forEach
                val durationMillis = (c.oreReali * 60f * 60f * 1000f).toLong()
                db.sessioneLavoroDao().insert(
                    SessioneLavoroEntity(
                        clienteId = cid,
                        inizio = now() - durationMillis,
                        fine = now() - daysToMillis(1),
                        descrizione = "Sessione recap progetto",
                        inserimentoManuale = true,
                    )
                )
            }
        }
    }

    // svuota le tabelle, va chiamata dentro una transazione.
    // il cascade dei clienti si porta dietro interazioni/task/sessioni/fatture/preventivi/allegati/progetti
    private suspend fun wipe() {
        db.progettoDao().cancellaTutti()
        db.fatturaDao().cancellaTutte()
        db.preventivoDao().cancellaTutti()
        db.sessioneLavoroDao().cancellaTutte()
        db.taskDao().cancellaTutti()
        db.interazioneDao().cancellaTutte()
        db.clienteDao().cancellaTutti()
        db.tagDao().cancellaTutti()
    }

    private fun now() = System.currentTimeMillis()
    private fun daysToMillis(d: Int): Long = d.toLong() * 24L * 60L * 60L * 1000L
    private fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    private fun startOfTodayPlusHours(hours: Int): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
