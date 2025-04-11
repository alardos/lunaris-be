--liquibase formatted sql
--changeset alardos:7
alter table workspace_user add column color varchar not null default '#BF6A02';
alter table workspace_user add column rank varchar not null default 'Owner';

--rollback alter table workspace_user drop column color;
--rollback alter table workspace_user drop column rank;
