import adapter.PieceTypeAdapter;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.GameListResponse;
import model.Game;
import model.GameStatus;
import model.User;
import model.piece.Piece;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class Home extends JFrame {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    final private String signedInUser;
    private final String serverUrl = "http://localhost:8080";
    String USER_URL = serverUrl + "/user";
    String GAME_URL = serverUrl + "/game";
    private JPanel HomePanel;
    private JTable roomTable;
    private JButton createRoomButton;
    private JButton viewProfileButton;
    private JButton leaderboardButton;
    private JButton refreshButton;

    public Home(String signedInUser) {
        FlatIntelliJLaf.setup();
        this.signedInUser = signedInUser;
        this.setTitle("Room List");
        setPreferredSize(new Dimension(500, 600));
        this.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getPreferredSize().width / 2, dim.height / 2 - this.getPreferredSize().height / 2);
        setContentPane(HomePanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        updateRoomList();

        roomTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = roomTable.rowAtPoint(evt.getPoint());
                int col = roomTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    String gameId = roomTable.getModel().getValueAt(row, 0).toString();
                    if (roomTable.getModel().getValueAt(row, 3).equals(GameStatus.WAITING_FOR_PLAYER)) {
                        int confirm = JOptionPane.showConfirmDialog(null, "Do you want to join this room?", "Join Room Confirmation", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            User user = getSignedInUser();
                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create(GAME_URL + "/joinRoom?gameId=" + gameId))
                                    .header("Content-Type", "application/json")
                                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user)))
                                    .build();
                            try {
                                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                                Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Piece.class, new PieceTypeAdapter())
                                        .create();
                                Game game = gson.fromJson(response.body(), Game.class);
                                dispose();
                                new GameForm(game, user);
                            } catch (Exception exception) {
                                JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
                            }
                        }
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(null, "Do you want to spectate this room?", "Spectate Room Confirmation", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create(GAME_URL + "/getRoom?gameId=" + gameId))
                                    .build();
                            try {
                                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                                Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Piece.class, new PieceTypeAdapter())
                                        .create();
                                Game game = gson.fromJson(response.body(), Game.class);
                                dispose();
                                new GameForm(game, getSignedInUser());
                            } catch (Exception exception) {
                                JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
                            }
                        }
                    }
                }
            }
        });

        createRoomButton.addActionListener(e -> {
            User user = getSignedInUser();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GAME_URL + "/createRoom"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(user)))
                    .build();
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Piece.class, new PieceTypeAdapter())
                        .create();
                Game game = gson.fromJson(response.body(), Game.class);
                dispose();
                new GameForm(game, user);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
            }
        });

        viewProfileButton.addActionListener(e -> {
            User user = getSignedInUser();
            JOptionPane.showMessageDialog(null, "Username: " + user.getUsername() + "\nWins: " + user.getWins() + "\nLosses: " + user.getLosses());
        });

        leaderboardButton.addActionListener(e -> {
            new Leaderboard(this);
        });

        refreshButton.addActionListener(e -> {
            updateRoomList();
        });
    }

    private void updateRoomList() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GAME_URL + "/getRooms"))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<Map<String, GameListResponse>>() {
            }.getType();
            Map<String, GameListResponse> gameList = gson.fromJson(response.body(), type);
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Game ID");
            tableModel.addColumn("Player 1");
            tableModel.addColumn("Player 2");
            tableModel.addColumn("Status");
            for (Map.Entry<String, GameListResponse> entry : gameList.entrySet()) {
                GameListResponse game = entry.getValue();
                tableModel.addRow(new Object[]{game.getId(), game.getPlayer1().getUsername(), game.getPlayer2() != null ? game.getPlayer2().getUsername() : "", game.getStatus()});
            }
            roomTable.setModel(tableModel);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
        }
    }

    public User getSignedInUser() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USER_URL + "/get?username=" + signedInUser))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            return gson.fromJson(response.body(), User.class);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
            return null;
        }
    }
}
