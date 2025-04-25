package com.alardos.lunaris.workspace.dao

import com.alardos.lunaris.workspace.Distribution
import com.alardos.lunaris.workspace.DistributionItem
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class DistributionDAO(@Autowired val jdbi: Jdbi) {

    fun find(id: UUID): Distribution? {
        return jdbi.withHandle<Distribution?, Exception>
        { handle ->
            Distribution(handle.select(
                """
                    select id as "card.id", place as "card.place", ordinal as "card.ordinal" from cards where workspace = '$id';
                """
            ).map(DistributionMapper()).list())
        }
    }

    fun update(distribution: Distribution) {
        return jdbi.withHandle<Unit, Exception> { handle ->
            handle.execute(
                distribution.items
                    .map { i -> "update cards set place = ${i.place}, ordinal = ${i.ordinal} where id = '${i.card}';" }
                    .reduce { acc, n -> acc + "\n" + n }
            )
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
