package com.freela.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.freela.app.domain.model.OrigineTask
import com.freela.app.domain.model.Priorita

@Entity(
    tableName = "task",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("clienteId"), Index("scadenza"), Index("completato")],
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titolo: String,
    val descrizione: String? = null,
    val clienteId: Long? = null,
    val scadenza: Long,
    val priorita: Priorita = Priorita.MEDIA,
    val completato: Boolean = false,
    val dataCompletamento: Long? = null,
    val origine: OrigineTask = OrigineTask.MANUALE,
)
