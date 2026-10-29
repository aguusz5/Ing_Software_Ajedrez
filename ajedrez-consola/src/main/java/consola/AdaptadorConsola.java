package consola;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;

import ia.Computadora;
import ia.JugadaDeLaComputadora;
import juego.JuegoDeAjedrez;
import juego.JugadaLegal;
import juego.ResultadoJugada;
import modelo.Color;
import modelo.Posicion;

/**
 * Adaptador de consola: lee jugadas del teclado, se las pasa al núcleo por el puerto
 * JuegoDeAjedrez y muestra la respuesta. No contiene ninguna regla del ajedrez.
 * Recibe la entrada y la salida por constructor, así en los tests se usa un texto armado.
 * Si recibe una Computadora, las negras las juega ella y se avisa quién eligió cada jugada.
 */
public final class AdaptadorConsola {

    private static final Color COLOR_DE_LA_COMPUTADORA = Color.NEGRO;

    private static final String COMANDO_SALIR = "salir";
    private static final String COMANDO_TERMINAR = "terminar";
    private static final String COMANDO_RENDIRSE = "rendirse";
    private static final String COMANDO_DESHACER = "deshacer";
    private static final String COMANDO_REHACER = "rehacer";
    private static final String COMANDO_PUNTOS = "puntos";

    private final JuegoDeAjedrez juego;
    private final TraductorDeNotacion traductor;
    private final DibujanteAscii dibujante;
    private final Scanner entrada;
    private final PrintStream salida;
    private final Optional<Computadora> computadora;

    /** Sin computadora se juega uno contra uno. */
    public AdaptadorConsola(JuegoDeAjedrez juego, TraductorDeNotacion traductor, DibujanteAscii dibujante,
                            Scanner entrada, PrintStream salida, Optional<Computadora> computadora) {
        this.juego = juego;
        this.traductor = traductor;
        this.dibujante = dibujante;
        this.entrada = entrada;
        this.salida = salida;
        this.computadora = computadora;
    }

    public void ejecutar() {
        dibujar();
        while (!juego.estado().terminada()) {
            if (leTocaALaComputadora()) {
                if (!jugarComputadora()) {
                    return;
                }
                continue;
            }
            // En jaque se ofrece rendirse; el comando igual funciona en cualquier momento.
            String rendirse = juego.estaEnJaque(juego.turnoActual()) ? "'" + COMANDO_RENDIRSE + "', " : "";
            salida.print("Turno de " + juego.turnoActual() + " (ejemplo: e2 e4, " + rendirse
                    + "'" + COMANDO_DESHACER + "', '" + COMANDO_REHACER + "', '" + COMANDO_PUNTOS + "', '"
                    + COMANDO_TERMINAR + "' o '" + COMANDO_SALIR + "'): ");
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
            } else if (linea.equalsIgnoreCase(COMANDO_DESHACER)) {
                redibujarSi(deshacerTurno(), "No hay nada para deshacer");
            } else if (linea.equalsIgnoreCase(COMANDO_REHACER)) {
                redibujarSi(rehacerTurno(), "No hay nada para rehacer");
            } else if (linea.equalsIgnoreCase(COMANDO_PUNTOS)) {
                mostrarPuntos();
            } else {
                procesar(linea);
            }
        }
        anunciarFin();
    }

    private boolean leTocaALaComputadora() {
        return computadora.isPresent() && !juego.estado().terminada()
                && juego.turnoActual() == COLOR_DE_LA_COMPUTADORA;
    }

    /**
     * Le pide la jugada a la computadora y la hace por el puerto, como cualquier otra.
     * Devuelve false si el núcleo la rechazó, para no quedar pidiéndole jugadas para siempre.
     */
    private boolean jugarComputadora() {
        salida.println("Piensa la computadora (" + computadora.get().asesor() + ")...");
        Optional<JugadaDeLaComputadora> elegida = computadora.get().elegirJugada(juego);
        if (elegida.isEmpty()) {
            return false;
        }
        JugadaLegal jugada = elegida.get().jugada();
        ResultadoJugada resultado = jugada.promocion()
                .map(tipo -> juego.jugar(jugada.desde(), jugada.hasta(), tipo))
                .orElseGet(() -> juego.jugar(jugada.desde(), jugada.hasta()));
        salida.println("La computadora jugó " + traductor.escribir(jugada.desde()) + " "
                + traductor.escribir(jugada.hasta()) + " (eligió " + elegida.get().asesor()
                + (elegida.get().deRespaldo() ? ", de respaldo" : "") + ")");
        mostrar(resultado);
        return resultado.valida();
    }

    /** Contra la computadora, deshacer vuelve hasta que le toque al jugador: su jugada y la de la computadora. */
    private boolean deshacerTurno() {
        boolean cambio = juego.deshacer();
        if (cambio && leTocaALaComputadora()) {
            juego.deshacer();
        }
        return cambio;
    }

    private boolean rehacerTurno() {
        boolean cambio = juego.rehacer();
        if (cambio && leTocaALaComputadora()) {
            juego.rehacer();
        }
        return cambio;
    }

    private void anunciarFin() {
        salida.println();
        salida.println("===== FIN DE LA PARTIDA =====");
        salida.println(juego.estado().ganador().map(color -> "Ganador: " + color).orElse("Resultado: tablas"));
        salida.println("Fin de la partida. " + juego.estado().motivo());
    }

    private void dibujar() {
        salida.println(dibujante.dibujar(juego.tablero()));
        mostrarPuntos();
    }

    private void mostrarPuntos() {
        salida.println("Puntos: BLANCO " + juego.puntos(Color.BLANCO)
                + " · NEGRO " + juego.puntos(Color.NEGRO));
    }

    /** Después de deshacer o rehacer: si cambió algo se redibuja; si no, se avisa. */
    private void redibujarSi(boolean cambio, String sinCambios) {
        if (cambio) {
            dibujar();
        } else {
            salida.println(sinCambios);
        }
    }

    private void procesar(String linea) {
        // "e2 e4", o "e7 e8 caballo" para coronar en algo que no sea dama. El enroque es "e1 g1".
        String[] casillas = linea.split("\\s+");
        if (casillas.length != 2 && casillas.length != 3) {
            salida.println("Escribí la casilla de origen y la de destino separadas por un espacio, por ejemplo: e2 e4"
                    + " (para coronar en otra pieza, agregala al final: e7 e8 caballo;"
                    + " para enrocar, mové el rey dos casillas hacia la torre: e1 g1)");
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
        mostrar(casillas.length == 3 ? juego.jugar(desde, hasta, casillas[2]) : juego.jugar(desde, hasta));
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
