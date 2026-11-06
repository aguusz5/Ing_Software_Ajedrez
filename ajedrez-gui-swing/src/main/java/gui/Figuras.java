package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Las figuras con las que se dibujan los personajes, en la caja de 100 x 100: elipses, rectángulos, polígonos,
 * líneas y curvas con grosor, uniones, recortes y reflejos. Las usan los personajes de todas las eras.
 */
final class Figuras {

    private Figuras() {
    }

    static final double LADO = Personaje.LADO;

    static Personaje.Capa capa(Shape forma, Paint pintura) {
        return new Personaje.Capa(forma, pintura);
    }

    static Color rgb(int rojo, int verde, int azul) {
        return new Color(rojo, verde, azul);
    }

    static Shape elipse(double x, double y, double ancho, double alto) {
        return new Ellipse2D.Double(x, y, ancho, alto);
    }

    static Shape rect(double x, double y, double ancho, double alto) {
        return new Rectangle2D.Double(x, y, ancho, alto);
    }

    static Shape redondeado(double x, double y, double ancho, double alto, double radio) {
        return new RoundRectangle2D.Double(x, y, ancho, alto, radio * 2, radio * 2);
    }

    /** Un polígono cerrado: x1, y1, x2, y2, ... */
    static Shape poligono(double... puntos) {
        Path2D poligono = new Path2D.Double();
        poligono.moveTo(puntos[0], puntos[1]);
        for (int i = 2; i < puntos.length; i += 2) {
            poligono.lineTo(puntos[i], puntos[i + 1]);
        }
        poligono.closePath();
        return poligono;
    }

    /** Una línea con grosor, como forma rellenable. */
    static Shape linea(double x1, double y1, double x2, double y2, double grosor) {
        return trazo(new Line2D.Double(x1, y1, x2, y2), grosor);
    }

    /** Una curva de (x1, y1) a (x2, y2) que se dobla hacia (cx, cy), con grosor. */
    static Shape arco(double x1, double y1, double cx, double cy, double x2, double y2, double grosor) {
        return trazo(new QuadCurve2D.Double(x1, y1, cx, cy, x2, y2), grosor);
    }

    static Shape trazo(Shape camino, double grosor) {
        return new BasicStroke((float) grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND).createStrokedShape(camino);
    }

    static Shape con(Shape una, Shape otra) {
        Area union = new Area(una);
        union.add(new Area(otra));
        return union;
    }

    /** La parte de la forma que queda dentro del límite. */
    static Shape dentro(Shape forma, Shape limite) {
        Area interseccion = new Area(forma);
        interseccion.intersect(new Area(limite));
        return interseccion;
    }

    /** La forma y su reflejo del otro lado de la cara: dos ojos, dos orejas, dos rodetes. */
    static Shape ambos(Shape forma) {
        AffineTransform espejo = new AffineTransform(-1, 0, 0, 1, LADO, 0);
        return con(forma, espejo.createTransformedShape(forma));
    }

    /** Una elipse con el borde en picos, como pelo: para Chewbacca. */
    static Shape pelaje(double cx, double cy, double rx, double ry, int puntas, double largo) {
        Path2D pelo = new Path2D.Double();
        for (int i = 0; i < puntas * 2; i++) {
            double angulo = Math.PI * i / puntas;
            double estiramiento = i % 2 == 0 ? largo : 0;
            double x = cx + (rx + estiramiento) * Math.cos(angulo);
            double y = cy + (ry + estiramiento) * Math.sin(angulo);
            if (i == 0) {
                pelo.moveTo(x, y);
            } else {
                pelo.lineTo(x, y);
            }
        }
        pelo.closePath();
        return pelo;
    }

    static Camino desde(double x, double y) {
        return new Camino(x, y);
    }

    /** Un contorno cerrado armado de a tramos: rectas y curvas. */
    static final class Camino {

        private final Path2D camino = new Path2D.Double();

        Camino(double x, double y) {
            camino.moveTo(x, y);
        }

        Camino a(double x, double y) {
            camino.lineTo(x, y);
            return this;
        }

        /** Curva hasta (x, y) que se dobla hacia (cx, cy). */
        Camino curva(double cx, double cy, double x, double y) {
            camino.quadTo(cx, cy, x, y);
            return this;
        }

        /** Curva hasta (x, y) con dos puntos de control. */
        Camino curva(double c1x, double c1y, double c2x, double c2y, double x, double y) {
            camino.curveTo(c1x, c1y, c2x, c2y, x, y);
            return this;
        }

        Shape cerrar() {
            camino.closePath();
            return camino;
        }
    }
}
