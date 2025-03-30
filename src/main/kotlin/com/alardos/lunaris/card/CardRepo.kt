package com.alardos.lunaris.card

import com.alardos.lunaris.core.toSqlList
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class CardRepo(@Autowired val jdbi: Jdbi) {

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
                    TextCard(id,owner,workspace,Date().time,candidate.content!!)
                }
            }
        }

    fun find(id: UUID):Card? =
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
    fun findUserAccess(cards: List<UUID>): List<CardAccess> {
        return jdbi.withHandle<List<CardAccess>, Exception>
        { handle -> handle.select("""
            select 
            c.id as "card.id",
            w.owner as "workspace.owner", 
            c.owner as "card.owner",
            array_remove(array_agg(wu.user),null) as "workspace.members"
            from cards c
            left join workspaces w on c.workspace = w.id
            left join workspace_user wu on w.id = wu.workspace
            where c.id in (${cards.toSqlList()})
            group by c.id, w.id;        
        """).map(CardAccessRowMapper()).list() }
    }

}