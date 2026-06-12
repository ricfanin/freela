package com.freela.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.freela.app.domain.model.StatoProgetto

@Entity(
    tableName = "progetto",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("clienteId")],
)
data class ProgettoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val nome: String,
    val deadline: Long? = null,
    val oreStimate: Int = 0,
    val stato: StatoProgetto = StatoProgetto.DA_INIZIARE,
    val dataCreazione: Long = System.currentTimeMillis(),
)
