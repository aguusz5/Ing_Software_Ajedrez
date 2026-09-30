package reglas;

import java.util.Optional;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

/**
 * Un color está en jaque si alguna pieza rival tiene, entre sus jugadas candidatas,
 * una que llega a la casilla de su rey. No conoce los tipos de pieza: le pregunta a cada una.
 */
public final class DetectorDeJaque {

    public boolean estaEnJaque(Tablero tablero, Color color) {
        Posicion rey = tablero.buscarRey(color);
        for (int columna = 0; columna < tablero.ancho(); columna++) {
            for (int fila = 0; fila < tablero.alto(); fila++) {
                Posicion posicion = new Posicion(columna, fila);
                Optional<Pieza> pieza = tablero.piezaEn(posicion);
                if (pieza.isPresent() && pieza.get().color() != color
                        && llegaA(rey, tablero, posicion, pieza.get())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean llegaA(Posicion objetivo, Tablero tablero, Posicion desde, Pieza pieza) {
        return pieza.jugadasCandidatas(tablero, desde).stream()
                .anyMatch(jugada -> jugada.hasta().equals(objetivo));
    }
}
