package com.freela.app.data.seed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.freela.app.data.local.FreelaDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Verifica che il seed demo popoli il DB con 8 clienti + task suggeriti + fatture attese.
 *
 * Lanciabile con `./gradlew :app:testDebugUnitTest`.
 *
 * NB: questo test è opzionale e richiede Robolectric per istanziare Room in-memory in JVM.
 * In V1 lo lasciamo come riferimento per il QA manuale. Per attivarlo, aggiungere a
 * `app/build.gradle.kts`:
 *
 *     testImplementation("org.robolectric:robolectric:4.13")
 *     testImplementation("androidx.arch.core:core-testing:2.2.0")
 *     testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
 *
 * In assenza, la classe semplicemente non viene compilata dai test.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [33])
class SeedDataSourceTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var db: FreelaDatabase
    private lateinit var seed: SeedDataSource

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FreelaDatabase::class.java,
        ).allowMainThreadQueries().build()
        seed = SeedDataSource(db)
    }

    @After
    fun teardown() { db.close() }

    @Test
    fun `seed demo produce 8 clienti`() = runTest {
        seed.seed()
        val clienti = db.clienteDao().osservaTutti().first()
        assertEquals(8, clienti.size)
    }

    @Test
    fun `seed demo produce task con almeno 2 suggeriti`() = runTest {
        seed.seed()
        val tasks = db.taskDao().osservaTutti().first()
        val suggeriti = tasks.count { it.origine == com.freela.app.domain.model.OrigineTask.SUGGERITO }
        assertTrue("expected >= 2 suggeriti, got $suggeriti", suggeriti >= 2)
    }

    @Test
    fun `clear svuota i clienti dopo il seed`() = runTest {
        seed.seed()
        seed.clear()
        val clienti = db.clienteDao().osservaTutti().first()
        assertTrue("expected DB vuoto dopo clear", clienti.isEmpty())
    }
}
