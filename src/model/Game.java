package model;

import lombok.Getter;
import lombok.Setter;
import model.piece.Piece;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@Setter
@Getter
public class Game {

    private String id;
    User player1;
    User player2;
    GameStatus status;
    Board board;
    User currentPlayer;
    public Game() {
        this.board = new Board();
    }
    public Game(User player1) {
        this.player1 = player1;
        this.status = GameStatus.WAITING_FOR_PLAYER;
        this.board = new Board();
    }

    public ArrayList<int[][]> getValidMoves(int selectCol, int selectRow) {
        Square square = board.getSquare(selectCol, selectRow);
        Piece piece = square.getPiece();
        if (piece != null) {
            return piece.getValidMoves(selectCol, selectRow, board);
        } else {
            return new ArrayList<>();
        }
    }

    public void move(int selectCol, int selectRow, int targetCol, int targetRow) {
        getValidMoves(selectCol, selectRow).stream()
                .filter(move -> move[0][0] == targetCol && move[0][1] == targetRow)
                .findFirst()
                .ifPresent(move -> {
                    board.movePiece(selectCol, selectRow, targetCol, targetRow);
                    currentPlayer = currentPlayer == player1 ? player2 : player1;
                });
    }
}
