package juego;

import java.util.Optional;

import modelo.Color;

/**
 * Respuesta de jugar(): si la jugada se hizo, el motivo si se rechazó
 * y, si algún rey quedó en jaque, de qué color es.
 */
public record ResultadoJugada(boolean valida, String motivo, Optional<Color> colorEnJaque) {

    public static ResultadoJugada ok() {
        return new ResultadoJugada(true, "", Optional.empty());
    }

    public static ResultadoJugada okConJaque(Color colorEnJaque) {
        return new ResultadoJugada(true, "", Optional.of(colorEnJaque));
    }

    public static ResultadoJugada rechazada(String motivo) {
        return new ResultadoJugada(false, motivo, Optional.empty());
    }
}
