package juego;

import static configuracion.TiposDePiezaEstandar.alfil;
import static configuracion.TiposDePiezaEstandar.caballo;
import static configuracion.TiposDePiezaEstandar.dama;
import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;
import static soporte.Casillas.casillas;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import configuracion.TableroEstandar;
import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import reglas.DetectorDeJaque;
import reglas.GeneradorDeJugadasLegales;
import reglas.ReglaEnroqueSeguro;
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

class PartidaTest {

    private Partida partida;

    @BeforeEach
    void crearPartidaEstandar() {
        partida = partidaSobre(new TableroEstandar().crearTablero());
    }

    private static Partida partidaSobre(Tablero tablero) {
        DetectorDeJaque detector = new DetectorDeJaque();
        ValidadorDeJugadas validador = new ValidadorDeJugadas(List.of(
                new ReglaPiezaPropia(),
                new ReglaEnroqueSeguro(detector),
                new ReglaNoDejarReyEnJaque(detector)));
        return new Partida(tablero, validador, detector, new GeneradorDeJugadasLegales(validador));
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
        // Arrange: Dh5+ con el peón de f7 ya movido; las negras pueden taparse con g6, no es mate.
        jugarValidas("e2 e4", "f7 f6", "d1 h5");

        // Act
        ResultadoJugada resultado = jugar("a7", "a6");

        // Assert
        assertFalse(resultado.valida());
        assertEquals("Esa jugada deja a tu rey en jaque", resultado.motivo());
    }

    // --- Puntos ---

    @Test
    void puntos_partidaNueva_treintaYNuevePorColor() {
        // Act & Assert
        assertEquals(39, partida.puntos(Color.BLANCO));
        assertEquals(39, partida.puntos(Color.NEGRO));
    }

    @Test
    void puntos_despuesDePerderUnaTorre_bajanCinco() {
        // Act: la torre blanca captura en h5 y la torre negra la captura a ella.
        jugarValidas("h2 h4", "g7 g5", "h4 g5", "h7 h5", "h1 h5", "h8 h5");

        // Assert
        assertEquals(34, partida.puntos(Color.BLANCO));
    }

    // --- Fin de partida ---

    @Test
    void jugar_mateDelLoco_terminaYGananLasNegras() {
        // Act
        jugarValidas("f2 f3", "e7 e5", "g2 g4", "d8 h4");

        // Assert
        assertEquals(new EstadoPartida(true, Optional.of(Color.NEGRO), "Jaque mate: gana NEGRO"), partida.estado());
    }

    @Test
    void jugar_mateDelPasilloConMenosPuntos_ganaElQueDaElMate() {
        // Arrange: el rey negro está encerrado por sus peones. Negras tienen 11 puntos y blancas 5.
        Partida partida = partidaSobre(new Tablero(8, 8)
                .con(casilla("g1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("a1"), new Pieza(Color.BLANCO, torre()))
                .con(casilla("g8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("h8"), new Pieza(Color.NEGRO, torre()))
                .con(casilla("h6"), new Pieza(Color.NEGRO, alfil()))
                .con(casilla("f7"), new Pieza(Color.NEGRO, peon()))
                .con(casilla("g7"), new Pieza(Color.NEGRO, peon()))
                .con(casilla("h7"), new Pieza(Color.NEGRO, peon())));

        // Act
        partida.jugar(casilla("a1"), casilla("a8"));

        // Assert
        assertEquals(new EstadoPartida(true, Optional.of(Color.BLANCO), "Jaque mate: gana BLANCO"), partida.estado());
    }

    @Test
    void jugar_ahogadoConMasPuntosDeUnLado_esTablasIgual() {
        // Arrange: la dama va a c7 y el rey negro de a8 se queda sin jugadas, sin estar en jaque.
        // Blancas tienen 9 puntos y negras 0, pero el ahogado no mira los puntos.
        Partida partida = partidaSobre(new Tablero(8, 8)
                .con(casilla("h1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("c1"), new Pieza(Color.BLANCO, dama()))
                .con(casilla("a8"), new Pieza(Color.NEGRO, rey())));

        // Act
        partida.jugar(casilla("c1"), casilla("c7"));

        // Assert
        assertEquals(new EstadoPartida(true, Optional.empty(), "Ahogado: tablas"), partida.estado());
    }

    @Test
    void terminar_conVentajaDeMaterial_ganaElQueTieneMasPuntos() {
        // Arrange: blancas pierden torre y peón (34), negras dos peones (37).
        jugarValidas("h2 h4", "g7 g5", "h4 g5", "h7 h5", "h1 h5", "h8 h5");

        // Act
        EstadoPartida estado = partida.terminar();

        // Assert
        assertEquals(new EstadoPartida(true, Optional.of(Color.NEGRO),
                "Partida terminada por los jugadores: gana NEGRO por 37 a 34 puntos"), estado);
    }

    @Test
    void terminar_conMaterialIgual_esTablas() {
        // Act
        EstadoPartida estado = partida.terminar();

        // Assert
        assertTrue(estado.terminada());
        assertEquals(Optional.empty(), estado.ganador());
    }

    @Test
    void rendirse_conMasPuntos_ganaElRivalIgual() {
        // Arrange: negras van ganando 37 a 34 y les toca mover.
        jugarValidas("h2 h4", "g7 g5", "h4 g5", "h7 h5", "h1 h5", "h8 h5", "a2 a3");

        // Act
        EstadoPartida estado = partida.rendirse();

        // Assert
        assertEquals(new EstadoPartida(true, Optional.of(Color.BLANCO), "NEGRO se rinde: gana BLANCO"), estado);
    }

    @Test
    void rendirse_conLaPartidaYaTerminada_noCambiaElResultado() {
        // Arrange
        EstadoPartida antes = partida.terminar();

        // Act & Assert
        assertEquals(antes, partida.rendirse());
    }

    // --- Deshacer / rehacer ---

    @Test
    void deshacer_despuesDeUnaJugada_vuelveAlMismoTableroYTurno() {
        // Arrange
        Tablero inicial = partida.tablero();
        jugarValidas("e2 e4");

        // Act
        boolean deshizo = partida.deshacer();

        // Assert
        assertTrue(deshizo);
        assertSame(inicial, partida.tablero());
        assertEquals(Color.BLANCO, partida.turnoActual());
    }

    @Test
    void deshacer_unaCaptura_devuelveLaPiezaYLosPuntos() {
        // Arrange: la torre negra captura a la torre blanca en h5.
        jugarValidas("h2 h4", "g7 g5", "h4 g5", "h7 h5", "h1 h5", "h8 h5");

        // Act
        partida.deshacer();

        // Assert
        assertEquals(39, partida.puntos(Color.BLANCO));
        assertEquals(Color.NEGRO, partida.turnoActual());
    }

    @Test
    void rehacer_despuesDeDeshacer_vuelveALaPosicion() {
        // Arrange
        jugarValidas("e2 e4");
        Tablero despuesDeJugar = partida.tablero();
        partida.deshacer();

        // Act
        boolean rehizo = partida.rehacer();

        // Assert
        assertTrue(rehizo);
        assertSame(despuesDeJugar, partida.tablero());
        assertEquals(Color.NEGRO, partida.turnoActual());
    }

    @Test
    void rehacer_despuesDeUnaJugadaNueva_noHayNadaParaRehacer() {
        // Arrange
        jugarValidas("e2 e4");
        partida.deshacer();
        jugarValidas("d2 d4");

        // Act & Assert
        assertFalse(partida.rehacer());
    }

    @Test
    void deshacer_unMate_reabreLaPartida() {
        // Arrange
        jugarValidas("f2 f3", "e7 e5", "g2 g4", "d8 h4");

        // Act
        partida.deshacer();

        // Assert
        assertEquals(EstadoPartida.enCurso(), partida.estado());
        assertEquals(Color.NEGRO, partida.turnoActual());
    }

    @Test
    void deshacer_unaRendicion_reabreLaPartidaSinDeshacerJugadas() {
        // Arrange
        jugarValidas("e2 e4");
        Tablero antesDeRendirse = partida.tablero();
        partida.rendirse();

        // Act
        partida.deshacer();

        // Assert
        assertEquals(EstadoPartida.enCurso(), partida.estado());
        assertSame(antesDeRendirse, partida.tablero());
    }

    @Test
    void deshacer_sinJugadas_devuelveFalse() {
        // Act & Assert
        assertFalse(partida.deshacer());
    }

    @Test
    void rehacer_sinNadaDeshecho_devuelveFalse() {
        // Arrange
        jugarValidas("e2 e4");

        // Act & Assert
        assertFalse(partida.rehacer());
    }

    @Test
    void deshacer_jugadaRechazada_noSeGuarda() {
        // Arrange: e2 e5 no es válida, así que no hay nada para deshacer.
        jugar("e2", "e5");

        // Act & Assert
        assertFalse(partida.deshacer());
    }

    // --- Promoción de peón ---

    /** Rey blanco en h1, rey negro en a3 y un peón blanco en e7 a punto de coronar. */
    private static Partida partidaConPeonEnE7() {
        return partidaSobre(new Tablero(8, 8)
                .con(casilla("h1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("a3"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("e7"), new Pieza(Color.BLANCO, peon()).movida()));
    }

    @Test
    void jugar_peonQueLlegaAlFinalSinElegir_coronaEnDama() {
        // Arrange
        Partida partida = partidaConPeonEnE7();

        // Act
        ResultadoJugada resultado = partida.jugar(casilla("e7"), casilla("e8"));

        // Assert
        assertTrue(resultado.valida());
        assertEquals(dama(), partida.tablero().piezaEn(casilla("e8")).orElseThrow().tipo());
    }

    @Test
    void jugar_peonQueLlegaAlFinalPidiendoCaballo_coronaEnCaballo() {
        // Arrange
        Partida partida = partidaConPeonEnE7();

        // Act
        ResultadoJugada resultado = partida.jugar(casilla("e7"), casilla("e8"), "caballo");

        // Assert
        assertTrue(resultado.valida());
        assertEquals(caballo(), partida.tablero().piezaEn(casilla("e8")).orElseThrow().tipo());
    }

    @Test
    void jugar_peonNegroQueCapturaEnLaPrimeraFila_coronaEnLoQuePide() {
        // Arrange: blancas mueven el rey y el peón negro de d2 captura la torre de e1.
        Partida partida = partidaSobre(new Tablero(8, 8)
                .con(casilla("h4"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("e1"), new Pieza(Color.BLANCO, torre()))
                .con(casilla("a6"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("d2"), new Pieza(Color.NEGRO, peon()).movida()));
        partida.jugar(casilla("h4"), casilla("h5"));

        // Act
        ResultadoJugada resultado = partida.jugar(casilla("d2"), casilla("e1"), "torre");

        // Assert
        assertTrue(resultado.valida());
        assertEquals(Optional.of(new Pieza(Color.NEGRO, torre()).movida()), partida.tablero().piezaEn(casilla("e1")));
    }

    @Test
    void jugar_coronarEnUnTipoQueNoSePuede_esRechazado() {
        // Arrange
        Partida partida = partidaConPeonEnE7();

        // Act
        ResultadoJugada resultado = partida.jugar(casilla("e7"), casilla("e8"), "rey");

        // Assert
        assertFalse(resultado.valida());
        assertEquals("No se puede coronar en rey", resultado.motivo());
    }

    @Test
    void jugar_conPromocionEnUnaJugadaQueNoCorona_laIgnora() {
        // Act
        ResultadoJugada resultado = partida.jugar(casilla("e2"), casilla("e4"), "dama");

        // Assert
        assertTrue(resultado.valida());
        assertEquals(peon(), partida.tablero().piezaEn(casilla("e4")).orElseThrow().tipo());
    }

    @Test
    void puntos_alCoronarEnDama_sumanOcho() {
        // Arrange
        Partida partida = partidaConPeonEnE7();
        int antes = partida.puntos(Color.BLANCO);

        // Act
        partida.jugar(casilla("e7"), casilla("e8"), "dama");

        // Assert
        assertEquals(antes + 8, partida.puntos(Color.BLANCO));
    }

    @Test
    void deshacer_unaCoronacion_devuelveElPeon() {
        // Arrange
        Partida partida = partidaConPeonEnE7();
        partida.jugar(casilla("e7"), casilla("e8"), "dama");

        // Act
        partida.deshacer();

        // Assert
        assertEquals(peon(), partida.tablero().piezaEn(casilla("e7")).orElseThrow().tipo());
        assertTrue(partida.tablero().piezaEn(casilla("e8")).isEmpty());
    }

    @Test
    void opcionesDeCoronacion_peonQueLlegaAlFinal_lasCuatroPiezas() {
        // Arrange
        Partida partida = partidaConPeonEnE7();

        // Act & Assert
        assertEquals(List.of("dama", "torre", "alfil", "caballo"),
                partida.opcionesDeCoronacion(casilla("e7"), casilla("e8")));
    }

    @Test
    void opcionesDeCoronacion_jugadaQueNoCorona_vacio() {
        // Act & Assert
        assertTrue(partida.opcionesDeCoronacion(casilla("e2"), casilla("e4")).isEmpty());
    }

    // --- Destinos para resaltar en la interfaz ---

    @Test
    void destinosLegales_peonInicial_avanzaUnaODosCasillas() {
        // Act & Assert
        assertEquals(casillas("e3", "e4"), Set.copyOf(partida.destinosLegales(casilla("e2"))));
    }

    @Test
    void destinosLegales_caballoInicial_saltaAa3YC3() {
        // Act & Assert
        assertEquals(casillas("a3", "c3"), Set.copyOf(partida.destinosLegales(casilla("b1"))));
    }

    @Test
    void destinosLegales_piezaDelRival_vacio() {
        // Act & Assert
        assertTrue(partida.destinosLegales(casilla("e7")).isEmpty());
        assertTrue(partida.destinosIlegales(casilla("e7")).isEmpty());
    }

    @Test
    void destinosIlegales_piezaClavada_marcaSusDiagonales() {
        // Arrange: el alfil de e2 tapa a la torre negra de e8.
        Partida partida = partidaSobre(new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("h8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("e2"), new Pieza(Color.BLANCO, alfil()))
                .con(casilla("e8"), new Pieza(Color.NEGRO, torre())));

        // Act & Assert
        assertTrue(partida.destinosLegales(casilla("e2")).isEmpty());
        assertEquals(casillas("d1", "f1", "d3", "c4", "b5", "a6", "f3", "g4", "h5"),
                Set.copyOf(partida.destinosIlegales(casilla("e2"))));
    }

    @Test
    void destinosIlegales_reyQueSeMeteEnJaque_marcaEsaCasilla() {
        // Arrange: la torre negra de d8 controla toda la columna d.
        Partida partida = partidaSobre(new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("h8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("d8"), new Pieza(Color.NEGRO, torre())));

        // Act & Assert
        assertEquals(casillas("e2", "f1", "f2"), Set.copyOf(partida.destinosLegales(casilla("e1"))));
        assertEquals(casillas("d1", "d2"), Set.copyOf(partida.destinosIlegales(casilla("e1"))));
    }

    @Test
    void destinosLegales_partidaTerminada_vacio() {
        // Arrange
        partida.terminar();

        // Act & Assert
        assertTrue(partida.destinosLegales(casilla("e2")).isEmpty());
        assertTrue(partida.destinosIlegales(casilla("e2")).isEmpty());
    }

    @Test
    void jugar_conLaPartidaTerminada_esRechazada() {
        // Arrange
        partida.terminar();

        // Act
        ResultadoJugada resultado = jugar("e2", "e4");

        // Assert
        assertFalse(resultado.valida());
        assertEquals("La partida terminó", resultado.motivo());
    }

    // --- Enroque ---

    /** Rey blanco en e1 con la torre de h1, y rey negro en a8. */
    private static Partida partidaListaParaEnrocar() {
        return partidaSobre(new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("h1"), new Pieza(Color.BLANCO, torre()))
                .con(casilla("a8"), new Pieza(Color.NEGRO, rey())));
    }

    @Test
    void jugar_enroqueCortoEnUnaApertura_mueveElReyYLaTorre() {
        // Arrange
        jugarValidas("e2 e4", "e7 e5", "g1 f3", "b8 c6", "f1 c4", "g8 f6");

        // Act: el rey se mueve dos casillas hacia la torre.
        ResultadoJugada resultado = jugar("e1", "g1");

        // Assert
        assertTrue(resultado.valida());
        assertEquals(rey(), partida.tablero().piezaEn(casilla("g1")).orElseThrow().tipo());
        assertEquals(torre(), partida.tablero().piezaEn(casilla("f1")).orElseThrow().tipo());
        assertTrue(partida.tablero().piezaEn(casilla("h1")).isEmpty());
        assertEquals(Color.NEGRO, partida.turnoActual());
    }

    @Test
    void jugar_enroqueLargoDeLosDosColores_dejaLasTorresEnD1YD8() {
        // Arrange
        jugarValidas("d2 d4", "d7 d5", "b1 c3", "b8 c6", "c1 f4", "c8 f5", "d1 d2", "d8 d7");

        // Act
        ResultadoJugada blancas = jugar("e1", "c1");
        ResultadoJugada negras = jugar("e8", "c8");

        // Assert
        assertTrue(blancas.valida());
        assertTrue(negras.valida());
        assertEquals(torre(), partida.tablero().piezaEn(casilla("d1")).orElseThrow().tipo());
        assertEquals(torre(), partida.tablero().piezaEn(casilla("d8")).orElseThrow().tipo());
        assertEquals(rey(), partida.tablero().piezaEn(casilla("c8")).orElseThrow().tipo());
    }

    @Test
    void jugar_reyQueSeMovioYVolvio_yaNoPuedeEnrocar() {
        // Arrange
        jugarValidas("e2 e4", "e7 e5", "g1 f3", "b8 c6", "f1 c4", "g8 f6", "e1 e2", "h7 h6", "e2 e1", "h6 h5");

        // Act
        ResultadoJugada resultado = jugar("e1", "g1");

        // Assert
        assertFalse(resultado.valida());
        assertEquals("Esa pieza no puede moverse a esa casilla", resultado.motivo());
    }

    @Test
    void jugar_enroqueConElReyEnJaque_esRechazadoYDiceElMotivo() {
        // Arrange: la torre negra de e8 le da jaque al rey blanco.
        partida = partidaSobre(new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("h1"), new Pieza(Color.BLANCO, torre()))
                .con(casilla("a8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("e8"), new Pieza(Color.NEGRO, torre())));

        // Act
        ResultadoJugada resultado = jugar("e1", "g1");

        // Assert
        assertFalse(resultado.valida());
        assertTrue(resultado.motivo().startsWith("No se puede enrocar"));
    }

    @Test
    void deshacer_unEnroque_devuelveReyYTorreSinMoverYSePuedeVolverAEnrocar() {
        // Arrange
        partida = partidaListaParaEnrocar();
        jugarValidas("e1 g1");

        // Act
        partida.deshacer();

        // Assert
        assertEquals(Optional.of(new Pieza(Color.BLANCO, rey())), partida.tablero().piezaEn(casilla("e1")));
        assertEquals(Optional.of(new Pieza(Color.BLANCO, torre())), partida.tablero().piezaEn(casilla("h1")));
        assertTrue(partida.destinosLegales(casilla("e1")).contains(casilla("g1")));
    }

    @Test
    void destinosLegales_reyQuePuedeEnrocar_incluyeLaCasillaDelEnroque() {
        // Arrange
        partida = partidaListaParaEnrocar();

        // Act & Assert
        assertEquals(casillas("d1", "d2", "e2", "f2", "f1", "g1"), Set.copyOf(partida.destinosLegales(casilla("e1"))));
    }

    @Test
    void destinosIlegales_enroqueQueCruzaUnaCasillaAtacada_loMarca() {
        // Arrange: la torre negra de f8 controla toda la columna f.
        partida = partidaSobre(new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("h1"), new Pieza(Color.BLANCO, torre()))
                .con(casilla("a8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("f8"), new Pieza(Color.NEGRO, torre())));

        // Act & Assert
        assertEquals(casillas("d1", "d2", "e2"), Set.copyOf(partida.destinosLegales(casilla("e1"))));
        assertEquals(casillas("f1", "f2", "g1"), Set.copyOf(partida.destinosIlegales(casilla("e1"))));
    }
}
