package gui;

import static gui.Figuras.ambos;
import static gui.Figuras.arco;
import static gui.Figuras.capa;
import static gui.Figuras.con;
import static gui.Figuras.dentro;
import static gui.Figuras.desde;
import static gui.Figuras.elipse;
import static gui.Figuras.linea;
import static gui.Figuras.poligono;
import static gui.Figuras.rect;
import static gui.Figuras.redondeado;
import static gui.Figuras.rgb;

import java.awt.Color;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Los personajes de la Guerra de los Clones: la República (blancas) contra los Separatistas (negras). El rey de
 * la República es el Maestro Yoda, que ya está dibujado en PersonajesDeStarWars. Igual que los otros, son
 * figuras simples en la caja de 100 x 100, hechas a mano.
 */
final class PersonajesDeLaGuerraDeLosClones {

    private static final Color PIEL = rgb(236, 196, 160);
    private static final Color AZUL_501 = rgb(40, 90, 200);

    private PersonajesDeLaGuerraDeLosClones() {
    }

    // ---------------------------------------------------------------- La República (blancas)

    /** Túnica oscura, pelo castaño hasta el cuello, la cicatriz del ojo y el sable azul en alto. */
    static Personaje anakin() {
        Shape cuerpo = desde(36, 62).a(64, 62).curva(80, 64, 84, 74).a(88, 98).a(12, 98).a(16, 74)
                .curva(20, 64, 36, 62).cerrar();
        return new Personaje("Anakin Skywalker", List.of(
                capa(cuerpo, rgb(52, 40, 34)),
                capa(poligono(40, 62, 60, 62, 50, 82), rgb(92, 70, 54)),
                capa(dentro(rect(10, 86, 80, 5), cuerpo), rgb(30, 22, 18)),
                capa(rect(44, 54, 12, 10), PIEL),
                capa(desde(31, 58).curva(26, 30, 34, 18).curva(50, 6, 66, 18).curva(74, 30, 69, 58)
                        .a(63, 58).a(62, 34).a(38, 34).a(37, 58).cerrar(), rgb(128, 84, 46)),
                capa(elipse(35, 22, 30, 38), PIEL),
                capa(desde(34, 38).curva(32, 18, 50, 17).curva(68, 18, 66, 38).curva(62, 27, 50, 27)
                        .curva(40, 27, 34, 38).cerrar(), rgb(128, 84, 46)),
                capa(ambos(elipse(42, 41, 4, 4)), rgb(50, 90, 140)),
                capa(linea(58, 36, 56.5, 47, 1.1), rgb(196, 120, 110)),
                capa(linea(46, 52, 54, 52, 1.6), rgb(170, 100, 90)),
                capa(linea(81, 60, 95, 8, 6), new Color(80, 160, 255, 150)),
                capa(linea(81, 60, 95, 8, 3.4), rgb(150, 205, 255)),
                capa(linea(81, 60, 95, 8, 1.4), rgb(255, 255, 255)),
                capa(linea(78, 72, 81, 60, 4.5), rgb(185, 185, 195)),
                capa(elipse(75, 64, 8, 8), rgb(60, 60, 66)))); // la mano mecánica, con guante negro
    }

    /** Túnica clara de Jedi con capa marrón, pelo y barba rojizos. */
    static Personaje obiWan() {
        Shape capaJedi = desde(30, 62).a(70, 62).curva(84, 68, 88, 80).a(92, 98).a(8, 98).a(12, 80)
                .curva(16, 68, 30, 62).cerrar();
        Shape tunica = desde(38, 62).a(62, 62).a(66, 98).a(34, 98).cerrar();
        Color pelo = rgb(176, 104, 56);
        return new Personaje("Obi-Wan Kenobi", List.of(
                capa(capaJedi, rgb(110, 76, 48)),
                capa(tunica, rgb(226, 212, 184)),
                capa(con(linea(44, 62, 50, 80, 3), linea(56, 62, 50, 80, 3)), rgb(196, 178, 146)),
                capa(rect(34, 84, 32, 5), rgb(120, 86, 56)),
                capa(rect(44, 54, 12, 10), PIEL),
                capa(elipse(35, 21, 30, 38), PIEL),
                capa(desde(33, 40).curva(31, 15, 50, 14).curva(69, 15, 67, 40).curva(64, 26, 50, 26)
                        .curva(38, 26, 33, 40).cerrar(), pelo),
                capa(desde(36, 44).curva(37, 62, 50, 64).curva(63, 62, 64, 44).curva(60, 54, 50, 55)
                        .curva(40, 54, 36, 44).cerrar(), pelo),
                capa(arco(44, 50, 50, 48, 56, 50, 2.2), pelo),
                capa(ambos(elipse(42, 40, 4, 4)), rgb(60, 110, 150)),
                capa(linea(46.5, 53, 53.5, 53, 1.3), rgb(150, 80, 70))));
    }

    /** Piel naranja con las marcas blancas, los montrales y los lekku a rayas azules y blancas. */
    static Personaje ahsoka() {
        Shape cuerpo = desde(36, 66).a(64, 66).curva(78, 68, 82, 78).a(86, 98).a(14, 98).a(18, 78)
                .curva(22, 68, 36, 66).cerrar();
        Shape montral = desde(36, 30).curva(28, 16, 31, 2).curva(40, 10, 44, 26).cerrar();
        Shape lekku = desde(35, 44).curva(26, 62, 30, 88).curva(36, 90, 40, 86).curva(38, 64, 42, 50).cerrar();
        Color naranja = rgb(220, 112, 52);
        Color blanco = rgb(246, 244, 240);
        Color azul = rgb(54, 96, 176);
        return new Personaje("Ahsoka Tano", List.of(
                capa(cuerpo, rgb(150, 60, 44)),
                capa(dentro(rect(10, 84, 80, 4), cuerpo), rgb(90, 60, 40)),
                capa(rect(44, 56, 12, 12), naranja),
                capa(ambos(lekku), blanco),
                capa(ambos(dentro(con(rect(20, 58, 30, 7), rect(20, 72, 30, 7)), lekku)), azul),
                capa(ambos(montral), blanco),
                capa(ambos(dentro(rect(20, 12, 30, 7), montral)), azul),
                capa(elipse(35, 24, 30, 36), naranja),
                capa(poligono(50, 27, 53.5, 32, 50, 37, 46.5, 32), blanco),
                capa(ambos(arco(38, 37, 41, 33.5, 45, 35, 1.8)), blanco),
                capa(con(arco(44, 56, 50, 59.5, 56, 56, 2.4), ambos(arco(37, 46, 38, 50, 41, 52, 1.6))), blanco),
                capa(ambos(elipse(41.5, 40, 5, 4.5)), rgb(40, 70, 130)),
                capa(linea(47, 51.5, 53, 51.5, 1.3), rgb(120, 50, 40))));
    }

    /** El caminante de seis patas de la República, gris claro con las marcas rojas. */
    static Personaje atte() {
        Color casco = rgb(208, 210, 204);
        Color sombra = rgb(150, 154, 150);
        Color rojo = rgb(170, 40, 40);
        Shape cuerpo = desde(10, 34).a(78, 30).curva(92, 30, 92, 44).a(90, 60).a(12, 64).curva(6, 50, 10, 34).cerrar();
        Shape cabina = desde(78, 32).a(96, 38).a(96, 54).a(84, 58).cerrar();
        Shape patas = con(con(linea(22, 60, 16, 88, 5), linea(46, 60, 46, 90, 5)), linea(70, 58, 78, 88, 5));
        Shape patasDeAtras = con(con(linea(30, 60, 30, 86, 4), linea(56, 60, 60, 86, 4)), linea(80, 56, 90, 84, 4));
        return new Personaje("AT-TE", List.of(
                capa(patasDeAtras, sombra),
                capa(con(con(rect(25, 84, 10, 6), rect(55, 84, 10, 6)), rect(85, 82, 10, 6)), sombra),
                capa(patas, casco),
                capa(con(con(redondeado(9, 86, 14, 8, 3), redondeado(39, 88, 14, 8, 3)),
                        redondeado(71, 86, 14, 8, 3)), rgb(120, 124, 120)),
                capa(cuerpo, casco),
                capa(dentro(rect(0, 52, 100, 14), cuerpo), sombra),
                capa(cabina, rgb(190, 192, 186)),
                capa(con(rect(86, 41, 8, 4), rect(86, 47, 8, 4)), rgb(30, 34, 40)),
                capa(redondeado(30, 22, 30, 12, 4), sombra),
                capa(linea(46, 22, 70, 12, 3.5), rgb(120, 124, 120)),
                capa(con(rect(16, 40, 6, 12), rect(40, 38, 6, 12)), rojo),
                capa(con(rect(64, 37, 6, 12), poligono(26, 44, 34, 40, 34, 48)), rojo)));
    }

    /** Casco de la fase II con el visor en T y las rayas azules de la 501. */
    static Personaje soldadoClon() {
        Shape hombros = desde(14, 98).a(18, 80).curva(50, 66, 82, 80).a(86, 98).cerrar();
        Shape casco = desde(50, 6).curva(78, 6, 80, 30, 80, 46).a(78, 70).curva(74, 84, 60, 86).a(40, 86)
                .curva(26, 84, 22, 70).a(20, 46).curva(20, 30, 22, 6, 50, 6).cerrar();
        Color blanco = rgb(242, 242, 238);
        return new Personaje("Soldado clon", List.of(
                capa(hombros, blanco),
                capa(dentro(con(rect(14, 84, 14, 14), rect(72, 84, 14, 14)), hombros), AZUL_501),
                capa(casco, blanco),
                capa(dentro(elipse(58, 0, 60, 100), casco), rgb(212, 214, 218)),
                capa(dentro(rect(45, 4, 10, 30), casco), AZUL_501),
                capa(ambos(dentro(poligono(20, 50, 28, 46, 30, 70, 22, 72), casco)), AZUL_501),
                capa(redondeado(27, 38, 46, 9, 3), rgb(20, 22, 26)),
                capa(poligono(45, 46, 55, 46, 53.5, 66, 46.5, 66), rgb(20, 22, 26)),
                capa(desde(36, 74).curva(50, 66, 64, 74).a(60, 80).curva(50, 74, 40, 80).cerrar(), rgb(150, 156, 166)),
                capa(ambos(rect(23, 54, 4, 10)), rgb(150, 156, 166))));
    }

    // ---------------------------------------------------------------- Los Separatistas (negras)

    /** Capa marrón oscura con cadena plateada, pelo y barba blancos, sable rojo de empuñadura curva. */
    static Personaje dooku() {
        Shape capaSith = desde(28, 62).a(72, 62).curva(86, 68, 90, 80).a(94, 98).a(6, 98).a(10, 80)
                .curva(14, 68, 28, 62).cerrar();
        Color canas = rgb(222, 222, 222);
        return new Personaje("Conde Dooku", List.of(
                capa(capaSith, rgb(70, 44, 32)),
                capa(poligono(38, 62, 62, 62, 60, 98, 40, 98), rgb(26, 24, 28)),
                capa(arco(36, 66, 50, 72, 64, 66, 1.8), rgb(196, 198, 206)),
                capa(rect(44, 54, 12, 10), PIEL),
                capa(elipse(35, 18, 30, 40), PIEL),
                capa(desde(34, 36).curva(32, 14, 50, 13).curva(68, 14, 66, 36).curva(62, 22, 50, 22)
                        .curva(38, 22, 34, 36).cerrar(), canas),
                capa(desde(40, 50).curva(41, 61, 50, 62).curva(59, 61, 60, 50).curva(56, 56, 50, 56)
                        .curva(44, 56, 40, 50).cerrar(), canas),
                capa(ambos(arco(39, 36, 42, 34.5, 46, 36, 1.4)), rgb(150, 150, 150)),
                capa(ambos(elipse(42, 39, 4, 3.5)), rgb(60, 50, 40)),
                capa(linea(47, 53, 53, 53, 1.2), rgb(150, 90, 80)),
                capa(linea(83, 62, 96, 12, 6), new Color(255, 60, 60, 150)),
                capa(linea(83, 62, 96, 12, 3.4), rgb(255, 140, 140)),
                capa(linea(83, 62, 96, 12, 1.4), rgb(255, 255, 255)),
                capa(arco(78, 74, 84, 70, 83, 62, 4), rgb(150, 110, 70)),
                capa(elipse(74, 66, 8, 8), PIEL)));
    }

    /** La máscara de hueso con los ojos amarillos, sobre el cuerpo de droide y la capa. */
    static Personaje grievous() {
        Shape capaGeneral = desde(22, 60).a(78, 60).curva(90, 72, 94, 98).a(6, 98).curva(10, 72, 22, 60).cerrar();
        Shape mascara = desde(50, 6).curva(72, 6, 74, 26).a(70, 52).curva(66, 72, 56, 80).a(44, 80)
                .curva(34, 72, 30, 52).a(26, 26).curva(28, 6, 50, 6).cerrar();
        Shape cuencas = ambos(poligono(31, 32, 46, 34, 45, 44, 35, 44));
        return new Personaje("General Grievous", List.of(
                capa(capaGeneral, rgb(110, 112, 120)),
                capa(poligono(40, 62, 60, 62, 56, 98, 44, 98), rgb(70, 74, 82)),
                capa(con(con(rect(46, 66, 8, 3), rect(46, 72, 8, 3)), rect(46, 78, 8, 3)), rgb(140, 146, 156)),
                capa(mascara, rgb(232, 226, 206)),
                capa(dentro(rect(56, 0, 30, 100), mascara), rgb(200, 194, 172)),
                capa(cuencas, rgb(28, 26, 30)),
                capa(ambos(elipse(37, 37, 6, 4)), rgb(250, 210, 40)),
                capa(ambos(elipse(39, 38, 2, 2)), rgb(30, 20, 10)),
                capa(con(linea(47, 46, 46, 60, 1.6), linea(53, 46, 54, 60, 1.6)), rgb(150, 144, 124)),
                capa(con(con(linea(42, 70, 42, 76, 1.4), linea(50, 70, 50, 77, 1.4)), linea(58, 70, 58, 76, 1.4)),
                        rgb(120, 114, 98)),
                capa(ambos(arco(30, 18, 40, 12, 50, 12, 2)), rgb(170, 164, 142))));
    }

    /** Cabeza rapada y pálida con las marcas oscuras, ropa negra y el sable rojo. */
    static Personaje ventress() {
        Shape cuerpo = desde(36, 64).a(64, 64).curva(78, 66, 82, 76).a(86, 98).a(14, 98).a(18, 76)
                .curva(22, 66, 36, 64).cerrar();
        Color palida = rgb(226, 226, 232);
        Color marcas = rgb(70, 70, 92);
        return new Personaje("Asajj Ventress", List.of(
                capa(cuerpo, rgb(30, 30, 38)),
                capa(con(arco(36, 66, 30, 80, 26, 98, 1.6), arco(64, 66, 70, 80, 74, 98, 1.6)), rgb(70, 70, 84)),
                capa(rect(45, 54, 10, 12), palida),
                capa(elipse(34, 14, 32, 44), palida),
                capa(con(linea(50, 14, 50, 30, 2), ambos(arco(40, 16, 38, 24, 40, 32, 1.8))), marcas),
                capa(ambos(arco(36, 28, 34, 36, 38, 42, 1.4)), marcas),
                capa(ambos(poligono(39, 37, 47, 38, 46, 42, 40, 41)), rgb(20, 20, 26)),
                capa(ambos(elipse(42, 38.5, 3, 3)), rgb(150, 190, 230)),
                capa(linea(46, 52, 54, 52, 1.6), rgb(110, 90, 110)),
                capa(linea(18, 62, 4, 10, 6), new Color(255, 60, 60, 150)),
                capa(linea(18, 62, 4, 10, 3.4), rgb(255, 140, 140)),
                capa(linea(18, 62, 4, 10, 1.4), rgb(255, 255, 255)),
                capa(arco(22, 76, 16, 72, 18, 62, 4), rgb(160, 160, 170)),
                capa(elipse(17, 66, 8, 8), palida)));
    }

    /** El droide destructor desplegado: cuerpo de bronce, tres patas, los cañones y su escudo azul. */
    static Personaje droideka() {
        Color bronce = rgb(166, 112, 64);
        Color oscuro = rgb(96, 62, 36);
        return new Personaje("Droideka", List.of(
                capa(elipse(4, 4, 92, 92), new Color(90, 170, 255, 70)),
                capa(con(con(linea(50, 60, 22, 92, 4.5), linea(50, 60, 78, 92, 4.5)), linea(50, 60, 50, 94, 4.5)), oscuro),
                capa(con(con(elipse(16, 89, 12, 6), elipse(72, 89, 12, 6)), elipse(44, 91, 12, 6)), bronce),
                capa(ambos(con(linea(38, 48, 18, 64, 4), linea(18, 64, 16, 76, 4))), oscuro),
                capa(ambos(redondeado(10, 72, 12, 8, 2)), rgb(70, 72, 80)),
                capa(desde(30, 52).curva(30, 34, 50, 34).curva(70, 34, 70, 52).a(64, 64).a(36, 64).cerrar(), bronce),
                capa(con(arco(34, 46, 50, 40, 66, 46, 1.4), arco(36, 56, 50, 52, 64, 56, 1.4)), oscuro),
                capa(desde(36, 34).curva(36, 16, 50, 14).curva(64, 16, 64, 34).cerrar(), bronce),
                capa(ambos(elipse(40, 24, 6, 6)), rgb(30, 20, 18)),
                capa(ambos(elipse(41.5, 25.5, 2, 2)), rgb(230, 70, 40)),
                capa(linea(50, 16, 50, 34, 1.4), oscuro)));
    }

    /** El súper droide de combate: hombros enormes, cabeza chica metida en el pecho, azul acero. */
    static Personaje superDroide() {
        Paint acero = new RadialGradientPaint(new Point2D.Double(40, 40), 60f, new float[] {0f, 1f},
                new Color[] {rgb(120, 130, 150), rgb(50, 56, 72)});
        Shape torso = desde(20, 32).curva(50, 22, 80, 32).a(88, 58).curva(80, 80, 64, 84).a(36, 84)
                .curva(20, 80, 12, 58).cerrar();
        return new Personaje("Súper droide de combate", List.of(
                capa(ambos(redondeado(4, 34, 16, 50, 6)), rgb(64, 70, 88)),
                capa(ambos(redondeado(2, 78, 20, 16, 4)), rgb(84, 90, 108)),
                capa(con(rect(36, 82, 10, 16), rect(54, 82, 10, 16)), rgb(64, 70, 88)),
                capa(torso, acero),
                capa(redondeado(30, 48, 40, 22, 6), rgb(70, 78, 98)),
                capa(con(rect(36, 54, 28, 2.5), rect(36, 60, 28, 2.5)), rgb(110, 118, 140)),
                capa(redondeado(41, 18, 18, 16, 6), rgb(84, 92, 112)),
                capa(ambos(elipse(44, 23, 4, 4)), rgb(250, 230, 160)),
                capa(ambos(elipse(14, 36, 8, 8)), rgb(140, 150, 170))));
    }

    /** El droide de combate B1: cabeza alargada de color arena, cuello fino y la mochila con la antena. */
    static Personaje droideDeCombate() {
        Color arena = rgb(222, 200, 150);
        Color sombra = rgb(176, 152, 104);
        Shape cabeza = desde(30, 14).curva(46, 6, 60, 12).a(64, 26).curva(66, 46, 56, 58).curva(50, 62, 46, 58)
                .curva(40, 44, 30, 30).cerrar();
        Shape torso = desde(24, 74).curva(50, 66, 76, 74).a(78, 98).a(22, 98).cerrar();
        return new Personaje("Droide de combate", List.of(
                capa(rect(66, 30, 2, 36), rgb(120, 110, 90)),
                capa(redondeado(60, 62, 18, 24, 3), sombra),
                capa(torso, arena),
                capa(dentro(rect(56, 60, 30, 40), torso), sombra),
                capa(poligono(40, 76, 60, 76, 56, 90, 44, 90), sombra),
                capa(con(linea(48, 56, 47, 74, 3.2), linea(53, 56, 54, 74, 3.2)), rgb(150, 130, 90)),
                capa(cabeza, arena),
                capa(dentro(rect(54, 0, 30, 70), cabeza), sombra),
                capa(con(elipse(36, 20, 7, 7), elipse(52, 20, 7, 7)), rgb(46, 40, 30)),
                capa(con(arco(44, 34, 48, 36, 52, 34, 1.2), arco(44, 42, 48, 44, 52, 42, 1.2)), sombra)));
    }
}
