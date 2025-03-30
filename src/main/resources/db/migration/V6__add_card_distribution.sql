--liquibase formatted sql
--changeset alardos:6
alter table cards
add column place int4 null;

alter table cards
add column ordinal int4 null;

--rollback alter table cards drop column place;
--rollback alter table cards drop column ordinal;
