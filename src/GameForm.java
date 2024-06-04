import com.google.gson.Gson;
import model.Game;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GameForm extends JFrame{
    public JLabel player1Label;
    public JLabel player2Label;
    private JPanel boardPanel;
    public JTextPane chatPane;
    private JTextField chatField;
    private JButton sendChatButton;
    private JPanel GamePanel;
    public JLabel turnLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    final private String GAME_URL = "http://localhost:8080/game";
    final private Game game;
    private GameSocketConnection GameSocket;

    public GameForm(Game game) {
        connectToGameSocket(game);
        ChessPanel chessPanel = new ChessPanel(game, GameSocket);
        GridLayout gridLayout = new GridLayout(1, 2);
        boardPanel.setLayout(gridLayout);
        boardPanel.add(chessPanel);
        setContentPane(GamePanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        this.game = game;

        player1Label.setText(game.getPlayer1().getUsername());
        player2Label.setText(game.getPlayer2() != null ? game.getPlayer2().getUsername() : " Waiting for player 2");
        turnLabel.setText(game.getCurrentPlayer() != null ? game.getCurrentPlayer().getUsername() + "'s turn" : "Game has not started yet");

        sendChatButton.addActionListener(e -> {
            String chat = chatField.getText();
            chatPane.setText(chatPane.getText() + "\n" + chat);
            chatField.setText("");
        });
    }

    private GameForm getGame(String gameId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GAME_URL + "/getRoom?gameId=" + gameId))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), GameForm.class);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
            return null;
        }
    }

    public void connectToGameSocket(Game game) {
        GameSocket = new GameSocketConnection();
        GameSocket.game = game;
        GameSocket.gameForm = this;
        GameSocket.connect();
    }
}