package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Reproduce los sonidos de los personajes: archivos WAV cortos en los recursos (gui/sonidos/). Suena uno
 * por vez: si se pide otro antes de que termine, el anterior se corta. Si la máquina no tiene salida de
 * audio (el CI, por ejemplo) o el archivo no está, no suena nada: un sonido nunca rompe la partida.
 */
public final class ReproductorDeSonidos {

    private static final String CARPETA = "sonidos/";

    private Optional<Clip> sonando = Optional.empty();
    private boolean activo = true; // se apaga con la casilla "Con sonido" del menú

    /** Reproduce el sonido con ese nombre de archivo, por ejemplo "yoda.wav". */
    void reproducir(String nombre) {
        detener();
        if (!activo) {
            return;
        }
        URL archivo = ReproductorDeSonidos.class.getResource(CARPETA + nombre);
        if (archivo == null) {
            return;
        }
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(archivo)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.addLineListener(evento -> {
                if (evento.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
            sonando = Optional.of(clip);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException
                | IllegalArgumentException | SecurityException sinAudio) {
            sonando = Optional.empty();
        }
    }

    /** Prende o apaga los sonidos. Apagarlos corta el que esté sonando. */
    void activar(boolean prendido) {
        activo = prendido;
        if (!prendido) {
            detener();
        }
    }

    boolean activo() {
        return activo;
    }

    /** Corta el sonido que esté sonando, si hay uno. */
    void detener() {
        sonando.ifPresent(Clip::stop);
        sonando = Optional.empty();
    }
}
