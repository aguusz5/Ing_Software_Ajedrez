package movimiento;

import modelo.Posicion;
import modelo.Tablero;

/**
 * Una jugada como objeto: de dónde sale, a dónde llega y cómo transforma el tablero.
 * Enroque, captura al paso o promoción serían implementaciones nuevas de esta interfaz.
 */
public interface Jugada {

    Posicion desde();

    Posicion hasta();

    /** Devuelve el tablero que resulta de hacer la jugada. El tablero recibido no cambia. */
    Tablero aplicarEn(Tablero tablero);
}
