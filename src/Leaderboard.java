import com.formdev.flatlaf.FlatIntelliJLaf;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class Leaderboard extends JDialog {
    private final String serverUrl = "http://localhost:8080";
    String USER_URL = serverUrl + "/user";
    HttpClient httpClient = HttpClient.newHttpClient();
    private JPanel LeaderboardPanel;
    private JTable playerTable;

    public Leaderboard(JFrame parent) {
        super(parent, "Leaderboard", true);
        updateLeaderboard();
        FlatIntelliJLaf.setup();
        setPreferredSize(new Dimension(500, 600));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getPreferredSize().width / 2, dim.height / 2 - this.getPreferredSize().height / 2);
        setContentPane(LeaderboardPanel);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void updateLeaderboard() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USER_URL + "/getLeaderboard"))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            java.lang.reflect.Type type = new TypeToken<Map<String, User>>() {
            }.getType();
            Map<String, User> leaderboard = new Gson().fromJson(response.body(), type);
            DefaultTableModel tableModel = (DefaultTableModel) playerTable.getModel();
            tableModel.addColumn("Username");
            tableModel.addColumn("Wins");
            tableModel.addColumn("Losses");
            tableModel.addColumn("Total Games");
            for (Map.Entry<String, User> entry : leaderboard.entrySet()) {
                User user = entry.getValue();
                tableModel.addRow(new Object[]{user.getUsername(), user.getWins(), user.getLosses(), user.getTotalGames()});
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
        }
    }
}
