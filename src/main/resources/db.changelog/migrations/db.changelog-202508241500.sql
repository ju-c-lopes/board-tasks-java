--liquibase formatted sql
--changeset juliano:202508241500
--comment: Add default value to order column in CARDS table

ALTER TABLE CARDS MODIFY COLUMN `order` int NOT NULL DEFAULT 1;

--rollback ALTER TABLE CARDS MODIFY COLUMN `order` int NOT NULL;
