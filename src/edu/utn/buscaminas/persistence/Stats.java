
package edu.utn.buscaminas.persistence;

import java.time.LocalDateTime;

public class Stats {
    public int gamesPlayed;
    public int gamesWon;
    public int gamesLost;
    public LocalDateTime lastPlayed;

    public Stats() {
        gamesPlayed = gamesWon = gamesLost = 0;
        lastPlayed = null;
    }

    public void registerGame(boolean win) {
        gamesPlayed++;
        if (win) gamesWon++; else gamesLost++;
        lastPlayed = LocalDateTime.now();
    }

    @Override
    public String toString() {
        String lp = (lastPlayed == null) ? "N/A" : lastPlayed.toString();
        return "Jugados: " + gamesPlayed + " | Ganados: " + gamesWon + " | Perdidos: " + gamesLost + " | Último: " + lp;
    }
}
