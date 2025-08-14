
package edu.utn.buscaminas.util;

import javax.swing.JOptionPane;
import java.awt.Component;

public class Dialogs {
    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public static boolean confirm(Component parent, String msg, String title) {
        int r = JOptionPane.showConfirmDialog(parent, msg, title, JOptionPane.YES_NO_OPTION);
        return r == JOptionPane.YES_OPTION;
    }
}
