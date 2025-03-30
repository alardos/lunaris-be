package com.alardos.lunaris.workspace

import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class WorkspaceRepo(@Autowired val jdbi: Jdbi) {


    fun find(id: UUID): Workspace? {
        return jdbi.withHandle<Workspace?, Exception>
        { handle -> handle.select("""
            select 
            w.id as "workspace.id", 
            w.name as "workspace.name", 
            w.owner as "workspace.owner", 
            array_remove(array_agg(wu.user),null) as "workspace.members"
            from workspaces w 
            left join workspace_user wu on w.id = wu.workspace
            where w.id = '$id'
            group by w.id;        
        """).map(WorkspaceMapper()).firstOrNull() }
    }

    fun findDetails(id: UUID): WorkspaceDetails? {
        return jdbi.withHandle<WorkspaceDetails?, Exception>
        { handle -> handle.select("""
            select
            w.id as "workspace.id", 
            w.name as "workspace.name", 
            w.owner as "workspace.owner", 
            array_remove(array_agg(wu.user),null) as "workspace.members",
            c.id as "card.id",
            c.owner as "card.owner",
            tc.content as "text_card.content",
            c.workspace as "card.workspace",
            c.created_at as "card.created_at"
            from workspaces w 
            left join workspace_user wu on w.id = wu.workspace
            left join cards c on c.workspace = w.id
            left join text_cards tc on tc.id = c.id
            where w.id = '$id'
            group by w.id, c.id, tc.id;        
        """).map(WorkspaceDetailsMapper())
        .reduce { acc, n -> acc.cards.addAll(n.cards); acc } }
    }

    fun distributionItemsFor(id: UUID): List<DistributionItem>? {
        return jdbi.withHandle<List<DistributionItem>?, Exception>
        { handle ->
            handle.select("""
                select id as "card.id", place as "card.place", ordinal as "card.ordinal" from cards where workspace = '$id';
            """).map(DistributionMapper()).list()
        }
    }

    fun findByOwner(ownerId: UUID): List<Workspace> {
        return jdbi.withHandle<List<Workspace>, Exception>
        { handle ->
            handle.select("""
                select 
                w.id as "workspace.id", 
                w.name as "workspace.name", 
                w.owner as "workspace.owner", 
                array_remove(array_agg(wu.user),null) as "workspace.members"
                from workspaces w
                left join workspace_user wu on w.id = wu.workspace
                where w.owner = '$ownerId'
                group by w.id;
            """)
            .map(WorkspaceMapper())
            .list()
        }
    }

    fun create(creator: UUID, workspace: WorkspaceCandidate): Workspace {
        return jdbi.withHandle<Workspace, Exception> { handle ->
            val id = handle.select("insert into workspaces(owner, name) values('${creator}', '${workspace.name}') returning id")
                .mapTo(UUID::class.java).first()
            Workspace(id, workspace.name,creator,listOf())
        }
    }

    fun updateDistribution(distribution: Distribution) {
        println(distribution)
        return jdbi.withHandle<Unit, Exception> { handle ->
            handle.execute(
                distribution.items
                    .map { i -> "update cards set place = ${i.place}, ordinal = ${i.ordinal} where id = '${i.card}';" }
                    .reduce { acc,n -> acc+"\n"+n }
            )
        }
    }

}