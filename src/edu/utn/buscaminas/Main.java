
package edu.utn.buscaminas;

import javax.swing.SwingUtilities;
import edu.utn.buscaminas.ui.MainFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
