package me.dio.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import me.dio.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {

    private final Connection connection;

    private BoardEntity insert(final BoardEntity entity) throws SQLException {
        String sql = "INSERT INTO BOARDS(name) VALUES (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
        }
        return entity;
    }

    private void delete(final Long id) throws SQLException {
        String sql = "DELETE FROM BOARDS WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private Optional<BoardEntity> findById(final Long id) throws SQLException {
        String sql = "SELECT id, name FROM BOARDS WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                BoardEntity entity = new BoardEntity();
                entity.setId(result.getLong("id"));
                entity.setName(result.getString("name"));
                return Optional.of(entity);
            }
            return Optional.empty();
        }
    }

    private boolean exists(final Long id) throws SQLException {
        String sql = "SELECT 1 FROM BOARDS WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            return statement.getResultSet().next();
        }
    }

}
