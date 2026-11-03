package modelo;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static soporte.Excepciones.excepcionDe;

import java.util.List;

import org.junit.jupiter.api.Test;
import movimiento.ReglaDeMovimiento;

class TipoDePiezaTest {

    @Test
    void constructor_sinReglasDeMovimiento_lanzaExcepcion() {
        // Arrange
        List<ReglaDeMovimiento> sinReglas = List.of();

        // Act
        RuntimeException error = excepcionDe(() -> new TipoDePieza("inmóvil", false, 1, sinReglas));

        // Assert
        assertInstanceOf(IllegalArgumentException.class, error);
    }
}
