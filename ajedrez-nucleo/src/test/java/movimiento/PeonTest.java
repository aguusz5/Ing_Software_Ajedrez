package movimiento;

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

class PeonTest {

    private static final Pieza PEON_BLANCO = new Pieza(Color.BLANCO, peon());
    private static final Pieza PEON_NEGRO = new Pieza(Color.NEGRO, peon());

    @Test
    void peonBlanco_sinHaberseMovido_puedeAvanzarUnaODosCasillas() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("e2"), PEON_BLANCO);

        // Act
        Set<Posicion> destinos = destinos(PEON_BLANCO.jugadasCandidatas(tablero, casilla("e2")));

        // Assert
        assertEquals(casillas("e3", "e4"), destinos);
    }

    @Test
    void peonBlanco_queYaSeMovio_soloAvanzaUnaCasilla() {
        // Arrange
        Pieza peon = PEON_BLANCO.movida();
        Tablero tablero = new Tablero(8, 8).con(casilla("e3"), peon);

        // Act
        Set<Posicion> destinos = destinos(peon.jugadasCandidatas(tablero, casilla("e3")));

        // Assert
        assertEquals(casillas("e4"), destinos);
    }

    @Test
    void peonNegro_sinHaberseMovido_avanzaHaciaLasFilasDeAbajo() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("d7"), PEON_NEGRO);

        // Act
        Set<Posicion> destinos = destinos(PEON_NEGRO.jugadasCandidatas(tablero, casilla("d7")));

        // Assert
        assertEquals(casillas("d6", "d5"), destinos);
    }

    @Test
    void peon_conUnaPiezaJustoAdelante_noPuedeAvanzar() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e2"), PEON_BLANCO)
                .con(casilla("e3"), PEON_NEGRO);

        // Act
        Set<Posicion> destinos = destinos(PEON_BLANCO.jugadasCandidatas(tablero, casilla("e2")));

        // Assert
        assertEquals(Set.of(), destinos);
    }

    @Test
    void peon_conLaSegundaCasillaOcupada_soloAvanzaUna() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e2"), PEON_BLANCO)
                .con(casilla("e4"), PEON_NEGRO);

        // Act
        Set<Posicion> destinos = destinos(PEON_BLANCO.jugadasCandidatas(tablero, casilla("e2")));

        // Assert
        assertEquals(casillas("e3"), destinos);
    }

    @Test
    void peon_conRivalesEnSusDiagonales_puedeCapturarlos() {
        // Arrange
        Pieza peon = PEON_BLANCO.movida();
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e4"), peon)
                .con(casilla("d5"), PEON_NEGRO)
                .con(casilla("f5"), PEON_NEGRO);

        // Act
        Set<Posicion> destinos = destinos(peon.jugadasCandidatas(tablero, casilla("e4")));

        // Assert
        assertEquals(casillas("e5", "d5", "f5"), destinos);
    }

    @Test
    void peon_conPiezaPropiaEnDiagonal_noLaCaptura() {
        // Arrange
        Pieza peon = PEON_BLANCO.movida();
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e4"), peon)
                .con(casilla("d5"), PEON_BLANCO);

        // Act
        Set<Posicion> destinos = destinos(peon.jugadasCandidatas(tablero, casilla("e4")));

        // Assert
        assertEquals(casillas("e5"), destinos);
    }

    @Test
    void peon_conRivalAtrasEnDiagonal_noRetrocedeNiLoCaptura() {
        // Arrange
        Pieza peon = PEON_BLANCO.movida();
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e4"), peon)
                .con(casilla("d3"), PEON_NEGRO);

        // Act
        Set<Posicion> destinos = destinos(peon.jugadasCandidatas(tablero, casilla("e4")));

        // Assert
        assertEquals(casillas("e5"), destinos);
    }

    @Test
    void peon_enLaUltimaFila_noTieneJugadas() {
        // Arrange
        Pieza peon = PEON_BLANCO.movida();
        Tablero tablero = new Tablero(8, 8).con(casilla("e8"), peon);

        // Act
        Set<Posicion> destinos = destinos(peon.jugadasCandidatas(tablero, casilla("e8")));

        // Assert
        assertEquals(Set.of(), destinos);
    }
}
