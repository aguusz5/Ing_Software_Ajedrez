package juego;

import java.util.List;

import modelo.Color;
import modelo.Posicion;
import modelo.Tablero;

/**
 * Puerto de entrada del núcleo: lo único que conocen la consola y la GUI.
 * Cualquier interfaz nueva (web, IA, tests) juega a través de estos métodos.
 */
public interface JuegoDeAjedrez {

    /** Intenta mover la pieza de "desde" a "hasta". Si la jugada es válida, pasa el turno. */
    ResultadoJugada jugar(Posicion desde, Posicion hasta);

    Tablero tablero();

    Color turnoActual();

    boolean estaEnJaque(Color color);

    /** Suma del valor de las piezas de ese color que siguen en el tablero. */
    int puntos(Color color);

    /** En curso, o terminada con su ganador (o tablas) y el motivo. */
    EstadoPartida estado();

    /** Los jugadores dan la partida por terminada: se decide por puntos. */
    EstadoPartida terminar();

    /** El jugador al que le toca mover abandona: gana el rival, sin mirar los puntos. */
    EstadoPartida rendirse();

    /** Casillas a las que la pieza de "desde" puede ir legalmente. Vacío si no es del turno o la partida terminó. */
    List<Posicion> destinosLegales(Posicion desde);

    /** Casillas que la pieza alcanza por su movimiento pero que no son legales (dejarían a su rey en jaque). */
    List<Posicion> destinosIlegales(Posicion desde);
}
