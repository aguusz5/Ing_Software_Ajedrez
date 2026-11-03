package ia;

import java.util.List;
import java.util.Optional;

import juego.JugadaLegal;
import modelo.Color;
import modelo.Tablero;

/**
 * Elige la jugada de la computadora sin confiar en su asesor: le pide una jugada, comprueba que esté entre las
 * legales y, si no está o no contestó, vuelve a pedir hasta "intentos" veces. Después le pide al respaldo; si
 * tampoco sirve, toma la primera legal, así la partida nunca se traba.
 * No juega ni conoce la partida: la jugada la hace ContraLaComputadora por el puerto, y ahí el núcleo la vuelve
 * a validar.
 */
public final class Computadora {

    private final AsesorDeJugadas asesor;
    private final AsesorDeJugadas respaldo;
    private final int intentos;

    public Computadora(AsesorDeJugadas asesor, AsesorDeJugadas respaldo, int intentos) {
        if (intentos < 1) {
            throw new IllegalArgumentException("La computadora necesita al menos un intento");
        }
        this.asesor = asesor;
        this.respaldo = respaldo;
        this.intentos = intentos;
    }

    /** Quién elige las jugadas, para avisarle al jugador mientras piensa. */
    public String asesor() {
        return asesor.nombre();
    }

    /**
     * Una de las jugadas legales de "turno" en ese tablero. Vacío si no hay ninguna. Recibe los datos y no el
     * puerto: solo necesita la posición, el turno y las legales (ISP). Quien la llama saca las legales del núcleo.
     */
    public Optional<JugadaDeLaComputadora> elegirJugada(Tablero tablero, Color turno, List<JugadaLegal> legales) {
        if (legales.isEmpty()) {
            return Optional.empty();
        }
        for (int intento = 0; intento < intentos; intento++) {
            Optional<JugadaLegal> sugerida = asesor.sugerirJugada(tablero, turno, legales)
                    .filter(legales::contains);
            if (sugerida.isPresent()) {
                return Optional.of(new JugadaDeLaComputadora(sugerida.get(), asesor.nombre(), false));
            }
        }
        JugadaLegal deRespaldo = respaldo.sugerirJugada(tablero, turno, legales)
                .filter(legales::contains)
                .orElse(legales.get(0));
        return Optional.of(new JugadaDeLaComputadora(deRespaldo, respaldo.nombre(), true));
    }
}
