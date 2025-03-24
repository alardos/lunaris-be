--liquibase formatted sql
--changeset alardos:5
create table cards (
    id uuid not null default gen_random_uuid() primary key,
    owner uuid not null references users(id),
    workspace uuid not null references workspaces(id),
    created_at timestamp not null default now()
);

create table text_cards (
    id uuid not null references cards(id) primary key,
    content varchar not null
);

--rollback drop table text_cards;
--rollback drop table cards;
