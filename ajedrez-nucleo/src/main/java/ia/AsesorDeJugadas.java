package ia;

import java.util.List;
import java.util.Optional;

import juego.JugadaLegal;
import modelo.Color;
import modelo.Tablero;

/**
 * Quien le sugiere una jugada a la computadora (patrón Strategy): hoy, al azar o con minimax.
 * Asesora, no arbitra: lo que devuelve lo verifica la Computadora y lo valida el núcleo.
 * Un motor externo (Stockfish) sería otra implementación, en un adaptador fuera del núcleo.
 */
public interface AsesorDeJugadas {

    /** Cómo se llama, para avisarle al jugador quién eligió la jugada. */
    String nombre();

    /**
     * Elige una de las jugadas legales del que mueve. Vacío si no pudo elegir (por ejemplo, un motor externo
     * que no respondió). Nunca tira una excepción hacia el juego.
     */
    Optional<JugadaLegal> sugerirJugada(Tablero tablero, Color turno, List<JugadaLegal> legales);
}
