package me.dio.ui;

import lombok.AllArgsConstructor;
import me.dio.dto.BoardColumnInfoDTO;
import me.dio.dto.BoardDetailsDTO;
import me.dio.persistence.entity.BoardColumnEntity;
import me.dio.persistence.entity.BoardEntity;
import me.dio.persistence.entity.CardEntity;
import me.dio.service.BoardColumnQueryService;
import me.dio.service.BoardQueryService;
import me.dio.service.CardQueryService;
import me.dio.service.CardService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static me.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Bem vindo ao board %s, selecione a operação desejada:\n", entity.getName());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um card");
                System.out.println("6 - Visualizar board");
                System.out.println("7 - Visualizar coluna com cards");
                System.out.println("8 - Visualizar card");
                System.out.println("9 - Voltar para o menu anterior");

                System.out.println("10 - Sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> ShowColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando ao menu anterior...");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção Inválida!\nEscolha uma opção do menu:");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException {
        CardEntity card = new CardEntity();
        System.out.println("Informe o título do card:");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do card:");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try (Connection connection = getConnection()) {
            new CardService(connection).insert(card);

        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Informe o id do card que deseja mover:");
        Long cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
            .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getType()))
            .toList();
        try (Connection connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() throws SQLException {
        try (Connection connection = getConnection()) {
            Optional<BoardDetailsDTO> optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("Coluna[%s] tipo [%s] tem %s cards\n", c.name(), c.type(), c.cardsAmount());
                });
            });
        }
    }

    private void ShowColumn() throws SQLException {
        System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
        List<Long> columnIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        long selectedColumn = -1L;
        while (!columnIds.contains(selectedColumn)) {
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n",
                    c.getId(), c.getName(), c.getType().name()));
            selectedColumn = scanner.nextLong();
        }
        try (Connection connection = getConnection()) {
            Optional<BoardColumnEntity> column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getType());
                co.getCards().forEach(ca -> {
                    System.out.printf("Card %s -%s\n", ca.getId(), ca.getTitle());
                    System.out.printf("Descrição: %s\n", ca.getDescription());
                });
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showCard() throws  SQLException {
        System.out.println("Informe o id do card que deseja visualizar:");
        Long selectedCardId = scanner.nextLong();
        try (Connection connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(
                        c -> {
                            System.out.printf("Card %s - %s\n", c.id(), c.title());
                            System.out.printf("Descrição: %s\n", c.description());
                            System.out.println(
                                c.blocked() ?
                                "Está bloqueado. Motivo: " + c.blockReason() + "\n" :
                                "Não está bloqueado\n");
                            System.out.printf("Já foi bloqueado %s vezes\n", c.blocksAmount());
                            System.out.printf("Está na coluna %s\n", c.columnId());
                        },
                        () -> System.out.printf("Não existe card com id %s\n", selectedCardId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
