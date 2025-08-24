package me.dio.service;

import lombok.AllArgsConstructor;
import me.dio.persistence.dao.BoardColumnDAO;
import me.dio.persistence.dao.BoardDAO;
import me.dio.dto.BoardColumnDTO;
import me.dio.dto.BoardDetailsDTO;
import me.dio.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        BoardDAO dao = new BoardDAO(connection);
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if (optional.isPresent()) {
            BoardEntity entity = optional.get();
            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    public Optional<BoardDetailsDTO> showBoardDetails(final Long id) throws SQLException {
        BoardDAO dao = new BoardDAO(connection);
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(connection);
        Optional<BoardEntity> optional = dao.findById(id);
        if (optional.isPresent()) {
            BoardEntity entity = optional.get();
            List<BoardColumnDTO> columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
            BoardDetailsDTO dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns);
            return Optional.of(dto);
        }
        return Optional.empty();
    }
}
