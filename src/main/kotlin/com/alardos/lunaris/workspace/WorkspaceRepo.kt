package com.alardos.lunaris.workspace

import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class WorkspaceRepo(@Autowired val jdbi: Jdbi) {

    fun find(id: String): Workspace? {
        return jdbi.withHandle<Workspace?, Exception> { handle ->
            handle.select("select * from workspaces where id = '$id'")
                .map(WorkspaceMapper())
                .firstOrNull()
        }
    }

    fun findByOwner(ownerId: String): List<Workspace> {
        return jdbi.withHandle<List<Workspace>, Exception> { handle ->
            handle.select("select * from workspaces where owner = '$ownerId'")
                .map(WorkspaceMapper())
                .list()
        }
    }

    fun create(creator: String, workspace: WorkspaceCandidate): Workspace {
        return jdbi.withHandle<Workspace, Exception> { handle ->
            handle.select("insert into workspaces(owner, name) values('${creator}', '${workspace.name}') returning *")
                .map(WorkspaceMapper()).first()
        }
    }

}