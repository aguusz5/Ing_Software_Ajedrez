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

    /**
     * Igual que jugar, pero si el peón corona lo hace en el tipo pedido ("dama", "torre", "alfil" o "caballo").
     * Sin elegir, corona en dama. Si la jugada no corona, la promoción se ignora.
     */
    ResultadoJugada jugar(Posicion desde, Posicion hasta, String promocion);

    /** En qué puede coronar esa jugada legal, para que la interfaz pregunte. Vacío si no corona. */
    List<String> opcionesDeCoronacion(Posicion desde, Posicion hasta);

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

    /** Vuelve un paso atrás: la última jugada, o el fin por "terminar" o rendición. false si no hay nada. */
    boolean deshacer();

    /** Vuelve a aplicar lo último que se deshizo. false si no hay nada; una jugada nueva lo descarta. */
    boolean rehacer();

    /** Casillas a las que la pieza de "desde" puede ir legalmente. Vacío si no es del turno o la partida terminó. */
    List<Posicion> destinosLegales(Posicion desde);

    /** Casillas que la pieza alcanza por su movimiento pero que no son legales (dejarían a su rey en jaque). */
    List<Posicion> destinosIlegales(Posicion desde);

    /** Todas las jugadas legales del que mueve, como datos. Vacío si la partida terminó. La IA elige entre estas. */
    List<JugadaLegal> jugadasLegales();

    /** Vuelve a la posición inicial: le toca a las blancas, la partida está en curso y no hay nada para deshacer. */
    void reiniciar();
}
