package reglas;

/**
 * Respuesta de una regla de validación: si la jugada pasa y, si no pasa, por qué.
 */
public record ResultadoValidacion(boolean valida, String motivo) {

    public static ResultadoValidacion ok() {
        return new ResultadoValidacion(true, "");
    }

    public static ResultadoValidacion rechazada(String motivo) {
        return new ResultadoValidacion(false, motivo);
    }
}
