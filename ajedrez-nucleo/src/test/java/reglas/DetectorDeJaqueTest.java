package reglas;

import static configuracion.TiposDePiezaEstandar.alfil;
import static configuracion.TiposDePiezaEstandar.caballo;
import static configuracion.TiposDePiezaEstandar.dama;
import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import configuracion.TableroEstandar;
import modelo.Color;
import modelo.Pieza;
import modelo.Tablero;
import modelo.TipoDePieza;

class DetectorDeJaqueTest {

    private final DetectorDeJaque detector = new DetectorDeJaque();

    /** Rey blanco en e1 y rey negro en h8, lejos de todo. */
    private static Tablero soloLosReyes() {
        return new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("h8"), new Pieza(Color.NEGRO, rey()));
    }

    static Stream<Arguments> atacantesDelReyEnE1() {
        return Stream.of(
                Arguments.of(torre(), "e8"),
                Arguments.of(alfil(), "a5"),
                Arguments.of(caballo(), "d3"),
                Arguments.of(dama(), "h4"),
                Arguments.of(peon(), "d2"));
    }

    @ParameterizedTest(name = "{0} negro en {1} da jaque al rey de e1")
    @MethodSource("atacantesDelReyEnE1")
    void estaEnJaque_reyAtacadoPorCadaTipoDePieza_devuelveTrue(TipoDePieza tipo, String casillaDelAtacante) {
        // Arrange
        Tablero tablero = soloLosReyes().con(casilla(casillaDelAtacante), new Pieza(Color.NEGRO, tipo));

        // Act
        boolean enJaque = detector.estaEnJaque(tablero, Color.BLANCO);

        // Assert
        assertTrue(enJaque);
    }

    @Test
    void estaEnJaque_torreConUnaPiezaEnElMedio_devuelveFalse() {
        // Arrange
        Tablero tablero = soloLosReyes()
                .con(casilla("e8"), new Pieza(Color.NEGRO, torre()))
                .con(casilla("e4"), new Pieza(Color.BLANCO, peon()));

        // Act & Assert
        assertFalse(detector.estaEnJaque(tablero, Color.BLANCO));
    }

    @Test
    void estaEnJaque_peonRivalJustoAdelanteDelRey_devuelveFalse() {
        // Arrange: el peón captura en diagonal, de frente no ataca.
        Tablero tablero = soloLosReyes().con(casilla("e2"), new Pieza(Color.NEGRO, peon()));

        // Act & Assert
        assertFalse(detector.estaEnJaque(tablero, Color.BLANCO));
    }

    @Test
    void estaEnJaque_soloReyBlancoAtacado_respondePorCadaColor() {
        // Arrange
        Tablero tablero = soloLosReyes().con(casilla("e8"), new Pieza(Color.NEGRO, torre()));

        // Act & Assert
        assertTrue(detector.estaEnJaque(tablero, Color.BLANCO));
        assertFalse(detector.estaEnJaque(tablero, Color.NEGRO));
    }

    @Test
    void estaEnJaque_posicionInicial_ningunReyEstaEnJaque() {
        // Arrange
        Tablero tablero = new TableroEstandar().crearTablero();

        // Act & Assert
        assertFalse(detector.estaEnJaque(tablero, Color.BLANCO));
        assertFalse(detector.estaEnJaque(tablero, Color.NEGRO));
    }
}
