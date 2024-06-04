import model.Game;
import model.GameStatus;
import model.Square;
import model.User;
import model.piece.Piece;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChessPanel extends JPanel implements Runnable {

    final int FPS = 60;
    final int TILE_SIZE = 70;
    final int WIDTH = 8;
    final int HEIGHT = 8;
    final String GAME_URL = "http://localhost:8080/game";

    Mouse mouse = new Mouse();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    User player;
    String color;
    Square selectedSquare;
    ArrayList<int[][]> validMoves;

    GameSocketConnection GameSocket;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ChessPanel(User player, GameSocketConnection GameSocket) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        this.GameSocket = GameSocket;
        this.player = player;
        this.color = player.getUsername().equals(GameSocket.game.getPlayer1().getUsername()) ? "white" : "black";
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        start();
    }

    public void start() {
        executorService.submit(this);
    }

    public void paintComponent(Graphics g) {
        Image offscreenImage = createImage(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        Graphics offscreenGraphics = offscreenImage.getGraphics();
        drawBoard((Graphics2D) offscreenGraphics);
        drawPieces((Graphics2D) offscreenGraphics);
        drawValidMoves((Graphics2D) offscreenGraphics);
        g.drawImage(offscreenImage, 0, 0, null);
    }

    public void drawBoard(Graphics2D g) {
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(new Color(209, 139, 71));
                } else {
                    g.setColor(new Color(255, 206, 158));
                }
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public void drawPieces(Graphics2D g) {
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                Piece piece = GameSocket.game.getBoard().getPiece(col, row);
                if (piece != null) {
                    g.drawImage(piece.image, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                }
            }
        }
    }

    public void drawValidMoves(Graphics2D g) {
        if (validMoves != null) {
            g.setColor(new Color(0, 255, 0, 100));
            for (int[][] move : validMoves) {
                g.fillRect(move[0][0] * TILE_SIZE, move[0][1] * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void update() {
        if (mouse.pressed) {
            int col = mouse.x / TILE_SIZE;
            int row = mouse.y / TILE_SIZE;
            if (GameSocket.game.getStatus().equals(GameStatus.IN_PROGRESS)) {
                if (GameSocket.game.getCurrentPlayer().getUsername().equals(player.getUsername())) {
                    if (selectedSquare == null && GameSocket.game.getBoard().getPiece(col, row) != null && GameSocket.game.getBoard().getPiece(col, row).getColor().equals(color)){
                        selectedSquare = GameSocket.game.getBoard().getSquare(col, row);
                        validMoves = GameSocket.game.getValidMoves(col, row);
                    } else if (selectedSquare != null && validMoves != null) {
                        if (validMoves.stream().anyMatch(move -> move[0][0] == col && move[0][1] == row)) {
                            movePiece(col, row);
                        }
                        selectedSquare = null;
                        validMoves = null;
                    } else {
                        selectedSquare = null;
                        validMoves = null;
                    }
                }
            }
            mouse.pressed = false;
        }

    }

    private void movePiece(int col, int row) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GAME_URL + "/move?gameId=" + GameSocket.game.getId() + "&selectCol=" + selectedSquare.getX() + "&selectRow=" + selectedSquare.getY() + "&targetCol=" + col + "&targetRow=" + row))
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            selectedSquare = null;
            validMoves = null;
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            update();
            repaint();
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
