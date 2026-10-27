package consola;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import configuracion.TableroEstandar;
import juego.JuegoDeAjedrez;
import juego.Partida;
import reglas.DetectorDeJaque;
import reglas.GeneradorDeJugadasLegales;
import reglas.ReglaEnroqueSeguro;
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

class AdaptadorConsolaTest {

    /** Corre la consola con un texto como si fuera el teclado y devuelve todo lo que imprimió. */
    private String ejecutarCon(String entrada) {
        DetectorDeJaque detector = new DetectorDeJaque();
        ValidadorDeJugadas validador = new ValidadorDeJugadas(List.of(
                new ReglaPiezaPropia(), new ReglaEnroqueSeguro(detector), new ReglaNoDejarReyEnJaque(detector)));
        JuegoDeAjedrez juego = new Partida(new TableroEstandar().crearTablero(), validador, detector,
                new GeneradorDeJugadasLegales(validador));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8);

        new AdaptadorConsola(juego, new TraductorDeNotacion(), new DibujanteAscii(),
                new Scanner(entrada), salida).ejecutar();

        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void ejecutar_jugadaValida_redibujaElTableroConLaPiezaMovida() {
        // Act
        String salida = ejecutarCon("e2 e4\nsalir\n");

        // Assert
        assertTrue(salida.contains(" 4  . . . . P . . ."));
        assertTrue(salida.contains("Turno de NEGRO"));
    }

    @Test
    void ejecutar_jugadaInvalida_muestraElMotivoQueDaElNucleo() {
        // Act
        String salida = ejecutarCon("e2 e5\nsalir\n");

        // Assert
        assertTrue(salida.contains("Jugada inválida: Esa pieza no puede moverse a esa casilla"));
    }

    @Test
    void ejecutar_textoMalEscrito_explicaElFormatoYSigueJugando() {
        // Act
        String salida = ejecutarCon("hola\ne2 e4\nsalir\n");

        // Assert
        assertTrue(salida.contains("por ejemplo: e2 e4"));
        assertTrue(salida.contains("Turno de NEGRO"));
    }

    @Test
    void ejecutar_reyDosCasillasHaciaLaTorre_enrocaYLaTorreQuedaAlLado() {
        // Act
        String salida = ejecutarCon("e2 e4\ne7 e5\ng1 f3\nb8 c6\nf1 c4\ng8 f6\ne1 g1\nsalir\n");

        // Assert
        assertTrue(salida.contains(" 1  T C A D . T R ."));
    }

    @Test
    void ejecutar_casillaFueraDelTablero_loInforma() {
        // Act
        String salida = ejecutarCon("e2 e9\nsalir\n");

        // Assert
        assertTrue(salida.contains("fuera del tablero"));
    }

    @Test
    void ejecutar_jugadaQueDaJaque_loAnuncia() {
        // Act
        String salida = ejecutarCon("f2 f3\ne7 e5\ng2 g4\nd8 h4\nsalir\n");

        // Assert
        assertTrue(salida.contains("¡Jaque al rey BLANCO!"));
    }

    @Test
    void ejecutar_comandoTerminar_decidePorPuntosYCierraLaPartida() {
        // Act: después de "terminar" ya no se lee "e2 e4".
        String salida = ejecutarCon("terminar\ne2 e4\n");

        // Assert
        assertTrue(salida.contains("Puntos: BLANCO 39 · NEGRO 39"));
        assertTrue(salida.contains("Fin de la partida. Partida terminada por los jugadores: tablas, 39 a 39 puntos"));
    }

    @Test
    void ejecutar_jaqueMate_anunciaElGanador() {
        // Act
        String salida = ejecutarCon("f2 f3\ne7 e5\ng2 g4\nd8 h4\n");

        // Assert
        assertTrue(salida.contains("Ganador: NEGRO"));
        assertTrue(salida.contains("Fin de la partida. Jaque mate: gana NEGRO"));
    }

    @Test
    void ejecutar_enJaque_ofreceRendirseYAlRendirseGanaElRival() {
        // Act: Dh5+ pone en jaque a las negras, que se rinden.
        String salida = ejecutarCon("e2 e4\nf7 f6\nd1 h5\nrendirse\n");

        // Assert
        assertTrue(salida.contains("Turno de NEGRO (ejemplo: e2 e4, 'rendirse', "));
        assertTrue(salida.contains("Ganador: BLANCO"));
        assertTrue(salida.contains("NEGRO se rinde: gana BLANCO"));
    }

    @Test
    void ejecutar_comandoDeshacer_vuelveElPeonYElTurno() {
        // Act
        String salida = ejecutarCon("e2 e4\ndeshacer\nsalir\n");

        // Assert: el último tablero dibujado vuelve a tener el peón en e2 y le toca a BLANCO.
        String despuesDeDeshacer = salida.substring(salida.lastIndexOf(" 8  "));
        assertTrue(despuesDeDeshacer.contains(" 2  P P P P P P P P"));
        assertTrue(despuesDeDeshacer.contains("Turno de BLANCO"));
    }

    @Test
    void ejecutar_comandoRehacer_vuelveAAplicarLaJugada() {
        // Act
        String salida = ejecutarCon("e2 e4\ndeshacer\nrehacer\nsalir\n");

        // Assert: se dibujaron tres tableros más el inicial, y el último es el de después de e2 e4.
        assertEquals(4, salida.split(" 8  ", -1).length - 1);
        String despuesDeRehacer = salida.substring(salida.lastIndexOf(" 8  "));
        assertTrue(despuesDeRehacer.contains(" 4  . . . . P . . ."));
        assertTrue(despuesDeRehacer.contains("Turno de NEGRO"));
    }

    @Test
    void ejecutar_deshacerSinJugadas_loAvisa() {
        // Act
        String salida = ejecutarCon("deshacer\nrehacer\nsalir\n");

        // Assert
        assertTrue(salida.contains("No hay nada para deshacer"));
        assertTrue(salida.contains("No hay nada para rehacer"));
    }

    @Test
    void ejecutar_comandoPuntos_muestraLosPuntosDeCadaColor() {
        // Act: "puntos" vuelve a imprimir la línea de puntos, que ya salió una vez debajo del tablero.
        String salida = ejecutarCon("puntos\nsalir\n");

        // Assert
        assertEquals(2, salida.split("Puntos: BLANCO 39 · NEGRO 39", -1).length - 1);
    }

    @Test
    void ejecutar_peonQueCoronaConLaPiezaEscrita_quedaEsaPieza() {
        // Act: el peón de a2 come en b5, a6 y b7, y corona en a8 comiendo la torre.
        String salida = ejecutarCon("a2 a4\nb7 b5\na4 b5\na7 a6\nb5 a6\nc8 b7\na6 b7\nb8 c6\nb7 a8 caballo\nsalir\n");

        // Assert: en a8 queda un caballo blanco (C).
        assertTrue(salida.contains(" 8  C . . d r a c t"));
    }

    @Test
    void ejecutar_seTerminaLaEntrada_terminaSinError() {
        // Act & Assert
        assertDoesNotThrow(() -> ejecutarCon(""));
    }
}
