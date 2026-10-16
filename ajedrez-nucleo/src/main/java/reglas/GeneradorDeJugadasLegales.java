package reglas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import movimiento.Jugada;

/**
 * Responde qué jugadas legales tiene un color: pide las candidatas de cada pieza y se queda con las
 * que el validador acepta. Lo usan el mate, el ahogado, el resaltado de la GUI y la IA.
 */
public final class GeneradorDeJugadasLegales {

    private final ValidadorDeJugadas validador;

    public GeneradorDeJugadasLegales(ValidadorDeJugadas validador) {
        this.validador = validador;
    }

    public List<Jugada> jugadasLegales(Tablero tablero, Color color) {
        List<Jugada> legales = new ArrayList<>();
        for (int columna = 0; columna < tablero.ancho(); columna++) {
            for (int fila = 0; fila < tablero.alto(); fila++) {
                legales.addAll(jugadasLegalesDesde(tablero, new Posicion(columna, fila), color));
            }
        }
        return legales;
    }

    /** Las piezas del rival no tienen jugadas legales: las descarta ReglaPiezaPropia. */
    public List<Jugada> jugadasLegalesDesde(Tablero tablero, Posicion desde, Color turno) {
        Optional<Pieza> pieza = tablero.piezaEn(desde);
        if (pieza.isEmpty()) {
            return List.of();
        }
        return pieza.get().jugadasCandidatas(tablero, desde).stream()
                .filter(jugada -> validador.validar(tablero, jugada, turno).valida())
                .toList();
    }
}
