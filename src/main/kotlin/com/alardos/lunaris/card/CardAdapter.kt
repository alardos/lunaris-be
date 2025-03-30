package com.alardos.lunaris.card

import com.alardos.lunaris.workspace.WorkspaceRepo
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CardAdapter(
    @Autowired val repo: CardRepo,
    @Autowired val workspaceRepo: WorkspaceRepo
) {
    val serv = CardServ()

    /** workspace must exist **/
    fun create(card: CardCandidate, owner: UUID, workspace: UUID):Card? =
        workspaceRepo.find(workspace)?.let {
            repo.insert(card, owner, it.id)
        }

    fun find(cardId: UUID): Card? = repo.find(cardId)

    fun forWorkspace(workspace: UUID) =
        repo.forWorkspace(workspace)

    fun update(card: Card)  =
        workspaceRepo.find(card.workspace)
            ?.let { w -> serv.validate(w,card) ?.let { Err(it) } }
            ?:run { Ok(repo.update(card)) }


    fun hasAccess(user: UUID, cards: List<UUID>, level: AccessLevel): Boolean {
        val access = repo.findUserAccess(cards)
        return serv.hasAccess(user, cards, access, level)
    }

}