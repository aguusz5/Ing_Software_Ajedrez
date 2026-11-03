package ia;

import java.util.Optional;

import juego.JuegoDeAjedrez;
import juego.JugadaLegal;
import juego.ResultadoJugada;
import modelo.Color;

/**
 * Las reglas de jugar contra la computadora, iguales para cualquier interfaz: le toca cuando la partida sigue
 * y es el turno de su color; su jugada se hace por el puerto, como cualquier otra, y el núcleo la vuelve a
 * validar; y deshacer o rehacer vuelve hasta que le toque a la persona (su jugada y la de la computadora).
 * Elegir y jugar van separados: la ventana elige en segundo plano sin que la partida cambie mientras la está
 * dibujando (decisión 27). Recibe la partida, la computadora y su color por constructor (decisión 34).
 */
public final class ContraLaComputadora {

    private final JuegoDeAjedrez juego;
    private final Computadora computadora;
    private final Color colorDeLaComputadora;

    public ContraLaComputadora(JuegoDeAjedrez juego, Computadora computadora, Color colorDeLaComputadora) {
        this.juego = juego;
        this.computadora = computadora;
        this.colorDeLaComputadora = colorDeLaComputadora;
    }

    /** Con qué color juega la computadora; la persona juega con el otro. */
    public Color colorDeLaComputadora() {
        return colorDeLaComputadora;
    }

    /** Quién elige las jugadas, para avisarle al jugador mientras piensa. */
    public String asesor() {
        return computadora.asesor();
    }

    /** Si la partida sigue y es el turno de la computadora. */
    public boolean leToca() {
        return !juego.estado().terminada() && juego.turnoActual() == colorDeLaComputadora;
    }

    /** La jugada que eligió la computadora, sin hacerla. Vacío si no le toca. No modifica la partida. */
    public Optional<JugadaDeLaComputadora> elegirJugada() {
        if (!leToca()) {
            return Optional.empty();
        }
        return computadora.elegirJugada(juego.tablero(), juego.turnoActual(), juego.jugadasLegales());
    }

    /**
     * Hace la jugada elegida por el puerto, como cualquier otra, coronando si corresponde. Fuera de su turno la
     * rechaza: una jugada legal del color de la persona no la puede hacer la computadora.
     */
    public ResultadoJugada jugar(JugadaDeLaComputadora elegida) {
        if (!leToca()) {
            return ResultadoJugada.rechazada("No le toca a la computadora");
        }
        JugadaLegal jugada = elegida.jugada();
        return jugada.promocion()
                .map(tipo -> juego.jugar(jugada.desde(), jugada.hasta(), tipo))
                .orElseGet(() -> juego.jugar(jugada.desde(), jugada.hasta()));
    }

    /** Vuelve hasta que le toque a la persona: deshace la última jugada y, si queda el turno de la computadora, otra. */
    public boolean deshacer() {
        boolean cambio = juego.deshacer();
        if (cambio && leToca()) {
            juego.deshacer();
        }
        return cambio;
    }

    /** Lo contrario de deshacer: rehace y, si queda el turno de la computadora, rehace también su jugada. */
    public boolean rehacer() {
        boolean cambio = juego.rehacer();
        if (cambio && leToca()) {
            juego.rehacer();
        }
        return cambio;
    }
}
