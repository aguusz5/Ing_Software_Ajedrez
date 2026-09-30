package gui;

import java.util.Map;

import modelo.Pieza;

/**
 * Asocia cada tipo de pieza con su símbolo Unicode (♔, ♕, ...) a partir del nombre del tipo.
 * El color no cambia el símbolo: la ventana lo pinta relleno de blanco o de negro.
 * Si el nombre no está en la tabla (una pieza nueva), devuelve su inicial:
 * así una pieza nueva se ve en la GUI sin tocar esta clase.
 */
public final class GlifosDePieza {

    private static final Map<String, String> GLIFOS = Map.of(
            "rey", "♔", "dama", "♕", "torre", "♖", "alfil", "♗", "caballo", "♘", "peón", "♙");

    public String glifoDe(Pieza pieza) {
        String nombre = pieza.tipo().nombre();
        return GLIFOS.getOrDefault(nombre, nombre.substring(0, 1).toUpperCase());
    }
}
