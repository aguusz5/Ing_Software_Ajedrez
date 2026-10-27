package movimiento;

import java.util.List;

import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

/**
 * El enroque: el rey va de "desde" a "hasta" (dos casillas hacia la torre) y la torre salta de
 * "desdeTorre" a "hastaTorre", la casilla que el rey cruzó. Las dos piezas quedan marcadas como movidas.
 */
public record JugadaDeEnroque(Posicion desde, Posicion hasta, Posicion desdeTorre, Posicion hastaTorre)
        implements Jugada {

    @Override
    public Tablero aplicarEn(Tablero tablero) {
        Pieza rey = tablero.piezaEn(desde)
                .orElseThrow(() -> new IllegalStateException("No hay pieza en " + desde));
        Pieza torre = tablero.piezaEn(desdeTorre)
                .orElseThrow(() -> new IllegalStateException("No hay pieza en " + desdeTorre));
        return tablero.sin(desde).sin(desdeTorre)
                .con(hasta, rey.movida())
                .con(hastaTorre, torre.movida());
    }

    /** El rey no puede enrocar estando en jaque, ni cruzar ni caer en una casilla atacada. */
    @Override
    public List<Posicion> casillasQueNoPuedenEstarAtacadas() {
        return List.of(desde, hastaTorre, hasta);
    }
}
