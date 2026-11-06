package juego;

import static configuracion.PotenciadoresStarWars.fuerza;
import static configuracion.PotenciadoresStarWars.hiperespacio;
import static configuracion.TiposDePiezaEstandar.rey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;
import static soporte.Excepciones.excepcionDe;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

class AparicionDePotenciadoresTest {

    private static final int CADA_DOS = 2;
    private static final int HASTA_DOS = 2;

    private static AparicionDePotenciadores conCatalogoCompleto() {
        return new AparicionDePotenciadores(List.of(hiperespacio(), fuerza()), CADA_DOS, HASTA_DOS, new Random(7));
    }

    @Test
    void trasLaJugada_jugadaQueNoLeToca_devuelveElMismoTablero() {
        // Arrange
        Tablero tablero = new Tablero(8, 8);

        // Act
        Tablero despues = conCatalogoCompleto().trasLaJugada(tablero, 3);

        // Assert
        assertSame(tablero, despues);
    }

    @Test
    void trasLaJugada_jugadaQueLeToca_apareceUnoEnUnaCasillaVacia() {
        // Arrange: un tablero de 2 x 1 con un rey: queda una sola casilla vacía.
        Tablero tablero = new Tablero(2, 1).con(new Posicion(0, 0), new Pieza(Color.BLANCO, rey()));

        // Act
        Tablero despues = conCatalogoCompleto().trasLaJugada(tablero, 4);

        // Assert
        assertEquals(1, despues.cantidadDePotenciadores());
        assertTrue(despues.potenciadorEn(new Posicion(1, 0)).isPresent());
    }

    @Test
    void alEmpezar_tableroVacio_poneTantosComoElMaximo() {
        // Arrange
        Tablero vacio = new Tablero(8, 8);

        // Act
        Tablero inicial = conCatalogoCompleto().alEmpezar(vacio);

        // Assert
        assertEquals(HASTA_DOS, inicial.cantidadDePotenciadores());
    }

    @Test
    void alEmpezar_menosCasillasLibresQueElMaximo_poneLosQueEntran() {
        // Arrange: un tablero de 2 x 1 con un rey: queda una sola casilla vacía.
        Tablero tablero = new Tablero(2, 1).con(new Posicion(0, 0), new Pieza(Color.BLANCO, rey()));

        // Act
        Tablero inicial = conCatalogoCompleto().alEmpezar(tablero);

        // Assert
        assertEquals(1, inicial.cantidadDePotenciadores());
    }

    @Test
    void trasLaJugada_yaHayElMaximo_noApareceOtro() {
        // Arrange
        Tablero tablero = new Tablero(8, 8)
                .conPotenciador(casilla("d4"), fuerza())
                .conPotenciador(casilla("e5"), fuerza());

        // Act
        Tablero despues = conCatalogoCompleto().trasLaJugada(tablero, 2);

        // Assert
        assertEquals(HASTA_DOS, despues.cantidadDePotenciadores());
    }

    @Test
    void trasLaJugada_catalogoVacio_nuncaAparece() {
        // Arrange
        AparicionDePotenciadores sinPotenciadores = new AparicionDePotenciadores(List.of(), 1, HASTA_DOS, new Random());

        // Act
        Tablero despues = sinPotenciadores.trasLaJugada(new Tablero(8, 8), 1);

        // Assert
        assertEquals(0, despues.cantidadDePotenciadores());
    }

    @Test
    void constructor_ceroJugadasEntrePotenciadores_lanzaExcepcion() {
        // Arrange
        int ceroJugadas = 0;

        // Act
        RuntimeException excepcion = excepcionDe(
                () -> new AparicionDePotenciadores(List.of(fuerza()), ceroJugadas, HASTA_DOS, new Random()));

        // Assert
        assertInstanceOf(IllegalArgumentException.class, excepcion);
    }
}
