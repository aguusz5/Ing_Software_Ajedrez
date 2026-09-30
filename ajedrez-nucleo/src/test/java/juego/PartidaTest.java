package juego;

import static configuracion.TiposDePiezaEstandar.peon;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import configuracion.TableroEstandar;
import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import reglas.DetectorDeJaque;
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

class PartidaTest {

    private Partida partida;

    @BeforeEach
    void crearPartidaEstandar() {
        DetectorDeJaque detector = new DetectorDeJaque();
        ValidadorDeJugadas validador = new ValidadorDeJugadas(List.of(
                new ReglaPiezaPropia(),
                new ReglaNoDejarReyEnJaque(detector)));
        partida = new Partida(new TableroEstandar().crearTablero(), validador, detector);
    }

    private ResultadoJugada jugar(String desde, String hasta) {
        return partida.jugar(casilla(desde), casilla(hasta));
    }

    /** Juega una secuencia como "e2 e4", "e7 e5"... y exige que cada jugada sea válida. */
    private void jugarValidas(String... jugadas) {
        for (String jugada : jugadas) {
            String[] casillas = jugada.split(" ");
            assertTrue(jugar(casillas[0], casillas[1]).valida(), "Debería ser válida: " + jugada);
        }
    }

    private int piezasDe(Color color) {
        Tablero tablero = partida.tablero();
        int cantidad = 0;
        for (int columna = 0; columna < tablero.ancho(); columna++) {
            for (int fila = 0; fila < tablero.alto(); fila++) {
                Optional<Pieza> pieza = tablero.piezaEn(new Posicion(columna, fila));
                if (pieza.isPresent() && pieza.get().color() == color) {
                    cantidad++;
                }
            }
        }
        return cantidad;
    }

    @Test
    void partidaNueva_empiezanLasBlancasYNadieEstaEnJaque() {
        // Act & Assert
        assertEquals(Color.BLANCO, partida.turnoActual());
        assertFalse(partida.estaEnJaque(Color.BLANCO));
        assertFalse(partida.estaEnJaque(Color.NEGRO));
    }

    @Test
    void jugar_movimientoValido_mueveLaPiezaYPasaElTurno() {
        // Act
        ResultadoJugada resultado = jugar("e2", "e4");

        // Assert
        assertTrue(resultado.valida());
        assertEquals(Optional.empty(), resultado.colorEnJaque());
        assertTrue(partida.tablero().piezaEn(casilla("e2")).isEmpty());
        assertEquals(Optional.of(new Pieza(Color.BLANCO, peon(), true)), partida.tablero().piezaEn(casilla("e4")));
        assertEquals(Color.NEGRO, partida.turnoActual());
    }

    @Test
    void jugar_dosJugadasValidas_elTurnoVuelveALasBlancas() {
        // Act
        jugarValidas("e2 e4", "e7 e5");

        // Assert
        assertEquals(Color.BLANCO, partida.turnoActual());
    }

    @Test
    void jugar_piezaDelRival_esRechazadaYNoCambiaNada() {
        // Arrange
        Tablero antes = partida.tablero();

        // Act
        ResultadoJugada resultado = jugar("e7", "e5");

        // Assert
        assertFalse(resultado.valida());
        assertSame(antes, partida.tablero());
        assertEquals(Color.BLANCO, partida.turnoActual());
    }

    @Test
    void jugar_destinoQueLaPiezaNoPuedeAlcanzar_esRechazado() {
        // Act
        ResultadoJugada resultado = jugar("e2", "e5");

        // Assert
        assertFalse(resultado.valida());
        assertEquals("Esa pieza no puede moverse a esa casilla", resultado.motivo());
    }

    @Test
    void jugar_desdeUnaCasillaVacia_esRechazado() {
        // Act
        ResultadoJugada resultado = jugar("e4", "e5");

        // Assert
        assertFalse(resultado.valida());
        assertEquals("No hay ninguna pieza en la casilla de origen", resultado.motivo());
    }

    @Test
    void jugar_sobreUnaPiezaPropia_esRechazado() {
        // Act: la torre de a1 no puede "capturar" a su propio peón de a2.
        ResultadoJugada resultado = jugar("a1", "a2");

        // Assert
        assertFalse(resultado.valida());
    }

    @Test
    void jugar_capturaDeUnaPiezaRival_laSacaDelTablero() {
        // Arrange
        jugarValidas("e2 e4", "d7 d5");

        // Act
        ResultadoJugada resultado = jugar("e4", "d5");

        // Assert
        assertTrue(resultado.valida());
        assertEquals(Color.BLANCO, partida.tablero().piezaEn(casilla("d5")).orElseThrow().color());
        assertEquals(15, piezasDe(Color.NEGRO));
    }

    @Test
    void jugar_jugadaQueDaJaque_informaElColorEnJaque() {
        // Arrange: mate del loco (1. f3 e5 2. g4 ...).
        jugarValidas("f2 f3", "e7 e5", "g2 g4");

        // Act: 2. ... Dh4
        ResultadoJugada resultado = jugar("d8", "h4");

        // Assert
        assertTrue(resultado.valida());
        assertEquals(Optional.of(Color.BLANCO), resultado.colorEnJaque());
        assertTrue(partida.estaEnJaque(Color.BLANCO));
    }

    @Test
    void jugar_conElReyEnJaque_rechazaLasJugadasQueNoLoSacan() {
        // Arrange
        jugarValidas("f2 f3", "e7 e5", "g2 g4", "d8 h4");

        // Act
        ResultadoJugada resultado = jugar("a2", "a3");

        // Assert
        assertFalse(resultado.valida());
        assertEquals("Esa jugada deja a tu rey en jaque", resultado.motivo());
    }
}
