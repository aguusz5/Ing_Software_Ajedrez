package ia;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import juego.JugadaLegal;
import modelo.Color;
import modelo.Tablero;
import movimiento.Jugada;
import reglas.DetectorDeJaque;
import reglas.GeneradorDeJugadasLegales;

/**
 * Mira "profundidad" jugadas hacia adelante (minimax, en su forma negamax) y elige la que deja mejor parado al
 * que mueve, suponiendo que el rival también juega lo mejor que puede. Cada posición se evalúa con los puntos:
 * los propios menos los del rival. El mate vale más que cualquier material; el ahogado, cero.
 * Como el tablero es inmutable, prueba cada jugada con aplicarEn sin tocar la partida real.
 * Entre jugadas igual de buenas elige al azar, para no jugar siempre la misma partida.
 */
public final class AsesorMinimax implements AsesorDeJugadas {

    /** Más que todo el material junto: un mate siempre es mejor que cualquier captura. */
    private static final int MATE = 10_000;

    private final GeneradorDeJugadasLegales generador;
    private final DetectorDeJaque detector;
    private final int profundidad;
    private final Random desempate;

    public AsesorMinimax(GeneradorDeJugadasLegales generador, DetectorDeJaque detector, int profundidad,
                         Random desempate) {
        if (profundidad < 1) {
            throw new IllegalArgumentException("La profundidad tiene que ser al menos 1");
        }
        this.generador = generador;
        this.detector = detector;
        this.profundidad = profundidad;
        this.desempate = desempate;
    }

    @Override
    public String nombre() {
        return "Minimax";
    }

    @Override
    public Optional<JugadaLegal> sugerirJugada(Tablero tablero, Color turno, List<JugadaLegal> legales) {
        List<JugadaLegal> mejores = new ArrayList<>();
        int mejorValor = Integer.MIN_VALUE;
        for (Jugada jugada : generador.jugadasLegales(tablero, turno)) {
            JugadaLegal comoDato = JugadaLegal.de(jugada);
            if (!legales.contains(comoDato)) {
                continue;
            }
            // Lo que gana el rival desde la posición siguiente es lo que pierde el que mueve.
            int valor = -valorPara(jugada.aplicarEn(tablero), turno.contrario(), profundidad - 1);
            if (valor > mejorValor) {
                mejorValor = valor;
                mejores.clear();
            }
            if (valor == mejorValor) {
                mejores.add(comoDato);
            }
        }
        if (mejores.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mejores.get(desempate.nextInt(mejores.size())));
    }

    /** Lo mejor que puede conseguir "turno" en este tablero mirando "restantes" jugadas más. */
    private int valorPara(Tablero tablero, Color turno, int restantes) {
        if (restantes == 0) {
            return tablero.puntosDe(turno) - tablero.puntosDe(turno.contrario());
        }
        List<Jugada> jugadas = generador.jugadasLegales(tablero, turno);
        if (jugadas.isEmpty()) {
            return detector.estaEnJaque(tablero, turno) ? -MATE : 0;
        }
        int mejor = Integer.MIN_VALUE;
        for (Jugada jugada : jugadas) {
            mejor = Math.max(mejor, -valorPara(jugada.aplicarEn(tablero), turno.contrario(), restantes - 1));
        }
        return mejor;
    }
}
