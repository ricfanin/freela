package com.freela.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Stub del foreground service per il timer di time tracking (PRD FR-18, NFR-17).
 *
 * Implementazione completa nella fase 8 del PRD §11.4:
 * - notifica persistente sul canale [com.freela.app.notification.NotificationHelper.CHANNEL_TIMER]
 * - aggiornamento elapsed ogni 1s
 * - azione "Stop" che chiude il service e persiste la sessione su Room
 *
 * Per ora la classe esiste perché è dichiarata nel Manifest e l'app deve installarsi.
 */
class TimerForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO PRD §11.4 fase 8: startForeground(..., buildTimerNotification(...))
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
