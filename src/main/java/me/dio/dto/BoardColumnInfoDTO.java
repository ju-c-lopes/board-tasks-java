package me.dio.dto;

import me.dio.persistence.entity.BoardColumnTypeEntity;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnTypeEntity type) {
}
