package com.alardos.lunaris.workspace

enum class WorkspaceValidatorError { NAME_TAKEN, WRONG_NAME_FORMAT }
class WorkspaceServ {
    fun validate(workspace: WorkspaceCandidate, usersOtherWorkspaces: List<Workspace>): WorkspaceValidatorError?  {
        return if (usersOtherWorkspaces.any { w -> w.name == workspace.name }) {
            WorkspaceValidatorError.NAME_TAKEN
        } else {
            validateName(workspace.name)
        }
    }

    private fun validateName(name: String): WorkspaceValidatorError? {
        return if (name.length < 4) {
            WorkspaceValidatorError.WRONG_NAME_FORMAT
        } else
            null

    }
}

