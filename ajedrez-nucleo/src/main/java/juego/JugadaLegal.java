package juego;

import java.util.Optional;

import modelo.Posicion;
import modelo.TipoDePieza;
import movimiento.Jugada;

/**
 * Una jugada legal escrita como datos: de dónde sale, a dónde llega y, si corona, en qué tipo.
 * Es lo que el puerto le muestra a quien elige jugadas desde afuera (la IA) sin mostrarle las reglas.
 */
public record JugadaLegal(Posicion desde, Posicion hasta, Optional<String> promocion) {

    /** La misma jugada del núcleo, como datos. */
    public static JugadaLegal de(Jugada jugada) {
        return new JugadaLegal(jugada.desde(), jugada.hasta(), jugada.promocion().map(TipoDePieza::nombre));
    }
}
