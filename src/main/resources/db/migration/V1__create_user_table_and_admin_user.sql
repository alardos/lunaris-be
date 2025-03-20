--liquibase formatted sql
--changeset alardos:1
create table users (
    id uuid not null default gen_random_uuid() primary key,
    email varchar not null unique,
    password varchar not null,
    first_name varchar not null,
    last_name varchar not null
);

--rollback drop table users;



