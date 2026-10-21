package movimiento;

import static configuracion.TiposDePiezaEstandar.caballo;
import static configuracion.TiposDePiezaEstandar.dama;
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

class JugadaConPromocionTest {

    private static final Pieza PEON_BLANCO = new Pieza(Color.BLANCO, peon()).movida();

    @Test
    void aplicarEn_peonQueLlegaAlFinal_dejaUnaPiezaDelTipoNuevoYDelMismoColor() {
        // Arrange
        Tablero antes = new Tablero(8, 8).con(casilla("e7"), PEON_BLANCO);
        Jugada jugada = new JugadaConPromocion(casilla("e7"), casilla("e8"), caballo());

        // Act
        Tablero despues = jugada.aplicarEn(antes);

        // Assert
        assertTrue(despues.piezaEn(casilla("e7")).isEmpty());
        assertEquals(Optional.of(new Pieza(Color.BLANCO, caballo()).movida()), despues.piezaEn(casilla("e8")));
    }

    @Test
    void aplicarEn_conRivalEnElDestino_loCapturaYCorona() {
        // Arrange
        Tablero antes = new Tablero(8, 8)
                .con(casilla("e7"), PEON_BLANCO)
                .con(casilla("d8"), new Pieza(Color.NEGRO, torre()));
        Jugada jugada = new JugadaConPromocion(casilla("e7"), casilla("d8"), dama());

        // Act
        Tablero despues = jugada.aplicarEn(antes);

        // Assert
        assertEquals(Optional.of(new Pieza(Color.BLANCO, dama()).movida()), despues.piezaEn(casilla("d8")));
    }

    @Test
    void promocion_devuelveElTipoNuevo() {
        // Act & Assert
        assertEquals(Optional.of(dama()), new JugadaConPromocion(casilla("e7"), casilla("e8"), dama()).promocion());
    }
}
