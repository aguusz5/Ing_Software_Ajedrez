package movimiento;

import java.util.ArrayList;
import java.util.List;

import modelo.Direccion;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import modelo.TipoDePieza;

/**
 * El enroque, como una regla más del rey. Hacia cada costado busca la primera pieza de su fila:
 * si es una compañera (la torre) del mismo color, ni ella ni el rey se movieron y las dos casillas
 * que recorre el rey están libres, propone el enroque. No usa números de columna, así que sirve en
 * cualquier tablero. Que el rey no esté ni pase por casillas atacadas lo controla ReglaEnroqueSeguro:
 * si esta regla le preguntara al detector de jaque, el detector le volvería a pedir las jugadas al rey
 * rival y se armaría una recursión infinita.
 */
public final class ReglaDeEnroque implements ReglaDeMovimiento {

    private static final List<Direccion> COSTADOS = List.of(new Direccion(1, 0), new Direccion(-1, 0));

    private final TipoDePieza companera;

    public ReglaDeEnroque(TipoDePieza companera) {
        this.companera = companera;
    }

    @Override
    public List<Jugada> jugadasCandidatas(Tablero tablero, Posicion desde, Pieza pieza) {
        List<Jugada> jugadas = new ArrayList<>();
        if (pieza.yaSeMovio()) {
            return jugadas;
        }
        for (Direccion costado : COSTADOS) {
            // El rey se mueve dos casillas hacia la torre y la torre salta a la que el rey cruzó.
            Posicion cruza = desde.desplazar(costado);
            Posicion llega = cruza.desplazar(costado);
            if (!estaLibre(tablero, cruza) || !estaLibre(tablero, llega)) {
                continue;
            }
            Posicion torre = primeraOcupada(tablero, llega.desplazar(costado), costado);
            boolean puedeEnrocar = tablero.piezaEn(torre)
                    .map(otra -> otra.color() == pieza.color() && otra.tipo().equals(companera) && !otra.yaSeMovio())
                    .orElse(false);
            if (puedeEnrocar) {
                jugadas.add(new JugadaDeEnroque(desde, llega, torre, cruza));
            }
        }
        return jugadas;
    }

    /** Avanza desde "inicio" mientras haya casillas libres: devuelve la primera ocupada, o la de afuera del borde. */
    private static Posicion primeraOcupada(Tablero tablero, Posicion inicio, Direccion costado) {
        Posicion actual = inicio;
        while (estaLibre(tablero, actual)) {
            actual = actual.desplazar(costado);
        }
        return actual;
    }

    private static boolean estaLibre(Tablero tablero, Posicion posicion) {
        return tablero.estaDentro(posicion) && tablero.piezaEn(posicion).isEmpty();
    }
}
