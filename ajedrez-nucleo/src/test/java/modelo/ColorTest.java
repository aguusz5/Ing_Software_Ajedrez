package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ColorTest {

    @Test
    void contrario_deCadaColor_esElOtro() {
        // Arrange
        Color blanco = Color.BLANCO;
        Color negro = Color.NEGRO;

        // Act
        Color contrarioDelBlanco = blanco.contrario();
        Color contrarioDelNegro = negro.contrario();

        // Assert
        assertEquals(Color.NEGRO, contrarioDelBlanco);
        assertEquals(Color.BLANCO, contrarioDelNegro);
    }

    @Test
    void sentidoDeAvance_deCadaColor_blancasSubenYNegrasBajan() {
        // Arrange
        Color blanco = Color.BLANCO;
        Color negro = Color.NEGRO;

        // Act
        int sentidoDelBlanco = blanco.sentidoDeAvance();
        int sentidoDelNegro = negro.sentidoDeAvance();

        // Assert
        assertEquals(1, sentidoDelBlanco);
        assertEquals(-1, sentidoDelNegro);
    }
}
