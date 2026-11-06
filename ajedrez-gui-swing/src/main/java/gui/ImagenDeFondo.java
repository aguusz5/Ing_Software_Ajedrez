package gui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Una imagen de fondo y dónde se corta. La imagen siempre cubre toda la ventana sin deformarse: se agranda
 * hasta tapar el ancho y el alto, y lo que sobra se corta. Si sobra a los costados, el borde derecho de la
 * ventana cae en {@code bordeDerecho} (la fracción del ancho de la imagen donde termina lo importante) y se
 * corta primero lo que hay más allá; si todavía sobra, se corta de la izquierda. En el alto queda centrada.
 *
 * <p>El póster de la Rebelión usa 1.0, pegado a la derecha, donde están los personajes. La batalla de los
 * clones usa 0.86: a la derecha del clon de la 501 solo hay rocas, así que eso es lo primero que se pierde y
 * las cañoneras y los clones de la izquierda se ven lo más posible.
 */
public record ImagenDeFondo(BufferedImage imagen, double bordeDerecho) {

    public ImagenDeFondo {
        if (bordeDerecho <= 0 || bordeDerecho > 1) {
            throw new IllegalArgumentException("El borde derecho es una fracción del ancho, entre 0 y 1: "
                    + bordeDerecho);
        }
    }

    /** Pinta la imagen cubriendo una ventana de ese tamaño. */
    void pintar(Graphics2D g, int ancho, int alto) {
        double escala = Math.max((double) ancho / imagen.getWidth(), (double) alto / imagen.getHeight());
        int anchoEscalado = (int) Math.ceil(imagen.getWidth() * escala);
        int altoEscalado = (int) Math.ceil(imagen.getHeight() * escala);
        // Que el borde derecho de la ventana caiga en bordeDerecho, sin dejar huecos a ningún costado.
        int x = (int) Math.round(ancho - bordeDerecho * anchoEscalado);
        x = Math.max(ancho - anchoEscalado, Math.min(0, x));
        Graphics2D fondo = (Graphics2D) g.create();
        fondo.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        fondo.drawImage(imagen, x, (alto - altoEscalado) / 2, anchoEscalado, altoEscalado, null);
        fondo.dispose();
    }
}
