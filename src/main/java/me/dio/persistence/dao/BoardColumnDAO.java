package me.dio.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.RequiredArgsConstructor;
import me.dio.dto.BoardColumnDTO;
import me.dio.persistence.entity.BoardColumnEntity;
import me.dio.persistence.entity.CardEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static me.dio.persistence.entity.BoardColumnTypeEntity.findByName;

@RequiredArgsConstructor
public class BoardColumnDAO {
    
    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        String sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, type, board_id) VALUES (?, ?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getType().name());
            statement.setLong(i, entity.getBoard().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
            return entity;
        }
    }

    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        String sql = "SELECT id, name, `order`, type FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            ResultSet result = statement.getResultSet();
            while (result.next()) {
                BoardColumnEntity entity = new BoardColumnEntity();
                entity.setId(result.getLong("id"));
                entity.setName(result.getString("name"));
                entity.setOrder(result.getInt("order"));
                entity.setType(findByName(result.getString("type")));
                entities.add(entity);
            }
            return entities;
        }
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        String sql =
                """
                SELECT bc.id,
                    bc.name,
                    bc.type,
                    (SELECT COUNT(c.id)
                        FROM CARDS c
                        WHERE c.board_column_id = bc.id) cards_amount
                FROM BOARDS_COLUMNS bc
                WHERE board_id = ?
                ORDER BY `order`;
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            ResultSet result = statement.getResultSet();
            while (result.next()) {
                BoardColumnDTO dto = new BoardColumnDTO(
                    result.getLong("bc.id"),
                    result.getString("bc.name"),
                    findByName(result.getString("bc.type")),
                    result.getInt("cards_amount")
                );
                dtos.add(dto);
            }
            return dtos;
        }
    }

    public Optional<BoardColumnEntity> findById(final Long boardId) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        String sql =
            """
            SELECT bc.name,
                bc.type,
                c.id,
                c.title,
                c.description
            FROM BOARDS_COLUMNS bc
            LEFT JOIN CARDS c
                ON c.board_column_id = bc.id
            WHERE bc.id = ?;
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                BoardColumnEntity entity = new BoardColumnEntity();
                entity.setName(result.getString("bc.name"));
                entity.setType(findByName(result.getString("bc.type")));
                do {
                    if (isNull(result.getString("c.title"))) {
                        break;
                    }
                    CardEntity card = new CardEntity();
                    card.setId(result.getLong("c.id"));
                    card.setTitle(result.getString("c.title"));
                    card.setDescription(result.getString("c.description"));
                    entity.getCards().add(card);
                } while (result.next());
                return Optional.of(entity);
            }
            return Optional.empty();
        }
    }
}
