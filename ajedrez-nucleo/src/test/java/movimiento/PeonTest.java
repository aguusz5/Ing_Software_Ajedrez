package movimiento;

import static configuracion.TiposDePiezaEstandar.peon;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;
import static soporte.Casillas.casillas;
import static soporte.Casillas.destinos;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import modelo.TipoDePieza;

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
    void peonBlanco_alLlegarALaUltimaFila_generaUnaJugadaPorCadaTipoDeCoronacion() {
        // Arrange
        Pieza peon = PEON_BLANCO.movida();
        Tablero tablero = new Tablero(8, 8).con(casilla("e7"), peon);

        // Act
        List<Jugada> jugadas = peon.jugadasCandidatas(tablero, casilla("e7"));

        // Assert
        assertEquals(casillas("e8"), destinos(jugadas));
        assertEquals(List.of("dama", "torre", "alfil", "caballo"), tiposDeCoronacion(jugadas));
    }

    @Test
    void peonNegro_capturandoEnLaPrimeraFila_tambienCorona() {
        // Arrange: d1 está ocupada, así que solo puede capturar en e1.
        Pieza peon = PEON_NEGRO.movida();
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("d2"), peon)
                .con(casilla("d1"), PEON_BLANCO)
                .con(casilla("e1"), PEON_BLANCO);

        // Act
        List<Jugada> jugadas = peon.jugadasCandidatas(tablero, casilla("d2"));

        // Assert
        assertEquals(casillas("e1"), destinos(jugadas));
        assertEquals(List.of("dama", "torre", "alfil", "caballo"), tiposDeCoronacion(jugadas));
    }

    @Test
    void peon_lejosDelFinal_noCorona() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("e2"), PEON_BLANCO);

        // Act
        List<Jugada> jugadas = PEON_BLANCO.jugadasCandidatas(tablero, casilla("e2"));

        // Assert
        assertTrue(jugadas.stream().allMatch(jugada -> jugada.promocion().isEmpty()));
    }

    private static List<String> tiposDeCoronacion(List<Jugada> jugadas) {
        return jugadas.stream()
                .map(jugada -> jugada.promocion().map(TipoDePieza::nombre).orElse("sin coronar"))
                .toList();
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
