--liquibase formatted sql
--changeset alardos:4
create table workspace_user (
    "user" uuid not null references users(id),
    "workspace" uuid not null references workspaces(id),
    primary key("user","workspace")
);

--rollback drop table workspace_user;
