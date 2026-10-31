package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

/**
 * Dibuja una pieza con su símbolo de ajedrez, en dos pasadas: la silueta rellena del color de la pieza
 * y, encima, el trazo del glifo en el color contrario. Así blancas y negras se distinguen en cualquier
 * casilla, con cualquier fuente, y también cuando el glifo es solo una inicial.
 */
record DibujoDeGlifo(String nombre, String glifo, Color relleno, Color trazo) implements DibujoDePieza {

    private static final Font FUENTE = new Font(Font.DIALOG, Font.PLAIN, 1);
    /** Tamaño de la fuente en proporción al lado de la casilla. */
    private static final float PROPORCION = 0.7f;
    /** Grosor del trazo en proporción al tamaño de la fuente. */
    private static final float GROSOR_DEL_TRAZO = 0.016f;

    @Override
    public void dibujar(Graphics graficos, int ancho, int alto) {
        Graphics2D g = (Graphics2D) graficos.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        float tamano = Math.min(ancho, alto) * PROPORCION;
        GlyphVector vector = FUENTE.deriveFont(tamano).createGlyphVector(g.getFontRenderContext(), glifo);
        Rectangle2D limites = vector.getVisualBounds();
        Shape forma = vector.getOutline(
                (float) ((ancho - limites.getWidth()) / 2 - limites.getX()),
                (float) ((alto - limites.getHeight()) / 2 - limites.getY()));
        g.setColor(relleno);
        g.fill(silueta(forma));
        g.setColor(trazo);
        g.setStroke(new BasicStroke(tamano * GROSOR_DEL_TRAZO));
        g.draw(forma);
        g.dispose();
    }

    /** Rellena cada contorno del glifo por separado: queda la silueta completa, sin huecos. */
    private static Area silueta(Shape forma) {
        Area silueta = new Area();
        Path2D contorno = new Path2D.Double();
        double[] p = new double[6];
        for (PathIterator it = forma.getPathIterator(null); !it.isDone(); it.next()) {
            switch (it.currentSegment(p)) {
                case PathIterator.SEG_MOVETO -> {
                    silueta.add(new Area(contorno));
                    contorno = new Path2D.Double();
                    contorno.moveTo(p[0], p[1]);
                }
                case PathIterator.SEG_LINETO -> contorno.lineTo(p[0], p[1]);
                case PathIterator.SEG_QUADTO -> contorno.quadTo(p[0], p[1], p[2], p[3]);
                case PathIterator.SEG_CUBICTO -> contorno.curveTo(p[0], p[1], p[2], p[3], p[4], p[5]);
                default -> contorno.closePath();
            }
        }
        silueta.add(new Area(contorno));
        return silueta;
    }
}
