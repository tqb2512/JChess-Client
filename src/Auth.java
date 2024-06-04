import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Auth extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signinButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JButton signupButton;
    private JPanel MainPanel;

    String SERVER_URL = "http://localhost:8080/user";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public Auth() {
        setContentPane(MainPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        signinButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "/signin?username=" + username + "&password=" + password))
                    .build();

            try {
                if (httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body().equals("User signed in")) {
                    dispose();
                    new Home(username);
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect password");
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
            }
        });

        signupButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "/signup?username=" + username + "&password=" + password))
                    .build();

            try {
                if (httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body().equals("User signed up")) {
                    JOptionPane.showMessageDialog(null, "User signed up");
                } else {
                    JOptionPane.showMessageDialog(null, "User already exists");
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, "Error: " + exception.getMessage());
            }
        });
    }
}