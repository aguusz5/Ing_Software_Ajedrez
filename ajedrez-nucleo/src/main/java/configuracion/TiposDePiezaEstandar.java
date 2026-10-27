package configuracion;

import java.util.List;

import modelo.Direccion;
import modelo.TipoDePieza;
import movimiento.ReglaAvanceDePeon;
import movimiento.ReglaCapturaDePeon;
import movimiento.ReglaDeEnroque;
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

    private static final TipoDePieza TORRE =
            new TipoDePieza("torre", false, 5, List.of(LINEAS_RECTAS));
    private static final TipoDePieza CABALLO =
            new TipoDePieza("caballo", false, 3, List.of(new ReglaDeSalto(Direccion.SALTOS_DE_CABALLO)));
    private static final TipoDePieza ALFIL =
            new TipoDePieza("alfil", false, 3, List.of(LINEAS_DIAGONALES));
    private static final TipoDePieza DAMA =
            new TipoDePieza("dama", false, 9, List.of(LINEAS_RECTAS, LINEAS_DIAGONALES));
    // El enroque es una regla más del rey: su compañera es la torre.
    private static final TipoDePieza REY = new TipoDePieza("rey", true, 0,
            List.of(new ReglaDeslizante(Direccion.TODAS, 1), new ReglaDeEnroque(TORRE)));

    /** En qué puede coronar el peón, en este orden: sin elegir, corona en la primera (dama). */
    private static final List<TipoDePieza> CORONACIONES = List.of(DAMA, TORRE, ALFIL, CABALLO);
    // Va después de las otras piezas porque las necesita para coronar.
    private static final TipoDePieza PEON = new TipoDePieza("peón", false, 1,
            List.of(new ReglaAvanceDePeon(CORONACIONES), new ReglaCapturaDePeon(CORONACIONES)));

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

    /** Se desliza un solo paso en cualquier dirección o enroca, y es la pieza que no puede quedar en jaque. */
    public static TipoDePieza rey() {
        return REY;
    }
}
