package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import ia.Computadora;
import ia.JugadaDeLaComputadora;
import juego.EstadoPartida;
import juego.JuegoDeAjedrez;
import juego.JugadaLegal;
import juego.ResultadoJugada;
import modelo.Posicion;
import modelo.Tablero;

/**
 * La pantalla de juego: los botones arriba, el tablero en el medio y el estado abajo. El primer clic
 * elige la pieza y el segundo, el destino; la jugada se le pide al núcleo por el puerto JuegoDeAjedrez.
 * No contiene reglas del ajedrez: si la jugada no vale, muestra el motivo que da el núcleo.
 * Contra la computadora, ella juega el color que no eligió el jugador y la barra avisa qué jugó.
 * Cómo se ve (fondo, casillas, piezas, nombres de los bandos) lo decide el tema elegido en el menú.
 */
final class PanelDeLaPartida extends JPanel {

    private static final Color CASILLA_ELEGIDA = new Color(246, 246, 105);
    private static final Color CASILLA_LEGAL = new Color(110, 160, 230);
    private static final Color CASILLA_ILEGAL = new Color(240, 150, 60);
    private static final Color CASILLA_JAQUE = new Color(220, 70, 60);
    /** Cuánto tapa el resaltado al color de la casilla: 0 = nada, 1 = color pleno. Ajustable a gusto. */
    private static final double OPACIDAD_RESALTADO = 0.45;
    private static final int GROSOR_BORDE = 2;
    private static final Border SIN_BORDE =
            BorderFactory.createEmptyBorder(GROSOR_BORDE, GROSOR_BORDE, GROSOR_BORDE, GROSOR_BORDE);

    private final JuegoDeAjedrez juego;
    private final Runnable alVolverAlMenu;
    private final PanelDelTablero tablero;
    private final JLabel estado = new JLabel(" ", SwingConstants.CENTER);
    private final JButton menu = new JButton("Menú");
    private final JButton reiniciar = new JButton("Reiniciar");
    private final JButton deshacer = new JButton("Deshacer");
    private final JButton rehacer = new JButton("Rehacer");
    private final JButton terminar = new JButton("Terminar partida");
    private final JButton rendirse = new JButton("Rendirse");
    private Tema tema; // cambia cuando se elige otro en el menú
    // Contra quién se juega. Vacío: uno contra uno. Cambia cada vez que se elige una partida en el menú.
    private Optional<Computadora> computadora = Optional.empty();
    private modelo.Color colorDeLaComputadora = modelo.Color.NEGRO;
    private Posicion origen; // null mientras no se eligió ninguna pieza
    private String mensaje = "";
    private boolean finAnunciado = false;
    private boolean pensando = false; // mientras la computadora elige, el tablero y los botones no responden

    PanelDeLaPartida(JuegoDeAjedrez juego, Tema tema, Runnable alVolverAlMenu) {
        super(new BorderLayout());
        this.juego = juego;
        this.alVolverAlMenu = alVolverAlMenu;
        Tablero inicial = juego.tablero();
        this.tablero = new PanelDelTablero(inicial.ancho(), inicial.alto(), this::alHacerClic);
        estado.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));
        add(crearBarraDeBotones(), BorderLayout.NORTH);
        add(tablero, BorderLayout.CENTER);
        add(estado, BorderLayout.SOUTH);
        usarTema(tema);
    }

    /** Cambia cómo se ve la partida; la partida en sí sigue igual. */
    void usarTema(Tema nuevo) {
        tema = nuevo;
        estado.setForeground(tema.paleta().texto());
        tablero.usarColores(tema.paleta().texto(), tema.paleta().marco());
        repintar();
        repaint();
    }

    /** El fondo lo pinta el tema; la barra, el tablero y el estado son transparentes y se ven encima. */
    @Override
    protected void paintComponent(Graphics graficos) {
        tema.pintarFondo((Graphics2D) graficos, getWidth(), getHeight());
    }

    /** Partida nueva entre dos personas en la misma compu, con las blancas abajo. */
    void empezarUnoContraUno() {
        computadora = Optional.empty();
        tablero.setInvertido(false);
        empezarDeNuevo();
    }

    /** Partida nueva contra la computadora: el jugador elige su color y lo tiene abajo; ella juega el otro. */
    void empezarContraLaComputadora(Computadora elegida, modelo.Color colorDelJugador) {
        computadora = Optional.of(elegida);
        colorDeLaComputadora = colorDelJugador.contrario();
        tablero.setInvertido(colorDelJugador == modelo.Color.NEGRO);
        empezarDeNuevo();
    }

    /** La misma modalidad desde la posición inicial. Si la computadora tiene blancas, mueve ella primero. */
    private void empezarDeNuevo() {
        juego.reiniciar();
        origen = null;
        mensaje = "";
        finAnunciado = false;
        repintar();
        pensarSiLeToca();
    }

    private JPanel crearBarraDeBotones() {
        menu.addActionListener(evento -> alVolverAlMenu.run());
        reiniciar.addActionListener(evento -> confirmarReinicio());
        // Deshacer también vale con la partida terminada: deshacer un mate o una rendición la reabre.
        deshacer.addActionListener(evento -> volverA(deshacerTurno(), "No hay nada para deshacer"));
        rehacer.addActionListener(evento -> volverA(rehacerTurno(), "No hay nada para rehacer"));
        terminar.addActionListener(evento -> {
            juego.terminar();
            repintar();
        });
        rendirse.addActionListener(evento -> {
            juego.rendirse();
            repintar();
        });
        // Alineada a la izquierda: cuando aparece Rendirse (al final), los demás botones no se mueven.
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        barra.setOpaque(false);
        for (JButton boton : List.of(menu, reiniciar, deshacer, rehacer, terminar, rendirse)) {
            boton.setFocusPainted(false);
            barra.add(boton);
        }
        return barra;
    }

    private void confirmarReinicio() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Empezar la partida de nuevo? Se pierde la partida actual.", "Reiniciar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            empezarDeNuevo();
        }
    }

    private void alHacerClic(Posicion posicion) {
        if (juego.estado().terminada() || pensando) {
            return; // partida cerrada o turno de la computadora: el tablero queda solo para mirar
        }
        if (origen == null) {
            if (juego.tablero().piezaEn(posicion).isPresent()) {
                origen = posicion;
            }
        } else if (origen.equals(posicion)) {
            origen = null; // segundo clic sobre la misma casilla: se cancela la selección
        } else {
            jugarHacia(posicion);
            origen = null;
        }
        repintar();
        pensarSiLeToca();
    }

    /** Si el núcleo dice que la jugada corona, se pregunta en qué; cerrar el diálogo cancela la jugada. */
    private void jugarHacia(Posicion destino) {
        List<String> opciones = juego.opcionesDeCoronacion(origen, destino);
        if (opciones.isEmpty()) {
            mensaje = describir(juego.jugar(origen, destino));
            return;
        }
        // Los botones dicen cómo se llama cada pieza en el tema: "dama" o "Princesa Leia (dama)".
        Object[] nombres = opciones.stream().map(tipo -> tema.nombreDe(tipo, juego.turnoActual())).toArray();
        int elegida = JOptionPane.showOptionDialog(this, "¿En qué pieza corona el peón?", "Promoción",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
        if (elegida != JOptionPane.CLOSED_OPTION) {
            mensaje = describir(juego.jugar(origen, destino, opciones.get(elegida)));
        }
    }

    /** Después de deshacer o rehacer: se suelta la pieza elegida y, si no cambió nada, se avisa. */
    private void volverA(boolean cambio, String sinCambios) {
        origen = null;
        mensaje = cambio ? "" : sinCambios;
        repintar();
        pensarSiLeToca();
    }

    /** Contra la computadora, deshacer vuelve hasta que le toque al jugador: su jugada y la de la computadora. */
    private boolean deshacerTurno() {
        boolean cambio = juego.deshacer();
        if (cambio && leTocaALaComputadora()) {
            juego.deshacer();
        }
        return cambio;
    }

    private boolean rehacerTurno() {
        boolean cambio = juego.rehacer();
        if (cambio && leTocaALaComputadora()) {
            juego.rehacer();
        }
        return cambio;
    }

    private boolean leTocaALaComputadora() {
        return computadora.isPresent() && !juego.estado().terminada()
                && juego.turnoActual() == colorDeLaComputadora;
    }

    /** La computadora elige en segundo plano (el minimax puede tardar un momento) y juega cuando termina. */
    private void pensarSiLeToca() {
        if (pensando || !leTocaALaComputadora()) {
            return;
        }
        pensando = true;
        mensaje = "Piensa la computadora…";
        repintar();
        Computadora quePiensa = computadora.get();
        new SwingWorker<Optional<JugadaDeLaComputadora>, Void>() {
            @Override
            protected Optional<JugadaDeLaComputadora> doInBackground() {
                return quePiensa.elegirJugada(juego);
            }

            @Override
            protected void done() {
                pensando = false;
                try {
                    get().ifPresent(PanelDeLaPartida.this::jugarLaDeLaComputadora);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    mensaje = "La computadora no pudo elegir una jugada: " + e.getCause().getMessage();
                }
                repintar();
            }
        }.execute();
    }

    /** La jugada la hace el núcleo por el puerto, como cualquier otra; la barra dice qué jugó. */
    private void jugarLaDeLaComputadora(JugadaDeLaComputadora elegida) {
        JugadaLegal jugada = elegida.jugada();
        ResultadoJugada resultado = jugada.promocion()
                .map(tipo -> juego.jugar(jugada.desde(), jugada.hasta(), tipo))
                .orElseGet(() -> juego.jugar(jugada.desde(), jugada.hasta()));
        String jaque = describir(resultado);
        mensaje = "La computadora jugó " + casilla(jugada.desde()) + " " + casilla(jugada.hasta())
                + (elegida.deRespaldo() ? " (eligió " + elegida.asesor() + ", de respaldo)" : "")
                + (jaque.isEmpty() ? "" : "  —  " + jaque);
    }

    private static String casilla(Posicion posicion) {
        return (char) ('a' + posicion.columna()) + String.valueOf(posicion.fila() + 1);
    }

    private String describir(ResultadoJugada resultado) {
        if (!resultado.valida()) {
            return "Jugada inválida: " + resultado.motivo();
        }
        return resultado.colorEnJaque().map(color -> "¡" + tema.bando(color) + " en jaque!").orElse("");
    }

    private void repintar() {
        Tablero actual = juego.tablero();
        Paleta paleta = tema.paleta();
        EstadoPartida fin = juego.estado();
        boolean terminada = fin.terminada();
        if (terminada) {
            origen = null;
        }
        // Lo que se pinta lo decide el núcleo: la pantalla solo le pregunta por el puerto.
        Set<Posicion> legales = origen == null ? Set.of() : Set.copyOf(juego.destinosLegales(origen));
        Set<Posicion> ilegales = origen == null ? Set.of() : Set.copyOf(juego.destinosIlegales(origen));
        boolean enJaque = juego.estaEnJaque(juego.turnoActual());
        Posicion reyEnJaque = enJaque ? actual.buscarRey(juego.turnoActual()) : null;
        for (int columna = 0; columna < actual.ancho(); columna++) {
            for (int fila = 0; fila < actual.alto(); fila++) {
                Posicion posicion = new Posicion(columna, fila);
                // El resaltado se mezcla con el color de la casilla (así se sigue viendo el damero) y la
                // casilla se enmarca con un borde del mismo tono, para distinguir casillas vecinas.
                Color base = (columna + fila) % 2 == 0 ? paleta.casillaOscura() : paleta.casillaClara();
                Color tinte = resaltado(posicion, reyEnJaque, legales, ilegales);
                tablero.mostrar(posicion,
                        actual.piezaEn(posicion).map(tema::dibujoDe),
                        tinte == null ? base : mezclar(base, tinte, OPACIDAD_RESALTADO),
                        tinte == null ? SIN_BORDE : BorderFactory.createLineBorder(tinte.darker(), GROSOR_BORDE));
            }
        }
        menu.setEnabled(!pensando);
        reiniciar.setEnabled(!pensando);
        deshacer.setEnabled(!pensando);
        rehacer.setEnabled(!pensando);
        terminar.setEnabled(!terminada && !pensando);
        rendirse.setVisible(!terminada && enJaque && !pensando);
        String puntos = tema.bando(modelo.Color.BLANCO) + " " + juego.puntos(modelo.Color.BLANCO)
                + " · " + tema.bando(modelo.Color.NEGRO) + " " + juego.puntos(modelo.Color.NEGRO) + "  —  ";
        if (terminada) {
            estado.setText(puntos + "FIN DE LA PARTIDA  —  " + resultado(fin));
            anunciarFin(fin);
        } else {
            finAnunciado = false; // si se deshizo el final, el próximo final se vuelve a anunciar
            estado.setText(puntos + turno() + (mensaje.isEmpty() ? "" : "  —  " + mensaje));
        }
    }

    private String turno() {
        modelo.Color turno = juego.turnoActual();
        String bando = tema.bando(turno);
        if (computadora.isEmpty()) {
            return "Turno: " + bando;
        }
        return turno == colorDeLaComputadora ? "Turno de la computadora (" + bando + ")" : "Tu turno (" + bando + ")";
    }

    /** Contra la computadora se habla en segunda persona: "Ganaste" o "Ganó la computadora". */
    private String resultado(EstadoPartida fin) {
        return fin.ganador().map(color -> computadora.isEmpty() ? "Ganador: " + tema.bando(color)
                        : color == colorDeLaComputadora ? "Ganó la computadora" : "¡Ganaste!")
                .orElse("Tablas");
    }

    /** Un único diálogo, después de que el tablero ya muestre la jugada final, con lo que se puede hacer. */
    private void anunciarFin(EstadoPartida fin) {
        if (finAnunciado) {
            return;
        }
        finAnunciado = true;
        SwingUtilities.invokeLater(() -> {
            Object[] opciones = {"Jugar de nuevo", "Volver al menú", "Ver el tablero"};
            int elegida = JOptionPane.showOptionDialog(this, resultado(fin) + "\n" + fin.motivo(),
                    "Fin de la partida", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    opciones, opciones[0]);
            if (elegida == 0) {
                empezarDeNuevo();
            } else if (elegida == 1) {
                alVolverAlMenu.run();
            }
        });
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
}
