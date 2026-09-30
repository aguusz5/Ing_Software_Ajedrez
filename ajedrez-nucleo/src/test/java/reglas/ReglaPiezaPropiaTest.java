package reglas;

import static configuracion.TiposDePiezaEstandar.peon;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Pieza;
import modelo.Tablero;
import movimiento.JugadaSimple;

class ReglaPiezaPropiaTest {

    private final ReglaPiezaPropia regla = new ReglaPiezaPropia();
    private final Tablero tablero = new Tablero(8, 8)
            .con(casilla("e2"), new Pieza(Color.BLANCO, peon()))
            .con(casilla("e7"), new Pieza(Color.NEGRO, peon()));

    @Test
    void validar_piezaDelColorQueJuega_esValida() {
        // Act
        ResultadoValidacion resultado =
                regla.validar(tablero, new JugadaSimple(casilla("e2"), casilla("e3")), Color.BLANCO);

        // Assert
        assertTrue(resultado.valida());
    }

    @Test
    void validar_piezaDelRival_esRechazada() {
        // Act
        ResultadoValidacion resultado =
                regla.validar(tablero, new JugadaSimple(casilla("e7"), casilla("e6")), Color.BLANCO);

        // Assert
        assertFalse(resultado.valida());
        assertFalse(resultado.motivo().isBlank());
    }

    @Test
    void validar_casillaDeOrigenVacia_esRechazada() {
        // Act
        ResultadoValidacion resultado =
                regla.validar(tablero, new JugadaSimple(casilla("d4"), casilla("d5")), Color.BLANCO);

        // Assert
        assertFalse(resultado.valida());
    }
}
