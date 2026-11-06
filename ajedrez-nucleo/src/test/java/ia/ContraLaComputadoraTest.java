package ia;

import static configuracion.TiposDePiezaEstandar.caballo;
import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.rey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Test;

import configuracion.TableroEstandar;
import juego.AparicionDePotenciadores;
import juego.JuegoDeAjedrez;
import juego.JugadaLegal;
import juego.Partida;
import juego.ResultadoJugada;
import modelo.Color;
import modelo.Pieza;
import modelo.Tablero;
import reglas.DetectorDeJaque;
import reglas.GeneradorDeJugadasLegales;
import reglas.ReglaEnroqueSeguro;
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

class ContraLaComputadoraTest {

    private static final JugadaLegal E2_E4 = new JugadaLegal(casilla("e2"), casilla("e4"), Optional.empty());
    private static final JugadaLegal E7_E5 = new JugadaLegal(casilla("e7"), casilla("e5"), Optional.empty());

    /** Doble de prueba (stub): siempre sugiere la misma jugada. */
    private record AsesorFijo(JugadaLegal jugada) implements AsesorDeJugadas {

        @Override
        public String nombre() {
            return "Fijo";
        }

        @Override
        public Optional<JugadaLegal> sugerirJugada(Tablero tablero, Color turno, List<JugadaLegal> legales) {
            return Optional.of(jugada);
        }
    }

    private static JuegoDeAjedrez partidaSobre(Tablero tablero) {
        DetectorDeJaque detector = new DetectorDeJaque();
        ValidadorDeJugadas validador = new ValidadorDeJugadas(List.of(
                new ReglaPiezaPropia(), new ReglaEnroqueSeguro(detector), new ReglaNoDejarReyEnJaque(detector)));
        return new Partida(tablero, validador, detector, new GeneradorDeJugadasLegales(validador),
                new AparicionDePotenciadores(List.of(), 1, 0, new Random()));
    }

    private static JuegoDeAjedrez partidaEstandar() {
        return partidaSobre(new TableroEstandar().crearTablero());
    }

    /** La computadora con negras, que siempre quiere contestar e7 e5. */
    private static ContraLaComputadora conNegras(JuegoDeAjedrez juego) {
        AsesorFijo asesor = new AsesorFijo(E7_E5);
        return new ContraLaComputadora(juego, new Computadora(asesor, asesor, 1), Color.NEGRO);
    }

    @Test
    void leToca_turnoDeSuColor_esVerdadero() {
        // Arrange
        JuegoDeAjedrez juego = partidaEstandar();
        ContraLaComputadora rival = conNegras(juego);
        juego.jugar(casilla("e2"), casilla("e4"));

        // Act
        boolean leToca = rival.leToca();

        // Assert
        assertTrue(leToca);
    }

    @Test
    void leToca_turnoDeLaPersona_esFalso() {
        // Arrange
        ContraLaComputadora rival = conNegras(partidaEstandar());

        // Act
        boolean leToca = rival.leToca();

        // Assert
        assertFalse(leToca);
    }

    @Test
    void leToca_partidaTerminada_esFalso() {
        // Arrange: con blancas le tocaría empezar, pero la partida ya terminó.
        JuegoDeAjedrez juego = partidaEstandar();
        AsesorFijo asesor = new AsesorFijo(E2_E4);
        ContraLaComputadora rival = new ContraLaComputadora(juego, new Computadora(asesor, asesor, 1), Color.BLANCO);
        juego.terminar();

        // Act
        boolean leToca = rival.leToca();

        // Assert
        assertFalse(leToca);
    }

    @Test
    void elegirJugada_turnoDeLaPersona_vacio() {
        // Arrange
        ContraLaComputadora rival = conNegras(partidaEstandar());

        // Act
        Optional<JugadaDeLaComputadora> elegida = rival.elegirJugada();

        // Assert
        assertTrue(elegida.isEmpty());
    }

    @Test
    void elegirJugada_enSuTurno_devuelveLaJugadaDelAsesor() {
        // Arrange
        JuegoDeAjedrez juego = partidaEstandar();
        ContraLaComputadora rival = conNegras(juego);
        juego.jugar(casilla("e2"), casilla("e4"));

        // Act
        Optional<JugadaDeLaComputadora> elegida = rival.elegirJugada();

        // Assert
        assertEquals(Optional.of(new JugadaDeLaComputadora(E7_E5, "Fijo", false)), elegida);
    }

    @Test
    void elegirJugada_enSuTurno_noModificaLaPartida() {
        // Arrange
        JuegoDeAjedrez juego = partidaEstandar();
        ContraLaComputadora rival = conNegras(juego);
        juego.jugar(casilla("e2"), casilla("e4"));
        Tablero antes = juego.tablero();

        // Act
        rival.elegirJugada();

        // Assert: elegir no juega; la jugada se hace después con jugar().
        assertSame(antes, juego.tablero());
        assertEquals(Color.NEGRO, juego.turnoActual());
    }

    @Test
    void jugar_enSuTurno_mueveLaPiezaYLeTocaALaPersona() {
        // Arrange
        JuegoDeAjedrez juego = partidaEstandar();
        ContraLaComputadora rival = conNegras(juego);
        juego.jugar(casilla("e2"), casilla("e4"));
        JugadaDeLaComputadora elegida = new JugadaDeLaComputadora(E7_E5, "Fijo", false);

        // Act
        ResultadoJugada resultado = rival.jugar(elegida);

        // Assert
        assertTrue(resultado.valida());
        assertTrue(juego.tablero().piezaEn(casilla("e7")).isEmpty());
        assertEquals(Optional.of(new Pieza(Color.NEGRO, peon(), true)), juego.tablero().piezaEn(casilla("e5")));
        assertEquals(Color.BLANCO, juego.turnoActual());
    }

    @Test
    void jugar_jugadaQueCorona_coronaEnLaPiezaElegida() {
        // Arrange: el peón negro de b2 corona en b1; antes juegan las blancas, que empiezan.
        JuegoDeAjedrez juego = partidaSobre(new Tablero(8, 8)
                .con(casilla("h3"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("a8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("b2"), new Pieza(Color.NEGRO, peon()).movida()));
        ContraLaComputadora rival = conNegras(juego);
        juego.jugar(casilla("h3"), casilla("h4"));
        JugadaLegal coronaEnCaballo = new JugadaLegal(casilla("b2"), casilla("b1"), Optional.of("caballo"));
        JugadaDeLaComputadora elegida = new JugadaDeLaComputadora(coronaEnCaballo, "Fijo", false);

        // Act
        ResultadoJugada resultado = rival.jugar(elegida);

        // Assert
        assertTrue(resultado.valida());
        assertEquals(caballo(), juego.tablero().piezaEn(casilla("b1")).orElseThrow().tipo());
    }

    @Test
    void jugar_fueraDeSuTurno_noHaceLaJugada() {
        // Arrange: e2 e4 es legal para las blancas, pero la computadora juega con negras.
        JuegoDeAjedrez juego = partidaEstandar();
        ContraLaComputadora rival = conNegras(juego);
        Tablero antes = juego.tablero();
        JugadaDeLaComputadora elegida = new JugadaDeLaComputadora(E2_E4, "Fijo", false);

        // Act
        ResultadoJugada resultado = rival.jugar(elegida);

        // Assert
        assertFalse(resultado.valida());
        assertSame(antes, juego.tablero());
        assertEquals(Color.BLANCO, juego.turnoActual());
    }

    @Test
    void deshacer_despuesDeLaJugadaDeLaComputadora_vuelveAlTurnoDeLaPersona() {
        // Arrange
        JuegoDeAjedrez juego = partidaEstandar();
        Tablero inicial = juego.tablero();
        ContraLaComputadora rival = conNegras(juego);
        juego.jugar(casilla("e2"), casilla("e4"));
        rival.jugar(rival.elegirJugada().orElseThrow());

        // Act
        boolean deshizo = rival.deshacer();

        // Assert: se deshicieron las dos jugadas, la de la computadora y la de la persona.
        assertTrue(deshizo);
        assertSame(inicial, juego.tablero());
        assertEquals(Color.BLANCO, juego.turnoActual());
    }

    @Test
    void deshacer_soloLaPrimeraJugadaDeLaComputadoraConBlancas_vuelveAlPrincipio() {
        // Arrange: con blancas, la computadora jugó primero y no hay jugada de la persona para deshacer.
        JuegoDeAjedrez juego = partidaEstandar();
        Tablero inicial = juego.tablero();
        AsesorFijo asesor = new AsesorFijo(E2_E4);
        ContraLaComputadora rival = new ContraLaComputadora(juego, new Computadora(asesor, asesor, 1), Color.BLANCO);
        rival.jugar(rival.elegirJugada().orElseThrow());

        // Act
        boolean deshizo = rival.deshacer();

        // Assert
        assertTrue(deshizo);
        assertSame(inicial, juego.tablero());
        assertEquals(Color.BLANCO, juego.turnoActual());
    }

    @Test
    void deshacer_sinJugadas_devuelveFalse() {
        // Arrange
        ContraLaComputadora rival = conNegras(partidaEstandar());

        // Act
        boolean deshizo = rival.deshacer();

        // Assert
        assertFalse(deshizo);
    }

    @Test
    void rehacer_despuesDeDeshacer_vuelveAAplicarLasDosJugadas() {
        // Arrange
        JuegoDeAjedrez juego = partidaEstandar();
        ContraLaComputadora rival = conNegras(juego);
        juego.jugar(casilla("e2"), casilla("e4"));
        rival.jugar(rival.elegirJugada().orElseThrow());
        Tablero despuesDeLasDos = juego.tablero();
        rival.deshacer();

        // Act
        boolean rehizo = rival.rehacer();

        // Assert
        assertTrue(rehizo);
        assertSame(despuesDeLasDos, juego.tablero());
        assertEquals(Color.BLANCO, juego.turnoActual());
    }
}
