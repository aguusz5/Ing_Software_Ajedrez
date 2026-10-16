package juego;

import java.util.Optional;

import modelo.Color;

/**
 * Si la partida sigue o terminó y, en ese caso, quién ganó y por qué. Ganador vacío = tablas.
 */
public record EstadoPartida(boolean terminada, Optional<Color> ganador, String motivo) {

    public static EstadoPartida enCurso() {
        return new EstadoPartida(false, Optional.empty(), "");
    }
}
