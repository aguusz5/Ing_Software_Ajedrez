package app;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import configuracion.TableroEstandar;
import consola.AdaptadorConsola;
import consola.DibujanteAscii;
import consola.TraductorDeNotacion;
import gui.Dificultad;
import gui.GlifosDePieza;
import gui.PersonajesDeStarWars;
import gui.Tema;
import gui.TemaClasico;
import gui.TemaStarWars;
import gui.VentanaSwing;
import ia.AsesorAleatorio;
import ia.AsesorMinimax;
import ia.Computadora;
import ia.ContraLaComputadora;
import juego.JuegoDeAjedrez;
import juego.Partida;
import modelo.Color;
import modelo.Tablero;
import reglas.DetectorDeJaque;
import reglas.GeneradorDeJugadasLegales;
import reglas.ReglaEnroqueSeguro;
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

/**
 * Raíz de composición: el único lugar que conoce todas las clases concretas, las crea con new
 * y las conecta. Con --gui se abre la ventana, que arranca en el menú de inicio con el tema Star Wars
 * (el clásico se elige en el menú). Sin argumentos se juega por consola; ahí, con --ia, las negras las
 * juega la computadora en la dificultad más alta.
 */
public final class Principal {

    /** Con 2 jugadas hacia adelante no regala piezas; con 3 además ve los mates en una del rival. */
    private static final int PROFUNDIDAD_MEDIA = 2;
    private static final int PROFUNDIDAD_DIFICIL = 3;
    /** Los asesores de la computadora siempre devuelven una jugada legal: con un intento alcanza. */
    private static final int INTENTOS = 1;
    /** El póster de fondo del tema Star Wars, en los recursos del módulo de la ventana (paquete gui). */
    private static final String FONDO_STAR_WARS = "fondo-star-wars.jpg";

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
        List<Dificultad> dificultades = crearDificultades(juego, generador, detector);
        if (opciones.contains("--gui")) {
            GlifosDePieza glifos = new GlifosDePieza();
            List<Tema> temas = List.of(
                    new TemaStarWars(glifos, new PersonajesDeStarWars(), cargarImagen(FONDO_STAR_WARS)),
                    new TemaClasico(glifos));
            SwingUtilities.invokeLater(() -> new VentanaSwing(juego, temas, dificultades).mostrar());
        } else {
            // En la consola la persona juega con blancas y la computadora, si se pidió, con negras.
            Optional<ContraLaComputadora> rival = opciones.contains("--ia")
                    ? Optional.of(dificultades.get(dificultades.size() - 1).paraJugarCon(Color.BLANCO))
                    : Optional.empty();
            new AdaptadorConsola(juego, new TraductorDeNotacion(), new DibujanteAscii(),
                    new Scanner(System.in), System.out, rival).ejecutar();
        }
    }

    /** Una imagen de los recursos de la ventana. Si no está o no se puede leer, vacía: el tema se arregla sin ella. */
    private static Optional<BufferedImage> cargarImagen(String nombre) {
        try (InputStream entrada = TemaStarWars.class.getResourceAsStream(nombre)) {
            return entrada == null ? Optional.empty() : Optional.ofNullable(ImageIO.read(entrada));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /** Las dificultades del menú, de la más fácil a la más difícil. Todo corre en la compu, sin internet. */
    private static List<Dificultad> crearDificultades(JuegoDeAjedrez juego, GeneradorDeJugadasLegales generador,
                                                      DetectorDeJaque detector) {
        return List.of(
                dificultad("Fácil", "juega cualquier jugada legal", juego,
                        new Computadora(new AsesorAleatorio(new Random()), new AsesorAleatorio(new Random()), INTENTOS)),
                dificultad("Medio", "mira 2 jugadas hacia adelante", juego,
                        new Computadora(new AsesorMinimax(generador, detector, PROFUNDIDAD_MEDIA, new Random()),
                                new AsesorAleatorio(new Random()), INTENTOS)),
                dificultad("Difícil", "mira 3 jugadas hacia adelante", juego,
                        new Computadora(new AsesorMinimax(generador, detector, PROFUNDIDAD_DIFICIL, new Random()),
                                new AsesorAleatorio(new Random()), INTENTOS)));
    }

    /**
     * Una dificultad con sus dos partidas contra la computadora: si la persona elige blancas, la computadora juega
     * con negras, y al revés. Se arman acá para que la ventana no tenga que hacer new al elegir el color.
     */
    private static Dificultad dificultad(String nombre, String descripcion, JuegoDeAjedrez juego,
                                         Computadora computadora) {
        return new Dificultad(nombre, descripcion,
                new ContraLaComputadora(juego, computadora, Color.NEGRO),
                new ContraLaComputadora(juego, computadora, Color.BLANCO));
    }
}
