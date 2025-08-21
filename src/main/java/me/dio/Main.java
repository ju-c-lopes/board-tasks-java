package me.dio;

import me.dio.persistence.migration.MigrationStrategy;

import java.sql.SQLException;

import static me.dio.persistence.config.ConnectionConfig.getConnection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        try (var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }
    }
}