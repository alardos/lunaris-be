package com.alardos.lunaris.workspace

import com.alardos.lunaris.card.Card
import com.alardos.lunaris.card.CardRowMapper
import org.jdbi.v3.core.mapper.RowMapper
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

class WorkspaceDetails(
    id: UUID,
    name: String,
    owner: UUID,
    members: List<UUID>,
    val cards: MutableList<Card>
): Workspace(id,name,owner,members)


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

class WorkspaceDetailsMapper : RowMapper<WorkspaceDetails> {
    override fun map(rs: ResultSet, ctx: StatementContext): WorkspaceDetails {
        return WorkspaceDetails(
            id = rs.getObject("workspace.id", UUID::class.java),
            owner = rs.getObject("workspace.owner", UUID::class.java),
            name = rs.getString("workspace.name"),
            // the cast asserts the type, will throw exception if the result set is incompatible
            members = (rs.getArray("workspace.members").array as Array<UUID>).asList(),
            cards = if (rs.getObject("card.id", UUID::class.java) != null)
                mutableListOf(CardRowMapper().map(rs,ctx))
            else
                mutableListOf()
        )
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