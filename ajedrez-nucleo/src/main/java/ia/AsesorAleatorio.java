package ia;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import juego.JugadaLegal;
import modelo.Color;
import modelo.Tablero;

/**
 * Elige cualquiera de las jugadas legales. Es la dificultad más fácil, el respaldo cuando el asesor principal
 * falla y un doble de prueba. El Random llega por constructor: con una semilla fija, los tests son repetibles.
 */
public final class AsesorAleatorio implements AsesorDeJugadas {

    private final Random azar;

    public AsesorAleatorio(Random azar) {
        this.azar = azar;
    }

    @Override
    public String nombre() {
        return "Aleatorio";
    }

    @Override
    public Optional<JugadaLegal> sugerirJugada(Tablero tablero, Color turno, List<JugadaLegal> legales) {
        if (legales.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(legales.get(azar.nextInt(legales.size())));
    }
}
