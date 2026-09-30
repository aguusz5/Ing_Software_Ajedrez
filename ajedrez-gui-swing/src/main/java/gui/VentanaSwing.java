package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import juego.JuegoDeAjedrez;
import juego.ResultadoJugada;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

/**
 * Adaptador gráfico: una grilla de ancho x alto botones. El primer clic elige la pieza y el
 * segundo, el destino; la jugada se le pide al núcleo por el puerto JuegoDeAjedrez.
 * No contiene reglas del ajedrez: si la jugada no vale, muestra el motivo que da el núcleo.
 */
public final class VentanaSwing {

    private static final Color CASILLA_CLARA = new Color(240, 217, 181);
    private static final Color CASILLA_OSCURA = new Color(181, 136, 99);
    private static final Color CASILLA_ELEGIDA = new Color(246, 246, 105);
    private static final int LADO_CASILLA = 72;
    private static final int LADO_ICONO = 60;

    private final JuegoDeAjedrez juego;
    private final GlifosDePieza glifos;
    private final JFrame ventana = new JFrame("Ajedrez");
    private final JLabel estado = new JLabel(" ", SwingConstants.CENTER);
    private final Map<Posicion, JButton> casillas = new HashMap<>();
    private Posicion origen; // null mientras no se eligió ninguna pieza
    private String mensaje = "";

    public VentanaSwing(JuegoDeAjedrez juego, GlifosDePieza glifos) {
        this.juego = juego;
        this.glifos = glifos;
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.add(crearGrilla(), BorderLayout.CENTER);
        ventana.add(estado, BorderLayout.SOUTH);
        repintar();
        ventana.pack();
        ventana.setLocationRelativeTo(null);
    }

    public void mostrar() {
        ventana.setVisible(true);
    }

    private JPanel crearGrilla() {
        Tablero tablero = juego.tablero();
        JPanel grilla = new JPanel(new GridLayout(tablero.alto(), tablero.ancho()));
        for (int fila = tablero.alto() - 1; fila >= 0; fila--) { // la última fila se dibuja arriba
            for (int columna = 0; columna < tablero.ancho(); columna++) {
                Posicion posicion = new Posicion(columna, fila);
                JButton boton = new JButton();
                boton.setPreferredSize(new Dimension(LADO_CASILLA, LADO_CASILLA));
                boton.setMargin(new Insets(0, 0, 0, 0));
                boton.setFocusPainted(false);
                boton.setBorderPainted(false);
                boton.setOpaque(true);
                boton.addActionListener(evento -> alHacerClic(posicion));
                casillas.put(posicion, boton);
                grilla.add(boton);
            }
        }
        return grilla;
    }

    private void alHacerClic(Posicion posicion) {
        if (origen == null) {
            if (juego.tablero().piezaEn(posicion).isPresent()) {
                origen = posicion;
            }
        } else if (origen.equals(posicion)) {
            origen = null; // segundo clic sobre la misma casilla: se cancela la selección
        } else {
            mensaje = describir(juego.jugar(origen, posicion));
            origen = null;
        }
        repintar();
    }

    private String describir(ResultadoJugada resultado) {
        if (!resultado.valida()) {
            return "Jugada inválida: " + resultado.motivo();
        }
        return resultado.colorEnJaque().map(color -> "¡Jaque al rey " + color + "!").orElse("");
    }

    private void repintar() {
        Tablero tablero = juego.tablero();
        casillas.forEach((posicion, boton) -> {
            boton.setIcon(tablero.piezaEn(posicion).map(this::iconoDe).orElse(null));
            boton.setBackground(colorDeFondo(posicion));
        });
        estado.setText("Turno de " + juego.turnoActual() + (mensaje.isEmpty() ? "" : "  —  " + mensaje));
    }

    private Icon iconoDe(Pieza pieza) {
        boolean esBlanca = pieza.color() == modelo.Color.BLANCO;
        return new IconoDePieza(glifos.glifoDe(pieza),
                esBlanca ? Color.WHITE : Color.BLACK,
                esBlanca ? Color.BLACK : Color.WHITE);
    }

    private Color colorDeFondo(Posicion posicion) {
        if (posicion.equals(origen)) {
            return CASILLA_ELEGIDA;
        }
        return (posicion.columna() + posicion.fila()) % 2 == 0 ? CASILLA_OSCURA : CASILLA_CLARA;
    }

    /**
     * Dibuja una pieza en dos pasadas: la silueta rellena del color de la pieza y, encima, el trazo
     * del glifo en el color contrario. Así blancas y negras se distinguen en cualquier casilla,
     * con cualquier fuente, y también cuando el glifo es solo una inicial.
     */
    private record IconoDePieza(String glifo, Color relleno, Color trazo) implements Icon {

        private static final Font FUENTE = new Font(Font.DIALOG, Font.PLAIN, 50);
        private static final BasicStroke GROSOR_DEL_TRAZO = new BasicStroke(0.8f);

        @Override
        public void paintIcon(Component componente, Graphics graficos, int x, int y) {
            Graphics2D g = (Graphics2D) graficos.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GlyphVector vector = FUENTE.createGlyphVector(g.getFontRenderContext(), glifo);
            Rectangle2D limites = vector.getVisualBounds();
            Shape forma = vector.getOutline(
                    (float) (x + (LADO_ICONO - limites.getWidth()) / 2 - limites.getX()),
                    (float) (y + (LADO_ICONO - limites.getHeight()) / 2 - limites.getY()));
            g.setColor(relleno);
            g.fill(silueta(forma));
            g.setColor(trazo);
            g.setStroke(GROSOR_DEL_TRAZO);
            g.draw(forma);
            g.dispose();
        }

        @Override
        public int getIconWidth() {
            return LADO_ICONO;
        }

        @Override
        public int getIconHeight() {
            return LADO_ICONO;
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
}
