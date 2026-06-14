package com.freela.app.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.freela.app.MainActivity
import com.freela.app.domain.repository.TimeTrackingRepository
import com.freela.app.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// foreground service così il timer continua a girare anche con l'app in background
@AndroidEntryPoint
class TimerForegroundService : Service() {

    @Inject lateinit var timeRepo: TimeTrackingRepository
    @Inject lateinit var notificationHelper: NotificationHelper

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_STOP -> handleStop()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun handleStart(intent: Intent) {
        val clienteId = intent.getLongExtra(EXTRA_CLIENTE_ID, -1L)
        val clienteNome = intent.getStringExtra(EXTRA_CLIENTE_NOME) ?: "Sessione di lavoro"
        val descrizione = intent.getStringExtra(EXTRA_DESCRIZIONE)
        val progettoId = intent.getLongExtra(EXTRA_PROGETTO_ID, -1L).takeIf { it > 0L }
        val inizio = System.currentTimeMillis()

        // devo chiamare startForeground subito, la persistenza la faccio dopo in coroutine
        startForegroundCompat(buildNotification(clienteNome, inizio))

        if (clienteId > 0L) {
            scope.launch { timeRepo.avvia(clienteId, progettoId, descrizione) }
        }
    }

    private fun handleStop() {
        scope.launch {
            timeRepo.osservaInCorso().first()?.let { timeRepo.ferma(it.id) }
            ServiceCompat.stopForeground(this@TimerForegroundService, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun buildNotification(titolo: String, inizio: Long) =
        notificationHelper.buildTimerNotification(
            titolo = titolo,
            inizioMillis = inizio,
            stopIntent = PendingIntent.getService(
                this,
                0,
                Intent(this, TimerForegroundService::class.java).apply { action = ACTION_STOP },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ),
            contentIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ),
        )

    private fun startForegroundCompat(notification: android.app.Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NotificationHelper.TIMER_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NotificationHelper.TIMER_NOTIFICATION_ID, notification)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "com.freela.app.action.TIMER_START"
        const val ACTION_STOP = "com.freela.app.action.TIMER_STOP"
        const val EXTRA_CLIENTE_ID = "cliente_id"
        const val EXTRA_CLIENTE_NOME = "cliente_nome"
        const val EXTRA_DESCRIZIONE = "descrizione"
        const val EXTRA_PROGETTO_ID = "progetto_id"

        fun avvia(
            context: Context,
            clienteId: Long,
            clienteNome: String,
            descrizione: String? = null,
            progettoId: Long? = null,
        ) {
            val intent = Intent(context, TimerForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_CLIENTE_ID, clienteId)
                putExtra(EXTRA_CLIENTE_NOME, clienteNome)
                putExtra(EXTRA_DESCRIZIONE, descrizione)
                putExtra(EXTRA_PROGETTO_ID, progettoId ?: -1L)
            }
            androidx.core.content.ContextCompat.startForegroundService(context, intent)
        }

        fun ferma(context: Context) {
            val intent = Intent(context, TimerForegroundService::class.java).apply { action = ACTION_STOP }
            androidx.core.content.ContextCompat.startForegroundService(context, intent)
        }
    }
}
