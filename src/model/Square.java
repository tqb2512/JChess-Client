package model;

import lombok.Getter;
import model.piece.Piece;

@Getter
public class Square {
    private final int x;
    private final int y;
    private Piece piece;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Square(int x, int y, Piece piece) {
        this.x = x;
        this.y = y;
        this.piece = piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}
