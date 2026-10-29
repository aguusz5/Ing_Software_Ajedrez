package ia;

import juego.JugadaLegal;

/**
 * La jugada que eligió la computadora y quién la eligió, para avisarle al jugador (transparencia).
 * deRespaldo es true si el asesor principal no dio una jugada válida y la eligió el respaldo.
 */
public record JugadaDeLaComputadora(JugadaLegal jugada, String asesor, boolean deRespaldo) {
}
