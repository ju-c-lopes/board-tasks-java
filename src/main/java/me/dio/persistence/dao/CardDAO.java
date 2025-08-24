package me.dio.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import me.dio.dto.CardDetailsDTO;
import me.dio.persistence.entity.CardEntity;

import java.sql.*;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static me.dio.persistence.coverter.OffSetDateTimeConverter.toOffSetDateTime;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        int nextOrder = getNextOrderForColumn(entity.getBoardColumn().getId());

        String sql = "INSERT INTO CARDS (title, description, `order`, board_column_id) VALUES(?, ?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;
            statement.setString(i++, entity.getTitle());
            statement.setString(i++, entity.getDescription());
            statement.setInt(i++, nextOrder);
            statement.setLong(i, entity.getBoardColumn().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
        }
        return entity;
    }

    public void moveToColumn(final Long cardId, final Long columnId) throws SQLException {
        int nextOrder = getNextOrderForColumn(columnId);

        String sql = "UPDATE CARDS SET board_column_id = ?, `order` = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;
            statement.setLong(i++, columnId);
            statement.setInt(i++, nextOrder);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    private int getNextOrderForColumn(Long boardColumnId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(`order`), 0) + 1 as next_order FROM CARDS WHERE board_column_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardColumnId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt("next_order");
            }
        }
        return 1; // Se não encontrar nenhum card, retorna 1
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        String sql =
            """
            SELECT
                c.id,
                c.title,
                c.description,
                b.block_reason,
                b.blocked_at,
                b.blocked,
                c.board_column_id,
                bc.name,
                (SELECT
                    COUNT(sub_b.id)
                    FROM BLOCKS sub_b
                    WHERE sub_b.card_id = c.id) blocks_amount
            FROM CARDS c
            LEFT JOIN BLOCKS b
                ON c.id = b.card_id
                AND b.unblocked_at IS NULL
            INNER JOIN BOARDS_COLUMNS bc
                ON bc.id = c.board_column_id
            WHERE c.id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                CardDetailsDTO dto = new CardDetailsDTO(
                    result.getLong("c.id"),
                    result.getString("c.title"),
                    result.getString("c.description"),
                    result.getString("b.block_reason"),
                    toOffSetDateTime(result.getTimestamp("b.blocked_at")),
                    nonNull(result.getString("b.block_reason")),
                    result.getInt("blocks_amount"),
                    result.getInt("c.board_column_id")
                );
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }
}
