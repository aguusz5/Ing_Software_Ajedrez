package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import configuracion.PotenciadoresStarWars;
import configuracion.TiposDePiezaEstandar;
import modelo.Color;
import modelo.Pieza;
import modelo.Potenciador;

class PotenciadorEnPantallaTest {

    private final GlifosDePieza glifos = new GlifosDePieza();
    private final TemaStarWars tema = new TemaStarWars(glifos, new PersonajesDeStarWars(), Optional.empty());

    @Test
    void glifoDe_cadaPotenciadorStarWars_esElSimboloDeLaPiezaCuyoMovimientoPresta() {
        // Act & Assert
        assertEquals("♘", glifos.glifoDe(PotenciadoresStarWars.hiperespacio()));
        assertEquals("♔", glifos.glifoDe(PotenciadoresStarWars.fuerza()));
        assertEquals("♖", glifos.glifoDe(PotenciadoresStarWars.propulsores()));
        assertEquals("♗", glifos.glifoDe(PotenciadoresStarWars.sableDeLuz()));
    }

    @Test
    void glifoDe_potenciadorNuevo_esUnaEstrella() {
        // Arrange
        Potenciador nuevo = new Potenciador("Carbonita", "no se mueve", (tablero, desde, pieza) -> List.of());

        // Act
        String glifo = glifos.glifoDe(nuevo);

        // Assert
        assertEquals("✦", glifo);
    }

    @Test
    void dibujoDe_potenciador_diceQueDaYQueDuraUnTurno() {
        // Arrange
        Potenciador hiperespacio = PotenciadoresStarWars.hiperespacio();

        // Act
        String nombre = tema.dibujoDe(hiperespacio).nombre();

        // Assert
        assertEquals("Potenciador: Salto al hiperespacio (salta como el caballo, un solo turno)", nombre);
    }

    @Test
    void dibujoPotenciado_torreConPotenciador_seLlamaComoLaPiezaYElPotenciadorYSeDibujaSinErrores() {
        // Arrange
        Pieza torre = new Pieza(Color.BLANCO, TiposDePiezaEstandar.torre());
        DibujoDePieza dibujo = new DibujoPotenciado(tema.dibujoDe(torre),
                tema.dibujoDe(PotenciadoresStarWars.sableDeLuz()));
        BufferedImage casilla = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

        // Act
        dibujo.dibujar(casilla.getGraphics(), 64, 64);

        // Assert
        assertTrue(dibujo.nombre().startsWith(tema.dibujoDe(torre).nombre() + " — Potenciador: Sable de luz"));
        assertTrue((casilla.getRGB(32, 32) >>> 24) > 0, "algo se pintó en el centro");
    }
}
