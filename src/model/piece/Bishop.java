package model.piece;

import model.Board;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Bishop extends Piece {
    public Bishop(String color) {
        super("Bishop", color);
        image = loadSVGImage(STR."/pieces/\{color.equals("white") ? "w" : "b"}-bishop.svg");
    }

    @Override
    public ArrayList<int[][]> getValidMoves(int currentCol, int currentRow, Board board) {
        ArrayList<int[][]> validMoves = new ArrayList<>();

        int[][] directions = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1}
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
