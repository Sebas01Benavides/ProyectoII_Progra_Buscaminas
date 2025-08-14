
package edu.utn.buscaminas.model;

public class Game {
    private Board board;
    private GameState state;

    public enum GameState { READY, PLAYING, WON, LOST }

    public Game(int size) {
        start(size);
    }

    public void start(int size) {
        this.board = new Board(size);
        this.state = GameState.PLAYING;
    }

    public Board getBoard() { return board; }

    public GameState getState() { return state; }

    public void toggleFlag(int r, int c) {
        if (state != GameState.PLAYING) return;
        board.toggleFlag(r, c);
        checkWin();
    }

    public void reveal(int r, int c) {
        if (state != GameState.PLAYING) return;
        Board.RevealResult res = board.reveal(r, c);
        if (!res.changed) return;
        if (res.hitMine) {
            board.revealAllMines();
            state = GameState.LOST;
        } else {
            checkWin();
        }
    }

    private void checkWin() {
        if (board.allSafeCellsRevealedAndMinesFlagged()) {
            state = GameState.WON;
        }
    }
}
