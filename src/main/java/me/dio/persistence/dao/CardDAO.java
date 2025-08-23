package me.dio.persistence.dao;

import lombok.AllArgsConstructor;
import me.dio.persistence.dto.CardDetailsDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static me.dio.persistence.coverter.OffSetDateTimeConverter.toOffSetDateTime;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        String sql =
            """
            SELECT
                c.id,
                c.title,
                c.description
                b.blocked_reason,
                b.blocked_at,
                b.blocked,
                c.board_column_id,
                bc.name,
                COUNT(
                    SELECT sub_b.id
                    FROM BLOCKS sub_b
                    WHERE sub_b.card_id = c.id) blocks_amount
            FROM CARDS c
            LEFT JOIN BLOCKS b
                ON c.id = b.card_id
                AND b.unblocked_at IS NULL
            INNER JOIN BOARD_COLUMNS bc
                ON bc.id = c.board_column_id
            WHERE c.id = ?
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            ResultSet result = statement.getResultSet();
            CardDetailsDTO dto = new CardDetailsDTO(
                result.getLong("c.id"),
                result.getString("c.title"),
                result.getString("c.description"),
                result.getString("b.block_reason"),
                toOffSetDateTime(result.getTimestamp("b.blocked_at")),
                result.getString("b.blocked_reason").isEmpty(),
                result.getLong("c.board_column_id"),
                result.getString("bc.name"),
                result.getInt("blocks_amount")
            );
            Optional.of(dto);
        }
        return Optional.empty();
    }
}
