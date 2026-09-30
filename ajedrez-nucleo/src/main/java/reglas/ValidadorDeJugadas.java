package reglas;

import java.util.List;

import modelo.Color;
import modelo.Tablero;
import movimiento.Jugada;

/**
 * Pasa la jugada por cada regla de validación, en orden. La primera que la rechaza decide.
 * Las reglas llegan por constructor: sumar una regla no obliga a modificar esta clase.
 */
public final class ValidadorDeJugadas {

    private final List<ReglaDeValidacion> reglas;

    public ValidadorDeJugadas(List<ReglaDeValidacion> reglas) {
        if (reglas.isEmpty()) {
            throw new IllegalArgumentException("El validador necesita al menos una regla");
        }
        this.reglas = List.copyOf(reglas);
    }

    public ResultadoValidacion validar(Tablero tablero, Jugada jugada, Color turno) {
        for (ReglaDeValidacion regla : reglas) {
            ResultadoValidacion resultado = regla.validar(tablero, jugada, turno);
            if (!resultado.valida()) {
                return resultado;
            }
        }
        return ResultadoValidacion.ok();
    }
}
