package me.dio.service;

import lombok.AllArgsConstructor;
import me.dio.persistence.dao.BoardColumnDAO;
import me.dio.persistence.dao.BoardDAO;
import me.dio.persistence.entity.BoardColumnEntity;
import me.dio.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        BoardDAO dao = new BoardDAO(connection);
        var optional = dao.findById(id);
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(connection);
        if (optional.isPresent()) {
            BoardEntity entity = optional.get();
            entity.getBoardsColumns(boardColumnDAO.findByBoardId(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();

    }
}
