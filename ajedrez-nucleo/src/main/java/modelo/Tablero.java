package modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Tablero inmutable de ancho x alto casillas.
 * con() y sin() no modifican este tablero: devuelven uno nuevo. Por eso se puede probar
 * una jugada (por ejemplo, para ver si deja al rey en jaque) sin efectos secundarios.
 */
public final class Tablero {

    private final int ancho;
    private final int alto;
    private final Map<Posicion, Pieza> piezas;

    /** Tablero vacío. */
    public Tablero(int ancho, int alto) {
        this(ancho, alto, Map.of());
    }

    private Tablero(int ancho, int alto, Map<Posicion, Pieza> piezas) {
        if (ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("El tablero necesita un ancho y un alto positivos");
        }
        this.ancho = ancho;
        this.alto = alto;
        this.piezas = Map.copyOf(piezas);
    }

    public int ancho() {
        return ancho;
    }

    public int alto() {
        return alto;
    }

    public boolean estaDentro(Posicion posicion) {
        return posicion.columna() >= 0 && posicion.columna() < ancho
                && posicion.fila() >= 0 && posicion.fila() < alto;
    }

    public Optional<Pieza> piezaEn(Posicion posicion) {
        return Optional.ofNullable(piezas.get(posicion));
    }

    /** Casilla donde está el rey de ese color. */
    public Posicion buscarRey(Color color) {
        return piezas.entrySet().stream()
                .filter(casilla -> casilla.getValue().color() == color && casilla.getValue().tipo().esRey())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay rey " + color + " en el tablero"));
    }

    /** Un tablero nuevo con la pieza en esa casilla. Si ya había otra, la reemplaza: así se captura. */
    public Tablero con(Posicion posicion, Pieza pieza) {
        exigirDentro(posicion);
        Map<Posicion, Pieza> nuevas = new HashMap<>(piezas);
        nuevas.put(posicion, pieza);
        return new Tablero(ancho, alto, nuevas);
    }

    /** Un tablero nuevo con esa casilla vacía. */
    public Tablero sin(Posicion posicion) {
        exigirDentro(posicion);
        Map<Posicion, Pieza> nuevas = new HashMap<>(piezas);
        nuevas.remove(posicion);
        return new Tablero(ancho, alto, nuevas);
    }

    private void exigirDentro(Posicion posicion) {
        if (!estaDentro(posicion)) {
            throw new IllegalArgumentException("La posición " + posicion + " está fuera del tablero");
        }
    }
}
