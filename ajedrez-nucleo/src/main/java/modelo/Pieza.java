package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import movimiento.Jugada;
import movimiento.ReglaDeMovimiento;

/**
 * Una pieza concreta: su color, su tipo, si ya se movió alguna vez (el peón lo necesita para saber si
 * puede avanzar dos casillas) y el potenciador que tomó, si tiene uno.
 * Es inmutable: mover una pieza produce otra con yaSeMovio = true y sin potenciador (lo gastó).
 */
public record Pieza(Color color, TipoDePieza tipo, boolean yaSeMovio, Optional<Potenciador> potenciador) {

    /** Pieza que todavía no se movió. */
    public Pieza(Color color, TipoDePieza tipo) {
        this(color, tipo, false);
    }

    /** Pieza sin potenciador. */
    public Pieza(Color color, TipoDePieza tipo, boolean yaSeMovio) {
        this(color, tipo, yaSeMovio, Optional.empty());
    }

    /** La misma pieza después de hacer una jugada. Si tenía un potenciador, lo gastó. */
    public Pieza movida() {
        return new Pieza(color, tipo, true);
    }

    /** La misma pieza con ese potenciador encima (reemplaza al que tuviera). */
    public Pieza conPotenciador(Potenciador nuevo) {
        return new Pieza(color, tipo, yaSeMovio, Optional.of(nuevo));
    }

    /** La misma pieza sin potenciador. */
    public Pieza sinPotenciador() {
        return new Pieza(color, tipo, yaSeMovio);
    }

    /**
     * Junta las jugadas que propone cada una de las reglas de su tipo y, si tiene, las de su potenciador. Las
     * prestadas pasan antes por las reglas del tipo, que pueden adaptarlas (el peón corona si llega al final).
     */
    public List<Jugada> jugadasCandidatas(Tablero tablero, Posicion desde) {
        List<Jugada> jugadas = new ArrayList<>();
        for (ReglaDeMovimiento regla : tipo.reglas()) {
            jugadas.addAll(regla.jugadasCandidatas(tablero, desde, this));
        }
        potenciador.ifPresent(extra -> jugadas.addAll(prestadas(tablero, desde, extra)));
        return jugadas;
    }

    private List<Jugada> prestadas(Tablero tablero, Posicion desde, Potenciador extra) {
        List<Jugada> prestadas = extra.regla().jugadasCandidatas(tablero, desde, this);
        for (ReglaDeMovimiento regla : tipo.reglas()) {
            List<Jugada> adaptadas = new ArrayList<>();
            for (Jugada prestada : prestadas) {
                adaptadas.addAll(regla.adaptarJugadaPrestada(tablero, prestada, this));
            }
            prestadas = adaptadas;
        }
        return prestadas;
    }
}
