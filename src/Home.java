import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Home extends JFrame {
    private JPanel HomePanel;
    private JList roomList;
    private JButton createRoomButton;
    private JButton viewProfileButton;
    private JButton joinRandomButton;
    private JButton button1;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    final private String signedInUser;
    String USER_URL = "http://localhost:8080/user";

    public Home(String signedInUser) {
        this.signedInUser = signedInUser;
        setContentPane(HomePanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        createRoomButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Create Room");
        });

        viewProfileButton.addActionListener(e -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(USER_URL + "/get?username=" + signedInUser))
                    .build();
            try {
                JOptionPane.showMessageDialog(null, httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body());
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
            }
        });

        joinRandomButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Join Random");
        });

        button1.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Button 1");
        });
    }
}
