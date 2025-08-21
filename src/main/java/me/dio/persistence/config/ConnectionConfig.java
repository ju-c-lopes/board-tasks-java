package me.dio.persistence.config;

import static lombok.AccessLevel.PRIVATE;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = PRIVATE)
public class ConnectionConfig {
    public static Connection getConnection() throws SQLException {
       Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/board", "root", "root");
       connection.setAutoCommit(false);
       return connection;
    }
}
