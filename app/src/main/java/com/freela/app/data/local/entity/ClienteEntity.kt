package com.freela.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.freela.app.domain.model.FasePipeline

@Entity(tableName = "clienti")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val telefono: String? = null,
    val email: String? = null,
    val fonteAcquisizione: String? = null,
    val faseCorrente: FasePipeline = FasePipeline.NUOVO_LEAD,
    val dataCreazione: Long,
    val note: String? = null,
    val fotoPath: String? = null,
    val orePreventivate: Float? = null,
    val importoPreventivato: Double? = null,
    val avatarColor: String? = null,
)
