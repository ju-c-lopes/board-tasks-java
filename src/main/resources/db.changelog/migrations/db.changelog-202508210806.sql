--liquibase formatted sql
--changeset juliano:202508210806
--comment: cards table create

CREATE TABLE CARDS (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   title VARCHAR(255) NOT NULL,
   description VARCHAR(255) NOT NULL,
   `order` int NOT NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   moved_at TIMESTAMP NULL,
   board_column_id BIGINT NOT NULL,
   CONSTRAINT boards_columns__cards_fk FOREIGN KEY (board_column_id) REFERENCES BOARDS_COLUMNS(id) ON DELETE CASCADE,
   CONSTRAINT id_order_uk UNIQUE KEY unique_board_id_order (board_column_id, `order`)
) ENGINE=InnoDB;

--rollback DROP TABLE CARDS