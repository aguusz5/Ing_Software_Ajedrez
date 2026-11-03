package gui;

import ia.ContraLaComputadora;
import modelo.Color;

/**
 * Una dificultad que ofrece el menú: cómo se llama, qué hace y la partida contra la computadora para cada color
 * que puede elegir la persona (la computadora juega el otro). Las arma Principal, que es el único que hace new:
 * el menú solo las muestra y la ventana elige la del color que se eligió.
 */
public record Dificultad(String nombre, String descripcion, ContraLaComputadora siElegisBlancas,
                         ContraLaComputadora siElegisNegras) {

    /** La partida contra la computadora cuando la persona juega con ese color. */
    public ContraLaComputadora paraJugarCon(Color colorDelJugador) {
        return colorDelJugador == Color.BLANCO ? siElegisBlancas : siElegisNegras;
    }
}
