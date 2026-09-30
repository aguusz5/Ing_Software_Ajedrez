package movimiento;

import static configuracion.TiposDePiezaEstandar.alfil;
import static configuracion.TiposDePiezaEstandar.peon;
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

class AlfilTest {

    private static final Pieza ALFIL_BLANCO = new Pieza(Color.BLANCO, alfil());

    @Test
    void alfil_enElCentroDeUnTableroVacio_recorreLasCuatroDiagonales() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("d4"), ALFIL_BLANCO);

        // Act
        Set<Posicion> destinos = destinos(ALFIL_BLANCO.jugadasCandidatas(tablero, casilla("d4")));

        // Assert
        assertEquals(casillas("a1", "b2", "c3", "e5", "f6", "g7", "h8",
                "a7", "b6", "c5", "e3", "f2", "g1"), destinos);
    }

    @Test
    void alfil_bloqueadoPorPropiaYConRivalAlLado_soloPuedeCapturar() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("c1"), ALFIL_BLANCO)
                .con(casilla("d2"), new Pieza(Color.BLANCO, peon()))
                .con(casilla("b2"), new Pieza(Color.NEGRO, peon()));

        // Act
        Set<Posicion> destinos = destinos(ALFIL_BLANCO.jugadasCandidatas(tablero, casilla("c1")));

        // Assert
        assertEquals(casillas("b2"), destinos);
    }
}
