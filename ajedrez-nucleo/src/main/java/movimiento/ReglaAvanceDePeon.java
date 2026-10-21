package movimiento;

import java.util.ArrayList;
import java.util.List;

import modelo.Direccion;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import modelo.TipoDePieza;

/**
 * El peón avanza una casilla si está libre, o dos si todavía no se movió y ambas están libres.
 * Avanzando nunca captura. El doble paso depende de yaSeMovio, no del número de fila.
 * Si llega a la última fila corona en alguno de los tipos que recibe por constructor.
 */
public final class ReglaAvanceDePeon implements ReglaDeMovimiento {

    private final List<TipoDePieza> coronaciones;

    public ReglaAvanceDePeon(List<TipoDePieza> coronaciones) {
        this.coronaciones = List.copyOf(coronaciones);
    }

    @Override
    public List<Jugada> jugadasCandidatas(Tablero tablero, Posicion desde, Pieza pieza) {
        List<Jugada> jugadas = new ArrayList<>();
        Direccion adelante = new Direccion(0, pieza.color().sentidoDeAvance());
        Posicion unPaso = desde.desplazar(adelante);
        if (!estaLibre(tablero, unPaso)) {
            return jugadas;
        }
        jugadas.addAll(JugadaConPromocion.deUnPeon(tablero, desde, unPaso, adelante, coronaciones));
        Posicion dosPasos = unPaso.desplazar(adelante);
        if (!pieza.yaSeMovio() && estaLibre(tablero, dosPasos)) {
            jugadas.addAll(JugadaConPromocion.deUnPeon(tablero, desde, dosPasos, adelante, coronaciones));
        }
        return jugadas;
    }

    private static boolean estaLibre(Tablero tablero, Posicion posicion) {
        return tablero.estaDentro(posicion) && tablero.piezaEn(posicion).isEmpty();
    }
}
