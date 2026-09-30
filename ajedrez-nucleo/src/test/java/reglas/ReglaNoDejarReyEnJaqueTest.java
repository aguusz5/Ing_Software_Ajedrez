package reglas;

import static configuracion.TiposDePiezaEstandar.alfil;
import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Pieza;
import modelo.Tablero;
import movimiento.JugadaSimple;

class ReglaNoDejarReyEnJaqueTest {

    private final ReglaNoDejarReyEnJaque regla = new ReglaNoDejarReyEnJaque(new DetectorDeJaque());

    /** Rey blanco en e1 y rey negro en h8. */
    private static Tablero soloLosReyes() {
        return new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("h8"), new Pieza(Color.NEGRO, rey()));
    }

    private ResultadoValidacion validar(Tablero tablero, String desde, String hasta) {
        return regla.validar(tablero, new JugadaSimple(casilla(desde), casilla(hasta)), Color.BLANCO);
    }

    @Test
    void validar_piezaClavadaQueDescubreAlRey_esRechazada() {
        // Arrange: el alfil de e2 tapa a la torre negra de e8.
        Tablero tablero = soloLosReyes()
                .con(casilla("e2"), new Pieza(Color.BLANCO, alfil()))
                .con(casilla("e8"), new Pieza(Color.NEGRO, torre()));

        // Act
        ResultadoValidacion resultado = validar(tablero, "e2", "d3");

        // Assert
        assertFalse(resultado.valida());
    }

    @Test
    void validar_reyQueEntraEnUnaCasillaAtacada_esRechazada() {
        // Arrange: la torre negra de d8 controla toda la columna d.
        Tablero tablero = soloLosReyes().con(casilla("d8"), new Pieza(Color.NEGRO, torre()));

        // Act
        ResultadoValidacion resultado = validar(tablero, "e1", "d1");

        // Assert
        assertFalse(resultado.valida());
    }

    @Test
    void validar_reyQueVaAUnaCasillaSegura_esValida() {
        // Arrange
        Tablero tablero = soloLosReyes().con(casilla("d8"), new Pieza(Color.NEGRO, torre()));

        // Act
        ResultadoValidacion resultado = validar(tablero, "e1", "f1");

        // Assert
        assertTrue(resultado.valida());
    }

    @Test
    void validar_salirDelJaqueCapturandoAlAtacante_esValida() {
        // Arrange: torre negra pegada al rey y sin nadie que la defienda.
        Tablero tablero = soloLosReyes().con(casilla("e2"), new Pieza(Color.NEGRO, torre()));

        // Act
        ResultadoValidacion resultado = validar(tablero, "e1", "e2");

        // Assert
        assertTrue(resultado.valida());
    }

    @Test
    void validar_salirDelJaqueInterponiendoUnaPieza_esValida() {
        // Arrange
        Tablero tablero = soloLosReyes()
                .con(casilla("e8"), new Pieza(Color.NEGRO, torre()))
                .con(casilla("a4"), new Pieza(Color.BLANCO, torre()));

        // Act
        ResultadoValidacion resultado = validar(tablero, "a4", "e4");

        // Assert
        assertTrue(resultado.valida());
    }

    @Test
    void validar_jugadaQueNoSacaDelJaque_esRechazada() {
        // Arrange
        Tablero tablero = soloLosReyes()
                .con(casilla("e8"), new Pieza(Color.NEGRO, torre()))
                .con(casilla("a4"), new Pieza(Color.BLANCO, torre()));

        // Act
        ResultadoValidacion resultado = validar(tablero, "a4", "a5");

        // Assert
        assertFalse(resultado.valida());
    }
}
