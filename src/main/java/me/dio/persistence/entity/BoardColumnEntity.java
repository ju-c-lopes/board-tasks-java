package me.dio.persistence.entity;

import lombok.Data;

@Data
public class BoardColumnEntity {

    private Long id;
    private String name;
    private int Order;
    private BoardColumnTypeEntity type;
    private BoardEntity board = new BoardEntity();

}
