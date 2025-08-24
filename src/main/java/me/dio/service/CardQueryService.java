package me.dio.service;

import lombok.AllArgsConstructor;
import me.dio.persistence.dao.CardDAO;
import me.dio.dto.CardDetailsDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {

    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        CardDAO dao = new CardDAO(connection);
        return dao.findById(id);
    }
}
