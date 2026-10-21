package movimiento;

import java.util.ArrayList;
import java.util.List;

import modelo.Direccion;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import modelo.TipoDePieza;

/**
 * El peón captura en diagonal hacia adelante, y solo si en esa casilla hay una pieza rival.
 * Si la captura lo deja en la última fila corona en alguno de los tipos que recibe por constructor.
 */
public final class ReglaCapturaDePeon implements ReglaDeMovimiento {

    private static final int[] COSTADOS = {-1, 1};

    private final List<TipoDePieza> coronaciones;

    public ReglaCapturaDePeon(List<TipoDePieza> coronaciones) {
        this.coronaciones = List.copyOf(coronaciones);
    }

    @Override
    public List<Jugada> jugadasCandidatas(Tablero tablero, Posicion desde, Pieza pieza) {
        List<Jugada> jugadas = new ArrayList<>();
        Direccion adelante = new Direccion(0, pieza.color().sentidoDeAvance());
        for (int costado : COSTADOS) {
            Posicion destino = desde.desplazar(new Direccion(costado, pieza.color().sentidoDeAvance()));
            boolean hayRival = tablero.piezaEn(destino)
                    .map(ocupante -> ocupante.color() != pieza.color())
                    .orElse(false);
            if (tablero.estaDentro(destino) && hayRival) {
                jugadas.addAll(JugadaConPromocion.deUnPeon(tablero, desde, destino, adelante, coronaciones));
            }
        }
        return jugadas;
    }
}
