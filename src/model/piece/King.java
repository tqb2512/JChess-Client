package model.piece;

import model.Board;

import java.util.ArrayList;

public class King extends Piece {
    public King(String color) {
        super("King", color);
        image = loadSVGImage(color.equals("white") ? "/pieces/w-king.svg" : "/pieces/b-king.svg");
    }

    @Override
    public ArrayList<int[][]> getValidMoves(int selectedCol, int selectedRow, Board board) {
        ArrayList<int[][]> validMoves = new ArrayList<>();

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] direction : directions) {
            int newCol = selectedCol + direction[0];
            int newRow = selectedRow + direction[1];

            if (newCol >= 0 && newCol < 8 && newRow >= 0 && newRow < 8) {
                Piece piece = board.getPiece(newCol, newRow);
                if (piece == null || !piece.getColor().equals(getColor())) {
                    validMoves.add(new int[][]{{newCol, newRow}});
                }
            }
        }

        return validMoves;
    }
}