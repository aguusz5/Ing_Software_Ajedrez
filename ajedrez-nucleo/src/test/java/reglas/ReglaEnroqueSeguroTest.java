package reglas;

import static configuracion.TiposDePiezaEstandar.peon;
import static configuracion.TiposDePiezaEstandar.rey;
import static configuracion.TiposDePiezaEstandar.torre;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static soporte.Casillas.casilla;

import org.junit.jupiter.api.Test;

import modelo.Color;
import modelo.Pieza;
import modelo.Tablero;
import movimiento.JugadaDeEnroque;
import movimiento.JugadaSimple;

class ReglaEnroqueSeguroTest {

    private static final JugadaDeEnroque ENROQUE_CORTO =
            new JugadaDeEnroque(casilla("e1"), casilla("g1"), casilla("h1"), casilla("f1"));
    private static final JugadaDeEnroque ENROQUE_LARGO =
            new JugadaDeEnroque(casilla("e1"), casilla("c1"), casilla("a1"), casilla("d1"));

    private final ReglaEnroqueSeguro regla = new ReglaEnroqueSeguro(new DetectorDeJaque());

    /** Rey blanco en e1 con sus dos torres y rey negro en h8. */
    private static Tablero listoParaEnrocar() {
        return new Tablero(8, 8)
                .con(casilla("e1"), new Pieza(Color.BLANCO, rey()))
                .con(casilla("a1"), new Pieza(Color.BLANCO, torre()))
                .con(casilla("h1"), new Pieza(Color.BLANCO, torre()))
                .con(casilla("h8"), new Pieza(Color.NEGRO, rey()));
    }

    @Test
    void validar_enroqueSinAmenazas_esValido() {
        // Act & Assert
        assertTrue(regla.validar(listoParaEnrocar(), ENROQUE_CORTO, Color.BLANCO).valida());
    }

    @Test
    void validar_enroqueConElReyEnJaque_esRechazado() {
        // Arrange: la torre negra de e8 le da jaque al rey.
        Tablero tablero = listoParaEnrocar().con(casilla("e8"), new Pieza(Color.NEGRO, torre()));

        // Act
        ResultadoValidacion resultado = regla.validar(tablero, ENROQUE_CORTO, Color.BLANCO);

        // Assert
        assertFalse(resultado.valida());
    }

    @Test
    void validar_enroqueQueCruzaUnaCasillaAtacada_esRechazado() {
        // Arrange: la torre negra de f8 controla f1, la casilla que cruza el rey.
        Tablero tablero = listoParaEnrocar().con(casilla("f8"), new Pieza(Color.NEGRO, torre()));

        // Act
        ResultadoValidacion resultado = regla.validar(tablero, ENROQUE_CORTO, Color.BLANCO);

        // Assert
        assertFalse(resultado.valida());
    }

    @Test
    void validar_enroqueQueCruzaUnaCasillaAtacadaPorUnPeon_esRechazado() {
        // Arrange: el peón negro de e2 ataca f1 en diagonal, aunque f1 esté vacía.
        Tablero tablero = listoParaEnrocar().con(casilla("e2"), new Pieza(Color.NEGRO, peon()).movida());

        // Act
        ResultadoValidacion resultado = regla.validar(tablero, ENROQUE_CORTO, Color.BLANCO);

        // Assert
        assertFalse(resultado.valida());
    }

    @Test
    void validar_enroqueLargoConB1Atacada_esValido() {
        // Arrange: por b1 pasa solo la torre, no el rey, así que puede estar atacada.
        Tablero tablero = listoParaEnrocar().con(casilla("b8"), new Pieza(Color.NEGRO, torre()));

        // Act & Assert
        assertTrue(regla.validar(tablero, ENROQUE_LARGO, Color.BLANCO).valida());
    }

    @Test
    void validar_jugadaQueNoEsEnroque_noLaControla() {
        // Arrange: el rey está en jaque, pero eso lo resuelve ReglaNoDejarReyEnJaque, no esta regla.
        Tablero tablero = listoParaEnrocar().con(casilla("e8"), new Pieza(Color.NEGRO, torre()));

        // Act & Assert
        assertTrue(regla.validar(tablero, new JugadaSimple(casilla("e1"), casilla("e2")), Color.BLANCO).valida());
    }
}
