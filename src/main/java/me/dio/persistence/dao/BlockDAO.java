package me.dio.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static me.dio.persistence.coverter.OffSetDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public void block(final Long cardId, final String blockReason) throws SQLException {
        String sql =
            """
            INSERT INTO BLOCKS (
                blocked_at,
                block_reason,
                blocked,
                card_id
            ) VALUES (?, ?, ?, ?);
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, blockReason);
            statement.setBoolean(i++, true);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    public void unblock(final Long cardId, final String unblockReason) throws SQLException {
        String sql =
            """
            UPDATE BLOCKS SET
                unblocked_at = ?,
                unblock_reason = ?,
                blocked = ?
                WHERE card_id = ? AND unblock_reason IS NULL;
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, unblockReason);
            statement.setBoolean(i++, false);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }
}
