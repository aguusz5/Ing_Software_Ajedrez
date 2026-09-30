package extensibilidad;

import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;
import static soporte.Casillas.casillas;
import static soporte.Casillas.destinos;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import juego.JuegoDeAjedrez;
import juego.Partida;
import juego.ResultadoJugada;
import modelo.Color;
import modelo.Direccion;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import modelo.TipoDePieza;
import movimiento.ReglaDeSalto;
import movimiento.ReglaDeslizante;
import reglas.DetectorDeJaque;
import reglas.ReglaNoDejarReyEnJaque;
import reglas.ReglaPiezaPropia;
import reglas.ValidadorDeJugadas;

/**
 * Las dos extensiones típicas de la defensa: una pieza nueva y otro tamaño de tablero.
 * Ninguna de las dos necesita modificar una sola clase del núcleo.
 */
class ExtensibilidadTest {

    /** Canciller = torre + caballo. Existe solo en este test. */
    private static final TipoDePieza CANCILLER = new TipoDePieza("canciller", false, List.of(
            new ReglaDeslizante(Direccion.ORTOGONALES, ReglaDeslizante.SIN_LIMITE),
            new ReglaDeSalto(Direccion.SALTOS_DE_CABALLO)));

    private static JuegoDeAjedrez partidaSobre(Tablero tablero) {
        DetectorDeJaque detector = new DetectorDeJaque();
        ValidadorDeJugadas validador = new ValidadorDeJugadas(List.of(
                new ReglaPiezaPropia(),
                new ReglaNoDejarReyEnJaque(detector)));
        return new Partida(tablero, validador, detector);
    }

    @Test
    void piezaNueva_definidaSoloEnElTest_mueveComoTorreMasCaballo() {
        // Arrange
        Pieza canciller = new Pieza(Color.BLANCO, CANCILLER);
        Tablero tablero = new Tablero(8, 8).con(casilla("d4"), canciller);

        // Act
        Set<Posicion> destinos = destinos(canciller.jugadasCandidatas(tablero, casilla("d4")));

        // Assert
        Set<Posicion> esperados = casillas(
                "d1", "d2", "d3", "d5", "d6", "d7", "d8", "a4", "b4", "c4", "e4", "f4", "g4", "h4",
                "b3", "b5", "c2", "c6", "e2", "e6", "f3", "f5");
        assertEquals(esperados, destinos);
    }

    @Test
    void piezaNueva_enUnaPartida_daJaqueSinTocarElDetector() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("e8"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("b5"), new Pieza(Color.BLANCO, CANCILLER));
        JuegoDeAjedrez juego = partidaSobre(tablero);

        // Act: salta como caballo a c7, desde donde ataca a e8.
        ResultadoJugada resultado = juego.jugar(casilla("b5"), casilla("c7"));

        // Assert
        assertTrue(resultado.valida());
        assertEquals(Optional.of(Color.NEGRO), resultado.colorEnJaque());
    }

    @Test
    void tableroDe10x10_unaPartida_funcionaSinCambiosEnElNucleo() {
        // Arrange
        Tablero tablero = new Tablero(10, 10)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("e10"), new Pieza(Color.NEGRO, rey()))
                .con(casilla("a1"), new Pieza(Color.BLANCO, torre()))
                .con(casilla("b2"), new Pieza(Color.BLANCO, peon()))
                .con(casilla("j9"), new Pieza(Color.NEGRO, peon()));
        JuegoDeAjedrez juego = partidaSobre(tablero);

        // Act
        ResultadoJugada dobleBlanco = juego.jugar(casilla("b2"), casilla("b4"));
        ResultadoJugada dobleNegro = juego.jugar(casilla("j9"), casilla("j7"));
        ResultadoJugada torreHastaLaFila10 = juego.jugar(casilla("a1"), casilla("a10"));

        // Assert
        assertTrue(dobleBlanco.valida());
        assertTrue(dobleNegro.valida());
        assertTrue(torreHastaLaFila10.valida());
        assertEquals(Optional.of(Color.NEGRO), torreHastaLaFila10.colorEnJaque());
    }
}
