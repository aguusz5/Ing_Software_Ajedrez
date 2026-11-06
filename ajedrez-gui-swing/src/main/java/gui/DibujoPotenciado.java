package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Una pieza que tomó un potenciador: detrás de ella brilla la esfera del potenciador y en la esquina de arriba
 * a la izquierda queda una copia chica, para que se vea qué movimiento extra tiene este turno. Envuelve el
 * dibujo que hace el tema (patrón Decorator): sirve igual para un glifo que para un personaje.
 */
record DibujoPotenciado(DibujoDePieza pieza, DibujoDePieza potenciador) implements DibujoDePieza {

    /** Lado de la copia chica, en proporción a la casilla. */
    private static final double INSIGNIA = 0.42;

    @Override
    public String nombre() {
        return pieza.nombre() + " — " + potenciador.nombre();
    }

    @Override
    public void dibujar(Graphics graficos, int ancho, int alto) {
        potenciador.dibujar(graficos, ancho, alto);
        pieza.dibujar(graficos, ancho, alto);
        Graphics2D esquina = (Graphics2D) graficos.create();
        int lado = (int) (Math.min(ancho, alto) * INSIGNIA);
        potenciador.dibujar(esquina, lado, lado);
        esquina.dispose();
    }
}
