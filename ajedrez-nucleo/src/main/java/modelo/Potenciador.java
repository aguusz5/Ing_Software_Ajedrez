package modelo;

import movimiento.ReglaDeMovimiento;

/**
 * Un potenciador (boost): aparece en una casilla del tablero y la pieza que cae ahí lo toma. Mientras lo
 * tiene, además de sus reglas se mueve con una más: la del potenciador. Dura un solo turno: en la próxima
 * jugada de su color se gasta, la haga esa pieza u otra (decisión 36).
 * No es un tipo de pieza nuevo ni una subclase: es una regla de movimiento que la pieza lleva encima.
 */
public final class Potenciador {

    private final String nombre;
    private final String descripcion;
    private final ReglaDeMovimiento regla;

    public Potenciador(String nombre, String descripcion, ReglaDeMovimiento regla) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.regla = regla;
    }

    public String nombre() {
        return nombre;
    }

    /** Qué le agrega a la pieza, para mostrarlo en pantalla: "salta como el caballo". */
    public String descripcion() {
        return descripcion;
    }

    /** Solo para Pieza, igual que TipoDePieza.reglas(): un adaptador no llega por acá a las reglas (decisión 8). */
    ReglaDeMovimiento regla() {
        return regla;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
