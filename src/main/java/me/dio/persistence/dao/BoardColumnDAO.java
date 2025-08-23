package me.dio.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.RequiredArgsConstructor;
import me.dio.persistence.entity.BoardColumnEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<BoardColumnEntity> findByBoardId(final Long id) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        String sql = "SELECT id, name, `order` FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
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
}
