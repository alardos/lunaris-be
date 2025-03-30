package com.alardos.lunaris.card

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
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

class CardRowMapper : RowMapper<Card> {
    override fun map(rs: ResultSet, ctx: StatementContext): Card =
        when {
            rs.getString("text_card.content") != null ->
                TextCard(
                    id = rs.getObject("card.id",UUID::class.java),
                    owner = rs.getObject("card.owner",UUID::class.java),
                    workspace = rs.getObject("card.workspace",UUID::class.java),
                    createdAt = rs.getTimestamp("card.created_at").time,
                    content = rs.getString("text_card.content"),
                )
            else -> throw RuntimeException("could not tell exact card type from query")
        }
}

data class CardAccess(val card: UUID, val cardOwner: UUID, val workspaceOwner: UUID, val workspaceMembers: List<UUID>)
class CardAccessRowMapper: RowMapper<CardAccess> {
    override fun map(
        rs: ResultSet,
        ctx: StatementContext?
    ): CardAccess =
        CardAccess(
            card = rs.getObject("card.id",UUID::class.java),
            cardOwner = rs.getObject("card.owner",UUID::class.java),
            workspaceOwner = rs.getObject("workspace.owner",UUID::class.java),
            workspaceMembers = (rs.getArray("workspace.members").array as Array<UUID>).asList(),
        )


}

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
