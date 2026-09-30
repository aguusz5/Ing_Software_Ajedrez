package movimiento;

import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Pieza;
import modelo.Tablero;

class JugadaSimpleTest {

    private static final Pieza TORRE_BLANCA = new Pieza(Color.BLANCO, torre());
    private static final Jugada A1_A5 = new JugadaSimple(casilla("a1"), casilla("a5"));

    @Test
    void aplicarEn_unaJugada_mueveLaPiezaYLaMarcaComoMovida() {
        // Arrange
        Tablero antes = new Tablero(8, 8).con(casilla("a1"), TORRE_BLANCA);

        // Act
        Tablero despues = A1_A5.aplicarEn(antes);

        // Assert
        assertTrue(despues.piezaEn(casilla("a1")).isEmpty());
        assertEquals(Optional.of(TORRE_BLANCA.movida()), despues.piezaEn(casilla("a5")));
    }

    @Test
    void aplicarEn_conRivalEnElDestino_loCaptura() {
        // Arrange
        Tablero antes = new Tablero(8, 8)
                .con(casilla("a1"), TORRE_BLANCA)
                .con(casilla("a5"), new Pieza(Color.NEGRO, peon()));

        // Act
        Tablero despues = A1_A5.aplicarEn(antes);

        // Assert
        assertEquals(Optional.of(TORRE_BLANCA.movida()), despues.piezaEn(casilla("a5")));
    }

    @Test
    void aplicarEn_cualquierJugada_noModificaElTableroOriginal() {
        // Arrange
        Tablero antes = new Tablero(8, 8).con(casilla("a1"), TORRE_BLANCA);

        // Act
        A1_A5.aplicarEn(antes);

        // Assert
        assertEquals(Optional.of(TORRE_BLANCA), antes.piezaEn(casilla("a1")));
        assertTrue(antes.piezaEn(casilla("a5")).isEmpty());
    }
}
