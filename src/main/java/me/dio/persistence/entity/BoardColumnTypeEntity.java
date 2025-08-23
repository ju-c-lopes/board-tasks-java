package me.dio.persistence.entity;

import java.util.stream.Stream;

public enum BoardColumnTypeEntity {

    INITIAL, FINAL, CANCEL, PENDING;

    public static BoardColumnTypeEntity findByName(final String name) {
        return Stream.of(BoardColumnTypeEntity.values())
                .filter(b -> b.name().equals(name))
                .findFirst()
                .orElseThrow();
    }
}
