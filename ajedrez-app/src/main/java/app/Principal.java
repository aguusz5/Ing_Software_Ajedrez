package app;

import java.util.List;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import configuracion.TableroEstandar;
import consola.AdaptadorConsola;
import consola.DibujanteAscii;
import consola.TraductorDeNotacion;
import gui.GlifosDePieza;
import gui.VentanaSwing;
import juego.JuegoDeAjedrez;
import juego.Partida;
import modelo.Tablero;
import reglas.DetectorDeJaque;
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

/**
 * Raíz de composición: el único lugar que conoce todas las clases concretas, las crea con new
 * y las conecta. Sin argumentos se juega por consola; con --gui se abre la ventana Swing.
 */
public final class Principal {

    private Principal() {
    }

    public static void main(String[] args) {
        JuegoDeAjedrez juego = crearJuego();
        if (List.of(args).contains("--gui")) {
            SwingUtilities.invokeLater(() -> new VentanaSwing(juego, new GlifosDePieza()).mostrar());
        } else {
            new AdaptadorConsola(juego, new TraductorDeNotacion(), new DibujanteAscii(),
                    new Scanner(System.in), System.out).ejecutar();
        }
    }

    private static JuegoDeAjedrez crearJuego() {
        DetectorDeJaque detector = new DetectorDeJaque();
        ValidadorDeJugadas validador = new ValidadorDeJugadas(List.of(
                new ReglaPiezaPropia(),
                new ReglaNoDejarReyEnJaque(detector)));
        Tablero tablero = new TableroEstandar().crearTablero();
        return new Partida(tablero, validador, detector);
    }
}
