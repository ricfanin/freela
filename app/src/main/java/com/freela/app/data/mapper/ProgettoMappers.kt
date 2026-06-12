package com.freela.app.data.mapper

import com.freela.app.data.local.entity.ProgettoEntity
import com.freela.app.domain.model.Progetto

fun ProgettoEntity.toDomain(): Progetto = Progetto(
    id = id,
    clienteId = clienteId,
    nome = nome,
    deadline = deadline,
    oreStimate = oreStimate,
    stato = stato,
    dataCreazione = dataCreazione,
)

fun Progetto.toEntity(): ProgettoEntity = ProgettoEntity(
    id = id,
    clienteId = clienteId,
    nome = nome,
    deadline = deadline,
    oreStimate = oreStimate,
    stato = stato,
    dataCreazione = dataCreazione,
)
