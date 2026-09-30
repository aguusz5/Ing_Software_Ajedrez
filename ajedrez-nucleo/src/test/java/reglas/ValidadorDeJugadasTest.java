package reglas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.List;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Tablero;
import movimiento.Jugada;
import movimiento.JugadaSimple;

class ValidadorDeJugadasTest {

    private static final Tablero TABLERO = new Tablero(8, 8);
    private static final Jugada JUGADA = new JugadaSimple(casilla("a1"), casilla("a2"));

    // Las reglas de prueba son lambdas: ReglaDeValidacion tiene un solo método.
    private static final ReglaDeValidacion ACEPTA = (tablero, jugada, turno) -> ResultadoValidacion.ok();

    @Test
    void validar_todasLasReglasAceptan_esValida() {
        // Arrange
        ValidadorDeJugadas validador = new ValidadorDeJugadas(List.of(ACEPTA, ACEPTA));

        // Act
        ResultadoValidacion resultado = validador.validar(TABLERO, JUGADA, Color.BLANCO);

        // Assert
        assertTrue(resultado.valida());
    }

    @Test
    void validar_variasReglasRechazan_devuelveElMotivoDeLaPrimera() {
        // Arrange
        ValidadorDeJugadas validador = new ValidadorDeJugadas(List.of(
                ACEPTA,
                (tablero, jugada, turno) -> ResultadoValidacion.rechazada("primera"),
                (tablero, jugada, turno) -> ResultadoValidacion.rechazada("segunda")));

        // Act
        ResultadoValidacion resultado = validador.validar(TABLERO, JUGADA, Color.BLANCO);

        // Assert
        assertFalse(resultado.valida());
        assertEquals("primera", resultado.motivo());
    }

    @Test
    void constructor_sinReglas_lanzaExcepcion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new ValidadorDeJugadas(List.of()));
    }
}
