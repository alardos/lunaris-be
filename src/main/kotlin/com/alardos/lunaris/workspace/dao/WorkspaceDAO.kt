package com.alardos.lunaris.workspace.dao

import com.alardos.lunaris.workspace.Workspace
import com.alardos.lunaris.workspace.WorkspaceCandidate
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class WorkspaceDAO(@Autowired val jdbi: Jdbi) {
    val dao = DistributionDAO(jdbi)

    fun find(id: UUID): Workspace? {
        return jdbi.withHandle<Workspace?, Exception>
        { handle -> handle.select("""
            select 
            w.id as "workspace.id", 
            w.name as "workspace.name", 
            w.owner as "workspace.owner", 
            array_remove(array_agg(wu.user),null) as "workspace.members"
            from workspaces w 
            left join workspace_user wu on w.id = wu.workspace
            where w.id = '$id'
            group by w.id;        
        """).map(WorkspaceMapper()).firstOrNull() }
    }

    fun findByOwner(ownerId: UUID): List<Workspace> {
        return jdbi.withHandle<List<Workspace>, Exception>
        { handle ->
            handle.select("""
                select 
                w.id as "workspace.id", 
                w.name as "workspace.name", 
                w.owner as "workspace.owner", 
                array_remove(array_agg(wu.user),null) as "workspace.members"
                from workspaces w
                left join workspace_user wu on w.id = wu.workspace
                where w.owner = '$ownerId'
                group by w.id;
            """)
            .map(WorkspaceMapper())
            .list()
        }
    }

    fun create(creator: UUID, workspace: WorkspaceCandidate): Workspace {
        return jdbi.withHandle<Workspace, Exception> { handle ->
            val id = handle.select("insert into workspaces(owner, name) values('${creator}', '${workspace.name}') returning id")
                .mapTo(UUID::class.java).first()
            Workspace(id, workspace.name,creator,listOf())
        }
    }

}

class WorkspaceMapper : RowMapper<Workspace> {
    override fun map(rs: ResultSet, ctx: StatementContext): Workspace {
        return Workspace(
            id = rs.getObject("workspace.id", UUID::class.java),
            owner = rs.getObject("workspace.owner", UUID::class.java),
            name = rs.getString("workspace.name"),
            // the cast asserts the type, will throw exception if the result set is incompatible
            members = (rs.getArray("workspace.members").array as Array<UUID>).asList()
        )
    }
}


