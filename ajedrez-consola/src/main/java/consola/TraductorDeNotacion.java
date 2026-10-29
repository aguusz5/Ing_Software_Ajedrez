package consola;

import modelo.Posicion;
import modelo.Tablero;

/**
 * Traduce lo que escribe el usuario ("e2") a una Posicion del núcleo (columna 4, fila 1), y al revés.
 * No asume 8x8: qué letras y números valen depende del tablero que recibe.
 */
public final class TraductorDeNotacion {

    public Posicion leer(String texto, Tablero tablero) {
        String casilla = texto.trim().toLowerCase();
        if (!casilla.matches("[a-z][0-9]{1,2}")) {
            throw new IllegalArgumentException("Casilla inválida: '" + texto + "' (ejemplo de casilla: e2)");
        }
        int columna = casilla.charAt(0) - 'a';
        int fila = Integer.parseInt(casilla.substring(1)) - 1;
        Posicion posicion = new Posicion(columna, fila);
        if (!tablero.estaDentro(posicion)) {
            throw new IllegalArgumentException("La casilla '" + texto + "' está fuera del tablero");
        }
        return posicion;
    }

    /** Para mostrar la jugada de la computadora: la columna 4, fila 1 se escribe "e2". */
    public String escribir(Posicion posicion) {
        return (char) ('a' + posicion.columna()) + String.valueOf(posicion.fila() + 1);
    }
}
