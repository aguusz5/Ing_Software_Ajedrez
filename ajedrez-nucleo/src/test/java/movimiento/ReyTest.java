package movimiento;

import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.rey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static soporte.Casillas.casilla;
import static soporte.Casillas.casillas;
import static soporte.Casillas.destinos;

import java.util.Set;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

class ReyTest {

    private static final Pieza REY_BLANCO = new Pieza(Color.BLANCO, rey());

    @Test
    void rey_enElCentro_mueveUnaCasillaEnCualquierDireccion() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("e4"), REY_BLANCO);

        // Act
        Set<Posicion> destinos = destinos(REY_BLANCO.jugadasCandidatas(tablero, casilla("e4")));

        // Assert
        assertEquals(casillas("d3", "d4", "d5", "e3", "e5", "f3", "f4", "f5"), destinos);
    }

    @Test
    void rey_enUnaEsquina_soloTieneTresJugadas() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("a1"), REY_BLANCO);

        // Act
        Set<Posicion> destinos = destinos(REY_BLANCO.jugadasCandidatas(tablero, casilla("a1")));

        // Assert
        assertEquals(casillas("a2", "b1", "b2"), destinos);
    }

    @Test
    void rey_conPiezasPropiasAlrededor_soloVaALaCasillaLibreOCapturando() {
        // Arrange
        Pieza peonBlanco = new Pieza(Color.BLANCO, peon());
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e1"), REY_BLANCO)
                .con(casilla("d1"), peonBlanco)
                .con(casilla("d2"), peonBlanco)
                .con(casilla("e2"), peonBlanco)
                .con(casilla("f2"), new Pieza(Color.NEGRO, peon()));

        // Act
        Set<Posicion> destinos = destinos(REY_BLANCO.jugadasCandidatas(tablero, casilla("e1")));

        // Assert
        assertEquals(casillas("f1", "f2"), destinos);
    }
}
