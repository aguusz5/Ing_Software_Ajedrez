package ia;

import static configuracion.TiposDePiezaEstandar.caballo;
import static configuracion.TiposDePiezaEstandar.dama;
import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Test;

import juego.JugadaLegal;
import modelo.Color;
import modelo.Pieza;
import modelo.Tablero;
import movimiento.JugadaSimple;
import reglas.DetectorDeJaque;
import reglas.GeneradorDeJugadasLegales;
import reglas.ReglaEnroqueSeguro;
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

class AsesorMinimaxTest {

    private final DetectorDeJaque detector = new DetectorDeJaque();
    private final GeneradorDeJugadasLegales generador = new GeneradorDeJugadasLegales(new ValidadorDeJugadas(List.of(
            new ReglaPiezaPropia(), new ReglaEnroqueSeguro(detector), new ReglaNoDejarReyEnJaque(detector))));

    /** Le pregunta al minimax de profundidad 3 qué jugaría el color dado en ese tablero. */
    private JugadaLegal sugerida(Tablero tablero, Color turno) {
        AsesorMinimax minimax = new AsesorMinimax(generador, detector, 3, new Random(1));
        List<JugadaLegal> legales = generador.jugadasLegales(tablero, turno).stream().map(JugadaLegal::de).toList();
        return minimax.sugerirJugada(tablero, turno, legales).orElseThrow();
    }

    private static JugadaLegal jugada(String desde, String hasta) {
        return new JugadaLegal(casilla(desde), casilla(hasta), Optional.empty());
    }

    @Test
    void sugerirJugada_damaRivalSinDefensa_laCaptura() {
        // Arrange: el caballo de c3 alcanza a la dama negra de d5, que nadie defiende.
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("c3"), new Pieza(Color.BLANCO, caballo()))
                .con(casilla("h8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("d5"), new Pieza(Color.NEGRO, dama()));

        // Act & Assert
        assertEquals(jugada("c3", "d5"), sugerida(tablero, Color.BLANCO));
    }

    @Test
    void sugerirJugada_damaPropiaAtacadaPorUnPeon_laSaca() {
        // Arrange: el peón negro de e5 ataca a la dama blanca de d4; cualquier otra jugada la pierde.
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("a1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("d4"), new Pieza(Color.BLANCO, dama()))
                .con(casilla("h2"), new Pieza(Color.BLANCO, peon()))
                .con(casilla("h8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("e5"), new Pieza(Color.NEGRO, peon()).movida())
                .con(casilla("f6"), new Pieza(Color.NEGRO, peon()).movida());

        // Act & Assert
        assertEquals(casilla("d4"), sugerida(tablero, Color.BLANCO).desde());
    }

    @Test
    void sugerirJugada_mateEnUnaJugada_loDa() {
        // Arrange: mate del pasillo, la torre de a1 llega a a8 y el rey negro no tiene salida.
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("g1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("a1"), new Pieza(Color.BLANCO, torre()))
                .con(casilla("g8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("f7"), new Pieza(Color.NEGRO, peon()))
                .con(casilla("g7"), new Pieza(Color.NEGRO, peon()))
                .con(casilla("h7"), new Pieza(Color.NEGRO, peon()));

        // Act & Assert
        assertEquals(jugada("a1", "a8"), sugerida(tablero, Color.BLANCO));
    }

    @Test
    void sugerirJugada_rivalAmenazaMateEnUna_loEvita() {
        // Arrange: si las blancas no hacen nada, la torre negra de a8 da mate del pasillo en a1.
        // El caballo de h5 no llega a tapar la primera fila.
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("g1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("f2"), new Pieza(Color.BLANCO, peon()))
                .con(casilla("g2"), new Pieza(Color.BLANCO, peon()))
                .con(casilla("h2"), new Pieza(Color.BLANCO, peon()))
                .con(casilla("h5"), new Pieza(Color.BLANCO, caballo()))
                .con(casilla("g8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("a8"), new Pieza(Color.NEGRO, torre()));
        Tablero sinHacerLugar = new JugadaSimple(casilla("h5"), casilla("f4")).aplicarEn(tablero);
        assertTrue(tieneMateEnUna(sinHacerLugar, Color.NEGRO), "La amenaza de mate tiene que ser real");
        JugadaLegal elegida = sugerida(tablero, Color.BLANCO);

        // Act
        Tablero despues = generador.jugadasLegales(tablero, Color.BLANCO).stream()
                .filter(jugada -> JugadaLegal.de(jugada).equals(elegida))
                .findFirst().orElseThrow()
                .aplicarEn(tablero);

        // Assert
        assertFalse(tieneMateEnUna(despues, Color.NEGRO));
    }

    /** Si "color" tiene alguna jugada que deja al rival en jaque y sin jugadas legales. */
    private boolean tieneMateEnUna(Tablero tablero, Color color) {
        return generador.jugadasLegales(tablero, color).stream()
                .map(jugada -> jugada.aplicarEn(tablero))
                .anyMatch(despues -> detector.estaEnJaque(despues, color.contrario())
                        && generador.jugadasLegales(despues, color.contrario()).isEmpty());
    }

    @Test
    void constructor_profundidadCero_esRechazado() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new AsesorMinimax(generador, detector, 0, new Random(1)));
    }
}
