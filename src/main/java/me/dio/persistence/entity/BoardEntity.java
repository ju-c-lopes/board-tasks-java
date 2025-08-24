package me.dio.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static me.dio.persistence.entity.BoardColumnTypeEntity.CANCEL;
import static me.dio.persistence.entity.BoardColumnTypeEntity.INITIAL;

@Data
public class BoardEntity {

    private Long id;
    private String name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();

    public BoardColumnEntity getInitialColumn() {
        return boardColumns.stream()
                .filter(bc -> bc.getType().equals(INITIAL))
                .findFirst().orElseThrow();
    }
}
