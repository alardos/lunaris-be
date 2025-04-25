package com.alardos.lunaris.card.dao

import com.alardos.lunaris.card.model.CardAccess
import com.alardos.lunaris.core.toSqlList
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class CardAccessDAO(@Autowired val jdbi: Jdbi) {

    fun find(cards: List<UUID>): List<CardAccess> {
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

class CardAccessRowMapper: RowMapper<CardAccess> {
    override fun map(
        rs: ResultSet,
        ctx: StatementContext?
    ): CardAccess =
        CardAccess(
            card = rs.getObject("card.id", UUID::class.java),
            cardOwner = rs.getObject("card.owner", UUID::class.java),
            workspaceOwner = rs.getObject("workspace.owner", UUID::class.java),
            workspaceMembers = (rs.getArray("workspace.members").array as Array<UUID>).asList(),
        )


}

