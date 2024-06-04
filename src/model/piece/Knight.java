package model.piece;

import model.Board;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(String color) {
        super("Knight", color);
        image = loadSVGImage(color.equals("white") ? "/pieces/w-knight.svg" : "/pieces/b-knight.svg");
    }

    @Override
    public ArrayList<int[][]> getValidMoves(int currentCol, int currentRow, Board board) {
        ArrayList<int[][]> validMoves = new ArrayList<>();

        int[][] moves = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
        };

        for (int[] move : moves) {
            int newCol = currentCol + move[0];
            int newRow = currentRow + move[1];

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
