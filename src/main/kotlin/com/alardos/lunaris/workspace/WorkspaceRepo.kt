package com.alardos.lunaris.workspace

import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class WorkspaceRepo(@Autowired val jdbi: Jdbi) {


    fun find(id: UUID): Workspace? {
        return jdbi.withHandle<Workspace?, Exception>
        { handle -> handle.select("""
            select w.*, array_remove(array_agg(wu.user),null) as "members" 
            from workspaces w 
            left join workspace_user wu on w.id = wu.workspace
            where w.id = '$id'
            group by w.id;        
        """).map(WorkspaceMapper()).firstOrNull() }
    }

    fun findDetails(id: UUID): WorkspaceDetails? {
        return jdbi.withHandle<WorkspaceDetails?, Exception>
        { handle -> handle.select("""
            select w.*, array_remove(array_agg(wu.user),null) as "members", c.*, tc.*
            from workspaces w 
            left join workspace_user wu on w.id = wu.workspace
            left join cards c on c.workspace = w.id
            left join text_cards tc on tc.id = c.id
            where w.id = '$id'
            group by w.id, c.id, tc.id;        
        """).map(WorkspaceDetailsMapper()).list()
        .reduce { acc, n -> acc.cards.addAll(n.cards); acc } }
    }

    fun findByOwner(ownerId: UUID): List<Workspace> {
        return jdbi.withHandle<List<Workspace>, Exception>
        { handle ->
            handle.select("""
                select w.*, array_remove(array_agg(wu.user),null) as "members"
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