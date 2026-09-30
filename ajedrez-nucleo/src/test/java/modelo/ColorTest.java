package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ColorTest {

    @Test
    void contrario_deCadaColor_esElOtro() {
        // Act & Assert
        assertEquals(Color.NEGRO, Color.BLANCO.contrario());
        assertEquals(Color.BLANCO, Color.NEGRO.contrario());
    }

    @Test
    void sentidoDeAvance_blancasSubenYNegrasBajan() {
        // Act & Assert
        assertEquals(1, Color.BLANCO.sentidoDeAvance());
        assertEquals(-1, Color.NEGRO.sentidoDeAvance());
    }
}
