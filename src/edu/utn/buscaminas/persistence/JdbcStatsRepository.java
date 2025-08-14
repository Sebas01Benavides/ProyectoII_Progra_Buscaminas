
package edu.utn.buscaminas.persistence;

import edu.utn.buscaminas.util.Config;
import java.sql.*;
import java.time.LocalDateTime;

public class JdbcStatsRepository implements StatsRepository {

    private Connection getConnection() throws Exception {
        String url = Config.get("jdbc.url");
        String user = Config.get("jdbc.username");
        String pass = Config.get("jdbc.password");
        String driver = Config.get("jdbc.driver");
        if (driver != null && !driver.isBlank()) {
            Class.forName(driver);
        }
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("jdbc.url no configurado");
        }
        return DriverManager.getConnection(url, user, pass);
    }

    @Override
    public Stats load() {
        try (Connection con = getConnection()) {
            // Se asume una sola fila en tabla 'stats' (id=1)
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT games_played, games_won, games_lost, last_played FROM stats WHERE id=1")) {
                ResultSet rs = ps.executeQuery();
                Stats s = new Stats();
                if (rs.next()) {
                    s.gamesPlayed = rs.getInt(1);
                    s.gamesWon = rs.getInt(2);
                    s.gamesLost = rs.getInt(3);
                    Timestamp ts = rs.getTimestamp(4);
                    s.lastPlayed = (ts == null) ? null : ts.toLocalDateTime();
                } else {
                    // si no existe, se crea en memoria; la persitencia se hará en save()
                }
                return s;
            }
        } catch (Exception ex) {
            // fallback a memoria si falla la conexión
            System.err.println("Advertencia: No se pudo cargar estadísticas vía JDBC: " + ex.getMessage());
            return new Stats();
        }
    }

    @Override
    public void save(Stats s) {
        try (Connection con = getConnection()) {
            // upsert asumiendo id=1
            try (PreparedStatement upd = con.prepareStatement(
                "UPDATE stats SET games_played=?, games_won=?, games_lost=?, last_played=? WHERE id=1")) {
                upd.setInt(1, s.gamesPlayed);
                upd.setInt(2, s.gamesWon);
                upd.setInt(3, s.gamesLost);
                if (s.lastPlayed == null) upd.setTimestamp(4, null);
                else upd.setTimestamp(4, Timestamp.valueOf(s.lastPlayed));
                int rows = upd.executeUpdate();
                if (rows == 0) {
                    try (PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO stats(id, games_played, games_won, games_lost, last_played) VALUES(1, ?, ?, ?, ?)")) {
                        ins.setInt(1, s.gamesPlayed);
                        ins.setInt(2, s.gamesWon);
                        ins.setInt(3, s.gamesLost);
                        if (s.lastPlayed == null) ins.setTimestamp(4, null);
                        else ins.setTimestamp(4, Timestamp.valueOf(s.lastPlayed));
                        ins.executeUpdate();
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Advertencia: No se pudo guardar estadísticas vía JDBC: " + ex.getMessage());
        }
    }
}
