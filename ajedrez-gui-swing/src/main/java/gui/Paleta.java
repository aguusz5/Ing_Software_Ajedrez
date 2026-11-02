package gui;

import java.awt.Color;

/**
 * Los colores de un tema: las casillas, el texto común (coordenadas, estado, menú), el título del menú,
 * el epígrafe (la línea chica arriba del título), el marco del tablero y el fondo del menú: un velo
 * semitransparente detrás de la columna de opciones, para que se lean sobre una imagen. Transparente
 * del todo si el fondo es liso.
 */
public record Paleta(Color casillaClara, Color casillaOscura, Color texto, Color titulo, Color epigrafe,
                     Color marco, Color fondoDelMenu) {
}
