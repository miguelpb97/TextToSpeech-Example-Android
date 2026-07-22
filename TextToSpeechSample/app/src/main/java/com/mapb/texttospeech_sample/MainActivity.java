package com.mapb.texttospeech_sample;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.mapb.texttospeech_sample.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Pantalla unica de la app: permite dictar o escribir un texto, traducirlo
 * (con deteccion automatica de idioma opcional via ML Kit) y reproducir la
 * traduccion con el motor TTS del sistema.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String IDIOMA_ORIGEN_POR_DEFECTO = "es";
    private static final String IDIOMA_DESTINO_POR_DEFECTO = "en";

    private ActivityMainBinding binding;
    private final TTSManager ttsManager = new TTSManager();

    private Locale idiomaOrigenElegido;
    private Locale idiomaTraduccionElegido;

    private final ActivityResultLauncher<Intent> reconocimientoVozLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onResultadoVoz);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Valores por defecto: idioma origen espanol, idioma destino ingles.
        // Se fijan ya aqui (y no solo al recibir el evento del spinner) para
        // que nunca queden a null aunque el listener del spinner no llegue a
        // dispararse antes de que el usuario pulse un boton.
        idiomaOrigenElegido = new Locale(IDIOMA_ORIGEN_POR_DEFECTO);
        idiomaTraduccionElegido = new Locale(IDIOMA_DESTINO_POR_DEFECTO);

        configurarSpinnersDeIdioma();
        configurarBotones();

        inicializarTts(idiomaTraduccionElegido);
    }

    private void configurarSpinnersDeIdioma() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.list_items_idiomas, ListadoIdiomas.getListaIdiomasDisponibles());
        adapter.setDropDownViewResource(R.layout.list_items_idiomas);

        binding.spinnerIdiomaOrigen.setAdapter(adapter);
        binding.spinnerIdiomaTraduccion.setAdapter(adapter);

        binding.spinnerIdiomaOrigen.setSelection(ListadoIdiomas.getIndicePorCodIdioma(IDIOMA_ORIGEN_POR_DEFECTO));
        binding.spinnerIdiomaTraduccion.setSelection(ListadoIdiomas.getIndicePorCodIdioma(IDIOMA_DESTINO_POR_DEFECTO));

        binding.spinnerIdiomaOrigen.setOnItemSelectedListener(new SimpleItemSelectedListener(position ->
                idiomaOrigenElegido = new Locale(ListadoIdiomas.getCodIdioma(position))));

        binding.spinnerIdiomaTraduccion.setOnItemSelectedListener(new SimpleItemSelectedListener(position -> {
            idiomaTraduccionElegido = new Locale(ListadoIdiomas.getCodIdioma(position));
            inicializarTts(idiomaTraduccionElegido);
        }));
    }

    private void inicializarTts(Locale locale) {
        ttsManager.init(this, locale, success -> {
            if (!success) {
                runOnUiThread(() -> Toast.makeText(this, R.string.error_tts_no_listo, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void configurarBotones() {
        binding.botonReproducir.setOnClickListener(v -> reproducirTraduccion());
        binding.botonTraducir.setOnClickListener(v -> onPulsarTraducir());
        binding.botonCapturarVoz.setOnClickListener(v -> {
            binding.etTextoATraducir.setText("");
            iniciarEntradaVoz(idiomaOrigenElegido);
        });
    }

    private void reproducirTraduccion() {
        String texto = binding.etTextoTraducido.getText().toString();
        if (texto.trim().isEmpty()) {
            return;
        }
        if (!ttsManager.isReady()) {
            Toast.makeText(this, R.string.error_tts_no_listo, Toast.LENGTH_SHORT).show();
            return;
        }
        ttsManager.speakNow(texto);
    }

    private void onPulsarTraducir() {
        String textoOrigen = binding.etTextoATraducir.getText().toString();
        if (textoOrigen.trim().isEmpty()) {
            Toast.makeText(this, R.string.error_texto_vacio, Toast.LENGTH_SHORT).show();
            return;
        }

        if (binding.switchDetectarIdiomaAuto.isChecked()) {
            // Esperamos a que ML Kit detecte el idioma antes de traducir: si
            // lanzabamos la traduccion en paralelo (como hacia la version
            // anterior), casi siempre se traducia con el idioma de origen
            // previo, porque la deteccion es asincrona y aun no habia terminado.
            LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
            languageIdentifier.identifyLanguage(textoOrigen)
                    .addOnSuccessListener(languageCode -> {
                        if ("und".equals(languageCode)) {
                            Toast.makeText(this, R.string.error_reconocer_idioma, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        idiomaOrigenElegido = new Locale(languageCode);
                        binding.spinnerIdiomaOrigen.setSelection(ListadoIdiomas.getIndicePorCodIdioma(languageCode));
                        traducirTexto(textoOrigen);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al reconocer el idioma de origen", e);
                        Toast.makeText(this, R.string.error_reconocer_idioma, Toast.LENGTH_SHORT).show();
                    });
        } else {
            traducirTexto(textoOrigen);
        }
    }

    private void traducirTexto(String textoOrigen) {
        TranslatorOptions opcionesTraductor = new TranslatorOptions.Builder()
                .setSourceLanguage(idiomaOrigenElegido.getLanguage())
                .setTargetLanguage(idiomaTraduccionElegido.getLanguage())
                .build();

        Translator traductor = Translation.getClient(opcionesTraductor);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        traductor.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(unused -> traductor.translate(textoOrigen)
                        .addOnSuccessListener(traduccion -> binding.etTextoTraducido.setText(traduccion))
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error al traducir", e);
                            Toast.makeText(this, R.string.error_traducir, Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al descargar el modelo de traduccion", e);
                    Toast.makeText(this, R.string.error_descargar_modelo, Toast.LENGTH_SHORT).show();
                });
    }

    private void iniciarEntradaVoz(Locale locale) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
        try {
            reconocimientoVozLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "No hay ninguna app de reconocimiento de voz disponible", e);
            Toast.makeText(this, R.string.error_reconocimiento_voz_no_disponible, Toast.LENGTH_SHORT).show();
        }
    }

    private void onResultadoVoz(ActivityResult result) {
        if (result.getResultCode() != RESULT_OK || result.getData() == null) {
            return;
        }
        ArrayList<String> resultados = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (resultados != null && !resultados.isEmpty()) {
            binding.etTextoATraducir.setText(resultados.get(0));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
        binding = null;
    }
}
