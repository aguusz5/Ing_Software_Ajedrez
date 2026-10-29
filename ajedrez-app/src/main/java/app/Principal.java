package app;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import configuracion.TableroEstandar;
import consola.AdaptadorConsola;
import consola.DibujanteAscii;
import consola.TraductorDeNotacion;
import gui.GlifosDePieza;
import gui.VentanaSwing;
import ia.AsesorAleatorio;
import ia.AsesorMinimax;
import ia.Computadora;
import juego.JuegoDeAjedrez;
import juego.Partida;
import modelo.Tablero;
import reglas.DetectorDeJaque;
import reglas.GeneradorDeJugadasLegales;
import reglas.ReglaEnroqueSeguro;
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

/**
 * Raíz de composición: el único lugar que conoce todas las clases concretas, las crea con new
 * y las conecta. Sin argumentos se juega por consola; con --gui se abre la ventana Swing.
 * Con --ia, las negras las juega la computadora.
 */
public final class Principal {

    /** Con 3 jugadas hacia adelante el minimax ve los mates en una del rival y tarda menos de un segundo. */
    private static final int PROFUNDIDAD_MINIMAX = 3;
    /** El minimax siempre devuelve una jugada legal: con un intento alcanza. */
    private static final int INTENTOS = 1;

    private Principal() {
    }

    public static void main(String[] args) {
        List<String> opciones = List.of(args);
        DetectorDeJaque detector = new DetectorDeJaque();
        ValidadorDeJugadas validador = new ValidadorDeJugadas(List.of(
                new ReglaPiezaPropia(),
                new ReglaEnroqueSeguro(detector),
                new ReglaNoDejarReyEnJaque(detector)));
        GeneradorDeJugadasLegales generador = new GeneradorDeJugadasLegales(validador);
        Tablero tablero = new TableroEstandar().crearTablero();
        JuegoDeAjedrez juego = new Partida(tablero, validador, detector, generador);
        Optional<Computadora> computadora = opciones.contains("--ia")
                ? Optional.of(crearComputadora(generador, detector))
                : Optional.empty();
        if (opciones.contains("--gui")) {
            SwingUtilities.invokeLater(() -> new VentanaSwing(juego, new GlifosDePieza(), computadora).mostrar());
        } else {
            new AdaptadorConsola(juego, new TraductorDeNotacion(), new DibujanteAscii(),
                    new Scanner(System.in), System.out, computadora).ejecutar();
        }
    }

    /** Juega el minimax, con el aleatorio de respaldo. Todo corre en la compu, sin internet. */
    private static Computadora crearComputadora(GeneradorDeJugadasLegales generador, DetectorDeJaque detector) {
        return new Computadora(new AsesorMinimax(generador, detector, PROFUNDIDAD_MINIMAX, new Random()),
                new AsesorAleatorio(new Random()), INTENTOS);
    }
}
