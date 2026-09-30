package reglas;

import java.util.Optional;

import modelo.Color;
import modelo.Pieza;
import modelo.Tablero;
import movimiento.Jugada;

/**
 * Solo se pueden mover piezas del color al que le toca jugar.
 */
public final class ReglaPiezaPropia implements ReglaDeValidacion {

    @Override
    public ResultadoValidacion validar(Tablero tablero, Jugada jugada, Color turno) {
        Optional<Pieza> pieza = tablero.piezaEn(jugada.desde());
        if (pieza.isEmpty()) {
            return ResultadoValidacion.rechazada("No hay ninguna pieza en la casilla de origen");
        }
        if (pieza.get().color() != turno) {
            return ResultadoValidacion.rechazada("Le toca jugar a " + turno + ": no podés mover piezas del rival");
        }
        return ResultadoValidacion.ok();
    }
}
