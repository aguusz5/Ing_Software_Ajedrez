package soporte;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import modelo.Posicion;
import movimiento.Jugada;

/**
 * Ayudas para que los tests se lean como una partida: casilla("e4") en vez de new Posicion(4, 3).
 */
public final class Casillas {

    private Casillas() {
    }

    public static Posicion casilla(String nombre) {
        return new Posicion(nombre.charAt(0) - 'a', Integer.parseInt(nombre.substring(1)) - 1);
    }

    public static Set<Posicion> casillas(String... nombres) {
        return Arrays.stream(nombres).map(Casillas::casilla).collect(Collectors.toSet());
    }

    /** Las casillas de destino de una lista de jugadas. */
    public static Set<Posicion> destinos(List<Jugada> jugadas) {
        return jugadas.stream().map(Jugada::hasta).collect(Collectors.toSet());
    }
}
