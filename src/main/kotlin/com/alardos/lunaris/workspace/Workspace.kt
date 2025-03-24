package com.alardos.lunaris.workspace

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.util.*

data class WorkspaceCandidate(val name: String)
data class Workspace(
    var id: UUID,
    var name: String,
    /** owner user's id */
    var owner: UUID,
    var members: List<UUID>,
)



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
