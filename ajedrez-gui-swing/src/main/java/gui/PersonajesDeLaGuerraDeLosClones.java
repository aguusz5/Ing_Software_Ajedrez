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
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

/**
 * Los personajes de la Guerra de los Clones: la República (blancas) contra los Separatistas (negras). El rey de
 * la República es el Maestro Yoda, que ya está dibujado en PersonajesDeStarWars. Igual que los otros, son
 * figuras simples en la caja de 100 x 100, hechas a mano.
 */
final class PersonajesDeLaGuerraDeLosClones {

    private static final Color PIEL = rgb(236, 196, 160);
    private static final Color AZUL_501 = rgb(40, 90, 200);
    private static final HojaDeSable AZUL_SABLE = new HojaDeSable(new Color(80, 160, 255, 150), rgb(150, 200, 255));
    private static final HojaDeSable VERDE_SABLE = new HojaDeSable(new Color(60, 220, 80, 150), rgb(160, 255, 160));
    private static final HojaDeSable ROJO_SABLE = new HojaDeSable(new Color(255, 60, 60, 150), rgb(255, 140, 140));

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

    /**
     * Los cuatro brazos abiertos, cada uno con un sable (dos azules y dos verdes, de los jedi que venció), la
     * máscara de hueso alargada con los ojos amarillos de reptil hundidos y el pecho blindado blanco.
     */
    static Personaje grievous() {
        Color hueso = rgb(234, 228, 208);
        Color sombraDeHueso = rgb(196, 188, 164);
        Color metal = rgb(96, 100, 110);
        Shape capaGeneral = desde(28, 56).a(72, 56).curva(88, 70, 90, 98).a(10, 98).curva(12, 70, 28, 56).cerrar();
        Shape pecho = desde(36, 60).a(64, 60).curva(70, 74, 60, 90).a(40, 90).curva(30, 74, 36, 60).cerrar();
        Shape mascara = desde(50, 3).curva(68, 3, 70, 20).a(72, 30).a(66, 36).a(62, 52).curva(58, 62, 50, 63)
                .curva(42, 62, 38, 52).a(34, 36).a(28, 30).a(30, 20).curva(32, 3, 50, 3).cerrar();
        List<Personaje.Capa> capas = new ArrayList<>();
        capas.add(capa(capaGeneral, rgb(78, 72, 70)));
        capas.addAll(sable(26, 56, 6, 4, AZUL_SABLE));
        capas.addAll(sable(24, 80, 2, 50, VERDE_SABLE));
        capas.addAll(sable(74, 56, 94, 4, VERDE_SABLE));
        capas.addAll(sable(76, 80, 98, 50, AZUL_SABLE));
        capas.addAll(List.of(
                capa(ambos(con(linea(40, 64, 30, 66, 3.4), linea(30, 66, 26, 56, 3.4))), metal),
                capa(ambos(con(linea(40, 80, 30, 86, 3.4), linea(30, 86, 24, 80, 3.4))), metal),
                capa(ambos(con(linea(26, 58, 25, 52, 3.4), linea(24, 82, 23, 76, 3.4))), rgb(170, 172, 180)),
                capa(ambos(con(elipse(23, 53, 6, 6), elipse(21, 77, 6, 6))), hueso),
                capa(rect(46, 58, 8, 6), metal),
                capa(pecho, hueso),
                capa(dentro(rect(54, 50, 30, 50), pecho), sombraDeHueso),
                capa(con(con(arco(38, 68, 50, 72, 62, 68, 1.4), arco(38, 75, 50, 79, 62, 75, 1.4)),
                        arco(40, 82, 50, 86, 60, 82, 1.4)), rgb(150, 142, 120)),
                capa(mascara, hueso),
                capa(dentro(rect(54, 0, 30, 70), mascara), sombraDeHueso),
                capa(linea(50, 8, 50, 40, 2.4), sombraDeHueso),
                capa(ambos(arco(34, 16, 40, 9, 48, 10, 1.6)), rgb(160, 150, 126)),
                capa(ambos(poligono(31, 23, 47, 28, 46, 38, 35, 33)), rgb(26, 22, 24)),
                capa(ambos(poligono(35, 27, 45, 30, 43, 34, 36, 31)), rgb(250, 206, 40)),
                capa(ambos(rect(40, 28.5, 1.6, 4)), rgb(30, 16, 8)),
                capa(con(con(linea(45, 46, 45, 58, 1.6), linea(50, 47, 50, 60, 1.6)), linea(55, 46, 55, 58, 1.6)),
                        rgb(70, 64, 56))));
        return new Personaje("General Grievous", capas);
    }

    /**
     * La cabeza rapada y pálida con los tatuajes oscuros (la raya y el rombo en la frente, las marcas bajo los
     * ojos), los labios oscuros y los dos sables rojos de empuñadura curva, uno en cada mano.
     */
    static Personaje ventress() {
        Shape cuerpo = desde(36, 64).a(64, 64).curva(78, 66, 82, 76).a(86, 98).a(14, 98).a(18, 76)
                .curva(22, 66, 36, 64).cerrar();
        Color palida = rgb(228, 228, 234);
        Color tatuaje = rgb(52, 50, 92);
        List<Personaje.Capa> capas = new ArrayList<>();
        capas.addAll(sable(17, 72, 4, 14, ROJO_SABLE));
        capas.addAll(sable(83, 72, 96, 14, ROJO_SABLE));
        capas.addAll(List.of(
                capa(cuerpo, rgb(36, 34, 50)),
                capa(ambos(desde(36, 64).curva(26, 64, 22, 72).a(30, 74).curva(34, 68, 40, 66).cerrar()), palida),
                capa(poligono(40, 64, 60, 64, 56, 80, 44, 80), rgb(60, 56, 84)),
                capa(con(arco(36, 80, 30, 90, 28, 98, 1.6), arco(64, 80, 70, 90, 72, 98, 1.6)), rgb(70, 66, 96)),
                capa(ambos(arco(22, 72, 20, 78, 18, 80, 5)), palida),
                capa(ambos(arco(18, 82, 15, 78, 17, 70, 3.4)), rgb(150, 150, 160)),
                capa(ambos(elipse(14, 72, 8, 8)), palida),
                capa(rect(45, 52, 10, 13), palida),
                capa(elipse(35, 8, 30, 48), palida),
                capa(dentro(rect(52, 0, 30, 60), elipse(35, 8, 30, 48)), rgb(206, 206, 216)),
                capa(con(con(linea(50, 9, 50, 22, 2.2), poligono(50, 22, 54, 27, 50, 32, 46, 27)),
                        ambos(arco(38, 12, 37, 20, 41, 28, 1.8))), tatuaje),
                capa(ambos(arco(37, 31, 42, 29, 47, 31, 1.8)), tatuaje),
                capa(ambos(poligono(38, 34, 47, 35, 46, 39, 39, 38)), rgb(20, 20, 28)),
                capa(ambos(elipse(41.5, 35.5, 2.6, 2.6)), rgb(170, 200, 230)),
                capa(ambos(arco(38, 41, 41, 43, 46, 41, 1.4)), tatuaje),
                capa(elipse(45, 47, 10, 4), rgb(60, 50, 72)),
                capa(linea(50, 52, 50, 56, 1.6), tatuaje)));
        return new Personaje("Asajj Ventress", capas);
    }

    /**
     * El droide destructor de perfil, desplegado como en combate: la espalda curva de bronce, la cabeza en cuña
     * colgando adelante, los cañones dobles apuntando, las tres patas con garras y el escudo azul alrededor.
     */
    static Personaje droideka() {
        Color bronce = rgb(170, 116, 66);
        Color claro = rgb(204, 152, 96);
        Color oscuro = rgb(92, 60, 36);
        Shape espalda = desde(56, 14).curva(20, 10, 18, 40).curva(18, 58, 34, 66).a(42, 58).curva(30, 50, 30, 38)
                .curva(32, 22, 54, 24).cerrar();
        Shape cabeza = desde(50, 14).a(70, 16).curva(80, 22, 78, 34).a(64, 38).curva(56, 32, 50, 24).cerrar();
        return new Personaje("Droideka", List.of(
                capa(elipse(3, 3, 94, 94), new Color(90, 170, 255, 60)),
                capa(Figuras.trazo(elipse(3, 3, 94, 94), 2.2), new Color(130, 200, 255, 200)),
                capa(con(con(linea(36, 62, 22, 78, 4.5), linea(22, 78, 16, 92, 4)),
                        con(linea(48, 64, 60, 78, 4.5), linea(60, 78, 70, 92, 4))), oscuro),
                capa(con(linea(42, 66, 44, 80, 4.5), linea(44, 80, 38, 93, 4)), bronce),
                capa(con(con(poligono(10, 95, 20, 89, 22, 95), poligono(64, 95, 70, 89, 78, 95)),
                        poligono(32, 95, 38, 90, 44, 95)), oscuro),
                capa(espalda, bronce),
                capa(arco(24, 36, 26, 22, 44, 16, 2), claro),
                capa(elipse(32, 52, 22, 16), bronce),
                capa(con(linea(50, 58, 60, 50, 4), linea(60, 50, 72, 52, 4)), oscuro),
                capa(con(redondeado(66, 45, 26, 4, 2), redondeado(66, 52, 26, 4, 2)), rgb(70, 72, 80)),
                capa(con(rect(90, 45, 3, 4), rect(90, 52, 3, 4)), rgb(30, 30, 34)),
                capa(cabeza, bronce),
                capa(dentro(rect(0, 28, 100, 20), cabeza), oscuro),
                capa(con(elipse(70, 24, 5, 5), elipse(63, 23, 4, 4)), rgb(30, 20, 18)),
                capa(elipse(71, 25, 2.4, 2.4), rgb(240, 80, 40))));
    }

    /**
     * Darth Maul: la cara roja con los tatuajes negros del sith, los ojos amarillos, la corona de cuernos, la
     * ropa negra y el sable rojo de doble hoja cruzado adelante.
     */
    static Personaje darthMaul() {
        Color rojo = rgb(196, 34, 30);
        Color negro = rgb(18, 16, 18);
        Color cuerno = rgb(232, 216, 176);
        Shape ropa = desde(30, 62).a(70, 62).curva(86, 66, 90, 80).a(94, 98).a(6, 98).a(10, 80)
                .curva(14, 66, 30, 62).cerrar();
        Shape cabeza = elipse(34, 16, 32, 42);
        Shape cuernos = con(con(cuernoEn(50, 16), ambos(cuernoEn(43.5, 17.5))),
                con(ambos(cuernoEn(38, 21)), ambos(cuernoEn(35, 27))));
        List<Personaje.Capa> capas = new ArrayList<>(List.of(
                capa(ropa, rgb(26, 24, 30)),
                capa(poligono(38, 62, 62, 62, 50, 82), rgb(46, 44, 52)),
                capa(poligono(42, 62, 58, 62, 50, 74), negro),
                capa(rect(44, 52, 12, 12), rojo),
                capa(cuernos, cuerno),
                capa(cabeza, rojo),
                capa(dentro(rect(54, 0, 30, 70), cabeza), rgb(160, 26, 24)),
                capa(con(poligono(47, 17, 53, 17, 51.5, 34, 48.5, 34), ambos(arco(37, 24, 40, 31, 45, 34, 3))), negro),
                capa(ambos(poligono(35, 33, 48, 35, 48, 43, 38, 42)), negro),
                capa(poligono(47, 34, 53, 34, 54, 47, 46, 47), negro),
                capa(ambos(arco(36, 43, 37, 50, 42, 55, 2.4)), negro),
                capa(desde(42, 48).curva(50, 45, 58, 48).curva(58, 57, 50, 58.5).curva(42, 57, 42, 48).cerrar(), negro),
                capa(con(linea(47, 50, 47, 56, 1.2), linea(53, 50, 53, 56, 1.2)), rojo),
                capa(ambos(elipse(39.5, 36.5, 6, 3.4)), rgb(250, 200, 40)),
                capa(ambos(elipse(41.5, 37, 2, 2.4)), rgb(200, 40, 20))));
        capas.addAll(sable(2, 92, 98, 60, ROJO_SABLE));
        capas.addAll(List.of(
                capa(linea(38, 80, 62, 72, 4.4), rgb(70, 72, 80)),
                capa(con(linea(44, 78, 44.5, 79.5, 4.6), linea(56, 74, 56.5, 75.5, 4.6)), rgb(150, 154, 164)),
                capa(con(elipse(36, 75, 8, 8), elipse(58, 68, 8, 8)), rgb(40, 38, 44))));
        return new Personaje("Darth Maul", capas);
    }

    /** Uno de los cuernos cortos de la cabeza de Maul, con la punta hacia arriba. */
    private static Shape cuernoEn(double x, double y) {
        return poligono(x - 2.4, y + 3, x, y - 5, x + 2.4, y + 3);
    }

    /**
     * El droide de combate B1 de perfil, que es como se reconoce: la cabeza larga y achatada que apunta hacia
     * adelante, el cuello finito que sale de atrás, el cuerpo flaco con la mochila y el rifle E-5.
     */
    static Personaje droideDeCombate() {
        Color arena = rgb(222, 200, 150);
        Color sombra = rgb(172, 148, 102);
        Color junta = rgb(120, 104, 74);
        Shape cabeza = desde(24, 14).curva(38, 8, 54, 14).a(86, 26).curva(90, 30, 84, 32).a(44, 34)
                .curva(20, 36, 18, 24).curva(18, 16, 24, 14).cerrar();
        Shape pecho = desde(30, 50).a(52, 48).curva(56, 58, 50, 70).a(34, 70).curva(28, 60, 30, 50).cerrar();
        return new Personaje("Droide de combate", List.of(
                capa(redondeado(16, 48, 15, 22, 3), sombra),
                capa(linea(20, 48, 20, 30, 1.6), junta),
                capa(con(linea(38, 70, 34, 84, 3.4), linea(34, 84, 36, 97, 3.4)), sombra),
                capa(redondeado(30, 95, 12, 4, 1.5), sombra),
                capa(linea(26, 34, 32, 50, 3), junta),
                capa(pecho, arena),
                capa(dentro(rect(0, 60, 100, 20), pecho), sombra),
                capa(poligono(34, 68, 50, 68, 48, 74, 36, 74), sombra),
                capa(con(linea(44, 72, 52, 84, 3.4), linea(52, 84, 50, 97, 3.4)), arena),
                capa(redondeado(46, 95, 13, 4, 1.5), arena),
                capa(con(redondeado(40, 56, 50, 4, 1.5), redondeado(36, 58, 12, 7, 2)), rgb(60, 60, 66)),
                capa(con(rect(56, 54, 10, 3), rect(88, 55, 6, 2)), rgb(40, 40, 46)),
                capa(con(linea(40, 52, 46, 64, 3), linea(46, 64, 58, 60, 3)), arena),
                capa(elipse(56, 57, 6, 6), sombra),
                capa(cabeza, arena),
                capa(dentro(rect(0, 26, 100, 20), cabeza), sombra),
                capa(arco(26, 16, 50, 12, 80, 26, 1.4), rgb(240, 226, 190)),
                capa(elipse(38, 19, 9, 9), rgb(40, 34, 26)),
                capa(elipse(40, 21, 3, 3), rgb(110, 96, 70))));
    }

    /** Un sable de luz: el resplandor de color, el borde claro y el centro blanco. */
    private static List<Personaje.Capa> sable(double x1, double y1, double x2, double y2, HojaDeSable hoja) {
        return List.of(
                capa(linea(x1, y1, x2, y2, 6), hoja.resplandor()),
                capa(linea(x1, y1, x2, y2, 3.4), hoja.borde()),
                capa(linea(x1, y1, x2, y2, 1.4), Color.WHITE));
    }

    /** Los dos colores de la hoja de un sable; el centro siempre es blanco. */
    private record HojaDeSable(Color resplandor, Color borde) {
    }
}
