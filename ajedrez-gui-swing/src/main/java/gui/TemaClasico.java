package gui;

import java.awt.Color;
import java.awt.Graphics2D;

import modelo.Pieza;

/** El tema de siempre: casillas de madera, piezas con su símbolo Unicode en blanco y negro. */
public final class TemaClasico implements Tema {

    private static final Color FONDO = new Color(245, 238, 226);
    private static final Paleta PALETA = new Paleta(
            new Color(240, 217, 181), new Color(181, 136, 99),
            new Color(60, 60, 60), new Color(40, 30, 20), new Color(120, 90, 60), new Color(90, 60, 40));

    private final GlifosDePieza glifos;

    public TemaClasico(GlifosDePieza glifos) {
        this.glifos = glifos;
    }

    @Override
    public String nombre() {
        return "Clásico";
    }

    @Override
    public String epigrafe() {
        return "";
    }

    @Override
    public Paleta paleta() {
        return PALETA;
    }

    @Override
    public String bando(modelo.Color color) {
        return color == modelo.Color.BLANCO ? "Blancas" : "Negras";
    }

    @Override
    public String nombreDe(String tipo, modelo.Color color) {
        return tipo;
    }

    @Override
    public DibujoDePieza dibujoDe(Pieza pieza) {
        boolean esBlanca = pieza.color() == modelo.Color.BLANCO;
        return new DibujoDeGlifo(pieza.tipo().nombre(), glifos.glifoDe(pieza),
                esBlanca ? Color.WHITE : Color.BLACK,
                esBlanca ? Color.BLACK : Color.WHITE);
    }

    @Override
    public void pintarFondo(Graphics2D g, int ancho, int alto) {
        g.setColor(FONDO);
        g.fillRect(0, 0, ancho, alto);
    }
}
