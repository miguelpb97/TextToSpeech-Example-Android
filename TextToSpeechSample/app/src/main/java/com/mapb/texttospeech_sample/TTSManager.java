package com.mapb.texttospeech_sample;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Envuelve al motor {@link TextToSpeech} de Android para simplificar su uso:
 * inicialización, cambio de idioma y reproducción en cola.
 *
 * El motor se crea una única vez por instancia de {@link TTSManager}; si se
 * pide cambiar de idioma, se reutiliza el mismo engine en lugar de crear uno
 * nuevo (crear un TextToSpeech por cada cambio de idioma, como hacía la
 * versión anterior, filtraba recursos nativos al no liberarse nunca).
 */
public class TTSManager {

    private static final String TAG = "TTSManager";

    /** Notifica cuando el motor TTS termina de (re)inicializarse. */
    public interface OnReadyListener {
        void onReady(boolean success);
    }

    private TextToSpeech textToSpeech;
    private boolean isReady = false;
    private Locale idioma;
    private OnReadyListener readyListener;

    /**
     * Inicializa el motor TTS la primera vez que se llama. En llamadas
     * posteriores solo actualiza el idioma del motor ya existente.
     */
    public void init(Context context, Locale locale, OnReadyListener listener) {
        this.idioma = locale;
        this.readyListener = listener;

        if (textToSpeech != null) {
            aplicarIdioma();
            return;
        }

        try {
            textToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    aplicarIdioma();
                } else {
                    isReady = false;
                    Log.e(TAG, "Fallo al inicializar el motor TTS.");
                    notifyReady(false);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Excepcion al crear TextToSpeech: " + e.getMessage(), e);
            notifyReady(false);
        }
    }

    private void aplicarIdioma() {
        if (textToSpeech == null || idioma == null) {
            return;
        }
        int result = textToSpeech.setLanguage(idioma);
        isReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED;
        if (!isReady) {
            Log.e(TAG, "Idioma no soportado o datos no disponibles: " + idioma);
        }
        notifyReady(isReady);
    }

    private void notifyReady(boolean success) {
        if (readyListener != null) {
            readyListener.onReady(success);
        }
    }

    public boolean isReady() {
        return isReady;
    }

    /** Añade texto a la cola de reproduccion sin interrumpir lo que se este reproduciendo. */
    public void addQueue(String texto) {
        if (!isReady || textToSpeech == null) {
            Log.e(TAG, "No se pudo encolar: el TTS no esta listo.");
            return;
        }
        textToSpeech.speak(texto, TextToSpeech.QUEUE_ADD, null, null);
    }

    /** Reproduce el texto inmediatamente, descartando lo que hubiera en cola. */
    public void speakNow(String texto) {
        if (!isReady || textToSpeech == null) {
            Log.e(TAG, "No se pudo reproducir: el TTS no esta listo.");
            return;
        }
        textToSpeech.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    /** Libera los recursos del motor TTS. Es seguro llamarlo aunque nunca se haya inicializado. */
    public void shutDown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        isReady = false;
    }
}
