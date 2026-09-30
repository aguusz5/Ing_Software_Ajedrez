package movimiento;

import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.torre;
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

class TorreTest {

    private static final Pieza TORRE_BLANCA = new Pieza(Color.BLANCO, torre());

    @Test
    void torre_enElCentroDeUnTableroVacio_recorreSuFilaYSuColumna() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("d4"), TORRE_BLANCA);

        // Act
        Set<Posicion> destinos = destinos(TORRE_BLANCA.jugadasCandidatas(tablero, casilla("d4")));

        // Assert
        assertEquals(casillas("d1", "d2", "d3", "d5", "d6", "d7", "d8",
                "a4", "b4", "c4", "e4", "f4", "g4", "h4"), destinos);
    }

    @Test
    void torre_conPiezaPropiaEnElCamino_seDetieneAntes() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("a1"), TORRE_BLANCA)
                .con(casilla("a3"), new Pieza(Color.BLANCO, peon()));

        // Act
        Set<Posicion> destinos = destinos(TORRE_BLANCA.jugadasCandidatas(tablero, casilla("a1")));

        // Assert
        assertEquals(casillas("a2", "b1", "c1", "d1", "e1", "f1", "g1", "h1"), destinos);
    }

    @Test
    void torre_conRivalEnElCamino_loPuedeCapturarPeroNoSigueDeLargo() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("a1"), TORRE_BLANCA)
                .con(casilla("a5"), new Pieza(Color.NEGRO, peon()));

        // Act
        Set<Posicion> destinos = destinos(TORRE_BLANCA.jugadasCandidatas(tablero, casilla("a1")));

        // Assert
        assertEquals(casillas("a2", "a3", "a4", "a5", "b1", "c1", "d1", "e1", "f1", "g1", "h1"), destinos);
    }
}
