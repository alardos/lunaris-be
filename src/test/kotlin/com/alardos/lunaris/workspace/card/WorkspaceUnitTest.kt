package com.alardos.lunaris.workspace.card

import com.alardos.lunaris.workspace.Workspace
import com.alardos.lunaris.workspace.WorkspaceCandidate
import com.alardos.lunaris.workspace.WorkspaceServ
import com.alardos.lunaris.workspace.WorkspaceValidatorError
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WorkspaceUnitTest {
    val serv: WorkspaceServ = WorkspaceServ()

    @Test fun `when no problem return null`() {
        val userId = "userId"
        val error = serv.validate(
            WorkspaceCandidate("new Name"),
            listOf(Workspace("1","1",userId),Workspace("2","2",userId))
        )
        assertNull(error)
    }

    @Test fun `given already has name x, when creation with name x reject`() {
        val userId = "userId"
        val error = serv.validate(
            WorkspaceCandidate("conflict"),
            listOf(Workspace("1","conflict",userId))
        )
        assertEquals(WorkspaceValidatorError.NAME_TAKEN, error)
    }

    @Test fun `when new name is empty, then reject`() {
        val error = serv.validate(
            WorkspaceCandidate(""),
            listOf()
        )
        assertEquals(WorkspaceValidatorError.WRONG_NAME_FORMAT, error)
    }
}