package com.freela.app.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class Coordinate(val latitudine: Double, val longitudine: Double)

/**
 * Wrapper su FusedLocationProviderClient + Geocoder per il tag GPS dei meeting (PRD FR-15).
 *
 * Il chiamante deve aver già ottenuto il permesso ACCESS_FINE_LOCATION a runtime (NFR-11):
 * qui si assume concesso ([SuppressLint] giustificato dal controllo a monte nella UI).
 */
@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fused: FusedLocationProviderClient,
) {

    @SuppressLint("MissingPermission")
    suspend fun posizioneCorrente(): Coordinate? = runCatching {
        val loc = fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            ?: fused.lastLocation.await()
        loc?.let { Coordinate(it.latitude, it.longitude) }
    }.getOrNull()

    /** Reverse geocoding → indirizzo testuale leggibile. */
    suspend fun indirizzoDa(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext null
        val geocoder = Geocoder(context)
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val deferred = CompletableDeferred<String?>()
                geocoder.getFromLocation(lat, lon, 1) { results ->
                    deferred.complete(results.firstOrNull()?.let(::formatta))
                }
                deferred.await()
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()?.let(::formatta)
            }
        }.getOrNull()
    }

    private fun formatta(a: android.location.Address): String {
        val via = listOfNotNull(a.thoroughfare, a.subThoroughfare).joinToString(" ")
        val citta = a.locality ?: a.subAdminArea
        return listOf(via, citta).filter { it.isNotBlank() }.joinToString(", ").ifBlank { a.getAddressLine(0) ?: "" }
    }
}
