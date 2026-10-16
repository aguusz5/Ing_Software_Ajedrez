package juego;

import java.util.List;
import java.util.Optional;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import movimiento.Jugada;
import reglas.DetectorDeJaque;
import reglas.GeneradorDeJugadasLegales;
import reglas.ResultadoValidacion;
import reglas.ValidadorDeJugadas;

/**
 * Implementación del puerto de entrada: guarda el tablero actual, de quién es el turno y si la
 * partida terminó. Recibe sus colaboradores por constructor; no hace new de ninguno.
 */
public final class Partida implements JuegoDeAjedrez {

    private final ValidadorDeJugadas validador;
    private final DetectorDeJaque detector;
    private final GeneradorDeJugadasLegales generador;
    private Tablero tablero;
    private Color turno;
    private EstadoPartida estado;

    public Partida(Tablero tablero, ValidadorDeJugadas validador, DetectorDeJaque detector,
                   GeneradorDeJugadasLegales generador) {
        this.tablero = tablero;
        this.validador = validador;
        this.detector = detector;
        this.generador = generador;
        this.turno = Color.BLANCO;
        this.estado = EstadoPartida.enCurso();
    }

    @Override
    public ResultadoJugada jugar(Posicion desde, Posicion hasta) {
        // 0. Con la partida terminada no se juega más.
        if (estado.terminada()) {
            return ResultadoJugada.rechazada("La partida terminó");
        }

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

        // 4. Se aplica la jugada y pasa el turno.
        tablero = jugada.get().aplicarEn(tablero);
        turno = turno.contrario();

        // 5. Si el que tiene que mover no tiene ninguna jugada legal, la partida termina:
        //    en jaque es mate y gana el que acaba de mover; si no, es ahogado y son tablas (sin mirar puntos).
        boolean enJaque = detector.estaEnJaque(tablero, turno);
        if (generador.jugadasLegales(tablero, turno).isEmpty()) {
            Color quienMovio = turno.contrario();
            estado = enJaque
                    ? new EstadoPartida(true, Optional.of(quienMovio), "Jaque mate: gana " + quienMovio)
                    : new EstadoPartida(true, Optional.empty(), "Ahogado: tablas");
        }
        return enJaque ? ResultadoJugada.okConJaque(turno) : ResultadoJugada.ok();
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

    @Override
    public int puntos(Color color) {
        return tablero.puntosDe(color);
    }

    @Override
    public EstadoPartida estado() {
        return estado;
    }

    @Override
    public EstadoPartida terminar() {
        if (!estado.terminada()) {
            estado = decidirPorPuntos("Partida terminada por los jugadores");
        }
        return estado;
    }

    @Override
    public EstadoPartida rendirse() {
        if (!estado.terminada()) {
            Color rival = turno.contrario();
            estado = new EstadoPartida(true, Optional.of(rival), turno + " se rinde: gana " + rival);
        }
        return estado;
    }

    @Override
    public List<Posicion> destinosLegales(Posicion desde) {
        if (estado.terminada()) {
            return List.of();
        }
        return generador.jugadasLegalesDesde(tablero, desde, turno).stream()
                .map(Jugada::hasta)
                .distinct()
                .toList();
    }

    @Override
    public List<Posicion> destinosIlegales(Posicion desde) {
        Optional<Pieza> pieza = tablero.piezaEn(desde);
        // Las piezas del rival no se marcan: si no, todas sus casillas saldrían ilegales por el turno.
        if (estado.terminada() || pieza.isEmpty() || pieza.get().color() != turno) {
            return List.of();
        }
        List<Posicion> legales = destinosLegales(desde);
        return pieza.get().jugadasCandidatas(tablero, desde).stream()
                .map(Jugada::hasta)
                .distinct()
                .filter(destino -> !legales.contains(destino))
                .toList();
    }

    /** La única regla de "ganar por puntos": si el profesor la quiere distinta, se cambia acá. */
    private EstadoPartida decidirPorPuntos(String motivo) {
        int blancas = puntos(Color.BLANCO);
        int negras = puntos(Color.NEGRO);
        if (blancas == negras) {
            return new EstadoPartida(true, Optional.empty(),
                    motivo + ": tablas, " + blancas + " a " + negras + " puntos");
        }
        Color ganador = blancas > negras ? Color.BLANCO : Color.NEGRO;
        return new EstadoPartida(true, Optional.of(ganador), motivo + ": gana " + ganador + " por "
                + Math.max(blancas, negras) + " a " + Math.min(blancas, negras) + " puntos");
    }
}
