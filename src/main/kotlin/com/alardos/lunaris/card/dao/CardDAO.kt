package com.alardos.lunaris.card.dao

import com.alardos.lunaris.card.model.Card
import com.alardos.lunaris.card.model.CardCandidate
import com.alardos.lunaris.card.model.CardStrType
import com.alardos.lunaris.card.model.TextCard
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class CardDAO(@Autowired val jdbi: Jdbi) {

    fun insert(candidate: CardCandidate, owner: UUID, workspace: UUID): Card =
        jdbi.withHandle<Card, Exception>
        { handle ->
            when(candidate.type) {
                CardStrType.text -> {
                    val id = handle.select("""
                        WITH inserted_card AS (
                            INSERT INTO cards (owner, workspace)
                            VALUES ('$owner', '$workspace')
                            RETURNING id
                        )
                        INSERT INTO text_cards (id, content)
                        SELECT id, '' FROM inserted_card
                        RETURNING id; """
                    ).mapTo(UUID::class.java).first()
                    TextCard(id, owner, workspace, Date().time, candidate.content!!)
                }
            }
        }

    fun find(id: UUID): Card? =
        jdbi.withHandle<Card, Exception>
            { handle ->
                handle.select("""
                    select 
                    c.id as "card.id",
                    c.owner as "card.owner",
                    c.workspace as "card.workspace",
                    c.created_at as "card.created_at",
                    tc.content as "text_card.content"
                    from cards c
                    left join text_cards tc on tc.id = c.id
                    where c.id = '$id';
                """.trimIndent())
                .map(CardRowMapper()).firstOrNull()
            }

    fun forWorkspace(workspace: UUID): List<Card> =
        jdbi.withHandle<List<Card>, Exception>
            { handle ->
                handle.select("""
                    select 
                    c.id as "card.id",
                    c.owner as "card.owner",
                    c.workspace as "card.workspace",
                    c.created_at as "card.created_at",
                    tc.content as "text_card.content"
                    from cards c
                    left join text_cards tc on tc.id = c.id
                    where c.workspace = '$workspace';
                """.trimIndent())
                    .map(CardRowMapper()).list()
            }


    fun update(card: Card): Card {
        return jdbi.withHandle<Card, Exception>
            { handle ->
                when (card) {
                    is TextCard -> {
                        handle.execute(
                            """
                            update text_cards set content = '${card.content}' where id = '${card.id}';
                            """.trimIndent()
                        )
                        card
                    }
                }

            }
    }
}

class CardRowMapper : RowMapper<Card> {
    override fun map(rs: ResultSet, ctx: StatementContext): Card =
        when {
            rs.getString("text_card.content") != null ->
                TextCard(
                    id = rs.getObject("card.id", UUID::class.java),
                    owner = rs.getObject("card.owner", UUID::class.java),
                    workspace = rs.getObject("card.workspace", UUID::class.java),
                    createdAt = rs.getTimestamp("card.created_at").time,
                    content = rs.getString("text_card.content"),
                )
            else -> throw RuntimeException("could not tell exact card type from query")
        }
}
