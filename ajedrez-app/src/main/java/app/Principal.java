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
import gui.Dificultad;
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
 * y las conecta. Con --gui se abre la ventana, que arranca en el menú de inicio. Sin argumentos se
 * juega por consola; ahí, con --ia, las negras las juega la computadora en la dificultad más alta.
 */
public final class Principal {

    /** Con 2 jugadas hacia adelante no regala piezas; con 3 además ve los mates en una del rival. */
    private static final int PROFUNDIDAD_MEDIA = 2;
    private static final int PROFUNDIDAD_DIFICIL = 3;
    /** Los asesores de la computadora siempre devuelven una jugada legal: con un intento alcanza. */
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
        List<Dificultad> dificultades = crearDificultades(generador, detector);
        if (opciones.contains("--gui")) {
            SwingUtilities.invokeLater(() -> new VentanaSwing(juego, new GlifosDePieza(), dificultades).mostrar());
        } else {
            Optional<Computadora> computadora = opciones.contains("--ia")
                    ? Optional.of(dificultades.get(dificultades.size() - 1).computadora())
                    : Optional.empty();
            new AdaptadorConsola(juego, new TraductorDeNotacion(), new DibujanteAscii(),
                    new Scanner(System.in), System.out, computadora).ejecutar();
        }
    }

    /** Las dificultades del menú, de la más fácil a la más difícil. Todo corre en la compu, sin internet. */
    private static List<Dificultad> crearDificultades(GeneradorDeJugadasLegales generador, DetectorDeJaque detector) {
        return List.of(
                new Dificultad("Fácil", "juega cualquier jugada legal",
                        new Computadora(new AsesorAleatorio(new Random()), new AsesorAleatorio(new Random()), INTENTOS)),
                new Dificultad("Medio", "mira 2 jugadas hacia adelante",
                        new Computadora(new AsesorMinimax(generador, detector, PROFUNDIDAD_MEDIA, new Random()),
                                new AsesorAleatorio(new Random()), INTENTOS)),
                new Dificultad("Difícil", "mira 3 jugadas hacia adelante",
                        new Computadora(new AsesorMinimax(generador, detector, PROFUNDIDAD_DIFICIL, new Random()),
                                new AsesorAleatorio(new Random()), INTENTOS)));
    }
}
