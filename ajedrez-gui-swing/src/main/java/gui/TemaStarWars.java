package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import modelo.Pieza;

/**
 * Tema Star Wars: la Rebelión (blancas) contra el Imperio (negras), en el espacio. Cada pieza es un
 * personaje con el brillo de su bando, azul la Rebelión y rojo el Imperio, y una insignia con el símbolo
 * de la pieza. Una pieza sin personaje (una pieza nueva del núcleo) se dibuja con su símbolo, en los
 * colores del bando: así se ve sin tocar este tema.
 */
public final class TemaStarWars implements Tema {

    private static final Color AZUL_REBELDE = new Color(80, 170, 255);
    private static final Color ROJO_IMPERIAL = new Color(255, 50, 50);
    private static final Paleta PALETA = new Paleta(
            new Color(170, 178, 192), new Color(58, 63, 76),
            new Color(205, 215, 235),
            new Color(255, 232, 31),   // el amarillo del título de las películas
            new Color(75, 213, 238),   // el celeste de "Hace mucho tiempo..."
            new Color(90, 140, 200));
    private static final Color ESPACIO_ARRIBA = new Color(12, 14, 34);
    private static final Color ESPACIO_ABAJO = new Color(2, 2, 8);
    private static final Color NEBULOSA_VIOLETA = new Color(110, 50, 160, 70);
    private static final Color NEBULOSA_AZUL = new Color(30, 90, 170, 60);
    /** Una estrella cada tantos píxeles: con la ventana más grande hay más estrellas, no más apretadas. */
    private static final int PIXELES_POR_ESTRELLA = 1300;
    private static final Color[] TONOS_DE_ESTRELLA = {
            Color.WHITE, Color.WHITE, Color.WHITE, new Color(170, 200, 255), new Color(255, 235, 190)};

    private final GlifosDePieza glifos;
    private final PersonajesDeStarWars personajes;

    public TemaStarWars(GlifosDePieza glifos, PersonajesDeStarWars personajes) {
        this.glifos = glifos;
        this.personajes = personajes;
    }

    @Override
    public String nombre() {
        return "Star Wars";
    }

    @Override
    public String epigrafe() {
        return "Hace mucho tiempo, en una galaxia muy, muy lejana…";
    }

    @Override
    public Paleta paleta() {
        return PALETA;
    }

    @Override
    public String bando(modelo.Color color) {
        return color == modelo.Color.BLANCO ? "Rebelión" : "Imperio";
    }

    /** "Darth Vader (dama)": el personaje y, entre paréntesis, la pieza, para saber cómo se mueve. */
    @Override
    public String nombreDe(String tipo, modelo.Color color) {
        return personajes.personajeDe(tipo, color).map(personaje -> personaje.nombre() + " (" + tipo + ")")
                .orElse(tipo);
    }

    @Override
    public DibujoDePieza dibujoDe(Pieza pieza) {
        String tipo = pieza.tipo().nombre();
        boolean rebelde = pieza.color() == modelo.Color.BLANCO;
        Color brillo = rebelde ? AZUL_REBELDE : ROJO_IMPERIAL;
        String glifo = glifos.glifoDe(pieza);
        return personajes.personajeDe(tipo, pieza.color())
                .<DibujoDePieza>map(personaje -> new DibujoDePersonaje(nombreDe(tipo, pieza.color()), personaje,
                        brillo, new DibujoDeGlifo(tipo, glifo, Color.WHITE, brillo.darker().darker())))
                .orElseGet(() -> new DibujoDeGlifo(tipo, glifo, rebelde ? Color.WHITE : Color.BLACK, brillo));
    }

    /** El espacio: un degradé casi negro, dos nebulosas, estrellas y un planeta en la esquina. */
    @Override
    public void pintarFondo(Graphics2D g, int ancho, int alto) {
        if (ancho <= 0 || alto <= 0) {
            return;
        }
        Graphics2D fondo = (Graphics2D) g.create();
        fondo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        fondo.setPaint(new GradientPaint(0, 0, ESPACIO_ARRIBA, 0, alto, ESPACIO_ABAJO));
        fondo.fillRect(0, 0, ancho, alto);
        double mayor = Math.max(ancho, alto);
        nebulosa(fondo, ancho * 0.1, alto * 0.3, mayor * 0.45, NEBULOSA_VIOLETA);
        nebulosa(fondo, ancho * 0.9, alto * 0.85, mayor * 0.4, NEBULOSA_AZUL);
        estrellas(fondo, ancho, alto);
        planeta(fondo, ancho, Math.min(ancho, alto) * 0.15);
        fondo.dispose();
    }

    private static void nebulosa(Graphics2D g, double x, double y, double radio, Color color) {
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x, y), (float) radio, new float[] {0f, 1f},
                new Color[] {color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)}));
        g.fill(new Ellipse2D.Double(x - radio, y - radio, 2 * radio, 2 * radio));
    }

    /** Casi todas chicas y tenues, unas pocas grandes y con destello. */
    private static void estrellas(Graphics2D g, int ancho, int alto) {
        int cantidad = ancho * alto / PIXELES_POR_ESTRELLA;
        for (int estrella = 0; estrella < cantidad; estrella++) {
            double x = azar(estrella, 1) * ancho;
            double y = azar(estrella, 2) * alto;
            double tamano = 0.8 + 2.4 * Math.pow(azar(estrella, 3), 6);
            Color tono = TONOS_DE_ESTRELLA[(int) (azar(estrella, 4) * TONOS_DE_ESTRELLA.length)];
            int opacidad = (int) (80 + 175 * azar(estrella, 5));
            g.setColor(new Color(tono.getRed(), tono.getGreen(), tono.getBlue(), opacidad));
            g.fill(new Ellipse2D.Double(x - tamano / 2, y - tamano / 2, tamano, tamano));
            if (tamano > 2.4) {
                g.setColor(new Color(tono.getRed(), tono.getGreen(), tono.getBlue(), opacidad / 3));
                g.fill(new Ellipse2D.Double(x - tamano * 2.5, y - 0.4, tamano * 5, 0.8));
                g.fill(new Ellipse2D.Double(x - 0.4, y - tamano * 2.5, 0.8, tamano * 5));
            }
        }
    }

    /** Un planeta anaranjado arriba a la derecha, iluminado desde la izquierda, con su atmósfera. */
    private static void planeta(Graphics2D g, int ancho, double radio) {
        double x = ancho - radio * 1.3;
        double y = radio * 1.6;
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x, y), (float) (radio * 1.25), new float[] {0.75f, 1f},
                new Color[] {new Color(255, 170, 100, 70), new Color(255, 170, 100, 0)}));
        g.fill(new Ellipse2D.Double(x - radio * 1.25, y - radio * 1.25, radio * 2.5, radio * 2.5));
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x - radio * 0.45, y - radio * 0.35), (float) (radio * 1.6),
                new float[] {0f, 0.45f, 1f},
                new Color[] {new Color(250, 200, 140), new Color(200, 110, 60), new Color(40, 16, 12)}));
        g.fill(new Ellipse2D.Double(x - radio, y - radio, radio * 2, radio * 2));
    }

    /**
     * Un número entre 0 y 1 que parece al azar pero sale siempre igual para la misma estrella y el mismo
     * dato: así las estrellas no cambian de lugar cada vez que se repinta la ventana.
     */
    private static double azar(int estrella, int dato) {
        long mezcla = estrella * 0x9E3779B97F4A7C15L + dato * 0xC2B2AE3D27D4EB4FL;
        mezcla = (mezcla ^ (mezcla >>> 33)) * 0xFF51AFD7ED558CCDL;
        mezcla = (mezcla ^ (mezcla >>> 33)) * 0xC4CEB9FE1A85EC53L;
        mezcla ^= mezcla >>> 33;
        return (mezcla >>> 11) / (double) (1L << 53);
    }
}
