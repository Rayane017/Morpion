import javax.swing.*;

public class MorpionApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Controleur().setVisible(true);
        });
    }
}