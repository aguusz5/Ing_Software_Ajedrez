package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.List;

/**
 * Un personaje de un tema: su nombre y su dibujo, hecho de capas de formas en una caja de 100 x 100.
 * De las capas se saca la silueta, para pintarle alrededor el brillo del bando: así se distingue de qué
 * lado es aunque el personaje sea blanco y esté sobre una casilla clara (un stormtrooper, por ejemplo).
 */
final class Personaje {

    /** Lado de la caja en la que están dibujadas las capas. Quien dibuja escala a su casilla. */
    static final double LADO = 100;
    /** El brillo son tres trazos alrededor de la silueta, del más ancho y tenue al más fino y fuerte. */
    private static final float[] GROSORES_DEL_BRILLO = {13f, 8f, 4f};
    private static final int[] OPACIDADES_DEL_BRILLO = {45, 90, 190};
    private static final Color CONTORNO = new Color(0, 0, 0, 170);
    private static final float GROSOR_DEL_CONTORNO = 1.6f;

    private final String nombre;
    private final List<Capa> capas;
    private final Area silueta = new Area();

    Personaje(String nombre, List<Capa> capas) {
        this.nombre = nombre;
        this.capas = List.copyOf(capas);
        for (Capa capa : this.capas) {
            silueta.add(new Area(capa.forma()));
        }
    }

    String nombre() {
        return nombre;
    }

    /** Dibuja en la caja de 100 x 100: primero el brillo y el contorno de la silueta, después las capas en orden. */
    void dibujar(Graphics2D g, Color brillo) {
        for (int i = 0; i < GROSORES_DEL_BRILLO.length; i++) {
            g.setColor(new Color(brillo.getRed(), brillo.getGreen(), brillo.getBlue(), OPACIDADES_DEL_BRILLO[i]));
            g.setStroke(new BasicStroke(GROSORES_DEL_BRILLO[i], BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(silueta);
        }
        g.setColor(CONTORNO);
        g.setStroke(new BasicStroke(GROSOR_DEL_CONTORNO, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(silueta);
        for (Capa capa : capas) {
            g.setPaint(capa.pintura());
            g.fill(capa.forma());
        }
    }

    /** Una parte del dibujo: una forma y con qué se rellena (un color o un degradé). */
    record Capa(Shape forma, Paint pintura) {
    }
}
