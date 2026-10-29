package ia;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Test;

import juego.JugadaLegal;
import modelo.Color;
import modelo.Tablero;

class AsesorAleatorioTest {

    private static final List<JugadaLegal> LEGALES = List.of(
            new JugadaLegal(casilla("e2"), casilla("e4"), Optional.empty()),
            new JugadaLegal(casilla("d2"), casilla("d4"), Optional.empty()),
            new JugadaLegal(casilla("g1"), casilla("f3"), Optional.empty()));

    @Test
    void sugerirJugada_conCienSemillasDistintas_siempreDevuelveUnaDeLaLista() {
        // Arrange
        Tablero tablero = new Tablero(8, 8);

        for (int semilla = 0; semilla < 100; semilla++) {
            AsesorAleatorio asesor = new AsesorAleatorio(new Random(semilla));

            // Act
            Optional<JugadaLegal> sugerida = asesor.sugerirJugada(tablero, Color.BLANCO, LEGALES);

            // Assert
            assertTrue(sugerida.isPresent() && LEGALES.contains(sugerida.get()));
        }
    }

    @Test
    void sugerirJugada_sinJugadasLegales_vacio() {
        // Act & Assert
        assertTrue(new AsesorAleatorio(new Random(1)).sugerirJugada(new Tablero(8, 8), Color.BLANCO, List.of()).isEmpty());
    }
}
