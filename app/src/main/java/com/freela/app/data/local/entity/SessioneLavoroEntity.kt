package com.freela.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessioni_lavoro",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("clienteId"), Index("inizio")],
)
data class SessioneLavoroEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val inizio: Long,
    val fine: Long? = null,
    val descrizione: String? = null,
    val inserimentoManuale: Boolean = false,
)
