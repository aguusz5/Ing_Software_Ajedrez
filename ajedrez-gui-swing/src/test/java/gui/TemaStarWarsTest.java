package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import configuracion.TiposDePiezaEstandar;
import modelo.Color;
import modelo.Pieza;
import modelo.TipoDePieza;
import java.util.ArrayList;

class TemaStarWarsTest {

    private static final List<TipoDePieza> ESTANDAR = List.of(
            TiposDePiezaEstandar.rey(), TiposDePiezaEstandar.dama(), TiposDePiezaEstandar.torre(),
            TiposDePiezaEstandar.alfil(), TiposDePiezaEstandar.caballo(), TiposDePiezaEstandar.peon());
    /** Una pieza que no existe en el ajedrez: no tiene personaje. Su regla no importa acá. */
    private static final TipoDePieza CANCILLER =
            new TipoDePieza("canciller", false, 8, List.of((tablero, desde, pieza) -> List.of()));

    private final Era rebelion = Era.rebelionContraImperio();
    private final PersonajesDeStarWars personajes = rebelion.personajes();
    private final TemaStarWars tema = new TemaStarWars(new GlifosDePieza(), rebelion, Optional.empty());

    @Test
    void personajeDe_cadaPiezaEstandarDeCadaBando_tienePersonaje() {
        // Arrange
        List<Color> bandos = List.of(Color.values());

        // Act
        List<String> sinPersonaje = new ArrayList<>();
        for (Color bando : bandos) {
            for (TipoDePieza tipo : ESTANDAR) {
                if (personajes.personajeDe(tipo.nombre(), bando).isEmpty()) {
                    sinPersonaje.add(tipo + " de " + bando);
                }
            }
        }

        // Assert
        assertEquals(List.of(), sinPersonaje);
    }

    @Test
    void nombreDe_damaNegra_esDarthVaderConLaPiezaEntreParentesis() {
        // Arrange
        String tipo = "dama";

        // Act
        String nombre = tema.nombreDe(tipo, Color.NEGRO);

        // Assert
        assertEquals("Darth Vader (dama)", nombre);
    }

    @Test
    void nombreDe_peonBlanco_esUnPilotoRebelde() {
        // Arrange
        String tipo = "peón";

        // Act
        String nombre = tema.nombreDe(tipo, Color.BLANCO);

        // Assert
        assertEquals("Piloto rebelde (peón)", nombre);
    }

    @Test
    void bando_blancasYNegras_sonLaRebelionYElImperio() {
        // Arrange
        List<Color> bandos = List.of(Color.BLANCO, Color.NEGRO);

        // Act
        List<String> nombres = bandos.stream().map(tema::bando).toList();

        // Assert
        assertEquals(List.of("Rebelión", "Imperio"), nombres);
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

    @Test
    void pintarFondo_conImagenMasAnchaQueLaVentana_laCubreEnteraSinDejarHuecos() {
        // Arrange
        BufferedImage roja = new BufferedImage(16, 9, BufferedImage.TYPE_INT_RGB);
        pintarDe(roja, java.awt.Color.RED);
        TemaStarWars conImagen = new TemaStarWars(new GlifosDePieza(), rebelion,
                Optional.of(new ImagenDeFondo(roja, 1.0)));
        BufferedImage ventana = new BufferedImage(60, 80, BufferedImage.TYPE_INT_RGB);

        // Act
        conImagen.pintarFondo(ventana.createGraphics(), ventana.getWidth(), ventana.getHeight());

        // Assert
        for (int[] esquina : new int[][] {{0, 0}, {59, 0}, {0, 79}, {59, 79}}) {
            assertEquals(java.awt.Color.RED.getRGB(), ventana.getRGB(esquina[0], esquina[1]),
                    "la esquina " + esquina[0] + "," + esquina[1] + " debería tener la imagen");
        }
    }

    @Test
    void pintarFondo_bordeDerechoAntesDelFinal_cortaLoQueSobraALaDerechaYNoALaIzquierda() {
        // Arrange
        BufferedImage mitades = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
        pintarDe(mitades, java.awt.Color.BLUE);
        java.awt.Graphics2D pincel = mitades.createGraphics();
        pincel.setColor(java.awt.Color.RED);
        pincel.fillRect(17, 0, 3, 10);
        pincel.dispose();
        TemaStarWars conImagen = new TemaStarWars(new GlifosDePieza(), rebelion,
                Optional.of(new ImagenDeFondo(mitades, 0.75)));
        BufferedImage ventana = new BufferedImage(30, 20, BufferedImage.TYPE_INT_RGB);

        // Act
        conImagen.pintarFondo(ventana.createGraphics(), ventana.getWidth(), ventana.getHeight());

        // Assert
        assertEquals(java.awt.Color.BLUE.getRGB(), ventana.getRGB(0, 10), "a la izquierda se ve el principio");
        assertEquals(java.awt.Color.BLUE.getRGB(), ventana.getRGB(29, 10), "la franja roja quedó afuera");
    }

    @Test
    void pintarFondo_sinImagen_pintaElEspacioOscuro() {
        // Arrange
        BufferedImage ventana = new BufferedImage(60, 80, BufferedImage.TYPE_INT_RGB);
        pintarDe(ventana, java.awt.Color.WHITE);

        // Act
        tema.pintarFondo(ventana.createGraphics(), ventana.getWidth(), ventana.getHeight());

        // Assert
        java.awt.Color esquina = new java.awt.Color(ventana.getRGB(0, ventana.getHeight() - 1));
        assertTrue(esquina.getRed() + esquina.getGreen() + esquina.getBlue() < 3 * 60, "el espacio es casi negro");
    }

    private static void pintarDe(BufferedImage imagen, java.awt.Color color) {
        java.awt.Graphics2D g = imagen.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, imagen.getWidth(), imagen.getHeight());
        g.dispose();
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
