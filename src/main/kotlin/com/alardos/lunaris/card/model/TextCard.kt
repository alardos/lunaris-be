package com.alardos.lunaris.card.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.util.*

@JsonDeserialize(using = CardJsonMapper::class)
sealed class Card(
    val type: CardStrType,
    val id: UUID,
    val owner: UUID,
    var workspace: UUID,
    val createdAt: Long
) {

}

enum class CardStrType { text }
data class CardCandidate(val type: CardStrType, val content: String?)
class TextCard(
    id: UUID,
    owner: UUID,
    workspace: UUID,
    createdAt: Long,
    var content: String
): Card(CardStrType.text,id,owner,workspace,createdAt)


data class CardAccess(val card: UUID, val cardOwner: UUID, val workspaceOwner: UUID, val workspaceMembers: List<UUID>)

class CardJsonMapper: JsonDeserializer<Card>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Card? {
        val node: JsonNode? = p.readValueAsTree()

        return when(node?.get("type")?.asText()) {
            "text" -> TextCard(
                UUID.fromString(node.get("id")?.asText()),
                UUID.fromString(node.get("owner")?.asText()),
                UUID.fromString(node.get("workspace")?.asText()),
                node.get("createdAt").asLong(),
                node.get("content").asText()
            )
            else -> throw RuntimeException()
        }
    }

}
