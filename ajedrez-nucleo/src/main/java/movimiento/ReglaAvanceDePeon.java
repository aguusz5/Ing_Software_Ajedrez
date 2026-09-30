package movimiento;

import java.util.ArrayList;
import java.util.List;

import modelo.Direccion;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

/**
 * El peón avanza una casilla si está libre, o dos si todavía no se movió y ambas están libres.
 * Avanzando nunca captura. El doble paso depende de yaSeMovio, no del número de fila.
 */
public final class ReglaAvanceDePeon implements ReglaDeMovimiento {

    @Override
    public List<Jugada> jugadasCandidatas(Tablero tablero, Posicion desde, Pieza pieza) {
        List<Jugada> jugadas = new ArrayList<>();
        Direccion adelante = new Direccion(0, pieza.color().sentidoDeAvance());
        Posicion unPaso = desde.desplazar(adelante);
        if (!estaLibre(tablero, unPaso)) {
            return jugadas;
        }
        jugadas.add(new JugadaSimple(desde, unPaso));
        Posicion dosPasos = unPaso.desplazar(adelante);
        if (!pieza.yaSeMovio() && estaLibre(tablero, dosPasos)) {
            jugadas.add(new JugadaSimple(desde, dosPasos));
        }
        return jugadas;
    }

    private static boolean estaLibre(Tablero tablero, Posicion posicion) {
        return tablero.estaDentro(posicion) && tablero.piezaEn(posicion).isEmpty();
    }
}
