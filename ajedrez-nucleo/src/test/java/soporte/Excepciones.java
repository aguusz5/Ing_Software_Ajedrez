package soporte;

/**
 * Para que un test de excepciones también tenga sus tres partes separadas (Arrange, Act, Assert): el Act
 * ejecuta la acción y guarda lo que lanzó, y el Assert verifica qué fue. Con assertThrows, la acción y la
 * verificación quedan en la misma línea.
 */
public final class Excepciones {

    private Excepciones() {
    }

    /** La excepción que lanzó la acción, o null si terminó sin lanzar ninguna. */
    public static RuntimeException excepcionDe(Runnable accion) {
        try {
            accion.run();
            return null;
        } catch (RuntimeException excepcion) {
            return excepcion;
        }
    }
}
