package model;

import lombok.Getter;
import model.piece.*;

@Getter
public class Board {
    private final Square[][] squares;
    public Board() {
        squares = new Square[8][8];
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                squares[col][row] = new Square(col, row);
            }
        }
        squares[0][0].setPiece(new Rook("black"));
        squares[1][0].setPiece(new Knight("black"));
        squares[2][0].setPiece(new Bishop("black"));
        squares[3][0].setPiece(new Queen("black"));
        squares[4][0].setPiece(new King("black"));
        squares[5][0].setPiece(new Bishop("black"));
        squares[6][0].setPiece(new Knight("black"));
        squares[7][0].setPiece(new Rook("black"));
        for (int col = 0; col < 8; col++) {
            squares[col][1].setPiece(new Pawn("black"));
        }
        for (int col = 0; col < 8; col++) {
            squares[col][6].setPiece(new Pawn("white"));
        }
        squares[0][7].setPiece(new Rook("white"));
        squares[1][7].setPiece(new Knight("white"));
        squares[2][7].setPiece(new Bishop("white"));
        squares[3][7].setPiece(new Queen("white"));
        squares[4][7].setPiece(new King("white"));
        squares[5][7].setPiece(new Bishop("white"));
        squares[6][7].setPiece(new Knight("white"));
        squares[7][7].setPiece(new Rook("white"));
    }

    public Square getSquare(int col, int row) {
        return squares[col][row];
    }

    public void setSquare(int col, int row, Square square) {
        squares[col][row] = square;
    }

    public Piece getPiece(int col, int row) {
        return getSquare(col, row).getPiece();
    }

    public void movePiece(int selectCol, int selectRow, int targetCol, int targetRow) {
        Square selectSquare = getSquare(selectCol, selectRow);
        Square targetSquare = getSquare(targetCol, targetRow);
        targetSquare.setPiece(selectSquare.getPiece());
        selectSquare.setPiece(null);
    }
}
