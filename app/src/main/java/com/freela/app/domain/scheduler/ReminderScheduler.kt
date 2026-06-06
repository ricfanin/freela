package com.freela.app.domain.scheduler

import com.freela.app.domain.model.Task

/**
 * Astrazione per la schedulazione dei reminder dei task (PRD FR-10).
 *
 * Vive nel layer domain così che [com.freela.app.data.repository.LocalTaskRepository]
 * possa armare/disarmare i reminder senza dipendere da AlarmManager (NFR-07/09).
 * L'implementazione concreta è [com.freela.app.data.scheduler.AlarmReminderScheduler].
 */
interface ReminderScheduler {
    /** Arma un reminder esatto alla scadenza del task. No-op se completato o scadenza passata. */
    fun schedula(task: Task)

    /** Annulla il reminder associato al task (su completamento o eliminazione). */
    fun annulla(taskId: Long)
}
