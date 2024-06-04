import javax.swing.*;

public class Game extends JFrame{
    private JLabel player1Label;
    private JLabel player2Label;
    private JPanel BoardPanel;
    private JTextPane chatPane;
    private JTextField chatField;
    private JButton sendChatButton;
    private JPanel GamePanel;

    public Game(){
        setContentPane(GamePanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        sendChatButton.addActionListener(e -> {
            String chat = chatField.getText();
            chatPane.setText(chatPane.getText() + "\n" + chat);
            chatField.setText("");
        });
    }
}
