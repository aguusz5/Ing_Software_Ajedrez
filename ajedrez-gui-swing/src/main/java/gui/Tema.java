package gui;

import java.awt.Graphics2D;

import modelo.Pieza;

/**
 * Cómo se ve la ventana: el fondo, los colores, cómo se dibuja cada pieza y cómo se llaman los bandos.
 * No cambia nada del juego: las reglas siguen en el núcleo. Los temas los arma Principal y se eligen en
 * el menú; un tema nuevo es una clase nueva, sin tocar las pantallas.
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

    /** Pinta el fondo de una pantalla entera. */
    void pintarFondo(Graphics2D g, int ancho, int alto);
}
