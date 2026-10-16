package modelo;

import java.util.List;

import movimiento.ReglaDeMovimiento;

/**
 * Qué es una pieza (peón, torre, ...): un nombre, cuántos puntos vale y la lista de reglas con las
 * que se mueve. No hay una clase por pieza: la dama es un tipo que tiene las reglas de la torre y las
 * del alfil.
 */
public final class TipoDePieza {

    private final String nombre;
    private final boolean esRey;
    private final int valor;
    private final List<ReglaDeMovimiento> reglas;

    public TipoDePieza(String nombre, boolean esRey, int valor, List<ReglaDeMovimiento> reglas) {
        if (reglas.isEmpty()) {
            throw new IllegalArgumentException("Un tipo de pieza necesita al menos una regla de movimiento");
        }
        this.nombre = nombre;
        this.esRey = esRey;
        this.valor = valor;
        this.reglas = List.copyOf(reglas);
    }

    /** Puntos que suma la pieza mientras sigue en el tablero. */
    public int valor() {
        return valor;
    }

    public String nombre() {
        return nombre;
    }

    /** Si es la pieza que no puede quedar en jaque. */
    public boolean esRey() {
        return esRey;
    }

    public List<ReglaDeMovimiento> reglas() {
        return reglas;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
