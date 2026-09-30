package modelo;

import java.util.ArrayList;
import java.util.List;

import movimiento.Jugada;
import movimiento.ReglaDeMovimiento;

/**
 * Una pieza concreta: su color, su tipo y si ya se movió alguna vez
 * (el peón lo necesita para saber si puede avanzar dos casillas).
 * Es inmutable: mover una pieza produce otra con yaSeMovio = true.
 */
public record Pieza(Color color, TipoDePieza tipo, boolean yaSeMovio) {

    /** Pieza que todavía no se movió. */
    public Pieza(Color color, TipoDePieza tipo) {
        this(color, tipo, false);
    }

    /** La misma pieza después de hacer una jugada. */
    public Pieza movida() {
        return new Pieza(color, tipo, true);
    }

    /** Junta las jugadas que propone cada una de las reglas de su tipo. */
    public List<Jugada> jugadasCandidatas(Tablero tablero, Posicion desde) {
        List<Jugada> jugadas = new ArrayList<>();
        for (ReglaDeMovimiento regla : tipo.reglas()) {
            jugadas.addAll(regla.jugadasCandidatas(tablero, desde, this));
        }
        return jugadas;
    }
}
