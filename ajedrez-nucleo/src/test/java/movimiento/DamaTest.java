package movimiento;

import static configuracion.TiposDePiezaEstandar.alfil;
import static configuracion.TiposDePiezaEstandar.dama;
import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static soporte.Casillas.casilla;
import static soporte.Casillas.casillas;
import static soporte.Casillas.destinos;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

class DamaTest {

    private static final Pieza DAMA_BLANCA = new Pieza(Color.BLANCO, dama());

    @Test
    void dama_enElCentro_mueveComoTorreMasAlfil() {
        // Arrange
        Pieza torreBlanca = new Pieza(Color.BLANCO, torre());
        Pieza alfilBlanco = new Pieza(Color.BLANCO, alfil());
        Set<Posicion> esperados = new HashSet<>();
        esperados.addAll(destinos(torreBlanca.jugadasCandidatas(new Tablero(8, 8).con(casilla("d4"), torreBlanca), casilla("d4"))));
        esperados.addAll(destinos(alfilBlanco.jugadasCandidatas(new Tablero(8, 8).con(casilla("d4"), alfilBlanco), casilla("d4"))));

        // Act
        Set<Posicion> destinos = destinos(DAMA_BLANCA.jugadasCandidatas(
                new Tablero(8, 8).con(casilla("d4"), DAMA_BLANCA), casilla("d4")));

        // Assert
        assertEquals(esperados, destinos);
        assertEquals(27, destinos.size());
    }

    @Test
    void dama_rodeadaDePiezasPropias_soloPuedeCapturarAlRival() {
        // Arrange
        Pieza peonBlanco = new Pieza(Color.BLANCO, peon());
        Tablero tablero = new Tablero(8, 8)
                .con(casilla("d1"), DAMA_BLANCA)
                .con(casilla("c1"), peonBlanco)
                .con(casilla("e1"), peonBlanco)
                .con(casilla("c2"), peonBlanco)
                .con(casilla("d2"), peonBlanco)
                .con(casilla("e2"), new Pieza(Color.NEGRO, peon()));

        // Act
        Set<Posicion> destinos = destinos(DAMA_BLANCA.jugadasCandidatas(tablero, casilla("d1")));

        // Assert
        assertEquals(casillas("e2"), destinos);
    }
}
