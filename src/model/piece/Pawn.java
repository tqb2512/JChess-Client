package model.piece;

import model.Board;

import java.util.ArrayList;

public class Pawn extends Piece {
    public Pawn(String color) {
        super("Pawn", color);
        image = loadSVGImage(color.equals("white") ? "/pieces/w-pawn.svg" : "/pieces/b-pawn.svg");
    }

    @Override
    public ArrayList<int[][]> getValidMoves(int selectedCol, int selectedRow, Board board) {
        ArrayList<int[][]> validMoves = new ArrayList<>();

        if ("white".equals(getColor())) {
            if (selectedRow > 0 && board.getPiece(selectedCol, selectedRow - 1) == null) {
                validMoves.add(new int[][]{{selectedCol, selectedRow - 1}});
            }

            if (selectedRow == 6 && board.getPiece(selectedCol, selectedRow - 2) == null) {
                validMoves.add(new int[][]{{selectedCol, selectedRow - 2}});
            }

            if (selectedCol > 0 && selectedRow > 0 && board.getPiece(selectedCol - 1, selectedRow - 1) != null
                    && "black".equals(board.getPiece(selectedCol - 1, selectedRow - 1).getColor())) {
                validMoves.add(new int[][]{{selectedCol - 1, selectedRow - 1}});
            }
            if (selectedCol < 7 && selectedRow > 0 && board.getPiece(selectedCol + 1, selectedRow - 1) != null
                    && "black".equals(board.getPiece(selectedCol + 1, selectedRow - 1).getColor())) {
                validMoves.add(new int[][]{{selectedCol + 1, selectedRow - 1}});
            }
        } else {
            if (selectedRow < 7 && board.getPiece(selectedCol, selectedRow + 1) == null) {
                validMoves.add(new int[][]{{selectedCol, selectedRow + 1}});
            }

            if (selectedRow == 1 && board.getPiece(selectedCol, selectedRow + 2) == null) {
                validMoves.add(new int[][]{{selectedCol, selectedRow + 2}});
            }

            if (selectedCol > 0 && selectedRow < 7 && board.getPiece(selectedCol - 1, selectedRow + 1) != null
                    && "white".equals(board.getPiece(selectedCol - 1, selectedRow + 1).getColor())) {
                validMoves.add(new int[][]{{selectedCol - 1, selectedRow + 1}});
            }
            if (selectedCol < 7 && selectedRow < 7 && board.getPiece(selectedCol + 1, selectedRow + 1) != null
                    && "white".equals(board.getPiece(selectedCol + 1, selectedRow + 1).getColor())) {
                validMoves.add(new int[][]{{selectedCol + 1, selectedRow + 1}});
            }
        }

        return validMoves;
    }
}
