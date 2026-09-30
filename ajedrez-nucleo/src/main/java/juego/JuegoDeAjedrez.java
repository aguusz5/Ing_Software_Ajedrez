package juego;

import modelo.Color;
import modelo.Posicion;
import modelo.Tablero;

/**
 * Puerto de entrada del núcleo: lo único que conocen la consola y la GUI.
 * Cualquier interfaz nueva (web, IA, tests) juega a través de estos cuatro métodos.
 */
public interface JuegoDeAjedrez {

    /** Intenta mover la pieza de "desde" a "hasta". Si la jugada es válida, pasa el turno. */
    ResultadoJugada jugar(Posicion desde, Posicion hasta);

    Tablero tablero();

    Color turnoActual();

    boolean estaEnJaque(Color color);
}
