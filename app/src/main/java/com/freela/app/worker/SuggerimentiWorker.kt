package com.freela.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.freela.app.domain.usecase.GeneraSuggerimentiFollowUp
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker periodico che genera i task suggeriti di follow-up (PRD FR-12).
 * Eseguito da WorkManager (vedi schedulazione in [com.freela.app.FreelaApp]).
 */
@HiltWorker
class SuggerimentiWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val generaSuggerimenti: GeneraSuggerimentiFollowUp,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result =
        runCatching { generaSuggerimenti() }
            .fold(onSuccess = { Result.success() }, onFailure = { Result.retry() })

    companion object {
        const val UNIQUE_NAME = "suggerimenti_follow_up"
    }
}
