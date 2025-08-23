package me.dio.persistence.dto;

import java.time.OffsetDateTime;

public record CardDetailsDTO(Long id,
                             String title,
                             String description,
                             String blockReason,
                             OffsetDateTime blockedAt,
                             boolean blocked,
                             long blocksAmount,
                             String columnId,
                             int columnName) {
}
