package consola;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

/**
 * Dibuja el tablero como texto. Cada pieza se muestra con la inicial de su nombre:
 * mayúscula para las blancas y minúscula para las negras (P, T, C, A, D, R).
 * Una pieza nueva se dibuja sola, con su inicial, sin tocar esta clase.
 */
public final class DibujanteAscii {

    private static final char CASILLA_VACIA = '.';

    public String dibujar(Tablero tablero) {
        StringBuilder texto = new StringBuilder();
        for (int fila = tablero.alto() - 1; fila >= 0; fila--) {
            texto.append(String.format("%2d ", fila + 1));
            for (int columna = 0; columna < tablero.ancho(); columna++) {
                texto.append(' ').append(simboloEn(tablero, new Posicion(columna, fila)));
            }
            texto.append('\n');
        }
        texto.append("   ");
        for (int columna = 0; columna < tablero.ancho(); columna++) {
            texto.append(' ').append((char) ('a' + columna));
        }
        return texto.append('\n').toString();
    }

    private char simboloEn(Tablero tablero, Posicion posicion) {
        return tablero.piezaEn(posicion).map(this::inicial).orElse(CASILLA_VACIA);
    }

    private char inicial(Pieza pieza) {
        char inicial = pieza.tipo().nombre().charAt(0);
        return pieza.color() == Color.BLANCO ? Character.toUpperCase(inicial) : Character.toLowerCase(inicial);
    }
}
