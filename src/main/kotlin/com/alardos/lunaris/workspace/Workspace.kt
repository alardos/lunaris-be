package com.alardos.lunaris.workspace

import com.alardos.lunaris.card.Card
import com.alardos.lunaris.card.CardRowMapper
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.result.ResultSetAccumulator
import org.jdbi.v3.core.statement.StatementContext
import java.security.MessageDigest
import java.sql.ResultSet
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


class WorkspaceMapper : RowMapper<Workspace> {
    override fun map(rs: ResultSet, ctx: StatementContext): Workspace {
        return Workspace(
            id = rs.getObject("workspace.id", UUID::class.java),
            owner = rs.getObject("workspace.owner", UUID::class.java),
            name = rs.getString("workspace.name"),
            // the cast asserts the type, will throw exception if the result set is incompatible
            members = (rs.getArray("workspace.members").array as Array<UUID>).asList()
        )
    }
}

class WorkspaceDetailsAccumulator : ResultSetAccumulator<WorkspaceDetails> {
    override fun apply(
        workspace: WorkspaceDetails?,
        rs: ResultSet,
        ctx: StatementContext
    ): WorkspaceDetails? {
        return if (workspace == null) {
            val user = rs.getObject("user.id") as UUID?
            val card = rs.getObject("card.id") as UUID?
            WorkspaceDetails(
                id = rs.getObject("workspace.id", UUID::class.java),
                name = rs.getString("workspace.name"),
                members = user
                    ?.let {
                        mutableListOf(
                            Member(
                                id = rs.getObject("user.id", UUID::class.java),
                                email = rs.getString("user.email"),
                                color = rs.getString("user.color"),
                                rank = MemberRank.valueOf(rs.getString("user.rank"))
                            )
                        )
                    }
                    ?: mutableListOf(),
                cards = card
                    ?.let { mutableListOf(CardRowMapper().map(rs, ctx)) }
                    ?: mutableListOf()
            )

        } else {
            val member = Member(
                id = rs.getObject("user.id", UUID::class.java),
                email = rs.getString("user.email"),
                color = rs.getString("user.color"),
                rank = MemberRank.valueOf(rs.getString("user.rank"))
            )
            if (workspace.members.none { o -> o.id == member.id })
                workspace.members.add(member)

            val card = CardRowMapper().map(rs, ctx)
            if (workspace.cards.none { o -> o.id == card.id })
                workspace.cards.add(card)

            workspace
        }

    }
}

class DistributionMapper: RowMapper<DistributionItem> {
    override fun map(rs: ResultSet, ctx: StatementContext): DistributionItem {
        return DistributionItem(
            card = rs.getObject("card.id", UUID::class.java),
            place = rs.getObject("card.place") as Int?, // getInt() - wont return null
            ordinal = rs.getObject("card.ordinal") as Int?, // getInt() - wont return null
        )

    }
}