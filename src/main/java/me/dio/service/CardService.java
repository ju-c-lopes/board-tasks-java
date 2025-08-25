package me.dio.service;

import lombok.AllArgsConstructor;
import me.dio.exception.CardBlockedException;
import me.dio.exception.CardFinishedException;
import me.dio.exception.EntityNotFoundException;
import me.dio.persistence.dao.CardDAO;
import me.dio.dto.BoardColumnInfoDTO;
import me.dio.dto.CardDetailsDTO;
import me.dio.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static me.dio.persistence.entity.BoardColumnTypeEntity.FINAL;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        try {
            CardDAO dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("O card de id " + cardId + " não foi encontrado.")
            );
            if (dto.blocked()) throw new CardBlockedException("O card de id " +
                cardId +
                " está bloqueado." +
                " É necessário desbloqueá-lo para movê-lo.");
            var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board."));
            if (currentColumn.type().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado.");
            }
            var nextColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado."));
            dao.moveToColumn(cardId, nextColumn.id());
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id " + cardId + " não foi encontrado.")
            );
            if (dto.blocked()) throw new CardBlockedException("O card de id " +
                    cardId +
                    " está bloqueado." +
                    " É necessário desbloqueá-lo para movê-lo.");
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board."));
            if (currentColumn.type().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado.");
            }
            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado."));
            dao.moveToColumn(cardId, cancelColumnId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
}
