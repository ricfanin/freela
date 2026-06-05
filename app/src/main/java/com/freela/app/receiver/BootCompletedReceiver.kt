package com.freela.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Stub per BOOT_COMPLETED (PRD FR-11, NFR-16).
 *
 * Implementazione completa nella fase 7 del PRD §11.4:
 * - leggere tutti i Task aperti con scadenza futura
 * - rischedulare AlarmManager per ciascuno
 *
 * Su alcuni vendor (Xiaomi, Huawei) l'autostart è limitata: documentato nel PRD §12 rischi.
 */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        // TODO PRD §11.4 fase 7: riarmare reminder schedulati
    }
}
