package me.dio.ui;

import me.dio.persistence.entity.BoardColumnEntity;
import me.dio.persistence.entity.BoardColumnTypeEntity;
import me.dio.persistence.entity.BoardEntity;
import me.dio.service.BoardQueryService;
import me.dio.service.BoardService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static me.dio.persistence.config.ConnectionConfig.getConnection;
import static me.dio.persistence.entity.BoardColumnTypeEntity.*;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao Gerenciador de Boards!\nEscolha a Opção desejada: ");
        var option = -1;
        while (true) {
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção Inválida!\nEscolha uma opção do menu:");
            }
        }
    }

    private void createBoard() throws SQLException {
        BoardEntity entity = new BoardEntity();
        System.out.println("Informe o nome do seu Board");
        entity.setName(scanner.next());

        System.out.println("Seu board terá colunas além das 3 padrões? Se sim, informe quantas terão, senão digite '0'");
        var additionalColumn = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial do seu board:");
        var initialColumnName = scanner.next();
        BoardColumnEntity initialColumn = createColumn(
            initialColumnName,
            INITIAL,
            0
        );
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumn; i ++) {
            System.out.println("Informe o Informe o nome da coluna de tarefa pendente do board:");
            var pendingColumnName = scanner.next();
            BoardColumnEntity pendingColumn = createColumn(
                pendingColumnName,
                PENDING,
                i + 1
            );
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final:");
        var finalColumnName = scanner.next();
        BoardColumnEntity finalColumn = createColumn(
            finalColumnName,
            FINAL,
            additionalColumn + 1
        );
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento do board:");
        var cancelColumnName = scanner.next();
        BoardColumnEntity cancelColumn = createColumn(
                cancelColumnName,
                CANCEL,
                additionalColumn + 2
        );
        columns.add(cancelColumn);

        entity.setBoardsColumns(columns);
        try (Connection connection = getConnection()) {
            BoardService service = new BoardService(connection);
            service.insert(entity);
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar:");
        Long id = scanner.nextLong();
        try (Connection connection = getConnection()) {
            BoardQueryService queryService = new BoardQueryService(connection);
            Optional<BoardEntity> optional = queryService.findById(id);
            optional.ifPresentOrElse(
                b -> new BoardMenu(b).execute(),
                () -> System.out.printf("Não foi encontrado board com o id %s\n", id)
            );

        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id do board a ser excluído:");
        Long id = scanner.nextLong();
        try (Connection connection = getConnection()) {
            BoardService service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("O board %s foi excluído!\n", id);
            } else {
                System.out.printf("Não foi encontrado board com o id %s\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnTypeEntity type, final int order) {
        BoardColumnEntity boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setType(type);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
