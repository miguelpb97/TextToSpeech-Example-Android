package com.mapb.texttospeech_sample;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSManager {
    private TextToSpeech mTts = null;
    private boolean isLoaded = false;
    private Locale idioma;

    // Metodo para inicializar el tts, en mi caso añadi por parametro el idioma de destino con un objeto de tipo locale que pasaremos desde el main activity
    public void init(Context context, Locale locale) {
        try {
            this.idioma = locale;
            mTts = new TextToSpeech(context, onInitListener);
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
    }

    // oninitListener para inizializar el TTS
    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTts.setLanguage(idioma);
                isLoaded = true;
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("Error: ", "Idioma no cargado.");
                }
            } else {
                Log.e("Error: ", "Fallo al inicializar.");
            }
        }
    };

    // Metodo para apagar el Text to speech
    public void shutDown() {
        mTts.shutdown();
    }

    // Metodo para agregar a la cola de reproduccion un texto
    public void addQueue(String texto) {
        if (isLoaded) {
            mTts.speak(texto, TextToSpeech.QUEUE_ADD, null);
        } else {
            Log.e("Error: ", "Fallo al  el TTS.");
        }
    }

    // Metodo para inicializar la cola de reproduccion
    public void initQueue(String texto) {
        if (isLoaded) {
            mTts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Log.e("Error: ", "Fallo al inicializar el TTS.");
        }
    }

}
