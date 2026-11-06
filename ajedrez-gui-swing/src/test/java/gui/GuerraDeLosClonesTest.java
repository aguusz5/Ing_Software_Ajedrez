package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import modelo.Color;

class GuerraDeLosClonesTest {

    private static final List<String> TIPOS_ESTANDAR = List.of("rey", "dama", "torre", "alfil", "caballo", "peón");

    private final Era clones = Era.republicaContraSeparatistas();
    private final TemaStarWars tema = new TemaStarWars(new GlifosDePieza(), clones, Optional.empty());

    @Test
    void personajeDe_cadaPiezaEstandarDeCadaBando_tienePersonaje() {
        // Arrange
        List<String> sinPersonaje = new ArrayList<>();

        // Act
        for (Color bando : Color.values()) {
            for (String tipo : TIPOS_ESTANDAR) {
                if (clones.personajes().personajeDe(tipo, bando).isEmpty()) {
                    sinPersonaje.add(tipo + " de " + bando);
                }
            }
        }

        // Assert
        assertEquals(List.of(), sinPersonaje);
    }

    @Test
    void bando_blancasYNegras_sonLaRepublicaYLosSeparatistas() {
        // Arrange
        List<Color> bandos = List.of(Color.BLANCO, Color.NEGRO);

        // Act
        List<String> nombres = bandos.stream().map(tema::bando).toList();

        // Assert
        assertEquals(List.of("República", "Separatistas"), nombres);
    }

    @Test
    void nombreDe_damaNegraYReyBlanco_sonGrievousYYoda() {
        // Arrange
        String dama = "dama";
        String rey = "rey";

        // Act
        String grievous = tema.nombreDe(dama, Color.NEGRO);
        String yoda = tema.nombreDe(rey, Color.BLANCO);

        // Assert
        assertEquals("General Grievous (dama)", grievous);
        assertEquals("Maestro Yoda (rey)", yoda);
    }

    @Test
    void nombre_dosEras_seDistinguenEnElMenu() {
        // Arrange
        Era rebelion = Era.rebelionContraImperio();

        // Act
        String nombreDeLosClones = tema.nombre();

        // Assert
        assertEquals("Guerra de los Clones", nombreDeLosClones);
        assertTrue(!nombreDeLosClones.equals(rebelion.nombre()));
    }

    @Test
    void dibujar_cadaPersonajeDeLaEra_pintaBuenaParteDeLaCaja() {
        // Arrange
        int lado = (int) Personaje.LADO;
        List<String> casiVacios = new ArrayList<>();

        // Act
        for (Color bando : Color.values()) {
            for (String tipo : TIPOS_ESTANDAR) {
                Personaje personaje = clones.personajes().personajeDe(tipo, bando).orElseThrow();
                BufferedImage imagen = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
                personaje.dibujar(imagen.createGraphics(), java.awt.Color.RED);
                if (pintados(imagen) < lado * lado / 6) {
                    casiVacios.add(personaje.nombre());
                }
            }
        }

        // Assert
        assertEquals(List.of(), casiVacios);
    }

    private static int pintados(BufferedImage imagen) {
        int cantidad = 0;
        for (int x = 0; x < imagen.getWidth(); x++) {
            for (int y = 0; y < imagen.getHeight(); y++) {
                if ((imagen.getRGB(x, y) >>> 24) > 0) {
                    cantidad++;
                }
            }
        }
        return cantidad;
    }
}
