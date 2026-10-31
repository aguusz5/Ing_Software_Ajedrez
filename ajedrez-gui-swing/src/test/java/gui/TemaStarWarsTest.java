package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.jupiter.api.Test;

import configuracion.TiposDePiezaEstandar;
import modelo.Color;
import modelo.Pieza;
import modelo.TipoDePieza;

class TemaStarWarsTest {

    private static final List<TipoDePieza> ESTANDAR = List.of(
            TiposDePiezaEstandar.rey(), TiposDePiezaEstandar.dama(), TiposDePiezaEstandar.torre(),
            TiposDePiezaEstandar.alfil(), TiposDePiezaEstandar.caballo(), TiposDePiezaEstandar.peon());
    /** Una pieza que no existe en el ajedrez: no tiene personaje. Su regla no importa acá. */
    private static final TipoDePieza CANCILLER =
            new TipoDePieza("canciller", false, 8, List.of((tablero, desde, pieza) -> List.of()));

    private final PersonajesDeStarWars personajes = new PersonajesDeStarWars();
    private final TemaStarWars tema = new TemaStarWars(new GlifosDePieza(), personajes);

    @Test
    void personajeDe_cadaPiezaEstandarDeCadaBando_tienePersonaje() {
        for (Color bando : Color.values()) {
            for (TipoDePieza tipo : ESTANDAR) {
                // Act & Assert
                assertTrue(personajes.personajeDe(tipo.nombre(), bando).isPresent(), tipo + " de " + bando);
            }
        }
    }

    @Test
    void nombreDe_damaNegra_esDarthVaderConLaPiezaEntreParentesis() {
        // Act & Assert
        assertEquals("Darth Vader (dama)", tema.nombreDe("dama", Color.NEGRO));
    }

    @Test
    void nombreDe_peonBlanco_esUnPilotoRebelde() {
        // Act & Assert
        assertEquals("Piloto rebelde (peón)", tema.nombreDe("peón", Color.BLANCO));
    }

    @Test
    void bando_blancasYNegras_sonLaRebelionYElImperio() {
        // Act & Assert
        assertEquals("Rebelión", tema.bando(Color.BLANCO));
        assertEquals("Imperio", tema.bando(Color.NEGRO));
    }

    @Test
    void dibujoDe_piezaSinPersonaje_seDibujaConSuNombreSinPersonaje() {
        // Arrange
        Pieza canciller = new Pieza(Color.NEGRO, CANCILLER);

        // Act
        DibujoDePieza dibujo = tema.dibujoDe(canciller);

        // Assert
        assertEquals("canciller", dibujo.nombre());
    }

    @Test
    void dibujar_stormtrooper_pintaLaCaja() {
        // Arrange
        Personaje stormtrooper = personajes.personajeDe("peón", Color.NEGRO).orElseThrow();
        int lado = (int) Personaje.LADO;
        BufferedImage imagen = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);

        // Act
        stormtrooper.dibujar(imagen.createGraphics(), java.awt.Color.RED);

        // Assert
        assertTrue(pixelesPintados(imagen) > lado * lado / 3, "el casco debería ocupar buena parte de la caja");
    }

    private static int pixelesPintados(BufferedImage imagen) {
        int pintados = 0;
        for (int x = 0; x < imagen.getWidth(); x++) {
            for (int y = 0; y < imagen.getHeight(); y++) {
                if ((imagen.getRGB(x, y) >>> 24) != 0) {
                    pintados++;
                }
            }
        }
        return pintados;
    }
}
