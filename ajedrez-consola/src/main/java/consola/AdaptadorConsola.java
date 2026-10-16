package consola;

import java.io.PrintStream;
import java.util.Scanner;

import juego.JuegoDeAjedrez;
import juego.ResultadoJugada;
import modelo.Color;
import modelo.Posicion;

/**
 * Adaptador de consola: lee jugadas del teclado, se las pasa al núcleo por el puerto
 * JuegoDeAjedrez y muestra la respuesta. No contiene ninguna regla del ajedrez.
 * Recibe la entrada y la salida por constructor, así en los tests se usa un texto armado.
 */
public final class AdaptadorConsola {

    private static final String COMANDO_SALIR = "salir";
    private static final String COMANDO_TERMINAR = "terminar";
    private static final String COMANDO_RENDIRSE = "rendirse";

    private final JuegoDeAjedrez juego;
    private final TraductorDeNotacion traductor;
    private final DibujanteAscii dibujante;
    private final Scanner entrada;
    private final PrintStream salida;

    public AdaptadorConsola(JuegoDeAjedrez juego, TraductorDeNotacion traductor, DibujanteAscii dibujante,
                            Scanner entrada, PrintStream salida) {
        this.juego = juego;
        this.traductor = traductor;
        this.dibujante = dibujante;
        this.entrada = entrada;
        this.salida = salida;
    }

    public void ejecutar() {
        dibujar();
        while (!juego.estado().terminada()) {
            // En jaque se ofrece rendirse; el comando igual funciona en cualquier momento.
            String rendirse = juego.estaEnJaque(juego.turnoActual()) ? "'" + COMANDO_RENDIRSE + "', " : "";
            salida.print("Turno de " + juego.turnoActual() + " (ejemplo: e2 e4, " + rendirse
                    + "'" + COMANDO_TERMINAR + "' o '" + COMANDO_SALIR + "'): ");
            if (!entrada.hasNextLine()) {
                return;
            }
            String linea = entrada.nextLine().trim();
            if (linea.equalsIgnoreCase(COMANDO_SALIR)) {
                return;
            }
            if (linea.equalsIgnoreCase(COMANDO_TERMINAR)) {
                juego.terminar();
            } else if (linea.equalsIgnoreCase(COMANDO_RENDIRSE)) {
                juego.rendirse();
            } else {
                procesar(linea);
            }
        }
        anunciarFin();
    }

    private void anunciarFin() {
        salida.println();
        salida.println("===== FIN DE LA PARTIDA =====");
        salida.println(juego.estado().ganador().map(color -> "Ganador: " + color).orElse("Resultado: tablas"));
        salida.println("Fin de la partida. " + juego.estado().motivo());
    }

    private void dibujar() {
        salida.println(dibujante.dibujar(juego.tablero()));
        salida.println("Puntos: BLANCO " + juego.puntos(Color.BLANCO)
                + " · NEGRO " + juego.puntos(Color.NEGRO));
    }

    private void procesar(String linea) {
        String[] casillas = linea.split("\\s+");
        if (casillas.length != 2) {
            salida.println("Escribí la casilla de origen y la de destino separadas por un espacio, por ejemplo: e2 e4");
            return;
        }
        Posicion desde;
        Posicion hasta;
        try {
            desde = traductor.leer(casillas[0], juego.tablero());
            hasta = traductor.leer(casillas[1], juego.tablero());
        } catch (IllegalArgumentException e) {
            salida.println(e.getMessage());
            return;
        }
        mostrar(juego.jugar(desde, hasta));
    }

    private void mostrar(ResultadoJugada resultado) {
        if (!resultado.valida()) {
            salida.println("Jugada inválida: " + resultado.motivo());
            return;
        }
        dibujar();
        resultado.colorEnJaque().ifPresent(color -> salida.println("¡Jaque al rey " + color + "!"));
    }
}
