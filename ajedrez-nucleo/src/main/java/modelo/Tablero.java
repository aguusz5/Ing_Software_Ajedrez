package modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Tablero inmutable de ancho x alto casillas, con sus piezas y los potenciadores que esperan en casillas vacías.
 * con() y sin() no modifican este tablero: devuelven uno nuevo. Por eso se puede probar
 * una jugada (por ejemplo, para ver si deja al rey en jaque) sin efectos secundarios.
 * Una casilla nunca tiene a la vez una pieza y un potenciador: la pieza que llega lo toma.
 */
public final class Tablero {

    private final int ancho;
    private final int alto;
    private final Map<Posicion, Pieza> piezas;
    private final Map<Posicion, Potenciador> potenciadores;

    /** Tablero vacío. */
    public Tablero(int ancho, int alto) {
        this(ancho, alto, Map.of(), Map.of());
    }

    private Tablero(int ancho, int alto, Map<Posicion, Pieza> piezas, Map<Posicion, Potenciador> potenciadores) {
        if (ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("El tablero necesita un ancho y un alto positivos");
        }
        this.ancho = ancho;
        this.alto = alto;
        this.piezas = Map.copyOf(piezas);
        this.potenciadores = Map.copyOf(potenciadores);
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

    /** El potenciador que espera en esa casilla, si hay uno. */
    public Optional<Potenciador> potenciadorEn(Posicion posicion) {
        return Optional.ofNullable(potenciadores.get(posicion));
    }

    /** Cuántos potenciadores esperan en el tablero (sin contar los que ya tomó alguna pieza). */
    public int cantidadDePotenciadores() {
        return potenciadores.size();
    }

    /** Casilla donde está el rey de ese color. */
    public Posicion buscarRey(Color color) {
        return piezas.entrySet().stream()
                .filter(casilla -> casilla.getValue().color() == color && casilla.getValue().tipo().esRey())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay rey " + color + " en el tablero"));
    }

    /** Suma del valor de las piezas de ese color que siguen en el tablero. */
    public int puntosDe(Color color) {
        return piezas.values().stream()
                .filter(pieza -> pieza.color() == color)
                .mapToInt(pieza -> pieza.tipo().valor())
                .sum();
    }

    /**
     * Un tablero nuevo con la pieza en esa casilla. Si ya había otra, la reemplaza: así se captura.
     * Si en la casilla esperaba un potenciador, la pieza lo toma.
     */
    public Tablero con(Posicion posicion, Pieza pieza) {
        exigirDentro(posicion);
        Map<Posicion, Pieza> nuevas = new HashMap<>(piezas);
        Map<Posicion, Potenciador> sinElDeLaCasilla = new HashMap<>(potenciadores);
        Optional<Potenciador> esperando = Optional.ofNullable(sinElDeLaCasilla.remove(posicion));
        nuevas.put(posicion, esperando.map(pieza::conPotenciador).orElse(pieza));
        return new Tablero(ancho, alto, nuevas, sinElDeLaCasilla);
    }

    /** Un tablero nuevo con esa casilla vacía. */
    public Tablero sin(Posicion posicion) {
        exigirDentro(posicion);
        Map<Posicion, Pieza> nuevas = new HashMap<>(piezas);
        nuevas.remove(posicion);
        return new Tablero(ancho, alto, nuevas, potenciadores);
    }

    /** Un tablero nuevo con un potenciador esperando en esa casilla, que tiene que estar vacía. */
    public Tablero conPotenciador(Posicion posicion, Potenciador potenciador) {
        exigirDentro(posicion);
        if (piezas.containsKey(posicion)) {
            throw new IllegalArgumentException(
                    "En " + posicion + " hay una pieza: el potenciador va en una casilla vacía");
        }
        Map<Posicion, Potenciador> nuevos = new HashMap<>(potenciadores);
        nuevos.put(posicion, potenciador);
        return new Tablero(ancho, alto, piezas, nuevos);
    }

    /** Un tablero nuevo en el que ninguna pieza de ese color tiene potenciador: se le venció el turno. */
    public Tablero sinPotenciadoresDe(Color color) {
        Map<Posicion, Pieza> nuevas = new HashMap<>(piezas);
        nuevas.replaceAll((posicion, pieza) -> pieza.color() == color ? pieza.sinPotenciador() : pieza);
        return new Tablero(ancho, alto, nuevas, potenciadores);
    }

    private void exigirDentro(Posicion posicion) {
        if (!estaDentro(posicion)) {
            throw new IllegalArgumentException("La posición " + posicion + " está fuera del tablero");
        }
    }
}
