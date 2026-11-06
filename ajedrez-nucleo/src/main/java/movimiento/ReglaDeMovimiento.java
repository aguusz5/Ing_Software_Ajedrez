package movimiento;

import java.util.List;

import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

/**
 * Una forma de moverse (patrón Strategy). Cada tipo de pieza se arma combinando reglas.
 * Regla del equipo: nunca devuelve jugadas que terminen sobre una pieza propia.
 */
public interface ReglaDeMovimiento {

    List<Jugada> jugadasCandidatas(Tablero tablero, Posicion desde, Pieza pieza);

    /**
     * Cómo queda una jugada que le presta un potenciador a una pieza que se mueve con esta regla. Casi todas la
     * dejan igual; el avance del peón la convierte en coronación si lo deja en la última fila (decisión 36).
     */
    default List<Jugada> adaptarJugadaPrestada(Tablero tablero, Jugada prestada, Pieza pieza) {
        return List.of(prestada);
    }
}
