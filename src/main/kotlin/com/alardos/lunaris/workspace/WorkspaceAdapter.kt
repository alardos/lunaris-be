package com.alardos.lunaris.workspace

import com.alardos.lunaris.auth.model.User
import com.alardos.lunaris.core.dbg
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class WorkspaceAdapter(
    @Autowired val repo: WorkspaceRepo
) {
    val serv = WorkspaceServ()

    fun findByOwner(ownerId: UUID): List<Workspace> {
        return dbg(repo.findByOwner(ownerId))
    }

    fun find(id:UUID): Workspace? {
        return dbg(repo.find(id))
    }

    fun findDetails(id: UUID): WorkspaceDetails? = repo.findDetails(id)

    fun create(creator: User, workspace: WorkspaceCandidate): Result<Workspace, WorkspaceValidatorError> {
        val others = repo.findByOwner(creator.id)

        return serv.validate(workspace,others)?.let {
            Err(it)
        }?: run {
            Ok(repo.create(creator.id, workspace))
        }
    }


}