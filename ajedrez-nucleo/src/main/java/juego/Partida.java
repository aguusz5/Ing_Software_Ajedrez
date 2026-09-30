package juego;

import java.util.Optional;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import movimiento.Jugada;
import reglas.DetectorDeJaque;
import reglas.ResultadoValidacion;
import reglas.ValidadorDeJugadas;

/**
 * Implementación del puerto de entrada: guarda el tablero actual y de quién es el turno.
 * Recibe sus colaboradores por constructor; no hace new de ninguno.
 */
public final class Partida implements JuegoDeAjedrez {

    private final ValidadorDeJugadas validador;
    private final DetectorDeJaque detector;
    private Tablero tablero;
    private Color turno;

    public Partida(Tablero tablero, ValidadorDeJugadas validador, DetectorDeJaque detector) {
        this.tablero = tablero;
        this.validador = validador;
        this.detector = detector;
        this.turno = Color.BLANCO;
    }

    @Override
    public ResultadoJugada jugar(Posicion desde, Posicion hasta) {
        // 1. Tiene que haber una pieza en el origen.
        Optional<Pieza> pieza = tablero.piezaEn(desde);
        if (pieza.isEmpty()) {
            return ResultadoJugada.rechazada("No hay ninguna pieza en la casilla de origen");
        }

        // 2. El destino tiene que ser una de las jugadas que permite el movimiento de esa pieza.
        Optional<Jugada> jugada = pieza.get().jugadasCandidatas(tablero, desde).stream()
                .filter(candidata -> candidata.hasta().equals(hasta))
                .findFirst();
        if (jugada.isEmpty()) {
            return ResultadoJugada.rechazada("Esa pieza no puede moverse a esa casilla");
        }

        // 3. La jugada tiene que pasar las reglas de validación (turno, no dejar al rey en jaque, ...).
        ResultadoValidacion validacion = validador.validar(tablero, jugada.get(), turno);
        if (!validacion.valida()) {
            return ResultadoJugada.rechazada(validacion.motivo());
        }

        // 4. Se aplica la jugada, pasa el turno y se avisa si el rival quedó en jaque.
        tablero = jugada.get().aplicarEn(tablero);
        turno = turno.contrario();
        return detector.estaEnJaque(tablero, turno)
                ? ResultadoJugada.okConJaque(turno)
                : ResultadoJugada.ok();
    }

    @Override
    public Tablero tablero() {
        return tablero;
    }

    @Override
    public Color turnoActual() {
        return turno;
    }

    @Override
    public boolean estaEnJaque(Color color) {
        return detector.estaEnJaque(tablero, color);
    }
}
