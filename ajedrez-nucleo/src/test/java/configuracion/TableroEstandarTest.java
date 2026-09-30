package configuracion;

import static configuracion.TiposDePiezaEstandar.caballo;
import static configuracion.TiposDePiezaEstandar.dama;
import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

class TableroEstandarTest {

    private final Tablero tablero = new TableroEstandar().crearTablero();

    @Test
    void crearTablero_esDe8x8Con32Piezas() {
        // Act
        int piezas = 0;
        for (int columna = 0; columna < tablero.ancho(); columna++) {
            for (int fila = 0; fila < tablero.alto(); fila++) {
                if (tablero.piezaEn(new Posicion(columna, fila)).isPresent()) {
                    piezas++;
                }
            }
        }

        // Assert
        assertEquals(8, tablero.ancho());
        assertEquals(8, tablero.alto());
        assertEquals(32, piezas);
    }

    @Test
    void crearTablero_piezasMayores_estanEnSuCasillaInicial() {
        // Act & Assert
        assertEquals(Optional.of(new Pieza(Color.BLANCO, rey())), tablero.piezaEn(casilla("e1")));
        assertEquals(Optional.of(new Pieza(Color.BLANCO, dama())), tablero.piezaEn(casilla("d1")));
        assertEquals(Optional.of(new Pieza(Color.BLANCO, caballo())), tablero.piezaEn(casilla("g1")));
        assertEquals(Optional.of(new Pieza(Color.NEGRO, rey())), tablero.piezaEn(casilla("e8")));
        assertEquals(Optional.of(new Pieza(Color.NEGRO, dama())), tablero.piezaEn(casilla("d8")));
        assertEquals(Optional.of(new Pieza(Color.NEGRO, torre())), tablero.piezaEn(casilla("h8")));
    }

    @Test
    void crearTablero_peones_ocupanLasFilas2y7YElCentroEstaVacio() {
        // Act & Assert
        for (char columna = 'a'; columna <= 'h'; columna++) {
            assertEquals(Optional.of(new Pieza(Color.BLANCO, peon())), tablero.piezaEn(casilla(columna + "2")));
            assertEquals(Optional.of(new Pieza(Color.NEGRO, peon())), tablero.piezaEn(casilla(columna + "7")));
            for (int fila = 3; fila <= 6; fila++) {
                assertTrue(tablero.piezaEn(casilla("" + columna + fila)).isEmpty());
            }
        }
    }
}
