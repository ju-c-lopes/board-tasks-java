package me.dio.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CardEntity {

    private Long id;
    private String title;
    private String description;
    private int order;
    private OffsetDateTime createdAt;
    private OffsetDateTime movedAt;
    private BoardColumnEntity boardColumn = new BoardColumnEntity();
}
