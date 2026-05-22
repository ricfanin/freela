package com.freela.app.data.seed

import androidx.room.withTransaction
import com.freela.app.data.local.FreelaDatabase
import com.freela.app.data.local.entity.ClienteEntity
import com.freela.app.data.local.entity.ClienteTagCrossRef
import com.freela.app.data.local.entity.FatturaEntity
import com.freela.app.data.local.entity.InterazioneEntity
import com.freela.app.data.local.entity.SessioneLavoroEntity
import com.freela.app.data.local.entity.TagEntity
import com.freela.app.data.local.entity.TaskEntity
import com.freela.app.domain.model.OrigineTask
import com.freela.app.domain.model.PersonaDemo
import com.freela.app.domain.model.Priorita
import com.freela.app.domain.model.StatoFattura
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Carica nel database il dataset demo della persona scelta.
 * Wipe completo + insert atomico in transazione.
 *
 * Fonte dati: design_handoff_freela/data.jsx (Giulia, Luca, Sara).
 */
@Singleton
class SeedDataSource @Inject constructor(
    private val db: FreelaDatabase,
) {

    suspend fun seed(persona: PersonaDemo) = withContext(Dispatchers.IO) {
        val payload = PersonaSeed.of(persona)

        db.withTransaction {
            // Wipe (CASCADE elimina interazioni/task/sessioni/fatture/preventivi/allegati legati ai clienti)
            db.fatturaDao().cancellaTutte()
            db.preventivoDao().cancellaTutti()
            db.sessioneLavoroDao().cancellaTutte()
            db.taskDao().cancellaTutti()
            db.interazioneDao().cancellaTutte()
            db.clienteDao().cancellaTutti()
            db.tagDao().cancellaTutti()

            // Tag unici
            val tagNomi = payload.clienti.map { it.tag }.toSet()
            val tagIds = tagNomi.associateWith { db.tagDao().upsert(it) }

            // Clienti
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

            // Interazioni di esempio (1 per cliente: ultima call/meeting)
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

            // Task "Oggi"
            payload.taskOggi.forEach { t ->
                val cid = clientiIds[t.clienteLocalId]
                db.taskDao().insert(
                    TaskEntity(
                        titolo = t.titolo,
                        clienteId = cid,
                        scadenza = startOfTodayPlusHours(t.oraOpzionale ?: 18),
                        priorita = if (t.urgente) Priorita.ALTA else Priorita.MEDIA,
                        origine = OrigineTask.MANUALE,
                    )
                )
            }
            // Task "Suggeriti" (PRD FR-12, generati a runtime in V1 dal seed)
            payload.taskSuggeriti.forEach { t ->
                val cid = clientiIds[t.clienteLocalId]
                db.taskDao().insert(
                    TaskEntity(
                        titolo = t.titolo,
                        clienteId = cid,
                        scadenza = now() + daysToMillis(2),
                        priorita = Priorita.MEDIA,
                        origine = OrigineTask.SUGGERITO,
                    )
                )
            }
            // Task "Settimana"
            payload.taskSettimana.forEach { t ->
                val cid = clientiIds[t.clienteLocalId]
                db.taskDao().insert(
                    TaskEntity(
                        titolo = t.titolo,
                        clienteId = cid,
                        scadenza = now() + daysToMillis(t.giorniInAvanti),
                        priorita = Priorita.MEDIA,
                        origine = OrigineTask.MANUALE,
                    )
                )
            }

            // Fatture
            payload.fatture.forEach { f ->
                val cid = clientiIds[f.clienteLocalId] ?: return@forEach
                val scadenza = if (f.giorniDallaScadenza >= 0) {
                    now() - daysToMillis(f.giorniDallaScadenza)
                } else {
                    now() + daysToMillis(-f.giorniDallaScadenza)
                }
                db.fatturaDao().insert(
                    FatturaEntity(
                        numero = f.numero,
                        clienteId = cid,
                        importo = f.importo,
                        dataEmissione = scadenza - daysToMillis(30),
                        dataScadenza = scadenza,
                        dataPagamento = if (f.pagata) scadenza - daysToMillis(5) else null,
                        stato = if (f.pagata) StatoFattura.PAGATA else StatoFattura.EMESSA,
                    )
                )
            }

            // Sessioni lavoro (per giocare il time tracking)
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

    private fun now() = System.currentTimeMillis()
    private fun daysToMillis(d: Int): Long = d.toLong() * 24L * 60L * 60L * 1000L

    private fun startOfTodayPlusHours(hours: Int): Long {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hours)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
