package com.alardos.lunaris.card

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.util.*

sealed class Card(
    val type: CardStrType,
    val id: UUID,
    val owner: UUID,
    val workspace: UUID,
    val createdAt: Date
) {

}

enum class CardStrType { text }
data class CardCandidate(val type: CardStrType, val content: String?)
class TextCard(
    id: UUID,
    owner: UUID,
    workspace: UUID,
    createdAt: Date,
    var content: String
): Card(CardStrType.text,id,owner,workspace,createdAt)

class CardMapper : RowMapper<Card> {
    override fun map(rs: ResultSet, ctx: StatementContext): Card =
        when {
            rs.getString("content") != null ->
                TextCard(
                    id = rs.getObject("id",UUID::class.java),
                    owner = rs.getObject("owner",UUID::class.java),
                    workspace = rs.getObject("workspace",UUID::class.java),
                    createdAt = rs.getDate("created_at"),
                    content = rs.getString("content"),
                )
            else -> throw RuntimeException("could not tell exact card type from query")
        }
}
