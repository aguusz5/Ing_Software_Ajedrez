package consola;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

class AdaptadorConsolaTest {

    /** Corre la consola con un texto como si fuera el teclado y devuelve todo lo que imprimió. */
    private String ejecutarCon(String entrada) {
        DetectorDeJaque detector = new DetectorDeJaque();
        JuegoDeAjedrez juego = new Partida(new TableroEstandar().crearTablero(),
                new ValidadorDeJugadas(List.of(new ReglaPiezaPropia(), new ReglaNoDejarReyEnJaque(detector))),
                detector);
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
    void ejecutar_seTerminaLaEntrada_terminaSinError() {
        // Act & Assert
        assertDoesNotThrow(() -> ejecutarCon(""));
    }
}
