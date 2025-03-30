package com.alardos.lunaris.card

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class CardUnitTest {
    val serv = CardServ()

    @Nested
    inner class CanAccess {

        @Nested
        inner class Move {

            @Test
            fun `my card`() {
                val me = UUID.randomUUID()
                val other = UUID.randomUUID()
                val target = UUID.randomUUID()
                val cards = listOf(target)
                val access = listOf(CardAccess(target, me, me, listOf(me, other)))
                Assertions.assertTrue {
                    serv.hasAccess(me, cards, access, AccessLevel.Move)
                }
            }

            @Test
            fun `other's card can access workspace`() {
                val me = UUID.randomUUID()
                val other = UUID.randomUUID()
                val card = UUID.randomUUID()
                val cards = listOf(card)
                val access = listOf(CardAccess(card, other, me, listOf(me, other)))
                Assertions.assertTrue {
                    serv.hasAccess(me, cards, access, AccessLevel.Move)
                }

            }

            @Test
            fun `not part of workspace`() {
                val me = UUID.randomUUID()
                val other = UUID.randomUUID()
                val card = UUID.randomUUID()
                val cards = listOf(card)
                val access = listOf(CardAccess(card, other, other, listOf(other)))
                Assertions.assertFalse {
                    serv.hasAccess(me, cards, access, AccessLevel.Move)
                }
            }
        }
    }
}