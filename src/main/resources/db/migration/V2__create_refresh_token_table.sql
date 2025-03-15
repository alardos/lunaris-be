--liquibase formatted sql
--changeset alardos:2
create table refresh_tokens (
    token varchar not null primary key
);

--rollback drop table refresh_tokens;
