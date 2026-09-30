package modelo;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class TipoDePiezaTest {

    @Test
    void constructor_sinReglasDeMovimiento_lanzaExcepcion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new TipoDePieza("inmóvil", false, List.of()));
    }
}
