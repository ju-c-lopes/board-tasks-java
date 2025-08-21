--liquibase formatted sql
--changeset juliano:202508210737
--comment: boards_columns table create

CREATE TABLE BOARDS_COLUMNS (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   `order` int NOT NULL,
   type VARCHAR(10) NOT NULL,
   board_id BIGINT NOT NULL,
   CONSTRAINT board__boards_columns_fk FOREIGN KEY (board_id) REFERENCES BOARDS(id) ON DELETE CASCADE,
   CONSTRAINT id_order_uk UNIQUE KEY unique_order_id_board (board_id, `order`)
) ENGINE=InnoDB;

--rollback DROP TABLE BOARDS_COLUMNS