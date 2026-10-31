package gui;

import java.awt.Color;

/**
 * Los colores de un tema: las casillas, el texto común (coordenadas, estado, menú), el título del menú,
 * el epígrafe (la línea chica arriba del título) y el marco del tablero.
 */
public record Paleta(Color casillaClara, Color casillaOscura, Color texto, Color titulo, Color epigrafe,
                     Color marco) {
}
