package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.Optional;

import javax.accessibility.AccessibleContext;
import javax.swing.BorderFactory;

import org.junit.jupiter.api.Test;

import configuracion.TableroEstandar;
import modelo.Posicion;
import modelo.Tablero;

class PanelDelTableroTest {

    private final Tablero estandar = new TableroEstandar().crearTablero();
    private final PanelDelTablero tablero = new PanelDelTablero(estandar.ancho(), estandar.alto(), posicion -> {});

    @Test
    void mostrar_damaEnE4_laCasillaSeLlamaE4YDiceQuePiezaTiene() {
        // Arrange
        DibujoDePieza vader = new DibujoDeGlifo("Darth Vader (dama)", "♕", Color.BLACK, Color.WHITE);

        // Act
        tablero.mostrar(new Posicion(4, 3), Optional.of(vader), Color.GRAY, BorderFactory.createEmptyBorder());

        // Assert
        AccessibleContext e4 = casillaLlamada("e4");
        assertEquals("Darth Vader (dama)", e4.getAccessibleDescription());
    }

    @Test
    void casillas_tableroEstandar_cadaUnaTieneSuCoordenadaComoNombre() {
        // Arrange
        int columnas = estandar.ancho();
        int filas = estandar.alto();

        // Act
        PanelDelTablero nuevo = new PanelDelTablero(columnas, filas, posicion -> {});

        // Assert: 64 casillas, cada una con un nombre distinto como "e4".
        assertEquals(64, coordenadasDistintas(nuevo));
    }

    /** Cuántas casillas del panel tienen como nombre una coordenada, sin contar repetidas. */
    private static long coordenadasDistintas(PanelDelTablero panel) {
        return Arrays.stream(panel.getComponents())
                .map(casilla -> casilla.getAccessibleContext().getAccessibleName())
                .filter(nombre -> nombre != null && nombre.matches("[a-z][0-9]+"))
                .distinct()
                .count();
    }

    private AccessibleContext casillaLlamada(String nombre) {
        return Arrays.stream(tablero.getComponents())
                .map(Component::getAccessibleContext)
                .filter(contexto -> nombre.equals(contexto.getAccessibleName()))
                .findFirst()
                .orElseThrow();
    }
}
