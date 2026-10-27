package reglas;

import java.util.List;

import modelo.Color;
import modelo.Pieza;
import modelo.Posicion;
import modelo.Tablero;
import movimiento.Jugada;

/**
 * El rey no puede enrocar estando en jaque ni pasar por una casilla atacada. Pregunta por las casillas
 * que la jugada dice que no pueden estar atacadas, así que no necesita saber que es un enroque.
 * Para cada casilla pone al rey ahí y le pregunta al detector si queda en jaque: así cuenta también la
 * captura en diagonal del peón, que el peón solo propone cuando hay una pieza rival en el destino.
 */
public final class ReglaEnroqueSeguro implements ReglaDeValidacion {

    private final DetectorDeJaque detector;

    public ReglaEnroqueSeguro(DetectorDeJaque detector) {
        this.detector = detector;
    }

    @Override
    public ResultadoValidacion validar(Tablero tablero, Jugada jugada, Color turno) {
        List<Posicion> casillas = jugada.casillasQueNoPuedenEstarAtacadas();
        if (casillas.isEmpty()) {
            return ResultadoValidacion.ok();
        }
        Pieza rey = tablero.piezaEn(jugada.desde())
                .orElseThrow(() -> new IllegalStateException("No hay pieza en " + jugada.desde()));
        for (Posicion casilla : casillas) {
            Tablero conElReyAhi = tablero.sin(jugada.desde()).con(casilla, rey);
            if (detector.estaEnJaque(conElReyAhi, turno)) {
                return ResultadoValidacion.rechazada(
                        "No se puede enrocar: el rey está en jaque o pasa por una casilla atacada");
            }
        }
        return ResultadoValidacion.ok();
    }
}
