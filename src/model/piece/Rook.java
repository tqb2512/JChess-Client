package model.piece;

import model.Board;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(String color) {
        super("Rook", color);
        image = loadSVGImage(color.equals("white") ? "/pieces/w-rook.svg" : "/pieces/b-rook.svg");
    }
    @Override
    public ArrayList<int[][]> getValidMoves(int currentCol, int currentRow, Board board) {
        ArrayList<int[][]> validMoves = new ArrayList<>();

        int[][] directions = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };

        for (int[] direction : directions) {
            for (int i = 1; i < 8; i++) {
                int newCol = currentCol + i * direction[0];
                int newRow = currentRow + i * direction[1];

                if (newCol >= 0 && newCol < 8 && newRow >= 0 && newRow < 8) {
                    Piece piece = board.getPiece(newCol, newRow);
                    if (piece == null) {
                        validMoves.add(new int[][]{{newCol, newRow}});
                    } else {
                        if (!piece.getColor().equals(getColor())) {
                            validMoves.add(new int[][]{{newCol, newRow}});
                        }
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return validMoves;
    }
}
