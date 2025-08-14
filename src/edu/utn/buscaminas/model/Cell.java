
package edu.utn.buscaminas.model;

public class Cell {
    public boolean isMine;
    public boolean revealed;
    public boolean flagged;
    public int neighborMines;

    public Cell() {
        this.isMine = false;
        this.revealed = false;
        this.flagged = false;
        this.neighborMines = 0;
    }
}
