package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

/**
 * Dibuja una pieza como un personaje, a la escala de la casilla y con el brillo de su bando alrededor.
 * En una esquina lleva una insignia con el símbolo clásico de la pieza, para saber cómo se mueve
 * sin tener que acordarse de qué personaje es qué pieza.
 */
record DibujoDePersonaje(String nombre, Personaje personaje, Color brillo, DibujoDeGlifo insignia)
        implements DibujoDePieza {

    /** Cuánto de la casilla ocupa el personaje: el resto queda para el brillo. */
    private static final double PROPORCION = 0.86;
    /** La insignia: un círculo en la esquina de abajo a la derecha, medido en la caja de 100 x 100. */
    private static final double LADO_DE_LA_INSIGNIA = 30;
    private static final double ESQUINA_DE_LA_INSIGNIA = Personaje.LADO - LADO_DE_LA_INSIGNIA + 7;
    /** El símbolo se dibuja un poco más grande que el círculo: los glifos no llenan su caja. */
    private static final double AUMENTO_DEL_SIMBOLO = 1.2;
    private static final Color BORDE_DE_LA_INSIGNIA = new Color(255, 255, 255, 220);

    @Override
    public void dibujar(Graphics graficos, int ancho, int alto) {
        Graphics2D g = (Graphics2D) graficos.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        double lado = Math.min(ancho, alto) * PROPORCION;
        g.translate((ancho - lado) / 2, (alto - lado) / 2);
        g.scale(lado / Personaje.LADO, lado / Personaje.LADO);
        personaje.dibujar(g, brillo);
        dibujarInsignia(g);
        g.dispose();
    }

    private void dibujarInsignia(Graphics2D g) {
        Ellipse2D circulo = new Ellipse2D.Double(
                ESQUINA_DE_LA_INSIGNIA, ESQUINA_DE_LA_INSIGNIA, LADO_DE_LA_INSIGNIA, LADO_DE_LA_INSIGNIA);
        g.setColor(brillo.darker().darker());
        g.fill(circulo);
        g.setColor(BORDE_DE_LA_INSIGNIA);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(circulo);
        Graphics2D adentro = (Graphics2D) g.create();
        double lado = LADO_DE_LA_INSIGNIA * AUMENTO_DEL_SIMBOLO;
        adentro.translate(circulo.getCenterX() - lado / 2, circulo.getCenterY() - lado / 2);
        insignia.dibujar(adentro, (int) lado, (int) lado);
        adentro.dispose();
    }
}
