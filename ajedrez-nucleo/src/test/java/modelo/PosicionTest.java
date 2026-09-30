package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PosicionTest {

    @Test
    void desplazar_unaDireccion_sumaDxALaColumnaYDyALaFila() {
        // Arrange
        Posicion e4 = new Posicion(4, 3);

        // Act
        Posicion resultado = e4.desplazar(new Direccion(1, 2));

        // Assert
        assertEquals(new Posicion(5, 5), resultado);
    }
}
