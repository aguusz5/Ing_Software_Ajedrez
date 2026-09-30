package reglas;

import modelo.Color;
import modelo.Tablero;
import movimiento.Jugada;

/**
 * Prohíbe las jugadas que dejan (o meten) al propio rey en jaque. Como el tablero es inmutable,
 * se aplica la jugada, se obtiene el tablero de "después" y se le pregunta al detector.
 */
public final class ReglaNoDejarReyEnJaque implements ReglaDeValidacion {

    private final DetectorDeJaque detector;

    public ReglaNoDejarReyEnJaque(DetectorDeJaque detector) {
        this.detector = detector;
    }

    @Override
    public ResultadoValidacion validar(Tablero tablero, Jugada jugada, Color turno) {
        Tablero despues = jugada.aplicarEn(tablero);
        if (detector.estaEnJaque(despues, turno)) {
            return ResultadoValidacion.rechazada("Esa jugada deja a tu rey en jaque");
        }
        return ResultadoValidacion.ok();
    }
}
