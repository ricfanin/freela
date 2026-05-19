package com.freela.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.freela.app.domain.model.StatoPreventivo

@Entity(
    tableName = "preventivi",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("clienteId"), Index("dataInvio")],
)
data class PreventivoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val importo: Double,
    val dataInvio: Long,
    val stato: StatoPreventivo = StatoPreventivo.INVIATO,
    val note: String? = null,
)
