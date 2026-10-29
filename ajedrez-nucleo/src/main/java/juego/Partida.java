package juego;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import modelo.TipoDePieza;
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
    // Para deshacer/rehacer alcanza con guardar fotos: el tablero es inmutable (decisión 7).
    private final Deque<Foto> pasado = new ArrayDeque<>();
    private final Deque<Foto> futuro = new ArrayDeque<>();
    // También por ser inmutable, para reiniciar alcanza con guardar el tablero con el que se empezó.
    private final Tablero inicial;
    private Tablero tablero;
    private Color turno;
    private EstadoPartida estado;

    public Partida(Tablero tablero, ValidadorDeJugadas validador, DetectorDeJaque detector,
                   GeneradorDeJugadasLegales generador) {
        this.inicial = tablero;
        this.validador = validador;
        this.detector = detector;
        this.generador = generador;
        reiniciar();
    }

    @Override
    public ResultadoJugada jugar(Posicion desde, Posicion hasta) {
        return jugar(desde, hasta, Optional.empty());
    }

    @Override
    public ResultadoJugada jugar(Posicion desde, Posicion hasta, String promocion) {
        return jugar(desde, hasta, Optional.of(promocion));
    }

    private ResultadoJugada jugar(Posicion desde, Posicion hasta, Optional<String> promocion) {
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
        //    Si corona hay una candidata por tipo: se toma la pedida o, sin elegir, la primera (dama).
        List<Jugada> alDestino = pieza.get().jugadasCandidatas(tablero, desde).stream()
                .filter(candidata -> candidata.hasta().equals(hasta))
                .toList();
        if (alDestino.isEmpty()) {
            return ResultadoJugada.rechazada("Esa pieza no puede moverse a esa casilla");
        }
        Optional<Jugada> jugada = alDestino.stream()
                .filter(candidata -> promocion.isEmpty() || coronaEn(candidata, promocion.get()))
                .findFirst();
        if (jugada.isEmpty()) {
            return ResultadoJugada.rechazada("No se puede coronar en " + promocion.get());
        }

        // 3. La jugada tiene que pasar las reglas de validación (turno, no dejar al rey en jaque, ...).
        ResultadoValidacion validacion = validador.validar(tablero, jugada.get(), turno);
        if (!validacion.valida()) {
            return ResultadoJugada.rechazada(validacion.motivo());
        }

        // 4. Se aplica la jugada y pasa el turno.
        guardarFoto();
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

    /** Una jugada que no corona acepta cualquier pedido (se ignora); una que corona, solo el de su tipo. */
    private static boolean coronaEn(Jugada jugada, String promocion) {
        return jugada.promocion().map(tipo -> tipo.nombre().equalsIgnoreCase(promocion)).orElse(true);
    }

    @Override
    public List<String> opcionesDeCoronacion(Posicion desde, Posicion hasta) {
        if (estado.terminada()) {
            return List.of();
        }
        return generador.jugadasLegalesDesde(tablero, desde, turno).stream()
                .filter(jugada -> jugada.hasta().equals(hasta))
                .flatMap(jugada -> jugada.promocion().stream())
                .map(TipoDePieza::nombre)
                .toList();
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
            guardarFoto();
            estado = decidirPorPuntos("Partida terminada por los jugadores");
        }
        return estado;
    }

    @Override
    public EstadoPartida rendirse() {
        if (!estado.terminada()) {
            guardarFoto();
            Color rival = turno.contrario();
            estado = new EstadoPartida(true, Optional.of(rival), turno + " se rinde: gana " + rival);
        }
        return estado;
    }

    @Override
    public boolean deshacer() {
        return moverFoto(pasado, futuro);
    }

    @Override
    public boolean rehacer() {
        return moverFoto(futuro, pasado);
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

    @Override
    public void reiniciar() {
        tablero = inicial;
        turno = Color.BLANCO;
        estado = EstadoPartida.enCurso();
        pasado.clear();
        futuro.clear();
    }

    @Override
    public List<JugadaLegal> jugadasLegales() {
        if (estado.terminada()) {
            return List.of();
        }
        return generador.jugadasLegales(tablero, turno).stream()
                .map(JugadaLegal::de)
                .toList();
    }

    /** Antes de cada cambio (jugada, terminar o rendirse) se guarda cómo estaba; lo deshecho se descarta. */
    private void guardarFoto() {
        pasado.push(new Foto(tablero, turno, estado));
        futuro.clear();
    }

    /** Restaura la última foto de "desde" y guarda la actual en "hacia". Así funcionan deshacer y rehacer. */
    private boolean moverFoto(Deque<Foto> desde, Deque<Foto> hacia) {
        if (desde.isEmpty()) {
            return false;
        }
        hacia.push(new Foto(tablero, turno, estado));
        Foto foto = desde.pop();
        tablero = foto.tablero();
        turno = foto.turno();
        estado = foto.estado();
        return true;
    }

    /** Cómo estaba la partida en un momento: alcanza para volver a él. */
    private record Foto(Tablero tablero, Color turno, EstadoPartida estado) { }

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
