package com.alardos.lunaris.workspace

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WorkspaceUnitTest {
    val serv: WorkspaceServ = WorkspaceServ()

    @Test
    fun `when no problem return null`() {
        val userId = UUID.randomUUID()
        val error = serv.validate(
            WorkspaceCandidate("new Name"),
            listOf(
                Workspace(UUID.randomUUID(),"1",userId,listOf()),
                Workspace(UUID.randomUUID(),"2",userId,listOf())
            )
        )
        assertNull(error)
    }

    @Test
    fun `given already has name x, when creation with name x reject`() {
        val userId = UUID.randomUUID()
        val error = serv.validate(
            WorkspaceCandidate("conflict"),
            listOf(Workspace(UUID.randomUUID(),"conflict",userId,listOf()))
        )
        assertEquals(WorkspaceValidatorError.NAME_TAKEN, error)
    }

    @Test
    fun `when new name is empty, then reject`() {
        val error = serv.validate(
            WorkspaceCandidate(""),
            listOf()
        )
        assertEquals(WorkspaceValidatorError.WRONG_NAME_FORMAT, error)
    }
}