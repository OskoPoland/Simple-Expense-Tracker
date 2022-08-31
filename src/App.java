import VIEW.*;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        LogGUI log = new LogGUI();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                log.logConstructor();
                log.initLog();
            }
        });
    }
}
