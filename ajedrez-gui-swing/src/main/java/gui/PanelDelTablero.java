package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

import modelo.Posicion;

/**
 * El tablero en pantalla: casillas que siempre quedan cuadradas, sea cual sea el tamaño de la ventana.
 * Ocupa todo el lugar que puede sin deformarse y queda centrado; lo que sobra es margen. Al costado y
 * abajo muestra las coordenadas, y se puede dar vuelta para que el que juega con negras las tenga abajo.
 * Es transparente: alrededor de las casillas se ve el fondo del tema. Al pasar el mouse por una pieza
 * dice cómo se llama. No sabe nada de reglas: la partida le dice qué mostrar en cada casilla.
 */
final class PanelDelTablero extends JPanel {

    /** Lado de casilla con el que arranca la ventana. */
    private static final int LADO_PREFERIDO = 64;
    /** Lugar para las coordenadas alrededor del tablero, medido en casillas. */
    private static final double MARGEN = 0.4;

    private final int columnas;
    private final int filas;
    private final Map<Posicion, Casilla> casillas = new HashMap<>();
    private boolean invertido = false;
    private Color colorDeCoordenadas = Color.DARK_GRAY;
    private Color colorDelMarco = Color.DARK_GRAY;
    // Dónde quedó el tablero en el último acomodo, para dibujar las coordenadas y el marco en el mismo lugar.
    private int lado;
    private int izquierda;
    private int arriba;

    PanelDelTablero(int columnas, int filas, Consumer<Posicion> alHacerClic) {
        super(null); // sin LayoutManager: las casillas se acomodan en doLayout
        setOpaque(false);
        this.columnas = columnas;
        this.filas = filas;
        for (int columna = 0; columna < columnas; columna++) {
            for (int fila = 0; fila < filas; fila++) {
                Posicion posicion = new Posicion(columna, fila);
                Casilla casilla = new Casilla();
                casilla.addActionListener(evento -> alHacerClic.accept(posicion));
                casillas.put(posicion, casilla);
                add(casilla);
            }
        }
        setPreferredSize(new Dimension(
                (int) Math.ceil((columnas + 2 * MARGEN) * LADO_PREFERIDO),
                (int) Math.ceil((filas + 2 * MARGEN) * LADO_PREFERIDO)));
    }

    /** true: las negras quedan abajo. false: las blancas quedan abajo. */
    void setInvertido(boolean invertido) {
        this.invertido = invertido;
        revalidate();
        repaint();
    }

    /** Los colores de las coordenadas y del marco, que dependen del fondo del tema. */
    void usarColores(Color coordenadas, Color marco) {
        colorDeCoordenadas = coordenadas;
        colorDelMarco = marco;
        repaint();
    }

    /** Qué se ve en una casilla: la pieza (o ninguna), el color de fondo y el borde. */
    void mostrar(Posicion posicion, Optional<DibujoDePieza> pieza, Color fondo, Border borde) {
        Casilla casilla = casillas.get(posicion);
        casilla.pieza = pieza;
        casilla.setToolTipText(pieza.map(DibujoDePieza::nombre).orElse(null));
        casilla.setBackground(fondo);
        casilla.setBorder(borde);
        casilla.repaint();
    }

    /** El lado de casilla más grande que entra a lo ancho y a lo alto: así las casillas nunca se deforman. */
    @Override
    public void doLayout() {
        lado = (int) Math.max(1, Math.min(
                getWidth() / (columnas + 2 * MARGEN),
                getHeight() / (filas + 2 * MARGEN)));
        izquierda = (getWidth() - columnas * lado) / 2;
        arriba = (getHeight() - filas * lado) / 2;
        casillas.forEach((posicion, casilla) -> casilla.setBounds(
                izquierda + columnaEnPantalla(posicion.columna()) * lado,
                arriba + filaEnPantalla(posicion.fila()) * lado,
                lado, lado));
    }

    private int columnaEnPantalla(int columna) {
        return invertido ? columnas - 1 - columna : columna;
    }

    /** Arriba de la pantalla va la última fila del tablero, salvo con el tablero dado vuelta. */
    private int filaEnPantalla(int fila) {
        return invertido ? fila : filas - 1 - fila;
    }

    /** El marco y las coordenadas: números a la izquierda y letras abajo, del tamaño de la casilla. */
    @Override
    protected void paintComponent(Graphics graficos) {
        super.paintComponent(graficos);
        Graphics2D g = (Graphics2D) graficos.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(colorDelMarco);
        g.drawRect(izquierda - 2, arriba - 2, columnas * lado + 3, filas * lado + 3);
        g.drawRect(izquierda - 1, arriba - 1, columnas * lado + 1, filas * lado + 1);
        g.setColor(colorDeCoordenadas);
        g.setFont(getFont().deriveFont(Font.BOLD, Math.max(10f, lado * 0.24f)));
        FontMetrics medidas = g.getFontMetrics();
        int margen = (int) (lado * MARGEN);
        int centroVertical = (medidas.getAscent() - medidas.getDescent()) / 2;
        for (int fila = 0; fila < filas; fila++) {
            String numero = String.valueOf(fila + 1);
            g.drawString(numero,
                    izquierda - (margen + medidas.stringWidth(numero)) / 2,
                    arriba + filaEnPantalla(fila) * lado + lado / 2 + centroVertical);
        }
        for (int columna = 0; columna < columnas; columna++) {
            String letra = String.valueOf((char) ('a' + columna));
            g.drawString(letra,
                    izquierda + columnaEnPantalla(columna) * lado + (lado - medidas.stringWidth(letra)) / 2,
                    arriba + filas * lado + margen / 2 + centroVertical);
        }
        g.dispose();
    }

    /** Una casilla: un botón que dibuja su pieza a la escala del botón. */
    private static final class Casilla extends JButton {

        private Optional<DibujoDePieza> pieza = Optional.empty();

        Casilla() {
            setMargin(new Insets(0, 0, 0, 0));
            setFocusPainted(false);
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            super.paintComponent(graficos);
            pieza.ifPresent(dibujo -> dibujo.dibujar(graficos, getWidth(), getHeight()));
        }
    }
}
