package ia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import configuracion.TableroEstandar;
import juego.JuegoDeAjedrez;
import juego.JugadaLegal;
import juego.Partida;
import modelo.Color;
import modelo.Tablero;
import reglas.DetectorDeJaque;
import reglas.GeneradorDeJugadasLegales;
import reglas.ReglaEnroqueSeguro;
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

class ComputadoraTest {

    private static final JugadaLegal E2_E4 = new JugadaLegal(casilla("e2"), casilla("e4"), Optional.empty());
    private static final JugadaLegal E2_E5 = new JugadaLegal(casilla("e2"), casilla("e5"), Optional.empty());
    private static final JugadaLegal D2_D4 = new JugadaLegal(casilla("d2"), casilla("d4"), Optional.empty());

    private JuegoDeAjedrez partida;

    @BeforeEach
    void crearPartidaEstandar() {
        DetectorDeJaque detector = new DetectorDeJaque();
        ValidadorDeJugadas validador = new ValidadorDeJugadas(List.of(
                new ReglaPiezaPropia(), new ReglaEnroqueSeguro(detector), new ReglaNoDejarReyEnJaque(detector)));
        partida = new Partida(new TableroEstandar().crearTablero(), validador, detector,
                new GeneradorDeJugadasLegales(validador));
    }

    /** Doble de prueba: siempre sugiere lo mismo y cuenta cuántas veces le preguntaron. */
    private static final class AsesorDePrueba implements AsesorDeJugadas {

        private final String nombre;
        private final Optional<JugadaLegal> respuesta;
        private int pedidos = 0;

        AsesorDePrueba(String nombre, Optional<JugadaLegal> respuesta) {
            this.nombre = nombre;
            this.respuesta = respuesta;
        }

        @Override
        public String nombre() {
            return nombre;
        }

        @Override
        public Optional<JugadaLegal> sugerirJugada(Tablero tablero, Color turno, List<JugadaLegal> legales) {
            pedidos++;
            return respuesta;
        }
    }

    @Test
    void elegirJugada_sugerenciaLegal_laUsaSinPedirleAlRespaldo() {
        // Arrange
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E4));
        AsesorDePrueba respaldo = new AsesorDePrueba("Respaldo", Optional.of(D2_D4));

        // Act
        JugadaDeLaComputadora elegida = new Computadora(asesor, respaldo, 2).elegirJugada(partida).orElseThrow();

        // Assert
        assertEquals(new JugadaDeLaComputadora(E2_E4, "Principal", false), elegida);
        assertEquals(0, respaldo.pedidos);
    }

    @Test
    void elegirJugada_sugerenciaIlegal_reintentaYDespuesUsaElRespaldo() {
        // Arrange: e2 e5 no es legal, así que la computadora no le cree.
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E5));
        AsesorDePrueba respaldo = new AsesorDePrueba("Respaldo", Optional.of(D2_D4));

        // Act
        JugadaDeLaComputadora elegida = new Computadora(asesor, respaldo, 2).elegirJugada(partida).orElseThrow();

        // Assert
        assertEquals(2, asesor.pedidos);
        assertEquals(new JugadaDeLaComputadora(D2_D4, "Respaldo", true), elegida);
    }

    @Test
    void elegirJugada_asesorQueNoResponde_usaElRespaldo() {
        // Arrange: así se vería, desde el núcleo, un motor externo que no respondió.
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.empty());
        AsesorDePrueba respaldo = new AsesorDePrueba("Respaldo", Optional.of(D2_D4));

        // Act
        JugadaDeLaComputadora elegida = new Computadora(asesor, respaldo, 1).elegirJugada(partida).orElseThrow();

        // Assert
        assertTrue(elegida.deRespaldo());
        assertEquals(D2_D4, elegida.jugada());
    }

    @Test
    void elegirJugada_respaldoQueTambienFalla_juegaLaPrimeraLegal() {
        // Arrange
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E5));
        AsesorDePrueba respaldo = new AsesorDePrueba("Respaldo", Optional.empty());

        // Act
        JugadaDeLaComputadora elegida = new Computadora(asesor, respaldo, 1).elegirJugada(partida).orElseThrow();

        // Assert
        assertEquals(partida.jugadasLegales().get(0), elegida.jugada());
    }

    @Test
    void elegirJugada_noModificaLaPartida() {
        // Arrange
        Tablero antes = partida.tablero();
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E4));

        // Act
        new Computadora(asesor, asesor, 1).elegirJugada(partida);

        // Assert: la jugada la hace después el adaptador, por el puerto.
        assertSame(antes, partida.tablero());
        assertEquals(Color.BLANCO, partida.turnoActual());
    }

    @Test
    void elegirJugada_partidaTerminada_vacio() {
        // Arrange
        partida.terminar();
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E4));

        // Act & Assert
        assertFalse(new Computadora(asesor, asesor, 1).elegirJugada(partida).isPresent());
    }

    @Test
    void constructor_sinIntentos_esRechazado() {
        // Arrange
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E4));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Computadora(asesor, asesor, 0));
    }
}
