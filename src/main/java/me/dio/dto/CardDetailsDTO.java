package me.dio.dto;

import java.time.OffsetDateTime;

public record CardDetailsDTO(Long id,
                             String title,
                             String description,
                             String blockReason,
                             OffsetDateTime blockedAt,
                             boolean blocked,
                             int blocksAmount,
                             long columnId) {
}
