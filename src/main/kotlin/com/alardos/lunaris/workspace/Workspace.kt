package com.alardos.lunaris.workspace

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

data class WorkspaceCandidate(val name: String)
data class Workspace(
    var id: String,
    var name: String,
    /** owner user's id */
    var owner: String,
)



class WorkspaceMapper : RowMapper<Workspace> {
    override fun map(rs: ResultSet, ctx: StatementContext): Workspace {
        return Workspace(
            id = rs.getString("id"),
            owner = rs.getString("owner"),
            name = rs.getString("name"),
        )
    }
}
