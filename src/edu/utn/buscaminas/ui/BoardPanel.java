
package edu.utn.buscaminas.ui;

import edu.utn.buscaminas.model.Board;
import edu.utn.buscaminas.model.Cell;
import edu.utn.buscaminas.model.Game;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

public class BoardPanel extends JPanel {
    private Game game;
    private TileButton[][] buttons;
    private StatusListener listener;

    public interface StatusListener {
        void onStatusChanged(String msg);
        void onGameEnded(boolean win);
        void onFlagsChanged(int remaining);
    }

    public BoardPanel(Game game, StatusListener listener) {
        super();
        this.listener = listener;
        setGame(game);
        ToolTipManager.sharedInstance().setInitialDelay(0);
    }

    public void setGame(Game game) {
        this.game = game;
        removeAll();
        Board board = game.getBoard();
        int n = board.getSize();
        setLayout(new GridLayout(n, n, 1, 1));
        buttons = new TileButton[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                TileButton b = new TileButton(r, c);
                b.setText("");
                b.setToolTipText("(" + r + "," + c + ")");
                b.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!b.isEnabled()) return;
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            game.reveal(b.row, b.col);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            game.toggleFlag(b.row, b.col);
                            listener.onFlagsChanged(game.getBoard().getFlagsRemaining());
                        }
                        refresh();
                        checkEnd();
                    }
                });
                buttons[r][c] = b;
                add(b);
            }
        }
        revalidate();
        repaint();
        refresh();
        listener.onFlagsChanged(board.getFlagsRemaining());
    }

    private void checkEnd() {
        if (game.getState() == Game.GameState.WON) {
            listener.onStatusChanged("¡Ganaste!");
            listener.onGameEnded(true);
        } else if (game.getState() == Game.GameState.LOST) {
            listener.onStatusChanged("¡Perdiste!");
            listener.onGameEnded(false);
        }
    }

    public void refresh() {
        Board board = game.getBoard();
        int n = board.getSize();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                TileButton b = buttons[r][c];
                Cell cell = board.get(r, c);
                if (cell.revealed) {
                    b.setEnabled(false);
                    if (cell.isMine) {
                        b.setText("💣");
                    } else if (cell.neighborMines > 0) {
                        b.setText(String.valueOf(cell.neighborMines));
                    } else {
                        b.setText("");
                    }
                } else {
                    b.setEnabled(true);
                    b.setText(cell.flagged ? "🚩" : "");
                }
            }
        }
    }
}
