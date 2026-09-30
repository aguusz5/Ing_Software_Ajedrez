package consola;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        // Act & Assert
        assertEquals(new Posicion(4, 1), traductor.leer("e2", tablero8x8));
    }

    @Test
    void leer_conMayusculasYEspacios_igualLaEntiende() {
        // Act & Assert
        assertEquals(new Posicion(7, 7), traductor.leer("  H8 ", tablero8x8));
    }

    @ParameterizedTest
    @ValueSource(strings = {"i1", "a9", "a0"})
    void leer_casillaFueraDelTablero_lanzaExcepcion(String casilla) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> traductor.leer(casilla, tablero8x8));
    }

    @ParameterizedTest
    @ValueSource(strings = {"hola", "22", "e", ""})
    void leer_textoQueNoEsUnaCasilla_lanzaExcepcion(String texto) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> traductor.leer(texto, tablero8x8));
    }

    @Test
    void leer_enTableroDe10x10_aceptaLaColumnaJYLaFila10() {
        // Act & Assert
        assertEquals(new Posicion(9, 9), traductor.leer("j10", new Tablero(10, 10)));
    }
}
