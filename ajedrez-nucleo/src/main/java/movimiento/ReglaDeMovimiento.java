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
}
