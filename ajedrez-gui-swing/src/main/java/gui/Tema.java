package gui;

import java.awt.Graphics2D;
import java.util.Optional;

import modelo.Pieza;
import modelo.Potenciador;

/**
 * Cómo se ve la ventana: el fondo, los colores, cómo se dibuja cada pieza, qué dice al elegirla y cómo
 * se llaman los bandos. No cambia nada del juego: las reglas siguen en el núcleo. Los temas los arma
 * Principal y se eligen en el menú; un tema nuevo es una clase nueva, sin tocar las pantallas.
 */
public interface Tema {

    /** Cómo aparece en el menú para elegirlo. */
    String nombre();

    /** Una línea chica arriba del título del menú; vacía si el tema no la usa. */
    String epigrafe();

    Paleta paleta();

    /** Cómo se llama cada bando en la pantalla: "Blancas", "Rebelión", ... */
    String bando(modelo.Color color);

    /** Cómo se llama una pieza de ese tipo y color, por ejemplo al elegir en qué corona un peón. */
    String nombreDe(String tipo, modelo.Color color);

    DibujoDePieza dibujoDe(Pieza pieza);

    /** Un potenciador esperando en su casilla. Detrás de la pieza que lo toma se dibuja lo mismo. */
    DibujoDePieza dibujoDe(Potenciador potenciador);

    /**
     * El sonido de la pieza al elegirla, como nombre de archivo en gui/sonidos ("yoda.wav"). Por defecto no
     * hay: solo los temas con personajes les ponen voz.
     */
    default Optional<String> sonidoDe(Pieza pieza) {
        return Optional.empty();
    }

    /** Pinta el fondo de una pantalla entera. */
    void pintarFondo(Graphics2D g, int ancho, int alto);
}
