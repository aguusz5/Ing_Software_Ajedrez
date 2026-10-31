package gui;

import java.awt.Graphics;

/**
 * Lo que se ve de una pieza en su casilla. Cada tema elige cómo: con el símbolo de ajedrez
 * (DibujoDeGlifo) o con un personaje (DibujoDePersonaje). El tablero solo le pide que se dibuje.
 */
public interface DibujoDePieza {

    /** Cómo se llama lo que se ve; el tablero lo muestra al pasar el mouse por la casilla. */
    String nombre();

    /** Se dibuja a la escala de la casilla: si la ventana crece, la pieza crece con ella. */
    void dibujar(Graphics graficos, int ancho, int alto);
}
