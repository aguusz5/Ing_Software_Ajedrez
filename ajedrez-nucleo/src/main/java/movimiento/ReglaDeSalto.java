package movimiento;

import java.util.ArrayList;
import java.util.List;

import modelo.Direccion;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;

/**
 * Salta directo a cada destino, sin importar lo que haya en el medio (el caballo).
 * El destino tiene que estar libre o tener una pieza rival.
 */
public final class ReglaDeSalto implements ReglaDeMovimiento {

    private final List<Direccion> saltos;

    public ReglaDeSalto(List<Direccion> saltos) {
        this.saltos = List.copyOf(saltos);
    }

    @Override
    public List<Jugada> jugadasCandidatas(Tablero tablero, Posicion desde, Pieza pieza) {
        List<Jugada> jugadas = new ArrayList<>();
        for (Direccion salto : saltos) {
            Posicion destino = desde.desplazar(salto);
            boolean libreOConRival = tablero.piezaEn(destino)
                    .map(ocupante -> ocupante.color() != pieza.color())
                    .orElse(true);
            if (tablero.estaDentro(destino) && libreOConRival) {
                jugadas.add(new JugadaSimple(desde, destino));
            }
        }
        return jugadas;
    }
}
