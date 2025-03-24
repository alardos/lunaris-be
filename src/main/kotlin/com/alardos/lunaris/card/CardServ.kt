package com.alardos.lunaris.card

import com.alardos.lunaris.workspace.Workspace

enum class CardValidationError { NAME_TAKEN, WRONG_NAME_FORMAT }
class CardServ {
    fun validate(workspace: Workspace, card: Card): CardValidationError? {
        return null
    }

}