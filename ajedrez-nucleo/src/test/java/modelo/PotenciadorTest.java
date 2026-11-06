package modelo;

import static configuracion.PotenciadoresStarWars.fuerza;
import static configuracion.PotenciadoresStarWars.hiperespacio;
import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;
import static soporte.Casillas.destinos;
import static soporte.Excepciones.excepcionDe;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import movimiento.Jugada;
import reglas.DetectorDeJaque;

/** Cómo una pieza toma, usa y pierde un potenciador, y cómo lo guarda el tablero (decisión 36). */
class PotenciadorTest {

    private static final Pieza TORRE_BLANCA = new Pieza(Color.BLANCO, torre());

    @Test
    void jugadasCandidatas_torreConHiperespacio_sumaLosSaltosDeCaballoASusLineasRectas() {
        // Arrange
        Pieza potenciada = TORRE_BLANCA.conPotenciador(hiperespacio());
        Tablero tablero = new Tablero(8, 8).con(casilla("d4"), potenciada);

        // Act
        Set<Posicion> alcanzables = destinos(potenciada.jugadasCandidatas(tablero, casilla("d4")));

        // Assert
        assertTrue(alcanzables.contains(casilla("e6")), "salto de caballo");
        assertTrue(alcanzables.contains(casilla("b3")), "salto de caballo");
        assertTrue(alcanzables.contains(casilla("d8")), "línea recta de la torre");
        assertEquals(14 + 8, alcanzables.size());
    }

    @Test
    void con_piezaQueLlegaAUnPotenciador_loTomaYLaCasillaQuedaSinPotenciador() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).conPotenciador(casilla("a3"), hiperespacio());

        // Act
        Tablero despues = tablero.con(casilla("a3"), TORRE_BLANCA);

        // Assert
        assertEquals(Optional.of(hiperespacio()), despues.piezaEn(casilla("a3")).orElseThrow().potenciador());
        assertTrue(despues.potenciadorEn(casilla("a3")).isEmpty());
        assertEquals(0, despues.cantidadDePotenciadores());
    }

    @Test
    void con_peonQueLlegaAUnPotenciador_loToma() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).conPotenciador(casilla("e3"), fuerza());

        // Act
        Tablero despues = tablero.con(casilla("e3"), new Pieza(Color.BLANCO, peon()));

        // Assert
        assertEquals(Optional.of(fuerza()), despues.piezaEn(casilla("e3")).orElseThrow().potenciador());
        assertTrue(despues.potenciadorEn(casilla("e3")).isEmpty());
    }

    @Test
    void jugadasCandidatas_peonConHiperespacioQueSaltaALaUltimaFila_corona() {
        // Arrange
        Pieza peon = new Pieza(Color.BLANCO, peon(), true).conPotenciador(hiperespacio());
        Tablero tablero = new Tablero(8, 8).con(casilla("d6"), peon);

        // Act
        List<Jugada> aC8 = peon.jugadasCandidatas(tablero, casilla("d6")).stream()
                .filter(jugada -> jugada.hasta().equals(casilla("c8")))
                .toList();

        // Assert
        assertEquals(4, aC8.size(), "una por cada tipo de coronación");
        assertTrue(aC8.stream().allMatch(jugada -> jugada.promocion().isPresent()));
    }

    @Test
    void jugadasCandidatas_peonConFuerzaQueRetrocede_noCorona() {
        // Arrange
        Pieza peon = new Pieza(Color.BLANCO, peon(), true).conPotenciador(fuerza());
        Tablero tablero = new Tablero(8, 8).con(casilla("d4"), peon);

        // Act
        List<Jugada> aD3 = peon.jugadasCandidatas(tablero, casilla("d4")).stream()
                .filter(jugada -> jugada.hasta().equals(casilla("d3")))
                .toList();

        // Assert
        assertEquals(1, aD3.size());
        assertTrue(aD3.get(0).promocion().isEmpty());
    }

    @Test
    void movida_piezaConPotenciador_loGasta() {
        // Arrange
        Pieza potenciada = TORRE_BLANCA.conPotenciador(hiperespacio());

        // Act
        Pieza movida = potenciada.movida();

        // Assert
        assertTrue(movida.potenciador().isEmpty());
        assertTrue(movida.yaSeMovio());
    }

    @Test
    void conPotenciador_casillaConUnaPieza_lanzaExcepcion() {
        // Arrange
        Tablero tablero = new Tablero(8, 8).con(casilla("a1"), TORRE_BLANCA);

        // Act
        RuntimeException excepcion = excepcionDe(() -> tablero.conPotenciador(casilla("a1"), fuerza()));

        // Assert
        assertInstanceOf(IllegalArgumentException.class, excepcion);
    }

    @Test
    void sinPotenciadoresDe_blancas_seLoSacaSoloALasBlancasYDejaLosDeLasCasillas() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("a1"), TORRE_BLANCA.conPotenciador(hiperespacio()))
                .con(casilla("a8"), new Pieza(Color.NEGRO, torre()).conPotenciador(fuerza()))
                .conPotenciador(casilla("d5"), fuerza());

        // Act
        Tablero despues = tablero.sinPotenciadoresDe(Color.BLANCO);

        // Assert
        assertTrue(despues.piezaEn(casilla("a1")).orElseThrow().potenciador().isEmpty());
        assertEquals(Optional.of(fuerza()), despues.piezaEn(casilla("a8")).orElseThrow().potenciador());
        assertEquals(Optional.of(fuerza()), despues.potenciadorEn(casilla("d5")));
    }

    @Test
    void estaEnJaque_torreConHiperespacioASaltoDeCaballoDelRey_daJaque() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("e8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("d6"), TORRE_BLANCA.conPotenciador(hiperespacio()));

        // Act
        boolean enJaque = new DetectorDeJaque().estaEnJaque(tablero, Color.NEGRO);

        // Assert
        assertTrue(enJaque);
    }
}
