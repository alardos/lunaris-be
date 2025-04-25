package com.alardos.lunaris.card

import com.alardos.lunaris.card.dao.CardAccessDAO
import com.alardos.lunaris.card.dao.CardDAO
import com.alardos.lunaris.card.model.Card
import com.alardos.lunaris.card.model.CardCandidate
import com.alardos.lunaris.workspace.dao.WorkspaceDAO
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CardAdapter(
    @Autowired val cardDAO: CardDAO,
    @Autowired val cardAccessDAO: CardAccessDAO,
    @Autowired val workspaceDAO: WorkspaceDAO
) {
    val serv = CardServ()

    /** workspace must exist **/
    fun create(card: CardCandidate, owner: UUID, workspace: UUID): Card? =
        workspaceDAO.find(workspace)?.let {
            cardDAO.insert(card, owner, it.id)
        }

    fun find(cardId: UUID): Card? = cardDAO.find(cardId)

    fun forWorkspace(workspace: UUID) =
        cardDAO.forWorkspace(workspace)

    fun update(card: Card)  =
        workspaceDAO.find(card.workspace)
            ?.let { w -> serv.validate(w,card) ?.let { Err(it) } }
            ?:run { Ok(cardDAO.update(card)) }


    fun hasAccess(user: UUID, cards: List<UUID>, level: AccessLevel): Boolean {
        val access = cardAccessDAO.find(cards)
        return serv.hasAccess(user, cards, access, level)
    }

}