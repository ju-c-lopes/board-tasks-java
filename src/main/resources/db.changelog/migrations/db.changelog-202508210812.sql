--liquibase formatted sql
--changeset juliano:202508210812
--comment: blocks table create

CREATE TABLE BLOCKS (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   block_reason VARCHAR(255) NOT NULL,
   blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   unblock_reason VARCHAR(255) NULL,
   unblocked_at TIMESTAMP NULL,
   blocked BOOLEAN NOT NULL,
   card_id BIGINT NOT NULL,
   CONSTRAINT cards__blocks_fk FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--rollback DROP TABLE BLOCKS