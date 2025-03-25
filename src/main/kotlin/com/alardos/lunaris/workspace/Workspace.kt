package com.alardos.lunaris.workspace

import com.alardos.lunaris.card.Card
import com.alardos.lunaris.card.CardRowMapper
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.util.*

data class WorkspaceCandidate(val name: String)
open class Workspace(
    var id: UUID,
    var name: String,
    var owner: UUID,
    var members: List<UUID>,
)

class WorkspaceDetails(
    id: UUID,
    name: String,
    owner: UUID,
    members: List<UUID>,
    val cards: MutableList<Card>
): Workspace(id,name,owner,members)


class WorkspaceMapper : RowMapper<Workspace> {
    override fun map(rs: ResultSet, ctx: StatementContext): Workspace {
        return Workspace(
            id = rs.getObject("id", UUID::class.java),
            owner = rs.getObject("id", UUID::class.java),
            name = rs.getString("name"),
            // the cast asserts the type, will throw exception if the result set is incompatible
            members = (rs.getArray("members").array as Array<UUID>).asList()
        )
    }
}

class WorkspaceDetailsMapper : RowMapper<WorkspaceDetails> {
    override fun map(rs: ResultSet, ctx: StatementContext): WorkspaceDetails {
        return WorkspaceDetails(
            id = rs.getObject("id", UUID::class.java),
            owner = rs.getObject("owner", UUID::class.java),
            name = rs.getString("name"),
            // the cast asserts the type, will throw exception if the result set is incompatible
            members = (rs.getArray("members").array as Array<UUID>).asList(),
            cards = mutableListOf(CardRowMapper().map(rs,ctx))
        )
    }
}
