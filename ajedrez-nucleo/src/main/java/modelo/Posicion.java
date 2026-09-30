package modelo;

/**
 * Una casilla del tablero. La columna 0 es la "a" y la fila 0 es la "1".
 */
public record Posicion(int columna, int fila) {

    public Posicion desplazar(Direccion direccion) {
        return new Posicion(columna + direccion.dx(), fila + direccion.dy());
    }
}
