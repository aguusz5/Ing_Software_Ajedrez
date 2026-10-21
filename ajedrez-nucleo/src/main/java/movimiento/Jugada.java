package movimiento;

import java.util.Optional;

import modelo.Posicion;
import modelo.Tablero;
import modelo.TipoDePieza;

/**
 * Una jugada como objeto: de dónde sale, a dónde llega y cómo transforma el tablero.
 * Enroque o captura al paso serían implementaciones nuevas de esta interfaz, como JugadaConPromocion.
 */
public interface Jugada {

    Posicion desde();

    Posicion hasta();

    /** Devuelve el tablero que resulta de hacer la jugada. El tablero recibido no cambia. */
    Tablero aplicarEn(Tablero tablero);

    /** En qué se convierte la pieza al llegar. Vacío si la jugada no corona. */
    default Optional<TipoDePieza> promocion() {
        return Optional.empty();
    }
}
