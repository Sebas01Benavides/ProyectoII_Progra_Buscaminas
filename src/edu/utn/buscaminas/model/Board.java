
package edu.utn.buscaminas.model;

import java.util.ArrayDeque;
import java.util.Random;

public class Board {
    private final int size;
    private final Cell[][] grid;
    private int mineCount;
    private int flagsPlaced;
    private boolean exploded;

    public Board(int size) {
        if (size <= 2) throw new IllegalArgumentException("L debe ser mayor a 2");
        this.size = size;
        this.grid = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Cell();
            }
        }
        this.mineCount = 2 * size;
        this.flagsPlaced = 0;
        this.exploded = false;
        placeMines();
        computeNeighborCounts();
    }

    public int getSize() { return size; }
    public int getMineCount() { return mineCount; }
    public int getFlagsRemaining() { return mineCount - flagsPlaced; }
    public boolean isExploded() { return exploded; }
    public Cell get(int r, int c) { return grid[r][c]; }

    private void placeMines() {
        Random rnd = new Random();
        int placed = 0;
        while (placed < mineCount) {
            int r = rnd.nextInt(size);
            int c = rnd.nextInt(size);
            if (!grid[r][c].isMine) {
                grid[r][c].isMine = true;
                placed++;
            }
        }
    }

    private void computeNeighborCounts() {
        int[] dr = {-1,-1,-1,0,0,1,1,1};
        int[] dc = {-1,0,1,-1,1,-1,0,1};
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c].isMine) {
                    grid[r][c].neighborMines = -1;
                    continue;
                }
                int count = 0;
                for (int k = 0; k < 8; k++) {
                    int nr = r + dr[k], nc = c + dc[k];
                    if (inBounds(nr, nc) && grid[nr][nc].isMine) count++;
                }
                grid[r][c].neighborMines = count;
            }
        }
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < size && c >= 0 && c < size;
    }

    public boolean toggleFlag(int r, int c) {
        Cell cell = grid[r][c];
        if (cell.revealed) return false;
        if (cell.flagged) {
            cell.flagged = false;
            flagsPlaced--;
            return true;
        } else {
            if (flagsPlaced >= mineCount) return false; // número de marcas igual a número de minas
            cell.flagged = true;
            flagsPlaced++;
            return true;
        }
    }

    public RevealResult reveal(int r, int c) {
        Cell cell = grid[r][c];
        if (cell.revealed || cell.flagged) return new RevealResult(false, false, 0);
        cell.revealed = true;
        if (cell.isMine) {
            exploded = true;
            return new RevealResult(true, true, -1);
        }
        int revealedCount = 1;
        if (cell.neighborMines == 0) {
            // BFS flood to reveal zeros and border numbers
            ArrayDeque<int[]> q = new ArrayDeque<>();
            q.add(new int[]{r, c});
            while (!q.isEmpty()) {
                int[] cur = q.poll();
                int cr = cur[0], cc = cur[1];
                int[] dr = {-1,-1,-1,0,0,1,1,1};
                int[] dc = {-1,0,1,-1,1,-1,0,1};
                for (int k = 0; k < 8; k++) {
                    int nr = cr + dr[k], nc = cc + dc[k];
                    if (!inBounds(nr, nc)) continue;
                    Cell nb = grid[nr][nc];
                    if (nb.revealed || nb.flagged) continue;
                    if (!nb.isMine) {
                        nb.revealed = true;
                        revealedCount++;
                        if (nb.neighborMines == 0) {
                            q.add(new int[]{nr, nc});
                        }
                    }
                }
            }
        }
        return new RevealResult(true, false, revealedCount);
    }

    public boolean allSafeCellsRevealedAndMinesFlagged() {
        int revealedSafe = 0;
        int totalSafe = size * size - mineCount;
        int correctFlags = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Cell cell = grid[r][c];
                if (!cell.isMine && cell.revealed) revealedSafe++;
                if (cell.isMine && cell.flagged) correctFlags++;
            }
        }
        return revealedSafe == totalSafe && correctFlags == mineCount;
    }

    public void revealAllMines() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c].isMine) grid[r][c].revealed = true;
            }
        }
    }

    public static class RevealResult {
        public final boolean changed;
        public final boolean hitMine;
        public final int revealedCount;
        public RevealResult(boolean changed, boolean hitMine, int revealedCount) {
            this.changed = changed; this.hitMine = hitMine; this.revealedCount = revealedCount;
        }
    }
}
