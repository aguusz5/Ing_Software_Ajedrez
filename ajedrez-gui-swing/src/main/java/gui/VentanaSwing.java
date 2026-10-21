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
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import juego.EstadoPartida;
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
    private static final Color CASILLA_LEGAL = new Color(110, 160, 230);
    private static final Color CASILLA_ILEGAL = new Color(240, 150, 60);
    private static final Color CASILLA_JAQUE = new Color(220, 70, 60);
    /** Cuánto tapa el resaltado al color de la casilla: 0 = nada, 1 = color pleno. Ajustable a gusto. */
    private static final double OPACIDAD_RESALTADO = 0.45;
    private static final int GROSOR_BORDE = 2;
    private static final Border SIN_BORDE =
            BorderFactory.createEmptyBorder(GROSOR_BORDE, GROSOR_BORDE, GROSOR_BORDE, GROSOR_BORDE);
    private static final int LADO_CASILLA = 72;
    private static final int LADO_ICONO = 60;

    private final JuegoDeAjedrez juego;
    private final GlifosDePieza glifos;
    private final JFrame ventana = new JFrame("Ajedrez");
    private final JLabel estado = new JLabel(" ", SwingConstants.CENTER);
    private final JButton terminar = new JButton("Terminar partida");
    private final JButton rendirse = new JButton("Rendirse");
    private final JButton deshacer = new JButton("Deshacer");
    private final JButton rehacer = new JButton("Rehacer");
    private final Map<Posicion, JButton> casillas = new HashMap<>();
    private Posicion origen; // null mientras no se eligió ninguna pieza
    private String mensaje = "";
    private boolean finAnunciado = false;

    public VentanaSwing(JuegoDeAjedrez juego, GlifosDePieza glifos) {
        this.juego = juego;
        this.glifos = glifos;
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.add(crearGrilla(), BorderLayout.CENTER);
        ventana.add(crearBarraInferior(), BorderLayout.SOUTH);
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
                boton.setBorder(SIN_BORDE);
                boton.setOpaque(true);
                boton.addActionListener(evento -> alHacerClic(posicion));
                casillas.put(posicion, boton);
                grilla.add(boton);
            }
        }
        return grilla;
    }

    private JPanel crearBarraInferior() {
        terminar.addActionListener(evento -> {
            juego.terminar();
            repintar();
        });
        rendirse.addActionListener(evento -> {
            juego.rendirse();
            repintar();
        });
        // Deshacer también vale con la partida terminada: deshacer un mate o una rendición la reabre.
        deshacer.addActionListener(evento -> volverA(juego.deshacer(), "No hay nada para deshacer"));
        rehacer.addActionListener(evento -> volverA(juego.rehacer(), "No hay nada para rehacer"));
        JPanel botones = new JPanel();
        botones.add(deshacer);
        botones.add(rehacer);
        botones.add(rendirse);
        botones.add(terminar);
        JPanel barra = new JPanel(new BorderLayout());
        barra.add(estado, BorderLayout.CENTER);
        barra.add(botones, BorderLayout.EAST);
        return barra;
    }

    private void alHacerClic(Posicion posicion) {
        if (juego.estado().terminada()) {
            return; // partida cerrada: el tablero queda solo para mirar
        }
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

    /** Después de deshacer o rehacer: se suelta la pieza elegida y, si no cambió nada, se avisa. */
    private void volverA(boolean cambio, String sinCambios) {
        origen = null;
        mensaje = cambio ? "" : sinCambios;
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
        EstadoPartida fin = juego.estado();
        boolean terminada = fin.terminada();
        if (terminada) {
            origen = null;
        }
        // Lo que se pinta lo decide el núcleo: la ventana solo le pregunta por el puerto.
        Set<Posicion> legales = origen == null ? Set.of() : Set.copyOf(juego.destinosLegales(origen));
        Set<Posicion> ilegales = origen == null ? Set.of() : Set.copyOf(juego.destinosIlegales(origen));
        boolean enJaque = juego.estaEnJaque(juego.turnoActual());
        Posicion reyEnJaque = enJaque ? tablero.buscarRey(juego.turnoActual()) : null;
        casillas.forEach((posicion, boton) -> {
            boton.setIcon(tablero.piezaEn(posicion).map(this::iconoDe).orElse(null));
            // El resaltado se mezcla con el color de la casilla (así se sigue viendo el damero) y la
            // casilla se enmarca con un borde del mismo tono, para distinguir casillas vecinas.
            Color base = (posicion.columna() + posicion.fila()) % 2 == 0 ? CASILLA_OSCURA : CASILLA_CLARA;
            Color tinte = resaltado(posicion, reyEnJaque, legales, ilegales);
            boton.setBackground(tinte == null ? base : mezclar(base, tinte, OPACIDAD_RESALTADO));
            boton.setBorder(tinte == null ? SIN_BORDE : BorderFactory.createLineBorder(tinte.darker(), GROSOR_BORDE));
        });
        String puntos = "Blancas " + juego.puntos(modelo.Color.BLANCO)
                + " · Negras " + juego.puntos(modelo.Color.NEGRO) + "  —  ";
        terminar.setEnabled(!terminada);
        rendirse.setVisible(!terminada && enJaque);
        if (terminada) {
            estado.setText(puntos + "FIN DE LA PARTIDA  —  " + resultado(fin));
            anunciarFin(fin);
        } else {
            finAnunciado = false; // si se deshizo el final, el próximo final se vuelve a anunciar
            estado.setText(puntos + "Turno de " + juego.turnoActual() + (mensaje.isEmpty() ? "" : "  —  " + mensaje));
        }
    }

    private static String resultado(EstadoPartida fin) {
        return fin.ganador().map(color -> "Ganador: " + color).orElse("Tablas");
    }

    /** Un único diálogo, después de que el tablero ya muestre la jugada final. */
    private void anunciarFin(EstadoPartida fin) {
        if (finAnunciado) {
            return;
        }
        finAnunciado = true;
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ventana,
                resultado(fin) + "\n" + fin.motivo(), "Fin de la partida", JOptionPane.INFORMATION_MESSAGE));
    }

    private Icon iconoDe(Pieza pieza) {
        boolean esBlanca = pieza.color() == modelo.Color.BLANCO;
        return new IconoDePieza(glifos.glifoDe(pieza),
                esBlanca ? Color.WHITE : Color.BLACK,
                esBlanca ? Color.BLACK : Color.WHITE);
    }

    /** Prioridad: elegida > rey en jaque > destino legal > destino ilegal. null = sin resaltar. */
    private Color resaltado(Posicion posicion, Posicion reyEnJaque, Set<Posicion> legales, Set<Posicion> ilegales) {
        if (posicion.equals(origen)) {
            return CASILLA_ELEGIDA;
        }
        if (posicion.equals(reyEnJaque)) {
            return CASILLA_JAQUE;
        }
        if (legales.contains(posicion)) {
            return CASILLA_LEGAL;
        }
        if (ilegales.contains(posicion)) {
            return CASILLA_ILEGAL;
        }
        return null;
    }

    /** El tinte encima de la base, como si fuera semitransparente (Swing no repinta bien fondos con alfa). */
    private static Color mezclar(Color base, Color tinte, double opacidad) {
        return new Color(
                (int) Math.round(base.getRed() * (1 - opacidad) + tinte.getRed() * opacidad),
                (int) Math.round(base.getGreen() * (1 - opacidad) + tinte.getGreen() * opacidad),
                (int) Math.round(base.getBlue() * (1 - opacidad) + tinte.getBlue() * opacidad));
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
