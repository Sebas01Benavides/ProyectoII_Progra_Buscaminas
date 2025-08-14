
package edu.utn.buscaminas.ui;

import edu.utn.buscaminas.model.Game;
import edu.utn.buscaminas.persistence.*;
import edu.utn.buscaminas.util.Dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame implements BoardPanel.StatusListener {
    private Game game;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private JLabel flagsLabel;
    private StatsRepository statsRepo;
    private Stats stats;

    public MainFrame() {
        super("Buscaminas - UTN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        this.statsRepo = initRepository();
        this.stats = statsRepo.load();

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Juego");
        JMenuItem nuevo = new JMenuItem(new AbstractAction("Juego Nuevo") {
            @Override public void actionPerformed(ActionEvent e) { startNewGameInteractive(); }
        });
        JMenuItem salir = new JMenuItem(new AbstractAction("Salir") {
            @Override public void actionPerformed(ActionEvent e) { System.exit(0); }
        });
        menu.add(nuevo); menu.addSeparator(); menu.add(salir);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        statusLabel = new JLabel("Listo");
        flagsLabel = new JLabel("🚩 --");
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(flagsLabel, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        // Mostrar estadísticas al inicio
        JOptionPane.showMessageDialog(this, stats.toString(), "Estadísticas", JOptionPane.INFORMATION_MESSAGE);

        startNewGameInteractive();
        pack();
        setLocationRelativeTo(null);
    }

    private StatsRepository initRepository() {
        try {
            // Intenta JDBC si hay URL configurada
            StatsRepository repo = new JdbcStatsRepository();
            // Probar una carga (puede lanzar)
            repo.load();
            return repo;
        } catch (Exception ex) {
            System.err.println("Usando repositorio en memoria: " + ex.getMessage());
            return new InMemoryStatsRepository();
        }
    }

    private void startNewGameInteractive() {
        Integer size = askBoardSize();
        if (size == null) return;
        this.game = new Game(size);
        if (boardPanel == null) {
            boardPanel = new BoardPanel(game, this);
            add(boardPanel, BorderLayout.CENTER);
        } else {
            boardPanel.setGame(game);
        }
        statusLabel.setText("L = " + size + " | Minas: " + game.getBoard().getMineCount());
        flagsLabel.setText("🚩 " + game.getBoard().getFlagsRemaining());
        pack();
    }

    private Integer askBoardSize() {
        while (true) {
            String resp = JOptionPane.showInputDialog(this, "Ingrese tamaño del lado L (mayor a 2):", "8");
            if (resp == null) return null; // cancel
            try {
                int L = Integer.parseInt(resp.trim());
                if (L > 2) return L;
                Dialogs.error(this, "L debe ser mayor a 2.");
            } catch (NumberFormatException e) {
                Dialogs.error(this, "Ingrese un número entero válido.");
            }
        }
    }

    @Override
    public void onStatusChanged(String msg) {
        statusLabel.setText(msg);
    }

    @Override
    public void onGameEnded(boolean win) {
        statsRepo.saveGameResult(win);
        stats = statsRepo.load();

        JOptionPane.showMessageDialog(this, stats.toString(), "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(this,
                win ? "¡Felicidades, ganaste!" : "Pisaste una mina. ¡Perdiste!",
                "Fin del juego",
                win ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

        int opt = JOptionPane.showConfirmDialog(this, "¿Desea jugar de nuevo?", "Jugar de nuevo", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            startNewGameInteractive();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void onFlagsChanged(int remaining) {
        flagsLabel.setText("🚩 " + remaining);
    }
}
