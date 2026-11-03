package ia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;
import static soporte.Excepciones.excepcionDe;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import configuracion.TableroEstandar;
import juego.JugadaLegal;
import modelo.Color;
import modelo.Tablero;

class ComputadoraTest {

    private static final JugadaLegal E2_E4 = new JugadaLegal(casilla("e2"), casilla("e4"), Optional.empty());
    private static final JugadaLegal E2_E5 = new JugadaLegal(casilla("e2"), casilla("e5"), Optional.empty());
    private static final JugadaLegal D2_D4 = new JugadaLegal(casilla("d2"), casilla("d4"), Optional.empty());
    /** Las jugadas legales que recibe la computadora en cada test: e2 e5 no está, así que no es legal. */
    private static final List<JugadaLegal> LEGALES = List.of(E2_E4, D2_D4);

    private final Tablero tablero = new TableroEstandar().crearTablero();

    /** Doble de prueba (mock): siempre sugiere lo mismo y registra cuántas veces le preguntaron y con qué. */
    private static final class AsesorDePrueba implements AsesorDeJugadas {

        private final String nombre;
        private final Optional<JugadaLegal> respuesta;
        private int pedidos = 0;
        private Tablero tableroRecibido;
        private Color turnoRecibido;

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
            tableroRecibido = tablero;
            turnoRecibido = turno;
            return respuesta;
        }
    }

    @Test
    void elegirJugada_sugerenciaLegal_laUsaSinPedirleAlRespaldo() {
        // Arrange
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E4));
        AsesorDePrueba respaldo = new AsesorDePrueba("Respaldo", Optional.of(D2_D4));
        Computadora computadora = new Computadora(asesor, respaldo, 2);

        // Act
        JugadaDeLaComputadora elegida = computadora.elegirJugada(tablero, Color.BLANCO, LEGALES).orElseThrow();

        // Assert
        assertEquals(new JugadaDeLaComputadora(E2_E4, "Principal", false), elegida);
        assertEquals(0, respaldo.pedidos);
    }

    @Test
    void elegirJugada_conUnaPosicion_leConsultaAlAsesorEsaPosicionYEseTurno() {
        // Arrange
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E4));
        Computadora computadora = new Computadora(asesor, asesor, 1);

        // Act
        computadora.elegirJugada(tablero, Color.NEGRO, LEGALES);

        // Assert
        assertSame(tablero, asesor.tableroRecibido);
        assertEquals(Color.NEGRO, asesor.turnoRecibido);
    }

    @Test
    void elegirJugada_sugerenciaIlegal_reintentaYDespuesUsaElRespaldo() {
        // Arrange: e2 e5 no es legal, así que la computadora no le cree.
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E5));
        AsesorDePrueba respaldo = new AsesorDePrueba("Respaldo", Optional.of(D2_D4));
        Computadora computadora = new Computadora(asesor, respaldo, 2);

        // Act
        JugadaDeLaComputadora elegida = computadora.elegirJugada(tablero, Color.BLANCO, LEGALES).orElseThrow();

        // Assert
        assertEquals(2, asesor.pedidos);
        assertEquals(new JugadaDeLaComputadora(D2_D4, "Respaldo", true), elegida);
    }

    @Test
    void elegirJugada_asesorQueNoResponde_usaElRespaldo() {
        // Arrange: así se vería, desde el núcleo, un motor externo que no respondió.
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.empty());
        AsesorDePrueba respaldo = new AsesorDePrueba("Respaldo", Optional.of(D2_D4));
        Computadora computadora = new Computadora(asesor, respaldo, 1);

        // Act
        JugadaDeLaComputadora elegida = computadora.elegirJugada(tablero, Color.BLANCO, LEGALES).orElseThrow();

        // Assert
        assertTrue(elegida.deRespaldo());
        assertEquals(D2_D4, elegida.jugada());
    }

    @Test
    void elegirJugada_respaldoQueTambienFalla_juegaLaPrimeraLegal() {
        // Arrange
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E5));
        AsesorDePrueba respaldo = new AsesorDePrueba("Respaldo", Optional.empty());
        Computadora computadora = new Computadora(asesor, respaldo, 1);

        // Act
        JugadaDeLaComputadora elegida = computadora.elegirJugada(tablero, Color.BLANCO, LEGALES).orElseThrow();

        // Assert
        assertEquals(E2_E4, elegida.jugada());
    }

    @Test
    void elegirJugada_sinJugadasLegales_vacio() {
        // Arrange
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E4));
        Computadora computadora = new Computadora(asesor, asesor, 1);

        // Act
        Optional<JugadaDeLaComputadora> elegida = computadora.elegirJugada(tablero, Color.BLANCO, List.of());

        // Assert
        assertTrue(elegida.isEmpty());
        assertEquals(0, asesor.pedidos);
    }

    @Test
    void constructor_sinIntentos_esRechazado() {
        // Arrange
        AsesorDePrueba asesor = new AsesorDePrueba("Principal", Optional.of(E2_E4));

        // Act
        RuntimeException error = excepcionDe(() -> new Computadora(asesor, asesor, 0));

        // Assert
        assertInstanceOf(IllegalArgumentException.class, error);
    }
}
