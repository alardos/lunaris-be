package com.alardos.lunaris.workspace.dao

import com.alardos.lunaris.card.dao.CardRowMapper
import com.alardos.lunaris.workspace.Member
import com.alardos.lunaris.workspace.MemberRank
import com.alardos.lunaris.workspace.WorkspaceDetails
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.result.ResultSetAccumulator
import org.jdbi.v3.core.statement.StatementContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class WorkspaceDetailsDAO(@Autowired val jdbi: Jdbi) {
    fun find(id: UUID): WorkspaceDetails? {
        return jdbi.withHandle<WorkspaceDetails?, Exception>
        { handle ->
            handle.select(
                """
                select
                w.id as "workspace.id", 
                w.name as "workspace.name", 
                w.owner as "workspace.owner", 
                u.id as "user.id",
                u.email as "user.email",
                wu.color as "user.color",
                wu.rank as "user.rank",
                c.id as "card.id",
                c.owner as "card.owner",
                tc.content as "text_card.content",
                c.workspace as "card.workspace",
                c.created_at as "card.created_at"
                from workspaces w 
                left join workspace_user wu on w.id = wu.workspace
                left join cards c on c.workspace = w.id
                left join text_cards tc on tc.id = c.id
                left join users u on wu."user" = u.id
                where w.id = '$id'        
            """
            ).reduceResultSet(null, WorkspaceDetailsAccumulator())
        }
    }
}

private class WorkspaceDetailsAccumulator : ResultSetAccumulator<WorkspaceDetails> {
    override fun apply(
        workspace: WorkspaceDetails?,
        rs: ResultSet,
        ctx: StatementContext
    ): WorkspaceDetails? {
        return if (workspace == null) {
            val user = rs.getObject("user.id") as UUID?
            val card = rs.getObject("card.id") as UUID?
            WorkspaceDetails(
                id = rs.getObject("workspace.id", UUID::class.java),
                name = rs.getString("workspace.name"),
                members = user
                    ?.let {
                        mutableListOf(
                            Member(
                                id = rs.getObject("user.id", UUID::class.java),
                                email = rs.getString("user.email"),
                                color = rs.getString("user.color"),
                                rank = MemberRank.valueOf(rs.getString("user.rank"))
                            )
                        )
                    }
                    ?: mutableListOf(),
                cards = card
                    ?.let { mutableListOf(CardRowMapper().map(rs, ctx)) }
                    ?: mutableListOf()
            )

        } else {
            val member = Member(
                id = rs.getObject("user.id", UUID::class.java),
                email = rs.getString("user.email"),
                color = rs.getString("user.color"),
                rank = MemberRank.valueOf(rs.getString("user.rank"))
            )
            if (workspace.members.none { o -> o.id == member.id })
                workspace.members.add(member)

            val card = CardRowMapper().map(rs, ctx)
            if (workspace.cards.none { o -> o.id == card.id })
                workspace.cards.add(card)

            workspace
        }

    }
}
