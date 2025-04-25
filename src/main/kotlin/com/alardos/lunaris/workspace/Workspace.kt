package com.alardos.lunaris.workspace

import com.alardos.lunaris.card.model.Card
import java.security.MessageDigest
import java.util.*

data class DistributionItem(val card: UUID, val place: Int?, val ordinal: Int?)
class Distribution {
    val items: List<DistributionItem>
    var hash: String
    constructor(items: List<DistributionItem>, hash: String? = null) {
        this.items = items
        this.hash = hash?:calcHash()
    }

    fun calcHash() =
        if (items.isEmpty()) ""
        else items.sortedBy { a -> a.card }
        .map { i -> "${i.place}-${i.ordinal}-${i.card}" }
        .reduce { acc, i -> "$acc-$i" }
        .sha256()

    fun updateHash(): Distribution {
        this.hash = this.calcHash()
        return this;
    }


}

fun String.sha256(): String {
    val bytes = this.toByteArray()
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(bytes)
    return hashBytes.joinToString("") { "%02x".format(it) }
}

data class WorkspaceCandidate(val name: String)
open class Workspace(
    var id: UUID,
    var name: String,
    var owner: UUID,
    var members: List<UUID>,
)

public enum class MemberRank{ Owner, Participant }
data class Member(
    val id: UUID,
    val email: String,
    val color: String,
    val rank: MemberRank
)
class WorkspaceDetails(
    val id: UUID,
    val name: String,
    val members: MutableList<Member>,
    val cards: MutableList<Card>
)

