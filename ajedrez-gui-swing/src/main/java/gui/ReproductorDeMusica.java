package gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;

/**
 * La música de fondo: MP3 de gui/musica/, una canción atrás de la otra, en un orden al azar que cambia en cada
 * vuelta, sin fin. Suena más baja que las voces de los personajes, que van por ReproductorDeSonidos y se oyen
 * encima. Java no lee MP3: los decodifica JLayer, de a un cuadro por vez, en un hilo aparte para no trabar la
 * ventana (decisión 40). Si la máquina no tiene salida de audio (el CI, por ejemplo) o no hay canciones, no suena
 * nada: la música nunca rompe la partida.
 */
public final class ReproductorDeMusica {

    private static final String CARPETA = "musica/";
    /** Por cuánto se multiplica cada muestra: unos 10 dB menos, para que quede de fondo y no tape las voces. */
    static final double VOLUMEN = 0.3;

    private final List<String> canciones;
    private final Random azar;
    private boolean activa = true; // se apaga con la casilla "Con música" del menú
    private boolean empezada;

    /** Las canciones son nombres de archivo de gui/musica/, por ejemplo "marcha-imperial.mp3". */
    public ReproductorDeMusica(List<String> canciones, Random azar) {
        this.canciones = List.copyOf(canciones);
        this.azar = azar;
    }

    /** Empieza a sonar en un hilo aparte. Llamarlo otra vez no hace nada: hay una sola música. */
    synchronized void empezar() {
        if (empezada || canciones.isEmpty()) {
            return;
        }
        empezada = true;
        Thread.ofPlatform().daemon().name("música de fondo").start(this::tocarSinFin);
    }

    /** Prende o apaga la música. Apagarla la pausa; al prenderla sigue desde donde quedó. */
    synchronized void activar(boolean prendida) {
        activa = prendida;
        notifyAll();
    }

    synchronized boolean activa() {
        return activa;
    }

    /**
     * Las muestras de 16 bits, multiplicadas por el volumen, como los bytes que espera la salida de audio
     * (little-endian: primero el byte bajo).
     */
    static byte[] aBytes(short[] muestras, int cantidad, double volumen) {
        byte[] bytes = new byte[cantidad * 2];
        for (int i = 0; i < cantidad; i++) {
            int muestra = (int) Math.round(muestras[i] * volumen);
            bytes[2 * i] = (byte) muestra;
            bytes[2 * i + 1] = (byte) (muestra >> 8);
        }
        return bytes;
    }

    /** Vuelta tras vuelta, cada una en otro orden. Si en una vuelta no sonó ninguna, no hay audio: termina. */
    private void tocarSinFin() {
        boolean sonoAlguna = true;
        while (sonoAlguna && !Thread.currentThread().isInterrupted()) {
            List<String> vuelta = new ArrayList<>(canciones);
            Collections.shuffle(vuelta, azar);
            sonoAlguna = false;
            for (String cancion : vuelta) {
                sonoAlguna |= tocar(cancion);
            }
        }
    }

    /** Toca una canción entera, cuadro por cuadro. Devuelve si pudo sonar. */
    private boolean tocar(String cancion) {
        URL archivo = ReproductorDeMusica.class.getResource(CARPETA + cancion);
        if (archivo == null) {
            return false;
        }
        try (InputStream entrada = archivo.openStream()) {
            Bitstream mp3 = new Bitstream(entrada);
            Decoder decodificador = new Decoder();
            Header cuadro = mp3.readFrame();
            if (cuadro == null) {
                return false;
            }
            SampleBuffer muestras = (SampleBuffer) decodificador.decodeFrame(cuadro, mp3);
            AudioFormat formato = new AudioFormat(muestras.getSampleFrequency(), 16, muestras.getChannelCount(),
                    true, false);
            try (SourceDataLine salida = AudioSystem.getSourceDataLine(formato)) {
                salida.open(formato);
                salida.start();
                while (cuadro != null) {
                    pausarMientrasEsteApagada(salida);
                    byte[] bytes = aBytes(muestras.getBuffer(), muestras.getBufferLength(), VOLUMEN);
                    salida.write(bytes, 0, bytes.length);
                    mp3.closeFrame();
                    cuadro = mp3.readFrame();
                    if (cuadro != null) {
                        muestras = (SampleBuffer) decodificador.decodeFrame(cuadro, mp3);
                    }
                }
                salida.drain();
            }
            return true;
        } catch (IOException | JavaLayerException | LineUnavailableException | IllegalArgumentException
                | SecurityException sinAudio) {
            return false;
        } catch (InterruptedException interrumpida) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /** Si la apagaron, frena la salida (sin tirar lo que tenía) y espera a que la prendan. */
    private void pausarMientrasEsteApagada(SourceDataLine salida) throws InterruptedException {
        synchronized (this) {
            if (activa) {
                return;
            }
            salida.stop();
            while (!activa) {
                wait();
            }
        }
        salida.start();
    }
}
