package configuracion;

import java.util.List;

import modelo.Direccion;
import modelo.TipoDePieza;
import movimiento.ReglaAvanceDePeon;
import movimiento.ReglaCapturaDePeon;
import movimiento.ReglaDeMovimiento;
import movimiento.ReglaDeSalto;
import movimiento.ReglaDeslizante;

/**
 * Las seis piezas del ajedrez clásico, armadas por composición de reglas de movimiento.
 * Para una pieza nueva no hace falta tocar esta clase: alcanza con crear otro TipoDePieza.
 */
public final class TiposDePiezaEstandar {

    private static final ReglaDeMovimiento LINEAS_RECTAS =
            new ReglaDeslizante(Direccion.ORTOGONALES, ReglaDeslizante.SIN_LIMITE);
    private static final ReglaDeMovimiento LINEAS_DIAGONALES =
            new ReglaDeslizante(Direccion.DIAGONALES, ReglaDeslizante.SIN_LIMITE);

    private static final TipoDePieza PEON =
            new TipoDePieza("peón", false, List.of(new ReglaAvanceDePeon(), new ReglaCapturaDePeon()));
    private static final TipoDePieza TORRE =
            new TipoDePieza("torre", false, List.of(LINEAS_RECTAS));
    private static final TipoDePieza CABALLO =
            new TipoDePieza("caballo", false, List.of(new ReglaDeSalto(Direccion.SALTOS_DE_CABALLO)));
    private static final TipoDePieza ALFIL =
            new TipoDePieza("alfil", false, List.of(LINEAS_DIAGONALES));
    private static final TipoDePieza DAMA =
            new TipoDePieza("dama", false, List.of(LINEAS_RECTAS, LINEAS_DIAGONALES));
    private static final TipoDePieza REY =
            new TipoDePieza("rey", true, List.of(new ReglaDeslizante(Direccion.TODAS, 1)));

    private TiposDePiezaEstandar() {
    }

    public static TipoDePieza peon() {
        return PEON;
    }

    public static TipoDePieza torre() {
        return TORRE;
    }

    public static TipoDePieza caballo() {
        return CABALLO;
    }

    public static TipoDePieza alfil() {
        return ALFIL;
    }

    /** Torre + alfil: las mismas dos reglas, juntas. */
    public static TipoDePieza dama() {
        return DAMA;
    }

    /** Se desliza un solo paso en cualquier dirección, y es la pieza que no puede quedar en jaque. */
    public static TipoDePieza rey() {
        return REY;
    }
}
