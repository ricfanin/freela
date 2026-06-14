package com.freela.app.data.mapper

import com.freela.app.data.local.entity.ClienteEntity
import com.freela.app.data.local.entity.ClienteWithTags
import com.freela.app.data.local.entity.FatturaEntity
import com.freela.app.data.local.entity.InterazioneEntity
import com.freela.app.data.local.entity.PreventivoEntity
import com.freela.app.data.local.entity.SessioneLavoroEntity
import com.freela.app.data.local.entity.TagEntity
import com.freela.app.data.local.entity.TaskEntity
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.Fattura
import com.freela.app.domain.model.Interazione
import com.freela.app.domain.model.Preventivo
import com.freela.app.domain.model.SessioneLavoro
import com.freela.app.domain.model.Tag
import com.freela.app.domain.model.Task

fun ClienteEntity.toDomain(tags: List<Tag> = emptyList()) = Cliente(
    id = id,
    nome = nome,
    telefono = telefono,
    email = email,
    fonteAcquisizione = fonteAcquisizione,
    faseCorrente = faseCorrente,
    dataCreazione = dataCreazione,
    note = note,
    fotoPath = fotoPath,
    orePreventivate = orePreventivate,
    importoPreventivato = importoPreventivato,
    avatarColor = avatarColor,
    preferito = preferito,
    tags = tags,
)

fun ClienteWithTags.toDomain() = cliente.toDomain(tags.map { it.toDomain() })

fun Cliente.toEntity() = ClienteEntity(
    id = id,
    nome = nome,
    telefono = telefono,
    email = email,
    fonteAcquisizione = fonteAcquisizione,
    faseCorrente = faseCorrente,
    dataCreazione = dataCreazione,
    note = note,
    fotoPath = fotoPath,
    orePreventivate = orePreventivate,
    importoPreventivato = importoPreventivato,
    avatarColor = avatarColor,
    preferito = preferito,
)

fun TagEntity.toDomain() = Tag(id = id, nome = nome)
fun Tag.toEntity() = TagEntity(id = id, nome = nome)

fun InterazioneEntity.toDomain() = Interazione(
    id, clienteId, tipo, data, durataMinuti, descrizione, latitudine, longitudine, indirizzo,
)

fun Interazione.toEntity() = InterazioneEntity(
    id, clienteId, tipo, data, durataMinuti, descrizione, latitudine, longitudine, indirizzo,
)

fun TaskEntity.toDomain() = Task(
    id, titolo, descrizione, clienteId, scadenza, priorita, completato, dataCompletamento,
)

fun Task.toEntity() = TaskEntity(
    id, titolo, descrizione, clienteId, scadenza, priorita, completato, dataCompletamento,
)

fun SessioneLavoroEntity.toDomain() = SessioneLavoro(
    id, clienteId, progettoId, inizio, fine, descrizione, inserimentoManuale,
)

fun SessioneLavoro.toEntity() = SessioneLavoroEntity(
    id, clienteId, progettoId, inizio, fine, descrizione, inserimentoManuale,
)

fun PreventivoEntity.toDomain() = Preventivo(id, clienteId, importo, dataInvio, stato, note)
fun Preventivo.toEntity() = PreventivoEntity(id, clienteId, importo, dataInvio, stato, note)

fun FatturaEntity.toDomain() = Fattura(
    id, numero, clienteId, importo, dataEmissione, dataScadenza, dataPagamento, stato,
)

fun Fattura.toEntity() = FatturaEntity(
    id, numero, clienteId, importo, dataEmissione, dataScadenza, dataPagamento, stato,
)
