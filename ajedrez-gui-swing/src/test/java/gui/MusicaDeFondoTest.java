package gui;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

class MusicaDeFondoTest {

    @Test
    void aBytes_volumenALaMitad_cadaMuestraQuedaALaMitadConElByteBajoPrimero() {
        // Arrange
        short[] muestras = {1000, -1000};

        // Act
        byte[] bytes = ReproductorDeMusica.aBytes(muestras, muestras.length, 0.5);

        // Assert
        // 500 = 0x01F4 y -500 = 0xFE0C
        assertArrayEquals(new byte[] {(byte) 0xF4, 0x01, 0x0C, (byte) 0xFE}, bytes);
    }

    @Test
    void aBytes_cantidadMenorQueElArreglo_soloConvierteEsasMuestras() {
        // Arrange
        short[] muestras = {100, 200, 300};

        // Act
        byte[] bytes = ReproductorDeMusica.aBytes(muestras, 2, 1.0);

        // Assert
        assertArrayEquals(new byte[] {100, 0, (byte) 200, 0}, bytes);
    }

    @Test
    void cancionesDeLaCarpeta_cadaUna_esUnMp3MonoQueSeDecodifica() throws Exception {
        // Arrange
        Path carpeta = Path.of(ReproductorDeMusica.class.getResource("musica").toURI());
        List<Path> canciones;
        try (Stream<Path> archivos = Files.list(carpeta)) {
            canciones = archivos.filter(archivo -> archivo.toString().endsWith(".mp3")).toList();
        }
        List<String> problemas = new ArrayList<>();

        // Act
        for (Path cancion : canciones) {
            try (InputStream entrada = Files.newInputStream(cancion)) {
                Bitstream mp3 = new Bitstream(entrada);
                Header cuadro = mp3.readFrame();
                if (cuadro == null) {
                    problemas.add(cancion.getFileName() + ": no tiene cuadros de MP3");
                    continue;
                }
                SampleBuffer muestras = (SampleBuffer) new Decoder().decodeFrame(cuadro, mp3);
                if (muestras.getChannelCount() != 1) {
                    problemas.add(cancion.getFileName() + ": no es mono");
                }
            }
        }

        // Assert
        assertFalse(canciones.isEmpty());
        assertEquals(List.of(), problemas);
    }

    @Test
    void empezar_cancionQueNoExiste_noRompe() {
        // Arrange
        ReproductorDeMusica musica = new ReproductorDeMusica(List.of("no-existe.mp3"), new Random(1));

        // Act & Assert
        assertDoesNotThrow(musica::empezar);
    }

    @Test
    void empezar_sinCanciones_noRompe() {
        // Arrange
        ReproductorDeMusica musica = new ReproductorDeMusica(List.of(), new Random(1));

        // Act & Assert
        assertDoesNotThrow(musica::empezar);
    }

    @Test
    void activar_apagada_quedaApagada() {
        // Arrange
        ReproductorDeMusica musica = new ReproductorDeMusica(List.of("no-existe.mp3"), new Random(1));

        // Act
        musica.activar(false);

        // Assert
        assertFalse(musica.activa());
    }

    @Test
    void activar_apagadaYDespuesPrendida_vuelveASonar() {
        // Arrange
        ReproductorDeMusica musica = new ReproductorDeMusica(List.of("no-existe.mp3"), new Random(1));
        musica.activar(false);

        // Act
        musica.activar(true);

        // Assert
        assertTrue(musica.activa());
    }
}
