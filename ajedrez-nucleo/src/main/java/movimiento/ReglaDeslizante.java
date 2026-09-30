package movimiento;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import modelo.Direccion;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

/**
 * Avanza en línea recta por cada dirección hasta pasosMaximos casillas, hasta el borde
 * o hasta chocar con una pieza (si es rival, la puede capturar).
 * Torre, alfil y dama usan SIN_LIMITE; el rey usa un solo paso.
 */
public final class ReglaDeslizante implements ReglaDeMovimiento {

    /** Sin tope propio: el límite lo pone el borde del tablero, sea del tamaño que sea. */
    public static final int SIN_LIMITE = Integer.MAX_VALUE;

    private final List<Direccion> direcciones;
    private final int pasosMaximos;

    public ReglaDeslizante(List<Direccion> direcciones, int pasosMaximos) {
        if (pasosMaximos < 1) {
            throw new IllegalArgumentException("pasosMaximos tiene que ser al menos 1");
        }
        this.direcciones = List.copyOf(direcciones);
        this.pasosMaximos = pasosMaximos;
    }

    @Override
    public List<Jugada> jugadasCandidatas(Tablero tablero, Posicion desde, Pieza pieza) {
        List<Jugada> jugadas = new ArrayList<>();
        for (Direccion direccion : direcciones) {
            Posicion actual = desde;
            for (int paso = 1; paso <= pasosMaximos; paso++) {
                actual = actual.desplazar(direccion);
                if (!tablero.estaDentro(actual)) {
                    break;
                }
                Optional<Pieza> ocupante = tablero.piezaEn(actual);
                if (ocupante.isPresent()) {
                    if (ocupante.get().color() != pieza.color()) {
                        jugadas.add(new JugadaSimple(desde, actual));
                    }
                    break; // no se puede pasar por encima de otra pieza
                }
                jugadas.add(new JugadaSimple(desde, actual));
            }
        }
        return jugadas;
    }
}
