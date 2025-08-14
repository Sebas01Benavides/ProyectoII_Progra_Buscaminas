
package edu.utn.buscaminas.ui;

import javax.swing.JButton;

public class TileButton extends JButton {
    public final int row;
    public final int col;

    public TileButton(int row, int col) {
        super();
        this.row = row;
        this.col = col;
        setFocusPainted(false);
        setMargin(new java.awt.Insets(0,0,0,0));
    }
}
