

import javax.swing.SwingUtilities;
import ui.LoginWindow;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
    }
}
