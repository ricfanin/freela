package com.freela.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ClienteWithTags(
    @Embedded val cliente: ClienteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ClienteTagCrossRef::class,
            parentColumn = "clienteId",
            entityColumn = "tagId",
        ),
    )
    val tags: List<TagEntity>,
)
