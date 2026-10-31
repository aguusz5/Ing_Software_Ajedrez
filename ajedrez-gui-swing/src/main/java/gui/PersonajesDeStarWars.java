package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Los personajes del tema Star Wars, uno por tipo de pieza y bando: la Rebelión juega con blancas y el
 * Imperio con negras. Se buscan por el nombre del tipo, como los glifos: una pieza que no está en la
 * tabla no tiene personaje, y el tema la dibuja con su símbolo.
 * Los dibujos son figuras simples (elipses, polígonos, curvas) en una caja de 100 x 100, hechos a mano:
 * no hay imágenes bajadas de internet.
 */
public final class PersonajesDeStarWars {

    private static final Map<String, Personaje> REBELION = Map.of(
            "rey", luke(), "dama", leia(), "alfil", yoda(),
            "caballo", chewbacca(), "torre", r2d2(), "peón", pilotoRebelde());
    private static final Map<String, Personaje> IMPERIO = Map.of(
            "rey", palpatine(), "dama", vader(), "alfil", guardiaImperial(),
            "caballo", bobaFett(), "torre", estrellaDeLaMuerte(), "peón", stormtrooper());

    Optional<Personaje> personajeDe(String tipo, modelo.Color bando) {
        return Optional.ofNullable((bando == modelo.Color.BLANCO ? REBELION : IMPERIO).get(tipo));
    }

    // ---------------------------------------------------------------- La Rebelión (blancas)

    private static Personaje luke() {
        Shape cuerpo = desde(36, 62).a(64, 62).curva(80, 64, 84, 74).a(88, 98).a(12, 98).a(16, 74)
                .curva(20, 64, 36, 62).cerrar();
        return new Personaje("Luke Skywalker", List.of(
                capa(cuerpo, rgb(232, 226, 210)),
                capa(dentro(rect(10, 86, 80, 5), cuerpo), rgb(120, 82, 50)),
                capa(arco(40, 62, 46, 74, 56, 86, 2), rgb(196, 186, 165)),
                capa(rect(44, 54, 12, 10), rgb(226, 182, 146)),
                capa(elipse(35, 22, 30, 38), rgb(238, 196, 160)),
                capa(desde(33, 40).curva(29, 13, 50, 12).curva(73, 12, 67, 40).curva(64, 26, 54, 25)
                        .curva(44, 31, 36, 27).curva(34, 32, 33, 40).cerrar(), rgb(214, 170, 90)),
                capa(ambos(elipse(42, 41, 4, 4)), rgb(50, 90, 140)),
                capa(linea(46, 52, 54, 52, 1.6), rgb(170, 100, 90)),
                // el sable de luz, en alto: hoja verde con el centro blanco y la empuñadura plateada
                capa(linea(81, 60, 95, 8, 6), new Color(110, 255, 130, 150)),
                capa(linea(81, 60, 95, 8, 3.4), rgb(170, 255, 180)),
                capa(linea(81, 60, 95, 8, 1.4), rgb(255, 255, 255)),
                capa(linea(78, 72, 81, 60, 4.5), rgb(185, 185, 195)),
                capa(linea(79.2, 67, 79.8, 64.5, 4.8), rgb(40, 40, 45)),
                capa(elipse(75, 64, 8, 8), rgb(238, 196, 160))));
    }

    private static Personaje leia() {
        Shape vestido = desde(40, 60).a(60, 60).curva(76, 66, 80, 78).a(86, 98).a(14, 98).a(20, 78)
                .curva(24, 66, 40, 60).cerrar();
        Shape rodete = elipse(12, 29, 23, 27);
        return new Personaje("Princesa Leia", List.of(
                capa(vestido, rgb(246, 246, 246)),
                capa(con(arco(40, 66, 36, 82, 30, 98, 1.6), arco(60, 66, 64, 82, 70, 98, 1.6)), rgb(205, 205, 212)),
                capa(dentro(rect(10, 90, 80, 3), vestido), rgb(170, 172, 180)),
                capa(rect(45, 52, 10, 10), rgb(226, 190, 160)),
                capa(ambos(rodete), rgb(96, 58, 32)),
                capa(ambos(arco(17, 42, 23, 33, 30, 41, 1.6)), rgb(66, 38, 20)),
                capa(ambos(arco(19, 47, 24, 53, 29, 46, 1.4)), rgb(66, 38, 20)),
                capa(elipse(33, 17, 34, 28), rgb(96, 58, 32)),
                capa(elipse(36, 25, 28, 34), rgb(242, 208, 180)),
                capa(desde(35, 38).curva(37, 21, 50, 21).curva(63, 21, 65, 38).curva(60, 28, 50, 29)
                        .curva(40, 28, 35, 38).cerrar(), rgb(96, 58, 32)),
                capa(ambos(elipse(42.5, 41, 4, 4)), rgb(60, 40, 30)),
                capa(elipse(46.5, 50.5, 7, 3), rgb(200, 90, 90))));
    }

    private static Personaje yoda() {
        Shape oreja = desde(38, 42).curva(18, 33, 1, 25).curva(6, 41, 20, 49).curva(29, 55, 37, 55).cerrar();
        Shape adentroDeLaOreja = desde(34, 45).curva(20, 39, 9, 33).curva(14, 43, 24, 48).curva(30, 51, 34, 51)
                .cerrar();
        Shape tunica = desde(34, 62).a(66, 62).curva(80, 68, 84, 80).a(88, 98).a(12, 98).a(16, 80)
                .curva(20, 68, 34, 62).cerrar();
        return new Personaje("Maestro Yoda", List.of(
                capa(tunica, rgb(150, 120, 80)),
                capa(poligono(41, 62, 59, 62, 50, 80), rgb(215, 200, 165)),
                capa(ambos(oreja), rgb(122, 160, 84)),
                capa(ambos(adentroDeLaOreja), rgb(150, 120, 100)),
                capa(elipse(28, 25, 44, 38), rgb(130, 168, 90)),
                capa(con(arco(38, 33, 50, 29, 62, 33, 1.3), arco(41, 37, 50, 34, 59, 37, 1.1)), rgb(98, 132, 66)),
                capa(ambos(elipse(35, 40, 11, 8)), rgb(225, 220, 170)),
                capa(ambos(elipse(39, 41.5, 4.5, 4.5)), rgb(40, 30, 20)),
                capa(ambos(arco(34.5, 41, 40.5, 37, 46.5, 41, 1.6)), rgb(98, 132, 66)),
                capa(linea(48, 49, 52, 49, 1.6), rgb(98, 132, 66)),
                capa(arco(42, 56, 50, 59, 58, 56, 1.5), rgb(80, 100, 55)),
                capa(con(arco(36, 28, 30, 22, 25, 22, 0.8), arco(64, 28, 70, 22, 75, 22, 0.8)), rgb(235, 235, 235))));
    }

    private static Personaje chewbacca() {
        Shape cuerpo = dentro(pelaje(50, 100, 44, 38, 26, 4), rect(0, 0, LADO, 98));
        return new Personaje("Chewbacca", List.of(
                capa(cuerpo, rgb(118, 76, 40)),
                capa(pelaje(50, 45, 25, 31, 24, 4), rgb(136, 88, 48)),
                capa(elipse(37, 46, 26, 22), rgb(176, 134, 88)),
                capa(ambos(arco(37, 38, 43, 34, 48, 37, 2.2)), rgb(176, 134, 88)),
                capa(ambos(elipse(40, 39, 6, 4.5)), rgb(28, 26, 36)),
                capa(elipse(44.5, 49, 11, 6.5), rgb(32, 24, 20)),
                capa(arco(43, 60, 50, 64, 57, 60, 1.8), rgb(60, 34, 24)),
                // la bandolera, con las cartucheras plateadas
                capa(dentro(linea(22, 64, 84, 100, 8), cuerpo), rgb(66, 44, 26)),
                capa(con(con(rect(31, 67.5, 5, 5), rect(43, 74.5, 5, 5)), con(rect(55, 81.5, 5, 5), rect(67, 88.5, 5, 5))),
                        rgb(178, 180, 188))));
    }

    private static Personaje r2d2() {
        Shape pata = redondeado(14, 42, 12, 44, 4);
        Shape cuerpo = redondeado(26, 34, 48, 54, 8);
        Shape domo = dentro(elipse(26, 12, 48, 44), rect(26, 12, 48, 22));
        Paint plateado = new RadialGradientPaint(new Point2D.Double(42, 18), 30f,
                new float[] {0f, 1f}, new Color[] {rgb(235, 238, 245), rgb(160, 166, 178)});
        Color azul = rgb(40, 90, 190);
        return new Personaje("R2-D2", List.of(
                capa(ambos(pata), rgb(232, 234, 240)),
                capa(ambos(rect(17.5, 52, 5, 22)), azul),
                capa(ambos(elipse(15, 41, 10, 10)), rgb(150, 158, 172)),
                capa(ambos(poligono(10, 86, 30, 86, 32, 96, 8, 96)), rgb(200, 202, 210)),
                capa(redondeado(43, 84, 14, 10, 3), rgb(190, 192, 200)),
                capa(cuerpo, rgb(238, 240, 244)),
                capa(dentro(rect(62, 34, 12, 54), cuerpo), rgb(212, 216, 224)),
                capa(con(con(rect(33, 44, 8, 22), rect(59, 44, 8, 22)), con(rect(45, 44, 10, 6), rect(45, 54, 10, 12))),
                        azul),
                capa(con(rect(45, 70, 10, 3), rect(45, 75, 10, 3)), rgb(150, 155, 165)),
                capa(domo, plateado),
                capa(rect(26, 32.5, 48, 3), rgb(140, 146, 158)),
                capa(con(con(rect(30, 24, 8, 7), rect(62, 24, 7, 7)), rect(41, 13.5, 18, 4)), azul),
                capa(elipse(44, 19, 12, 12), rgb(20, 20, 26)),
                capa(elipse(47, 22, 5, 5), rgb(60, 70, 110)),
                capa(elipse(58.5, 26, 3.5, 3.5), rgb(230, 40, 40))));
    }

    private static Personaje pilotoRebelde() {
        Shape traje = desde(34, 66).a(66, 66).curva(80, 70, 84, 80).a(88, 98).a(12, 98).a(16, 80)
                .curva(20, 70, 34, 66).cerrar();
        Shape casco = con(elipse(27, 10, 46, 46), redondeado(27, 30, 46, 32, 14));
        return new Personaje("Piloto rebelde", List.of(
                capa(traje, rgb(226, 112, 36)),
                capa(dentro(con(rect(34, 66, 7, 34), rect(59, 66, 7, 34)), traje), rgb(240, 240, 234)),
                capa(redondeado(40, 78, 20, 12, 2), rgb(165, 170, 178)),
                capa(con(elipse(43, 81, 4, 4), elipse(53, 81, 4, 4)), rgb(210, 50, 40)),
                capa(rect(43, 58, 14, 10), rgb(60, 60, 66)),
                capa(casco, rgb(240, 240, 236)),
                capa(dentro(rect(45.5, 9, 9, 16), casco), rgb(205, 55, 40)),
                capa(ambos(dentro(rect(27, 36, 5, 8), casco)), rgb(205, 55, 40)),
                capa(redondeado(36, 32, 28, 28, 11), rgb(236, 196, 160)),
                capa(redondeado(32, 24, 36, 10, 5), rgb(74, 62, 50)),
                capa(ambos(elipse(42.5, 41, 4, 4)), rgb(60, 45, 35)),
                capa(linea(46, 52, 54, 52, 1.6), rgb(170, 100, 90)),
                capa(arco(36, 54, 50, 66, 64, 54, 2), rgb(70, 70, 76))));
    }

    // ---------------------------------------------------------------- El Imperio (negras)

    private static Personaje palpatine() {
        Shape capucha = desde(50, 4).curva(78, 4, 86, 40, 86, 60).a(94, 98).a(6, 98).a(14, 60)
                .curva(14, 40, 22, 4, 50, 4).cerrar();
        Shape hueco = desde(50, 18).curva(70, 18, 74, 44, 72, 62).curva(70, 84, 50, 88).curva(30, 84, 28, 62)
                .curva(26, 44, 30, 18, 50, 18).cerrar();
        Color sombra = rgb(8, 6, 10);
        return new Personaje("Emperador Palpatine", List.of(
                capa(capucha, rgb(34, 30, 40)),
                capa(con(arco(30, 24, 18, 50, 16, 96, 2), arco(70, 24, 82, 50, 84, 96, 2)), rgb(70, 64, 80)),
                capa(hueco, sombra),
                capa(elipse(35, 34, 30, 46), rgb(196, 186, 168)),
                capa(elipse(29, 22, 42, 26), sombra),
                capa(ambos(elipse(39.5, 51, 8, 4.5)), rgb(240, 190, 40)),
                capa(ambos(elipse(42, 51.5, 3, 3.5)), rgb(20, 10, 10)),
                capa(ambos(arco(39, 58, 43, 61, 47, 58, 1)), rgb(140, 128, 112)),
                capa(ambos(arco(38, 64, 40, 70, 44, 74, 1)), rgb(140, 128, 112)),
                capa(linea(50, 54, 50, 63, 1.2), rgb(160, 148, 130)),
                capa(arco(43, 71, 50, 74, 57, 71, 1.6), rgb(90, 50, 50))));
    }

    private static Personaje vader() {
        Shape falda = desde(26, 34).curva(22, 70, 8, 90).a(14, 95).a(86, 95).a(92, 90).curva(78, 70, 74, 34)
                .cerrar();
        Shape mascara = desde(31, 36).a(69, 36).a(67, 60).curva(64, 80, 50, 88).curva(36, 80, 33, 60).cerrar();
        return new Personaje("Darth Vader", List.of(
                capa(con(elipse(24, 6, 52, 50), falda), rgb(22, 22, 26)),
                capa(arco(30, 22, 40, 10, 58, 9, 3.5), rgb(105, 105, 118)),
                capa(ambos(arco(24, 60, 18, 78, 11, 89, 1.6)), rgb(80, 80, 92)),
                capa(mascara, rgb(56, 57, 64)),
                capa(linea(30, 35, 70, 35, 2.2), rgb(105, 105, 118)),
                capa(ambos(poligono(32, 40, 47, 43, 46, 52, 35, 52)), rgb(10, 10, 12)),
                capa(ambos(elipse(36, 43, 4, 2.5)), rgb(150, 40, 40)),
                capa(poligono(47, 43, 53, 43, 55, 62, 45, 62), rgb(76, 76, 86)),
                capa(poligono(39, 64, 61, 64, 50, 85), rgb(160, 162, 172)),
                capa(con(con(linea(44, 66, 47, 76, 1.2), linea(50, 66, 50, 80, 1.2)), linea(56, 66, 53, 76, 1.2)),
                        rgb(40, 40, 46)),
                capa(ambos(linea(35, 62, 38, 76, 1.6)), rgb(150, 150, 162))));
    }

    private static Personaje guardiaImperial() {
        Shape manto = desde(30, 50).a(70, 50).curva(84, 70, 90, 98).a(10, 98).curva(16, 70, 30, 50).cerrar();
        Shape casco = con(elipse(33, 6, 34, 40), poligono(31, 26, 69, 26, 76, 60, 24, 60));
        return new Personaje("Guardia Imperial", List.of(
                capa(rect(85, 8, 3.4, 90), rgb(150, 150, 162)),
                capa(poligono(83.5, 10, 89.5, 10, 86.7, 0), rgb(190, 190, 200)),
                capa(manto, rgb(168, 20, 30)),
                capa(con(con(arco(36, 62, 30, 80, 26, 98, 1.6), arco(64, 62, 70, 80, 74, 98, 1.6)),
                        arco(50, 62, 49, 80, 50, 98, 1.6)), rgb(118, 10, 20)),
                capa(casco, rgb(200, 30, 40)),
                capa(arco(37, 22, 39, 11, 49, 8, 3), rgb(236, 96, 96)),
                capa(ambos(arco(30, 34, 27, 48, 26, 58, 1.4)), rgb(150, 18, 28)),
                capa(redondeado(37, 30, 26, 5.5, 2.5), rgb(10, 10, 10))));
    }

    private static Personaje bobaFett() {
        Shape casco = desde(26, 20).curva(26, 8, 50, 8).curva(74, 8, 74, 20).a(76, 64).curva(76, 82, 62, 85)
                .a(38, 85).curva(24, 82, 24, 64).cerrar();
        return new Personaje("Boba Fett", List.of(
                capa(redondeado(71, 28, 9, 22, 3), rgb(118, 124, 112)),
                capa(rect(73.5, 2, 3, 28), rgb(150, 150, 156)),
                capa(redondeado(71, 0, 8, 9, 2), rgb(120, 124, 120)),
                capa(casco, rgb(88, 110, 72)),
                capa(dentro(rect(24, 56, 52, 30), casco), rgb(176, 156, 92)),
                capa(dentro(rect(24, 22, 52, 7), casco), rgb(150, 56, 40)),
                capa(arco(30, 14, 36, 11, 42, 14, 1.2), rgb(60, 76, 50)),
                capa(con(rect(30, 34, 40, 9), rect(45.5, 34, 9, 34)), rgb(16, 16, 18)),
                capa(ambos(linea(36, 70, 40, 80, 1.4)), rgb(100, 88, 50))));
    }

    private static Personaje estrellaDeLaMuerte() {
        Paint gris = new RadialGradientPaint(new Point2D.Double(36, 32), 70f, new float[] {0f, 0.55f, 1f},
                new Color[] {rgb(210, 213, 220), rgb(138, 141, 150), rgb(52, 55, 63)});
        Paint plato = new RadialGradientPaint(new Point2D.Double(67, 37), 13f, new float[] {0f, 1f},
                new Color[] {rgb(80, 83, 92), rgb(150, 153, 162)});
        Color luces = new Color(250, 245, 210, 210);
        return new Personaje("Estrella de la Muerte", List.of(
                capa(elipse(8, 8, 84, 84), gris),
                capa(con(arco(14, 30, 50, 36, 86, 30, 0.9), arco(14, 70, 50, 76, 86, 70, 0.9)), rgb(110, 113, 122)),
                capa(arco(9, 50, 50, 56, 91, 50, 2.6), rgb(48, 50, 58)),
                capa(elipse(54, 24, 26, 26), plato),
                capa(trazo(new Ellipse2D.Double(58, 28, 18, 18), 1), rgb(60, 62, 70)),
                capa(elipse(65, 35, 4, 4), rgb(40, 42, 50)),
                capa(con(con(rect(24, 40, 2, 2), rect(32, 62, 2, 2)), con(rect(48, 66, 2, 2), rect(28, 26, 2, 2))), luces),
                capa(con(rect(62, 60, 2, 2), rect(40, 44, 2, 2)), luces)));
    }

    private static Personaje stormtrooper() {
        Shape casco = desde(50, 6).curva(80, 6, 84, 30, 82, 50).a(84, 72).curva(84, 86, 70, 92).a(30, 92)
                .curva(16, 86, 16, 72).a(18, 50).curva(16, 30, 20, 6, 50, 6).cerrar();
        return new Personaje("Stormtrooper", List.of(
                capa(casco, rgb(240, 240, 238)),
                capa(dentro(elipse(60, 0, 60, 100), casco), rgb(208, 211, 216)),
                capa(arco(22, 38, 50, 28, 78, 38, 2), rgb(172, 177, 188)),
                capa(ambos(rect(16.5, 55, 5, 13)), rgb(150, 156, 168)),
                capa(ambos(poligono(26, 44, 44, 46, 46, 52, 42, 58, 30, 56, 25, 50)), rgb(20, 22, 26)),
                capa(poligono(47, 50, 53, 50, 55, 66, 45, 66), rgb(222, 224, 228)),
                capa(desde(33, 77).curva(50, 63, 67, 77).a(63, 83).curva(50, 73, 37, 83).cerrar(), rgb(70, 72, 80)),
                capa(con(con(linea(44, 72, 44, 76, 1.2), linea(50, 70.5, 50, 75, 1.2)), linea(56, 72, 56, 76, 1.2)),
                        rgb(160, 162, 170)),
                capa(ambos(poligono(24, 70, 30, 68, 32, 80, 26, 82)), rgb(118, 124, 132))));
    }

    // ---------------------------------------------------------------- Ayudas para dibujar en la caja de 100 x 100

    private static final double LADO = Personaje.LADO;

    private static Personaje.Capa capa(Shape forma, Paint pintura) {
        return new Personaje.Capa(forma, pintura);
    }

    private static Color rgb(int rojo, int verde, int azul) {
        return new Color(rojo, verde, azul);
    }

    private static Shape elipse(double x, double y, double ancho, double alto) {
        return new Ellipse2D.Double(x, y, ancho, alto);
    }

    private static Shape rect(double x, double y, double ancho, double alto) {
        return new Rectangle2D.Double(x, y, ancho, alto);
    }

    private static Shape redondeado(double x, double y, double ancho, double alto, double radio) {
        return new RoundRectangle2D.Double(x, y, ancho, alto, radio * 2, radio * 2);
    }

    /** Un polígono cerrado: x1, y1, x2, y2, ... */
    private static Shape poligono(double... puntos) {
        Path2D poligono = new Path2D.Double();
        poligono.moveTo(puntos[0], puntos[1]);
        for (int i = 2; i < puntos.length; i += 2) {
            poligono.lineTo(puntos[i], puntos[i + 1]);
        }
        poligono.closePath();
        return poligono;
    }

    /** Una línea con grosor, como forma rellenable. */
    private static Shape linea(double x1, double y1, double x2, double y2, double grosor) {
        return trazo(new Line2D.Double(x1, y1, x2, y2), grosor);
    }

    /** Una curva de (x1, y1) a (x2, y2) que se dobla hacia (cx, cy), con grosor. */
    private static Shape arco(double x1, double y1, double cx, double cy, double x2, double y2, double grosor) {
        return trazo(new QuadCurve2D.Double(x1, y1, cx, cy, x2, y2), grosor);
    }

    private static Shape trazo(Shape camino, double grosor) {
        return new BasicStroke((float) grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND).createStrokedShape(camino);
    }

    private static Shape con(Shape una, Shape otra) {
        Area union = new Area(una);
        union.add(new Area(otra));
        return union;
    }

    /** La parte de la forma que queda dentro del límite. */
    private static Shape dentro(Shape forma, Shape limite) {
        Area interseccion = new Area(forma);
        interseccion.intersect(new Area(limite));
        return interseccion;
    }

    /** La forma y su reflejo del otro lado de la cara: dos ojos, dos orejas, dos rodetes. */
    private static Shape ambos(Shape forma) {
        AffineTransform espejo = new AffineTransform(-1, 0, 0, 1, LADO, 0);
        return con(forma, espejo.createTransformedShape(forma));
    }

    /** Una elipse con el borde en picos, como pelo: para Chewbacca. */
    private static Shape pelaje(double cx, double cy, double rx, double ry, int puntas, double largo) {
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

    private static Camino desde(double x, double y) {
        return new Camino(x, y);
    }

    /** Un contorno cerrado armado de a tramos: rectas y curvas. */
    private static final class Camino {

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
