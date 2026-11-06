package juego;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import modelo.Posicion;
import modelo.Potenciador;
import modelo.Tablero;

/**
 * Cuándo y dónde aparecen los potenciadores: al empezar la partida, hasta llenar el máximo, y después uno cada
 * tantas jugadas, mientras no haya ya demasiados esperando. Cada uno es del catálogo elegido al azar, en una
 * casilla vacía también al azar. Con el catálogo vacío no aparece ninguno:
 * es el ajedrez de siempre. El Random llega por constructor: con una semilla fija, los tests son repetibles.
 */
public final class AparicionDePotenciadores {

    private final List<Potenciador> catalogo;
    private final int cadaCuantasJugadas;
    private final int maximoEnElTablero;
    private final Random azar;

    public AparicionDePotenciadores(List<Potenciador> catalogo, int cadaCuantasJugadas, int maximoEnElTablero,
                                    Random azar) {
        if (cadaCuantasJugadas < 1) {
            throw new IllegalArgumentException("cadaCuantasJugadas tiene que ser al menos 1");
        }
        this.catalogo = List.copyOf(catalogo);
        this.cadaCuantasJugadas = cadaCuantasJugadas;
        this.maximoEnElTablero = maximoEnElTablero;
        this.azar = azar;
    }

    /** El tablero con el que empieza la partida: el inicial con tantos potenciadores como el máximo (si entran). */
    public Tablero alEmpezar(Tablero inicial) {
        Tablero tablero = inicial;
        for (int puestos = 0; puestos < maximoEnElTablero; puestos++) {
            tablero = conUnoMas(tablero);
        }
        return tablero;
    }

    /**
     * El tablero después de la jugada número "jugadasHechas" (contando las de los dos colores): igual, o con un
     * potenciador nuevo si le toca aparecer y hay lugar.
     */
    public Tablero trasLaJugada(Tablero tablero, int jugadasHechas) {
        if (jugadasHechas % cadaCuantasJugadas != 0) {
            return tablero;
        }
        return conUnoMas(tablero);
    }

    /** Uno más en una casilla libre al azar; el mismo tablero si no hay catálogo, ya está el máximo o no hay lugar. */
    private Tablero conUnoMas(Tablero tablero) {
        if (catalogo.isEmpty() || tablero.cantidadDePotenciadores() >= maximoEnElTablero) {
            return tablero;
        }
        List<Posicion> libres = casillasLibres(tablero);
        if (libres.isEmpty()) {
            return tablero;
        }
        Posicion donde = libres.get(azar.nextInt(libres.size()));
        return tablero.conPotenciador(donde, catalogo.get(azar.nextInt(catalogo.size())));
    }

    /** Sin pieza y sin otro potenciador, en un orden fijo: así el azar es lo único que decide. */
    private static List<Posicion> casillasLibres(Tablero tablero) {
        List<Posicion> libres = new ArrayList<>();
        for (int columna = 0; columna < tablero.ancho(); columna++) {
            for (int fila = 0; fila < tablero.alto(); fila++) {
                Posicion posicion = new Posicion(columna, fila);
                if (tablero.piezaEn(posicion).isEmpty() && tablero.potenciadorEn(posicion).isEmpty()) {
                    libres.add(posicion);
                }
            }
        }
        return libres;
    }
}
