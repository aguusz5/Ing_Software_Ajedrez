package modelo;

import java.util.List;
import java.util.stream.Stream;

/**
 * Un desplazamiento en el tablero: dx columnas y dy filas.
 * Las constantes agrupan las direcciones con las que se arman las piezas estándar.
 */
public record Direccion(int dx, int dy) {

    public static final List<Direccion> ORTOGONALES = List.of(
            new Direccion(1, 0), new Direccion(-1, 0), new Direccion(0, 1), new Direccion(0, -1));

    public static final List<Direccion> DIAGONALES = List.of(
            new Direccion(1, 1), new Direccion(1, -1), new Direccion(-1, 1), new Direccion(-1, -1));

    public static final List<Direccion> TODAS =
            Stream.concat(ORTOGONALES.stream(), DIAGONALES.stream()).toList();

    public static final List<Direccion> SALTOS_DE_CABALLO = List.of(
            new Direccion(1, 2), new Direccion(2, 1), new Direccion(2, -1), new Direccion(1, -2),
            new Direccion(-1, -2), new Direccion(-2, -1), new Direccion(-2, 1), new Direccion(-1, 2));
}
