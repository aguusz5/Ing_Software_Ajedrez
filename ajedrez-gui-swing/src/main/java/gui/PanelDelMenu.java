package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

/**
 * La pantalla de inicio: jugar uno contra uno o contra la computadora (eligiendo dificultad y color),
 * volver a la partida en curso, elegir el tema, ver cómo se juega o salir. No juega: avisa qué se eligió
 * y la ventana arma la partida o cambia el tema.
 */
final class PanelDelMenu extends JPanel {

    private static final int ANCHO = 380;
    private static final int ALTO_DE_BOTON = 42;

    /** Los textos comunes: al cambiar de tema toman su color de texto. */
    private final List<JComponent> textos = new ArrayList<>();
    private final JLabel epigrafe = texto(" ", Font.ITALIC, 14f);
    private final JLabel titulo = texto("Ajedrez", Font.BOLD, 40f);
    private final JButton volver = boton("Volver a la partida");
    private final List<JRadioButton> opcionesDeDificultad = new ArrayList<>();
    private final JRadioButton conBlancas = comun(new JRadioButton("Blancas", true));
    private final JRadioButton conNegras = comun(new JRadioButton("Negras"));
    private final TitledBorder bordeDeLaComputadora = BorderFactory.createTitledBorder("Contra la computadora");
    private Tema tema; // cambia cuando se elige otro

    /** El primer tema de la lista es el que se ve al abrir. */
    PanelDelMenu(List<Dificultad> dificultades, List<Tema> temas, Runnable alElegirUnoContraUno,
                 BiConsumer<Dificultad, modelo.Color> alElegirContraLaComputadora, Consumer<Tema> alElegirTema,
                 Runnable alVolverALaPartida, Runnable alSalir) {
        super(new GridBagLayout()); // centra la columna en la ventana, sea del tamaño que sea
        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setOpaque(false);

        columna.add(epigrafe);
        columna.add(Box.createVerticalStrut(6));
        columna.add(titulo);
        columna.add(comun(texto("TPO Ingeniería de Software · UADE", Font.PLAIN, 13f)));
        columna.add(Box.createVerticalStrut(24));

        // Solo aparece cuando ya hay una partida empezada.
        volver.setVisible(false);
        volver.addActionListener(evento -> alVolverALaPartida.run());
        columna.add(volver);
        columna.add(Box.createVerticalStrut(12));

        JButton unoContraUno = boton("Uno contra uno");
        unoContraUno.addActionListener(evento -> alElegirUnoContraUno.run());
        columna.add(unoContraUno);
        columna.add(comun(texto("Dos personas en la misma compu", Font.PLAIN, 12f)));
        columna.add(Box.createVerticalStrut(16));

        columna.add(seccionContraLaComputadora(dificultades, alElegirContraLaComputadora));
        columna.add(Box.createVerticalStrut(12));
        columna.add(filaDeTemas(temas, alElegirTema));
        columna.add(Box.createVerticalStrut(12));

        JButton comoSeJuega = boton("Cómo se juega");
        comoSeJuega.addActionListener(evento -> mostrarAyuda());
        columna.add(comoSeJuega);
        columna.add(Box.createVerticalStrut(8));
        JButton salir = boton("Salir");
        salir.addActionListener(evento -> alSalir.run());
        columna.add(salir);

        add(columna);
        usarTema(temas.get(0));
    }

    /** Los colores, el epígrafe y los nombres de los bandos salen del tema. */
    void usarTema(Tema nuevo) {
        tema = nuevo;
        Paleta paleta = tema.paleta();
        textos.forEach(texto -> texto.setForeground(paleta.texto()));
        titulo.setForeground(paleta.titulo());
        epigrafe.setForeground(paleta.epigrafe());
        epigrafe.setText(tema.epigrafe());
        epigrafe.setVisible(!tema.epigrafe().isEmpty());
        conBlancas.setText(tema.bando(modelo.Color.BLANCO));
        conNegras.setText(tema.bando(modelo.Color.NEGRO));
        bordeDeLaComputadora.setTitleColor(paleta.texto());
        bordeDeLaComputadora.setBorder(BorderFactory.createLineBorder(paleta.marco()));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graficos) {
        tema.pintarFondo((Graphics2D) graficos, getWidth(), getHeight());
    }

    /** Desde que hay una partida empezada, el menú ofrece volver a ella. */
    void permitirVolverALaPartida() {
        volver.setVisible(true);
        revalidate();
    }

    private JPanel seccionContraLaComputadora(List<Dificultad> dificultades,
                                              BiConsumer<Dificultad, modelo.Color> alElegir) {
        JPanel seccion = new JPanel();
        seccion.setLayout(new BoxLayout(seccion, BoxLayout.Y_AXIS));
        seccion.setOpaque(false);
        seccion.setBorder(BorderFactory.createCompoundBorder(
                bordeDeLaComputadora, BorderFactory.createEmptyBorder(4, 10, 10, 10)));
        seccion.setAlignmentX(Component.CENTER_ALIGNMENT);

        seccion.add(aLaIzquierda(comun(new JLabel("Dificultad:"))));
        ButtonGroup grupoDeDificultad = new ButtonGroup();
        for (Dificultad dificultad : dificultades) {
            JRadioButton opcion = comun(new JRadioButton(
                    "<html><b>" + dificultad.nombre() + "</b>: " + dificultad.descripcion() + "</html>"));
            opcion.setOpaque(false);
            grupoDeDificultad.add(opcion);
            opcionesDeDificultad.add(opcion);
            seccion.add(aLaIzquierda(opcion));
        }
        // Arranca elegida la del medio: ni la más fácil ni la más difícil.
        if (!opcionesDeDificultad.isEmpty()) {
            opcionesDeDificultad.get(opcionesDeDificultad.size() / 2).setSelected(true);
        }

        seccion.add(Box.createVerticalStrut(8));
        JPanel color = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        color.setOpaque(false);
        color.add(comun(new JLabel("Jugás con:  ")));
        ButtonGroup grupoDeColor = new ButtonGroup();
        for (JRadioButton opcion : List.of(conBlancas, conNegras)) {
            opcion.setOpaque(false);
            grupoDeColor.add(opcion);
            color.add(opcion);
        }
        seccion.add(aLaIzquierda(color));
        seccion.add(Box.createVerticalStrut(10));

        JButton jugar = boton("Jugar contra la computadora");
        jugar.setAlignmentX(Component.LEFT_ALIGNMENT);
        jugar.setEnabled(!dificultades.isEmpty());
        jugar.addActionListener(evento -> {
            int elegida = opcionesDeDificultad.indexOf(
                    opcionesDeDificultad.stream().filter(JRadioButton::isSelected).findFirst().orElseThrow());
            alElegir.accept(dificultades.get(elegida), conNegras.isSelected() ? modelo.Color.NEGRO : modelo.Color.BLANCO);
        });
        seccion.add(jugar);
        seccion.setMaximumSize(new Dimension(ANCHO, seccion.getPreferredSize().height));
        return seccion;
    }

    /** Un botón por tema; el primero arranca elegido. */
    private JPanel filaDeTemas(List<Tema> temas, Consumer<Tema> alElegirTema) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        fila.setOpaque(false);
        fila.add(comun(new JLabel("Tema:")));
        ButtonGroup grupo = new ButtonGroup();
        for (Tema opcion : temas) {
            JRadioButton boton = comun(new JRadioButton(opcion.nombre(), opcion == temas.get(0)));
            boton.setOpaque(false);
            boton.setFocusPainted(false);
            boton.addActionListener(evento -> alElegirTema.accept(opcion));
            grupo.add(boton);
            fila.add(boton);
        }
        fila.setAlignmentX(Component.CENTER_ALIGNMENT);
        fila.setMaximumSize(new Dimension(ANCHO, fila.getPreferredSize().height));
        return fila;
    }

    private void mostrarAyuda() {
        JOptionPane.showMessageDialog(this, """
                <html><body style='width: 380px'>
                <p><b>Mover:</b> hacé clic en una pieza y después en la casilla de destino.
                Otro clic sobre la misma pieza la suelta.</p><br>
                <p><b>Colores:</b> amarillo, la pieza elegida; azul, adonde puede ir; naranja, adonde llegaría
                pero dejaría a su rey en jaque; rojo, el rey en jaque.</p><br>
                <p><b>Enroque:</b> mové el rey dos casillas hacia la torre. <b>Coronación:</b> cuando un peón
                llega al final, elegís en qué pieza se convierte.</p><br>
                <p><b>Deshacer y Rehacer:</b> contra la computadora vuelven hasta tu turno.</p><br>
                <p><b>Terminar partida:</b> gana el que tiene más puntos (peón 1, caballo y alfil 3, torre 5,
                dama 9). <b>Rendirse</b> aparece cuando estás en jaque.</p><br>
                <p><b>Reiniciar</b> empieza de nuevo con el mismo modo. <b>Menú</b> vuelve acá sin perder
                la partida.</p><br>
                <p><b>Tema:</b> cambia solo cómo se ve. En Star Wars la Rebelión (blancas, brillo azul) juega
                contra el Imperio (negras, brillo rojo). La insignia de la esquina dice qué pieza es cada
                personaje, y si dejás el mouse quieto sobre una pieza aparece su nombre.</p>
                </body></html>""", "Cómo se juega", JOptionPane.INFORMATION_MESSAGE);
    }

    private static JButton boton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(boton.getFont().deriveFont(Font.BOLD, 15f));
        boton.setFocusPainted(false);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setPreferredSize(new Dimension(ANCHO, ALTO_DE_BOTON));
        boton.setMaximumSize(new Dimension(ANCHO, ALTO_DE_BOTON));
        return boton;
    }

    private static JLabel texto(String texto, int estilo, float tamano) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(etiqueta.getFont().deriveFont(estilo, tamano));
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        return etiqueta;
    }

    /** Registra un texto común, para pintarlo con el color de texto del tema. */
    private <T extends JComponent> T comun(T componente) {
        textos.add(componente);
        return componente;
    }

    private static JComponent aLaIzquierda(JComponent componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        return componente;
    }
}
