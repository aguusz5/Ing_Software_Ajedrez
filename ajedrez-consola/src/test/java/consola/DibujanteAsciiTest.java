package consola;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import configuracion.TableroEstandar;
import modelo.Tablero;

class DibujanteAsciiTest {

    private final DibujanteAscii dibujante = new DibujanteAscii();

    @Test
    void dibujar_posicionInicial_muestraCadaPiezaConSuInicial() {
        // Act
        String[] lineas = dibujante.dibujar(new TableroEstandar().crearTablero()).split("\n");

        // Assert
        assertEquals(" 8  t c a d r a c t", lineas[0]);
        assertEquals(" 7  p p p p p p p p", lineas[1]);
        assertEquals(" 4  . . . . . . . .", lineas[4]);
        assertEquals(" 1  T C A D R A C T", lineas[7]);
        assertEquals("    a b c d e f g h", lineas[8]);
    }

    @Test
    void dibujar_tableroDe10x10_numeraFilasConDosDigitosYUsaDiezColumnas() {
        // Act
        String[] lineas = dibujante.dibujar(new Tablero(10, 10)).split("\n");

        // Assert
        assertEquals("10  . . . . . . . . . .", lineas[0]);
        assertEquals(" 1  . . . . . . . . . .", lineas[9]);
        assertEquals("    a b c d e f g h i j", lineas[10]);
    }
}
