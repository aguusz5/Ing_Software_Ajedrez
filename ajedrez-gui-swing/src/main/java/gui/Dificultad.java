package gui;

import ia.Computadora;

/**
 * Una dificultad que ofrece el menú: cómo se llama, qué hace y la computadora que juega con ella.
 * Las arma Principal: el menú solo las muestra.
 */
public record Dificultad(String nombre, String descripcion, Computadora computadora) {
}
