package movimiento;

import static configuracion.TiposDePiezaEstandar.caballo;
import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;
import static soporte.Casillas.casillas;
import static soporte.Casillas.destinos;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

class EnroqueTest {

    private static final Pieza REY_BLANCO = new Pieza(Color.BLANCO, rey());
    private static final Pieza TORRE_BLANCA = new Pieza(Color.BLANCO, torre());

    /** Rey blanco en e1 con sus dos torres, nadie se movió. */
    private static Tablero reyYTorresBlancas() {
        return new Tablero(8, 8)
                .con(casilla("e1"), REY_BLANCO)
                .con(casilla("a1"), TORRE_BLANCA)
                .con(casilla("h1"), TORRE_BLANCA);
    }

    private static Set<Posicion> destinosDelRey(Tablero tablero, String casilla) {
        Pieza rey = tablero.piezaEn(casilla(casilla)).orElseThrow();
        return destinos(rey.jugadasCandidatas(tablero, casilla(casilla)));
    }

    /** La jugada del rey que llega a "hasta" (el enroque, si el rey se mueve dos casillas). */
    private static Jugada jugadaDelRey(Tablero tablero, String desde, String hasta) {
        Pieza rey = tablero.piezaEn(casilla(desde)).orElseThrow();
        return rey.jugadasCandidatas(tablero, casilla(desde)).stream()
                .filter(jugada -> jugada.hasta().equals(casilla(hasta)))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void rey_conLasDosTorresSinMover_proponeEnroqueCortoYLargo() {
        // Act
        Set<Posicion> destinos = destinosDelRey(reyYTorresBlancas(), "e1");

        // Assert: además de los cinco pasos de rey, g1 y c1.
        assertEquals(casillas("d1", "d2", "e2", "f2", "f1", "g1", "c1"), destinos);
    }

    @Test
    void enroqueCorto_aplicado_dejaAlReyEnG1YALaTorreEnF1() {
        // Arrange
        Tablero tablero = reyYTorresBlancas();

        // Act
        Tablero despues = jugadaDelRey(tablero, "e1", "g1").aplicarEn(tablero);

        // Assert
        assertEquals(Optional.of(REY_BLANCO.movida()), despues.piezaEn(casilla("g1")));
        assertEquals(Optional.of(TORRE_BLANCA.movida()), despues.piezaEn(casilla("f1")));
        assertTrue(despues.piezaEn(casilla("e1")).isEmpty());
        assertTrue(despues.piezaEn(casilla("h1")).isEmpty());
    }

    @Test
    void enroqueLargo_deLasNegras_dejaAlReyEnC8YALaTorreEnD8() {
        // Arrange
        Pieza reyNegro = new Pieza(Color.NEGRO, rey());
        Pieza torreNegra = new Pieza(Color.NEGRO, torre());
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e8"), reyNegro)
                .con(casilla("a8"), torreNegra);

        // Act
        Tablero despues = jugadaDelRey(tablero, "e8", "c8").aplicarEn(tablero);

        // Assert
        assertEquals(Optional.of(reyNegro.movida()), despues.piezaEn(casilla("c8")));
        assertEquals(Optional.of(torreNegra.movida()), despues.piezaEn(casilla("d8")));
        assertTrue(despues.piezaEn(casilla("a8")).isEmpty());
        assertTrue(despues.piezaEn(casilla("e8")).isEmpty());
    }

    @Test
    void rey_queYaSeMovio_noEnroca() {
        // Arrange
        Tablero tablero = reyYTorresBlancas().con(casilla("e1"), REY_BLANCO.movida());

        // Act
        Set<Posicion> destinos = destinosDelRey(tablero, "e1");

        // Assert
        assertEquals(casillas("d1", "d2", "e2", "f2", "f1"), destinos);
    }

    @Test
    void torre_queYaSeMovio_noEnrocaDeEseLado() {
        // Arrange
        Tablero tablero = reyYTorresBlancas().con(casilla("h1"), TORRE_BLANCA.movida());

        // Act
        Set<Posicion> destinos = destinosDelRey(tablero, "e1");

        // Assert: el largo sigue en pie, el corto no.
        assertTrue(destinos.contains(casilla("c1")));
        assertFalse(destinos.contains(casilla("g1")));
    }

    @Test
    void rey_conUnaPiezaEntreElYLaTorre_noEnrocaDeEseLado() {
        // Arrange: el caballo de b1 no está en el camino del rey, pero sí en el de la torre.
        Tablero tablero = reyYTorresBlancas().con(casilla("b1"), new Pieza(Color.BLANCO, caballo()));

        // Act
        Set<Posicion> destinos = destinosDelRey(tablero, "e1");

        // Assert
        assertFalse(destinos.contains(casilla("c1")));
        assertTrue(destinos.contains(casilla("g1")));
    }

    @Test
    void rey_conUnaTorreRivalEnLaEsquina_noEnroca() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e1"), REY_BLANCO)
                .con(casilla("h1"), new Pieza(Color.NEGRO, torre()));

        // Act
        Set<Posicion> destinos = destinosDelRey(tablero, "e1");

        // Assert
        assertEquals(casillas("d1", "d2", "e2", "f2", "f1"), destinos);
    }

    @Test
    void jugadaDeEnroque_casillasQueNoPuedenEstarAtacadas_laDeSalidaLaQueCruzaYLaDeLlegada() {
        // Arrange
        Jugada enroqueCorto = jugadaDelRey(reyYTorresBlancas(), "e1", "g1");

        // Act
        List<Posicion> casillas = enroqueCorto.casillasQueNoPuedenEstarAtacadas();

        // Assert
        assertEquals(List.of(casilla("e1"), casilla("f1"), casilla("g1")), casillas);
    }

    @Test
    void jugadaSimple_casillasQueNoPuedenEstarAtacadas_vacio() {
        // Act & Assert
        assertTrue(new JugadaSimple(casilla("e1"), casilla("f1")).casillasQueNoPuedenEstarAtacadas().isEmpty());
    }
}
