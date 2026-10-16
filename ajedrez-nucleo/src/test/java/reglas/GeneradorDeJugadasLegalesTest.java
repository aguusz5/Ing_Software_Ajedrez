package reglas;

import static configuracion.TiposDePiezaEstandar.alfil;
import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import configuracion.TableroEstandar;
import modelo.Color;
import modelo.Pieza;
import modelo.Tablero;
import movimiento.Jugada;

class GeneradorDeJugadasLegalesTest {

    private final GeneradorDeJugadasLegales generador;

    GeneradorDeJugadasLegalesTest() {
        DetectorDeJaque detector = new DetectorDeJaque();
        generador = new GeneradorDeJugadasLegales(new ValidadorDeJugadas(List.of(
                new ReglaPiezaPropia(),
                new ReglaNoDejarReyEnJaque(detector))));
    }

    /** Rey blanco en e1 y rey negro en h8. */
    private static Tablero soloLosReyes() {
        return new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("h8"), new Pieza(Color.NEGRO, rey()));
    }

    @Test
    void jugadasLegales_posicionInicialBlancas_sonVeinte() {
        // Arrange
        Tablero tablero = new TableroEstandar().crearTablero();

        // Act
        List<Jugada> legales = generador.jugadasLegales(tablero, Color.BLANCO);

        // Assert
        assertEquals(20, legales.size());
    }

    @Test
    void jugadasLegales_piezaClavada_noIncluyeSusJugadas() {
        // Arrange: el alfil de e2 tapa a la torre negra de e8.
        Tablero tablero = soloLosReyes()
                .con(casilla("e2"), new Pieza(Color.BLANCO, alfil()))
                .con(casilla("e8"), new Pieza(Color.NEGRO, torre()));

        // Act
        List<Jugada> legales = generador.jugadasLegales(tablero, Color.BLANCO);

        // Assert
        assertTrue(legales.stream().noneMatch(jugada -> jugada.desde().equals(casilla("e2"))));
    }

    @Test
    void jugadasLegales_reyEnJaque_soloLasQueLoSacan() {
        // Arrange: la torre negra de e8 da jaque; la torre blanca de a4 puede taparlo en e4.
        Tablero tablero = soloLosReyes()
                .con(casilla("e8"), new Pieza(Color.NEGRO, torre()))
                .con(casilla("a4"), new Pieza(Color.BLANCO, torre()));

        // Act
        Set<String> legales = generador.jugadasLegales(tablero, Color.BLANCO).stream()
                .map(jugada -> jugada.desde() + "-" + jugada.hasta())
                .collect(Collectors.toSet());

        // Assert
        Set<String> esperadas = Set.of(
                casilla("e1") + "-" + casilla("d1"), casilla("e1") + "-" + casilla("d2"),
                casilla("e1") + "-" + casilla("f1"), casilla("e1") + "-" + casilla("f2"),
                casilla("a4") + "-" + casilla("e4"));
        assertEquals(esperadas, legales);
    }

    @Test
    void jugadasLegalesDesde_casillaVacia_devuelveListaVacia() {
        // Act & Assert
        assertTrue(generador.jugadasLegalesDesde(soloLosReyes(), casilla("d4"), Color.BLANCO).isEmpty());
    }
}
