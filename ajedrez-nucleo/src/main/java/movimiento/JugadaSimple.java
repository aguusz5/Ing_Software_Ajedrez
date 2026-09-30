package movimiento;

import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

/**
 * Llevar una pieza de una casilla a otra. Si en el destino había una pieza rival,
 * queda capturada porque el destino se sobrescribe.
 */
public record JugadaSimple(Posicion desde, Posicion hasta) implements Jugada {

    @Override
    public Tablero aplicarEn(Tablero tablero) {
        Pieza pieza = tablero.piezaEn(desde)
                .orElseThrow(() -> new IllegalStateException("No hay pieza en " + desde));
        return tablero.sin(desde).con(hasta, pieza.movida());
    }
}
