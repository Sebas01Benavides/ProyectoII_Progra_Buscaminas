
package edu.utn.buscaminas.persistence;

public interface StatsRepository {
    Stats load();
    void save(Stats stats);
    default void saveGameResult(boolean win) {
        Stats s = load();
        s.registerGame(win);
        save(s);
    }
}
