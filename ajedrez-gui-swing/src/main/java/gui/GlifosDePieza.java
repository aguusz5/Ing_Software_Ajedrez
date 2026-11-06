package gui;

import java.util.Map;

import modelo.Pieza;
import modelo.Potenciador;

/**
 * Asocia cada tipo de pieza con su símbolo Unicode (♔, ♕, ...) a partir del nombre del tipo.
 * El color no cambia el símbolo: la ventana lo pinta relleno de blanco o de negro.
 * Si el nombre no está en la tabla (una pieza nueva), devuelve su inicial:
 * así una pieza nueva se ve en la GUI sin tocar esta clase.
 * Un potenciador se muestra con el símbolo de la pieza cuyo movimiento presta; uno nuevo, con una estrella.
 */
public final class GlifosDePieza {

    private static final Map<String, String> GLIFOS = Map.of(
            "rey", "♔", "dama", "♕", "torre", "♖", "alfil", "♗", "caballo", "♘", "peón", "♙");
    private static final Map<String, String> GLIFOS_DE_POTENCIADOR = Map.of(
            "Salto al hiperespacio", "♘", "Empuje de la Fuerza", "♔", "Propulsores", "♖", "Sable de luz", "♗");
    private static final String GLIFO_DE_POTENCIADOR_NUEVO = "✦";

    public String glifoDe(Pieza pieza) {
        String nombre = pieza.tipo().nombre();
        return GLIFOS.getOrDefault(nombre, nombre.substring(0, 1).toUpperCase());
    }

    public String glifoDe(Potenciador potenciador) {
        return GLIFOS_DE_POTENCIADOR.getOrDefault(potenciador.nombre(), GLIFO_DE_POTENCIADOR_NUEVO);
    }
}
