package configuracion;

import java.util.List;

import modelo.Direccion;
import modelo.Potenciador;
import movimiento.ReglaDeSalto;
import movimiento.ReglaDeslizante;

/**
 * Los potenciadores de la partida en la ventana, con nombres de Star Wars. Son ventajas leves a propósito:
 * cada uno presta un movimiento corto (o el salto del caballo) por un solo turno, y hay que saber aprovecharlo.
 * Se arman con las mismas reglas de movimiento que las piezas: un potenciador nuevo no necesita código nuevo.
 */
public final class PotenciadoresStarWars {

    /** Lo lejos que llegan los potenciadores que deslizan: dos casillas, para que no superen a una torre. */
    private static final int ALCANCE_CORTO = 2;

    private static final Potenciador HIPERESPACIO = new Potenciador("Salto al hiperespacio",
            "salta como el caballo", new ReglaDeSalto(Direccion.SALTOS_DE_CABALLO));
    private static final Potenciador FUERZA = new Potenciador("Empuje de la Fuerza",
            "da un paso en cualquier dirección", new ReglaDeslizante(Direccion.TODAS, 1));
    private static final Potenciador PROPULSORES = new Potenciador("Propulsores",
            "avanza hasta dos casillas en línea recta", new ReglaDeslizante(Direccion.ORTOGONALES, ALCANCE_CORTO));
    private static final Potenciador SABLE_DE_LUZ = new Potenciador("Sable de luz",
            "avanza hasta dos casillas en diagonal", new ReglaDeslizante(Direccion.DIAGONALES, ALCANCE_CORTO));

    private PotenciadoresStarWars() {
    }

    public static Potenciador hiperespacio() {
        return HIPERESPACIO;
    }

    public static Potenciador fuerza() {
        return FUERZA;
    }

    public static Potenciador propulsores() {
        return PROPULSORES;
    }

    public static Potenciador sableDeLuz() {
        return SABLE_DE_LUZ;
    }

    /** Todos, para que la partida elija al azar entre ellos. */
    public static List<Potenciador> todos() {
        return List.of(HIPERESPACIO, FUERZA, PROPULSORES, SABLE_DE_LUZ);
    }
}
