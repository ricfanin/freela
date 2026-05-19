package com.freela.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.freela.app.domain.model.StatoFattura

@Entity(
    tableName = "fatture",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("clienteId"), Index("dataScadenza"), Index(value = ["numero"], unique = true)],
)
data class FatturaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val numero: String,
    val clienteId: Long,
    val importo: Double,
    val dataEmissione: Long,
    val dataScadenza: Long,
    val dataPagamento: Long? = null,
    val stato: StatoFattura = StatoFattura.EMESSA,
)
