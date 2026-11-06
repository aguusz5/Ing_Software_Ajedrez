package gui;

import java.awt.Color;

/**
 * Una era de Star Wars para el tema: cómo aparece en el menú, la línea de arriba del título, cómo se llama
 * cada bando, el brillo de cada uno y sus personajes. El tema es el mismo para todas (TemaStarWars): una era
 * nueva son datos nuevos, no una clase nueva (decisión 37).
 */
public record Era(String nombre, String epigrafe, String bandoDeLasBlancas, String bandoDeLasNegras,
                  Color brilloDeLasBlancas, Color brilloDeLasNegras, PersonajesDeStarWars personajes) {

    /** La Rebelión (blancas, brillo azul) contra el Imperio (negras, brillo rojo). */
    public static Era rebelionContraImperio() {
        return new Era("Star Wars", "Hace mucho tiempo, en una galaxia muy, muy lejana…", "Rebelión", "Imperio",
                new Color(80, 170, 255), new Color(255, 50, 50), PersonajesDeStarWars.rebelionContraImperio());
    }

    /** La República (blancas, brillo azul) contra los Separatistas (negras, brillo naranja de droide). */
    public static Era republicaContraSeparatistas() {
        return new Era("Guerra de los Clones", "¡Guerra! La República se desmorona…", "República", "Separatistas",
                new Color(80, 170, 255), new Color(255, 140, 30), PersonajesDeStarWars.republicaContraSeparatistas());
    }
}
