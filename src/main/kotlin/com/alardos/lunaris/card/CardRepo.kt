package com.alardos.lunaris.card

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
                    TextCard(id,owner,workspace,Date(),candidate.content!!)
                }
            }
        }

    fun find(id: UUID):Card? =
        jdbi.withHandle<Card, Exception>
            { handle ->
                handle.select("""
                    select * 
                    from cards c
                    left join text_cards tc on tc.id = c.id
                    where c.id = '$id';
                """.trimIndent())
                .map(CardMapper()).firstOrNull()
            }

    fun forWorkspace(workspace: UUID): List<Card> =
        jdbi.withHandle<List<Card>, Exception>
            { handle ->
                handle.select("""
                    select * 
                    from cards c
                    left join text_cards tc on tc.id = c.id
                    where c.workspace = '$workspace';
                """.trimIndent())
                    .map(CardMapper()).list()
            }

}