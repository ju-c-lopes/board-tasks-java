package me.dio.persistence.coverter;

import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class OffSetDateTimeConverter {

    public static OffsetDateTime toOffSetDateTime(final Timestamp value) {
        return OffsetDateTime.ofInstant(value.toInstant(), UTC);
    }
}
