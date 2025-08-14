
package edu.utn.buscaminas.persistence;

public class InMemoryStatsRepository implements StatsRepository {
    private Stats stats = new Stats();
    @Override public Stats load() { return stats; }
    @Override public void save(Stats stats) { this.stats = stats; }
}
