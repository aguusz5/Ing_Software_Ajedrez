package movimiento;

import java.util.List;
import java.util.Optional;

import modelo.Direccion;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import modelo.TipoDePieza;

/**
 * Un peón que llega a la última fila: en el destino queda una pieza del mismo color con el tipo nuevo.
 * Si en el destino había una pieza rival, queda capturada igual que en una JugadaSimple.
 */
public record JugadaConPromocion(Posicion desde, Posicion hasta, TipoDePieza nuevoTipo) implements Jugada {

    /**
     * Las jugadas de un peón que va de "desde" a "hasta": una por cada tipo de coronación si "hasta" es la
     * última fila (un paso más adelante ya sale del tablero), o una JugadaSimple si no. Sin números de fila.
     */
    static List<Jugada> deUnPeon(Tablero tablero, Posicion desde, Posicion hasta, Direccion adelante,
                                 List<TipoDePieza> coronaciones) {
        if (tablero.estaDentro(hasta.desplazar(adelante))) {
            return List.of(new JugadaSimple(desde, hasta));
        }
        return coronaciones.stream()
                .<Jugada>map(tipo -> new JugadaConPromocion(desde, hasta, tipo))
                .toList();
    }

    @Override
    public Tablero aplicarEn(Tablero tablero) {
        Pieza peon = tablero.piezaEn(desde)
                .orElseThrow(() -> new IllegalStateException("No hay pieza en " + desde));
        return tablero.sin(desde).con(hasta, new Pieza(peon.color(), nuevoTipo).movida());
    }

    @Override
    public Optional<TipoDePieza> promocion() {
        return Optional.of(nuevoTipo);
    }
}
