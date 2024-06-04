import adapter.PieceTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.GameListResponse;
import model.Game;
import model.User;
import model.piece.Piece;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;

public class Home extends JFrame {
    private JPanel HomePanel;
    private JTable roomTable;
    private JButton createRoomButton;
    private JButton viewProfileButton;
    private JButton joinRandomButton;
    private JButton refreshButton;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    final private String signedInUser;
    String USER_URL = "http://localhost:8080/user";
    String GAME_URL = "http://localhost:8080/game";

    public Home(String signedInUser) {
        this.signedInUser = signedInUser;
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
                            new GameForm(game);
                        } catch (Exception exception) {
                            JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
                        }
                    }
                }
            }
        }
        );

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
                new GameForm(game);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
            }
        });

        viewProfileButton.addActionListener(e -> {
            User user = getSignedInUser();
            JOptionPane.showMessageDialog(null, "Username: " + user.getUsername() + "\nWins: " + user.getWins() + "\nLosses: " + user.getLosses());
        });

        joinRandomButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Join Random");
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
            java.lang.reflect.Type type = new TypeToken<Map<String, GameListResponse>>(){}.getType();
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
