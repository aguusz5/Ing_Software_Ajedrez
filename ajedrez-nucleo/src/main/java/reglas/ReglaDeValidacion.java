package reglas;

import modelo.Color;
import modelo.Tablero;
import movimiento.Jugada;

/**
 * Una condición que toda jugada tiene que cumplir, además de respetar el movimiento de su pieza.
 * Una regla nueva es una clase nueva que se suma a la lista del ValidadorDeJugadas.
 */
public interface ReglaDeValidacion {

    ResultadoValidacion validar(Tablero tablero, Jugada jugada, Color turno);
}
