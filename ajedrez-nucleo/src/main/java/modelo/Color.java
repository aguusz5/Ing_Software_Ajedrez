package modelo;

/**
 * Color de las piezas y de los jugadores.
 * Cada color sabe quién es su rival y hacia dónde avanzan sus peones.
 */
public enum Color {
    BLANCO(1),
    NEGRO(-1);

    private final int sentidoDeAvance;

    Color(int sentidoDeAvance) {
        this.sentidoDeAvance = sentidoDeAvance;
    }

    public Color contrario() {
        return this == BLANCO ? NEGRO : BLANCO;
    }

    /** +1 si avanza hacia las filas de arriba (blancas), -1 si avanza hacia las de abajo (negras). */
    public int sentidoDeAvance() {
        return sentidoDeAvance;
    }
}
