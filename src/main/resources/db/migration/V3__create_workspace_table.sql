--liquibase formatted sql
--changeset alardos:3
create table workspaces (
    id uuid not null default gen_random_uuid() primary key,
    owner uuid not null references users(id),
    name varchar not null,
    unique (owner,name)
);

--rollback drop table workspaces;
