package movimiento;

import java.util.ArrayList;
import java.util.List;

import modelo.Direccion;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

/**
 * El peón captura en diagonal hacia adelante, y solo si en esa casilla hay una pieza rival.
 */
public final class ReglaCapturaDePeon implements ReglaDeMovimiento {

    private static final int[] COSTADOS = {-1, 1};

    @Override
    public List<Jugada> jugadasCandidatas(Tablero tablero, Posicion desde, Pieza pieza) {
        List<Jugada> jugadas = new ArrayList<>();
        for (int costado : COSTADOS) {
            Posicion destino = desde.desplazar(new Direccion(costado, pieza.color().sentidoDeAvance()));
            boolean hayRival = tablero.piezaEn(destino)
                    .map(ocupante -> ocupante.color() != pieza.color())
                    .orElse(false);
            if (tablero.estaDentro(destino) && hayRival) {
                jugadas.add(new JugadaSimple(desde, destino));
            }
        }
        return jugadas;
    }
}
