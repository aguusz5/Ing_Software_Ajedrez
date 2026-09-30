package configuracion;

import java.util.List;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import modelo.TipoDePieza;

/**
 * Arma la posición inicial del ajedrez clásico. Es el único lugar del núcleo que conoce
 * el tamaño 8x8: todo lo demás le pregunta al tablero su ancho y su alto.
 */
public final class TableroEstandar {

    private static final int LADO = 8;

    public Tablero crearTablero() {
        List<TipoDePieza> filaDeAtras = List.of(
                TiposDePiezaEstandar.torre(), TiposDePiezaEstandar.caballo(), TiposDePiezaEstandar.alfil(),
                TiposDePiezaEstandar.dama(), TiposDePiezaEstandar.rey(),
                TiposDePiezaEstandar.alfil(), TiposDePiezaEstandar.caballo(), TiposDePiezaEstandar.torre());

        Tablero tablero = new Tablero(LADO, LADO);
        for (int columna = 0; columna < LADO; columna++) {
            tablero = tablero
                    .con(new Posicion(columna, 0), new Pieza(Color.BLANCO, filaDeAtras.get(columna)))
                    .con(new Posicion(columna, 1), new Pieza(Color.BLANCO, TiposDePiezaEstandar.peon()))
                    .con(new Posicion(columna, LADO - 2), new Pieza(Color.NEGRO, TiposDePiezaEstandar.peon()))
                    .con(new Posicion(columna, LADO - 1), new Pieza(Color.NEGRO, filaDeAtras.get(columna)));
        }
        return tablero;
    }
}
