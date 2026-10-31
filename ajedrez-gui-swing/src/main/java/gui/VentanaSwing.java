package gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import juego.JuegoDeAjedrez;

/**
 * Adaptador gráfico: una ventana con dos pantallas, el menú de inicio y la partida. Arranca en el menú;
 * desde ahí se elige uno contra uno o contra la computadora, y desde la partida se puede volver al menú
 * sin perderla. La ventana solo cambia de pantalla: jugar lo hace PanelDeLaPartida, por el puerto
 * JuegoDeAjedrez. Las pantallas son partes de la vista, así que las crea la ventana; lo que tiene
 * comportamiento propio (el juego, los temas, las dificultades) llega por constructor desde Principal.
 * Cuando en el menú se elige otro tema, la ventana se lo pasa a las dos pantallas.
 */
public final class VentanaSwing {

    private static final String TITULO = "Ajedrez";
    private static final String MENU = "menú";
    private static final String PARTIDA = "partida";
    private static final Dimension TAMANO_MINIMO = new Dimension(600, 560); // que el menú entre entero

    private final JFrame ventana = new JFrame(TITULO);
    private final CardLayout pantallas = new CardLayout();
    private final JPanel contenido = new JPanel(pantallas);
    private final PanelDelMenu menu;
    private final PanelDeLaPartida partida;
    private String tituloDeLaPartida = TITULO;

    /**
     * El primer tema de la lista es el que se ve al abrir. Si no hay ninguna dificultad, el menú no
     * ofrece jugar contra la computadora.
     */
    public VentanaSwing(JuegoDeAjedrez juego, List<Tema> temas, List<Dificultad> dificultades) {
        partida = new PanelDeLaPartida(juego, temas.get(0), this::mostrarMenu);
        menu = new PanelDelMenu(dificultades, temas, this::unoContraUno, this::contraLaComputadora,
                this::usarTema, this::volverALaPartida, this::salir);
        contenido.add(menu, MENU);
        contenido.add(partida, PARTIDA);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setContentPane(contenido);
        ventana.pack();
        ventana.setMinimumSize(TAMANO_MINIMO);
        ventana.setLocationRelativeTo(null);
        pantallas.show(contenido, MENU);
    }

    public void mostrar() {
        ventana.setVisible(true);
    }

    private void unoContraUno() {
        partida.empezarUnoContraUno();
        mostrarPartida(TITULO + " — uno contra uno");
    }

    private void contraLaComputadora(Dificultad dificultad, modelo.Color colorDelJugador) {
        partida.empezarContraLaComputadora(dificultad.computadora(), colorDelJugador);
        mostrarPartida(TITULO + " — contra la computadora (" + dificultad.nombre() + ")");
    }

    private void usarTema(Tema tema) {
        menu.usarTema(tema);
        partida.usarTema(tema);
    }

    private void volverALaPartida() {
        mostrarPartida(tituloDeLaPartida);
    }

    private void mostrarPartida(String titulo) {
        tituloDeLaPartida = titulo;
        ventana.setTitle(titulo);
        pantallas.show(contenido, PARTIDA);
    }

    private void mostrarMenu() {
        menu.permitirVolverALaPartida();
        ventana.setTitle(TITULO);
        pantallas.show(contenido, MENU);
    }

    /** Como cerrar la ventana con la X. */
    private void salir() {
        ventana.dispatchEvent(new WindowEvent(ventana, WindowEvent.WINDOW_CLOSING));
    }
}
