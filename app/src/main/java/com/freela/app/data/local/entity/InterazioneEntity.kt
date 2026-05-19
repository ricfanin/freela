package com.freela.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.freela.app.domain.model.TipoInterazione

@Entity(
    tableName = "interazioni",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("clienteId"), Index("data")],
)
data class InterazioneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val tipo: TipoInterazione,
    val data: Long,
    val durataMinuti: Int? = null,
    val descrizione: String? = null,
    val latitudine: Double? = null,
    val longitudine: Double? = null,
    val indirizzo: String? = null,
)
