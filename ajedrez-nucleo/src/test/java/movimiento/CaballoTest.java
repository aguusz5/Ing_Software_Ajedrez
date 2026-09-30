package movimiento;

import static configuracion.TiposDePiezaEstandar.caballo;
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

class CaballoTest {

    private static final Pieza CABALLO_BLANCO = new Pieza(Color.BLANCO, caballo());
    private static final Pieza PEON_BLANCO = new Pieza(Color.BLANCO, peon());

    @Test
    void caballo_enElCentro_tieneOchoSaltos() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("d4"), CABALLO_BLANCO);

        // Act
        Set<Posicion> destinos = destinos(CABALLO_BLANCO.jugadasCandidatas(tablero, casilla("d4")));

        // Assert
        assertEquals(casillas("b3", "b5", "c2", "c6", "e2", "e6", "f3", "f5"), destinos);
    }

    @Test
    void caballo_enUnaEsquina_soloTieneDosSaltos() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("a1"), CABALLO_BLANCO);

        // Act
        Set<Posicion> destinos = destinos(CABALLO_BLANCO.jugadasCandidatas(tablero, casilla("a1")));

        // Assert
        assertEquals(casillas("b3", "c2"), destinos);
    }

    @Test
    void caballo_rodeadoDePiezas_saltaPorEncima() {
        // Arrange: como en la posición inicial, con los peones delante.
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("b1"), CABALLO_BLANCO)
                .con(casilla("a2"), PEON_BLANCO)
                .con(casilla("b2"), PEON_BLANCO)
                .con(casilla("c2"), PEON_BLANCO)
                .con(casilla("d2"), PEON_BLANCO);

        // Act
        Set<Posicion> destinos = destinos(CABALLO_BLANCO.jugadasCandidatas(tablero, casilla("b1")));

        // Assert
        assertEquals(casillas("a3", "c3"), destinos);
    }

    @Test
    void caballo_noCaeSobrePiezaPropiaPeroSiCapturaRival() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("d4"), CABALLO_BLANCO)
                .con(casilla("b3"), PEON_BLANCO)
                .con(casilla("f5"), new Pieza(Color.NEGRO, peon()));

        // Act
        Set<Posicion> destinos = destinos(CABALLO_BLANCO.jugadasCandidatas(tablero, casilla("d4")));

        // Assert
        assertEquals(casillas("b5", "c2", "c6", "e2", "e6", "f3", "f5"), destinos);
    }
}
