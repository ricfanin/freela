package com.freela.app.domain.scheduler

import com.freela.app.domain.model.Task

// sta nel domain così il repository può armare/disarmare i reminder senza dipendere da AlarmManager
interface ReminderScheduler {
    // no-op se il task è completato o la scadenza è già passata
    fun schedula(task: Task)

    fun annulla(taskId: Long)
}
