
# Buscaminas (Programación I - UTN)

Implementación simplificada del juego **Buscaminas** en Java Swing.
- El usuario elige `L` (tamaño del tablero LxL, L > 2).
- Se colocan exactamente `2*L` minas sin repetición.
- Interfaz gráfica con `JFrame` + `JPanel` (grid), menús: **Juego Nuevo** y **Salir**.
- Estadísticas (jugados/ganados/perdidos) con repositorio configurable:
  - `InMemoryStatsRepository` (por defecto).
  - `JdbcStatsRepository` (listo para conexión a BD vía `db.properties`).
 

## Ejecutar
```bash
mvn -q -DskipTests exec:java
```
o desde NetBeans ejecutar `Main`.

## Atajos de uso
- **Click izquierdo**: destapar.
- **Click derecho**: marcar/desmarcar (bandera).
- No se puede pisar una casilla marcada.
- Si se pisa una mina: se pierde y se muestran todas.
- Ganas cuando todas las minas quedan **marcadas** (exactamente) y el resto del tablero está destapado.

## Configuración JDBC

- Sugerida: `stats(games_played INT, games_won INT, games_lost INT, last_played TIMESTAMP)` con una sola fila (id=1).
- Puede adaptar `JdbcStatsRepository` a su diseño final.

## Créditos
Inspirado por tutoriales públicos de Minesweeper en Java, con implementación propia simplificada para evitar plagio.
# ProyectoII_Progra_Buscaminas
