package gui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import configuracion.TiposDePiezaEstandar;
import modelo.Color;
import modelo.Pieza;
import modelo.TipoDePieza;

class SonidosDeLosPersonajesTest {

    private static final List<TipoDePieza> TIPOS = List.of(TiposDePiezaEstandar.rey(), TiposDePiezaEstandar.dama(),
            TiposDePiezaEstandar.torre(), TiposDePiezaEstandar.alfil(), TiposDePiezaEstandar.caballo(),
            TiposDePiezaEstandar.peon());

    private final GlifosDePieza glifos = new GlifosDePieza();

    @Test
    void sonidoDe_reyDeLaRebelion_esLuke() {
        // Arrange
        Tema tema = new TemaStarWars(glifos, Era.rebelionContraImperio(), Optional.empty());

        // Act
        Optional<String> sonido = tema.sonidoDe(new Pieza(Color.BLANCO, TiposDePiezaEstandar.rey()));

        // Assert
        assertEquals(Optional.of("luke.wav"), sonido);
    }

    @Test
    void sonidoDe_reyDeLaRepublica_esYoda() {
        // Arrange
        Tema tema = new TemaStarWars(glifos, Era.republicaContraSeparatistas(), Optional.empty());

        // Act
        Optional<String> sonido = tema.sonidoDe(new Pieza(Color.BLANCO, TiposDePiezaEstandar.rey()));

        // Assert
        assertEquals(Optional.of("yoda.wav"), sonido);
    }

    @Test
    void sonidoDe_cadaPiezaDeLasDosEras_tieneSonido() {
        // Arrange
        List<Era> eras = List.of(Era.rebelionContraImperio(), Era.republicaContraSeparatistas());
        List<String> mudas = new ArrayList<>();

        // Act
        for (Era era : eras) {
            Tema tema = new TemaStarWars(glifos, era, Optional.empty());
            for (Color bando : Color.values()) {
                for (TipoDePieza tipo : TIPOS) {
                    if (tema.sonidoDe(new Pieza(bando, tipo)).isEmpty()) {
                        mudas.add(tipo.nombre() + " de " + tema.bando(bando));
                    }
                }
            }
        }

        // Assert
        assertEquals(List.of(), mudas);
    }

    @Test
    void sonidoDe_temaClasico_noSuenaNinguna() {
        // Arrange
        Tema tema = new TemaClasico(glifos);
        List<String> conSonido = new ArrayList<>();

        // Act
        for (Color bando : Color.values()) {
            for (TipoDePieza tipo : TIPOS) {
                tema.sonidoDe(new Pieza(bando, tipo)).ifPresent(conSonido::add);
            }
        }

        // Assert
        assertEquals(List.of(), conSonido);
    }

    @Test
    void sonidoDe_todasLasEras_cadaArchivoEstaEnLosRecursos() {
        // Arrange
        List<Era> eras = List.of(Era.rebelionContraImperio(), Era.republicaContraSeparatistas());
        List<String> faltantes = new ArrayList<>();

        // Act
        for (Era era : eras) {
            Tema tema = new TemaStarWars(glifos, era, Optional.empty());
            for (Color bando : Color.values()) {
                for (TipoDePieza tipo : TIPOS) {
                    tema.sonidoDe(new Pieza(bando, tipo))
                            .filter(archivo -> ReproductorDeSonidos.class.getResource("sonidos/" + archivo) == null)
                            .ifPresent(faltantes::add);
                }
            }
        }

        // Assert
        assertEquals(List.of(), faltantes);
    }

    @Test
    void reproducir_archivoQueNoExiste_noRompe() {
        // Arrange
        ReproductorDeSonidos reproductor = new ReproductorDeSonidos();

        // Act & Assert
        assertDoesNotThrow(() -> reproductor.reproducir("no-existe.wav"));
    }

    @Test
    void reproducir_sonidoReal_noRompeAunqueNoHayaSalidaDeAudio() {
        // Arrange
        ReproductorDeSonidos reproductor = new ReproductorDeSonidos();

        // Act & Assert
        assertDoesNotThrow(() -> {
            reproductor.reproducir("yoda.wav");
            reproductor.detener();
        });
    }

    @Test
    void activar_apagado_dejaElReproductorEnSilencioYNoRompeAlReproducir() {
        // Arrange
        ReproductorDeSonidos reproductor = new ReproductorDeSonidos();

        // Act
        reproductor.activar(false);
        reproductor.reproducir("yoda.wav");

        // Assert
        assertFalse(reproductor.activo());
    }

    @Test
    void activar_apagadoYDespuesPrendido_vuelveASonar() {
        // Arrange
        ReproductorDeSonidos reproductor = new ReproductorDeSonidos();
        reproductor.activar(false);

        // Act
        reproductor.activar(true);

        // Assert
        assertTrue(reproductor.activo());
    }
}
