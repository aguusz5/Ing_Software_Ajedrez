package consola;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import modelo.Posicion;
import modelo.Tablero;

class TraductorDeNotacionTest {

    private final TraductorDeNotacion traductor = new TraductorDeNotacion();
    private final Tablero tablero8x8 = new Tablero(8, 8);

    @Test
    void leer_e2_devuelveColumna4Fila1() {
        // Arrange
        String texto = "e2";

        // Act
        Posicion posicion = traductor.leer(texto, tablero8x8);

        // Assert
        assertEquals(new Posicion(4, 1), posicion);
    }

    @Test
    void leer_conMayusculasYEspacios_igualLaEntiende() {
        // Arrange
        String texto = "  H8 ";

        // Act
        Posicion posicion = traductor.leer(texto, tablero8x8);

        // Assert
        assertEquals(new Posicion(7, 7), posicion);
    }

    @ParameterizedTest
    @ValueSource(strings = {"i1", "a9", "a0"})
    void leer_casillaFueraDelTablero_lanzaExcepcion(String casilla) {
        // Arrange: cada casilla de @ValueSource está bien escrita pero cae fuera del tablero de 8x8.

        // Act
        RuntimeException error = excepcionDe(() -> traductor.leer(casilla, tablero8x8));

        // Assert
        assertInstanceOf(IllegalArgumentException.class, error);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hola", "22", "e", ""})
    void leer_textoQueNoEsUnaCasilla_lanzaExcepcion(String texto) {
        // Arrange: cada texto de @ValueSource no tiene la forma de una casilla.

        // Act
        RuntimeException error = excepcionDe(() -> traductor.leer(texto, tablero8x8));

        // Assert
        assertInstanceOf(IllegalArgumentException.class, error);
    }

    /** La excepción que lanzó la acción, o null si no lanzó ninguna: así Act y Assert quedan separados. */
    private static RuntimeException excepcionDe(Runnable accion) {
        try {
            accion.run();
            return null;
        } catch (RuntimeException excepcion) {
            return excepcion;
        }
    }

    @Test
    void leer_enTableroDe10x10_aceptaLaColumnaJYLaFila10() {
        // Arrange
        Tablero tablero10x10 = new Tablero(10, 10);
        String texto = "j10";

        // Act
        Posicion posicion = traductor.leer(texto, tablero10x10);

        // Assert
        assertEquals(new Posicion(9, 9), posicion);
    }

    @Test
    void escribir_columna4Fila1_devuelveE2() {
        // Arrange
        Posicion posicion = new Posicion(4, 1);

        // Act
        String texto = traductor.escribir(posicion);

        // Assert
        assertEquals("e2", texto);
    }

    @Test
    void escribir_columna9Fila9_devuelveJ10() {
        // Arrange
        Posicion posicion = new Posicion(9, 9);

        // Act
        String texto = traductor.escribir(posicion);

        // Assert
        assertEquals("j10", texto);
    }
}
