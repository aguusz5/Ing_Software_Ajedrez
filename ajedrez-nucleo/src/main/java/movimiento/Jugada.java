package movimiento;

import java.util.List;
import java.util.Optional;

import modelo.Posicion;
import modelo.Tablero;
import modelo.TipoDePieza;

/**
 * Una jugada como objeto: de dónde sale, a dónde llega y cómo transforma el tablero.
 * Las jugadas especiales son implementaciones nuevas de esta interfaz: JugadaConPromocion y JugadaDeEnroque.
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

    /**
     * Casillas por las que pasa el rey y que el rival no puede estar atacando cuando se hace la jugada.
     * Vacío para casi todas; el enroque devuelve la casilla de salida, la que cruza y la de llegada.
     */
    default List<Posicion> casillasQueNoPuedenEstarAtacadas() {
        return List.of();
    }
}
