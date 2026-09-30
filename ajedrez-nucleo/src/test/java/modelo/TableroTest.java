package modelo;

import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class TableroTest {

    private static final Pieza TORRE_BLANCA = new Pieza(Color.BLANCO, torre());

    @Test
    void tableroNuevo_cualquierCasilla_estaVacia() {
        // Arrange
        Tablero tablero = new Tablero(8, 8);

        // Act & Assert
        assertTrue(tablero.piezaEn(casilla("a1")).isEmpty());
        assertTrue(tablero.piezaEn(casilla("h8")).isEmpty());
    }

    @Test
    void estaDentro_tableroDe10x8_respetaAnchoYAlto() {
        // Arrange
        Tablero tablero = new Tablero(10, 8);

        // Act & Assert
        assertTrue(tablero.estaDentro(new Posicion(0, 0)));
        assertTrue(tablero.estaDentro(new Posicion(9, 7)));
        assertFalse(tablero.estaDentro(new Posicion(10, 0)));
        assertFalse(tablero.estaDentro(new Posicion(0, 8)));
        assertFalse(tablero.estaDentro(new Posicion(-1, 3)));
    }

    @Test
    void con_ponerUnaPieza_devuelveTableroNuevoSinModificarElOriginal() {
        // Arrange
        Tablero original = new Tablero(8, 8);

        // Act
        Tablero nuevo = original.con(casilla("a1"), TORRE_BLANCA);

        // Assert
        assertEquals(Optional.of(TORRE_BLANCA), nuevo.piezaEn(casilla("a1")));
        assertTrue(original.piezaEn(casilla("a1")).isEmpty());
    }

    @Test
    void sin_sacarUnaPieza_devuelveTableroNuevoSinModificarElOriginal() {
        // Arrange
        Tablero original = new Tablero(8, 8).con(casilla("a1"), TORRE_BLANCA);

        // Act
        Tablero nuevo = original.sin(casilla("a1"));

        // Assert
        assertTrue(nuevo.piezaEn(casilla("a1")).isEmpty());
        assertEquals(Optional.of(TORRE_BLANCA), original.piezaEn(casilla("a1")));
    }

    @Test
    void con_casillaFueraDelTablero_lanzaExcepcion() {
        // Arrange
        Tablero tablero = new Tablero(8, 8);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tablero.con(new Posicion(8, 0), TORRE_BLANCA));
    }

    @Test
    void buscarRey_conAmbosReyes_devuelveLaCasillaDelReyPedido() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("e8"), new Pieza(Color.NEGRO, rey()));

        // Act & Assert
        assertEquals(casilla("e1"), tablero.buscarRey(Color.BLANCO));
        assertEquals(casilla("e8"), tablero.buscarRey(Color.NEGRO));
    }

    @Test
    void buscarRey_sinReyDeEseColor_lanzaExcepcion() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("e1"), new Pieza(Color.BLANCO, rey()));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> tablero.buscarRey(Color.NEGRO));
    }
}
