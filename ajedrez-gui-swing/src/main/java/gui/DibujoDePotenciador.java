package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Un potenciador esperando en su casilla: una esfera que brilla, con el símbolo de la pieza cuyo movimiento
 * presta (♘ = salta como el caballo). Así se entiende qué da sin leer el nombre. El color lo elige el tema.
 */
record DibujoDePotenciador(String nombre, DibujoDeGlifo insignia, Color brillo) implements DibujoDePieza {

    /** Radios en proporción al lado de la casilla: el halo, el anillo y el símbolo. */
    private static final double HALO = 0.46;
    private static final double ANILLO = 0.27;
    private static final double SIMBOLO = 0.42;
    private static final Color CENTRO = new Color(10, 12, 24, 220);

    @Override
    public void dibujar(Graphics graficos, int ancho, int alto) {
        Graphics2D g = (Graphics2D) graficos.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double lado = Math.min(ancho, alto);
        double x = ancho / 2.0;
        double y = alto / 2.0;
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x, y), (float) (lado * HALO), new float[] {0.4f, 1f},
                new Color[] {conAlfa(brillo, 170), conAlfa(brillo, 0)}));
        g.fill(circulo(x, y, lado * HALO));
        g.setColor(CENTRO);
        g.fill(circulo(x, y, lado * ANILLO));
        g.setColor(brillo);
        g.setStroke(new BasicStroke((float) Math.max(1.5, lado * 0.035)));
        g.draw(circulo(x, y, lado * ANILLO));
        int simbolo = (int) (lado * SIMBOLO);
        g.translate(x - simbolo / 2.0, y - simbolo / 2.0);
        insignia.dibujar(g, simbolo, simbolo);
        g.dispose();
    }

    private static Ellipse2D circulo(double x, double y, double radio) {
        return new Ellipse2D.Double(x - radio, y - radio, 2 * radio, 2 * radio);
    }

    private static Color conAlfa(Color color, int alfa) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alfa);
    }
}
