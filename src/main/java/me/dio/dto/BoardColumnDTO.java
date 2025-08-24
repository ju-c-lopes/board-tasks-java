package me.dio.dto;

import me.dio.persistence.entity.BoardColumnTypeEntity;

public record BoardColumnDTO(Long id,
                             String name,
                             BoardColumnTypeEntity type,
                             int cardsAmount) {
}
