package com.alardos.lunaris.card

import com.alardos.lunaris.workspace.Workspace
import java.util.*

enum class CardValidationError { NAME_TAKEN, WRONG_NAME_FORMAT }
enum class AccessLevel { Edit, Move }
class CardServ {
    fun validate(workspace: Workspace, card: Card): CardValidationError? {
        return null
    }


    fun hasAccess(user:UUID, cards: List<UUID>, accesses: List<CardAccess>, level: AccessLevel): Boolean {
        // todo: write a unit test for this
        for (card in cards) {
            val access = accesses.find { a -> a.card == card }
            if (access == null) throw RuntimeException("Missing access configuration")
            if (level != AccessLevel.Move) TODO("Other access level(s) are not implemented yet")
            if (
                user != access.cardOwner &&
                user != access.workspaceOwner &&
                access.workspaceMembers.none { id -> id == user }
            ) {
               return false;
            }
        }
        return true;
    }

}