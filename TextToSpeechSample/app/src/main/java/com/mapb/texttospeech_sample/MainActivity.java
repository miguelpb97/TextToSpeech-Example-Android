package com.mapb.texttospeech_sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private Button botonTraducirVoz, botonTraducir, botonReproducir;
    private EditText etTextoATraducir, etTextoTraducido;
    private Spinner spinnerListaIdiomaOrigen, spinnerListaIdiomaTraduccion;
    private Switch swAuto;
    private TTSManager ttsManager = null;
    private Locale idiomaOrigenElegido;
    private Locale idiomaTraduccionElegido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asignamos los componentes de la vista
        spinnerListaIdiomaOrigen = findViewById(R.id.spinner_idioma_origen);
        spinnerListaIdiomaTraduccion = findViewById(R.id.spinner_idioma_traduccion);
        botonTraducir = findViewById(R.id.boton_traducir);
        botonReproducir = findViewById(R.id.boton_reproducir);
        etTextoATraducir = findViewById(R.id.et_texto_a_traducir);
        etTextoTraducido = findViewById(R.id.et_texto_traducido);
        swAuto = findViewById(R.id.switch_detectar_idioma_auto);
        botonTraducirVoz = findViewById(R.id.boton_capturar_voz);

        // Cramos un array adapter de string donde asignaremos la lista de idiomas deisponibles de mlkit y posteriormente asignaremos a los spinner que nos muestran el idioma origen y destino
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ListadoIdiomas.getListaIdiomasDisponibles());
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

        // Asignamos el arrayAdapter de la lista de idiomas y los setteamos a los spinners encargados de mostrarnos la lista
        spinnerListaIdiomaOrigen.setAdapter(adapter);
        spinnerListaIdiomaTraduccion.setAdapter(adapter);

        // Establezco el item de la lista que nos muestra, en mi caso he puesto para la lista de idioma origen español(posicion 13 del array del listado de idiomas) e idioma de traduccion ingles(posicion 11)
        // Mediante el metodo estatico getIndicePorCodIdioma de la clase ListadoIdiomas le pasamos al spinner la posicion segun su codigo de idioma
        spinnerListaIdiomaOrigen.setSelection(ListadoIdiomas.getIndicePorCodIdioma("es"));
        spinnerListaIdiomaTraduccion.setSelection(ListadoIdiomas.getIndicePorCodIdioma("en"));

        // Al pulsar el spinner encargado de seleccionar el idioma de origen para la traduccion
        spinnerListaIdiomaOrigen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Le pasamos a la variable idiomaOrigenElegido un locale con el idioma en cuestion, cogemos la posicion de la lista, la pasamos por el metodo, este nos devuelve el codigo de idioma que asignaremos a dicho objeto locale
                idiomaOrigenElegido = new Locale(ListadoIdiomas.getCodIdioma(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Accion cuando no se seleciona ningun item de la lista
                // Aqui lo dejamos vacio
            }
        });

        // Al pulsar el spinner encargado de seleccionar el idioma de traduccion
        spinnerListaIdiomaTraduccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Le pasamos a la variable idiomaTraduccionElegido un locale con el idioma en cuestion, cogemos la posicion de la lista, la pasamos por el metodo, este nos devuelve el codigo de idioma que asignaremos a dicho objeto locale
                idiomaTraduccionElegido = new Locale(ListadoIdiomas.getCodIdioma(position));
                // Creamos un objeto TTSManager, clase encargada de realizar el text to speech
                ttsManager = new TTSManager();
                // Le pasamos el idioma de traduccion
                ttsManager.init(MainActivity.this, idiomaTraduccionElegido);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Accion cuando no se seleciona ningun item de la lista
                // Aqui lo dejamos vacio
            }
        });

        // Al pulsar el boton de reproducir
        botonReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Le pasamos el contenido del edit text del texto traducido al metodo init de la clase encargada del text to speech
                ttsManager.initQueue(etTextoTraducido.getText().toString());
            }
        });

        // Al pulsar el boton de traducir
        botonTraducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Si el switch de automatico esta marcado
                if (swAuto.isChecked()) {
                    // Creamos un identificar de lenguaje para
                    LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
                    // Le pasamos el edit text que contiene el texto de origen para traducir
                    languageIdentifier.identifyLanguage(etTextoATraducir.getText().toString())
                            .addOnSuccessListener(
                                    new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(@Nullable String languageCode) {
                                            // Cambiamos el idioma de origenl y asignamos al spinner el idioma en caso de que lo reconozca
                                            idiomaOrigenElegido = new Locale(languageCode);
                                            spinnerListaIdiomaOrigen.setSelection(ListadoIdiomas.getIndicePorCodIdioma(languageCode));

                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // En caso contrario pasamos un error al logcat
                                            Log.e("Error: ", "Error al reconocer el idioma de origen");
                                        }
                                    });
                    // Si hay contenido en el edittext de la traduccion
                    if (!etTextoTraducido.getText().toString().isEmpty()) {
                        // Se vacia el contenido, ya que vamos a insertar texto ahi
                        etTextoTraducido.setText("");
                        // Se llama al metodo encargado de descargar el modelo, traducir el texto y setearlo en el edit text
                        traducirTexto();
                    } else {
                        // Se llama al metodo encargado de descargar el modelo, traducir el texto y setearlo en el edit text
                        traducirTexto();
                    }
                    // Si el switch de automatico no esta activado
                } else if (!swAuto.isChecked()) {
                    // Si hay contenido en el edittext de la traduccion
                    if (!etTextoTraducido.getText().toString().isEmpty()) {
                        // Se vacia el contenido, ya que vamos a insertar texto ahi
                        etTextoTraducido.setText("");
                        // Se llama al metodo encargado de descargar el modelo, traducir el texto y setearlo en el edit text
                        traducirTexto();
                    } else {
                        // Se llama al metodo encargado de descargar el modelo, traducir el texto y setearlo en el edit text
                        traducirTexto();
                    }
                }
            }
        });

        // Al pulsar el boton de traducir voz o capturar voz
        botonTraducirVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Al comenzar el reconocimiento de voz, se vacia el edit text del texto origen ya que se presupone que vamos a traducir lo que reconozcamos por voz
                etTextoATraducir.setText("");
                // Llamamos al metodo encargado del reconocimiento de voz
                iniciarEntradaVoz(idiomaOrigenElegido);
            }
        });
    }

    //
    private void traducirTexto() {
        // Creamos un TranslatorOptions y le pasamos el idioma origen y destino elegido
        TranslatorOptions opcionesTraductor = new TranslatorOptions.Builder()
                .setSourceLanguage(idiomaOrigenElegido.getLanguage())
                .setTargetLanguage(idiomaTraduccionElegido.getLanguage())
                .build();

        // Pasamos por parametro las opciones al traductor mediante el metodo Translation,getClient(opcionesTraductor)
        Translator traductor = Translation.getClient(opcionesTraductor);

        // Establecemos un DownloadConditions, encargado de descargar el modelo para la traduccion entre dos idiomas
        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        // Descargamos el modelo si es necesario
        traductor.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Si se descarga correctammente, traducimos el texto del edit text de texto a traducir
                        traductor.translate(etTextoATraducir.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        // Establecemos el texto traducido a su correspondiente edittext
                                        etTextoTraducido.setText(s);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // En caso de fallar passamos un mensage de error al Logcat
                                        Log.e("Error: ", "Error al traducir.");
                                    }
                                });
                    }
                })
                // En caso de fallar la descarga del modelo
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Error: ", "Error al descargar el modelo.");
                    }
                });
    }

    // Metodo encargado de ccrear un intent para reconocimiento de voz y llamar a un start activity for resultt que finalmente reconocozca el texto y lo establezca en el edittext correspondiente
    public void iniciarEntradaVoz(Locale locale) {
        // Creamos un Intent RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
        // Iniciamos el starAtivityforResult que recogera la voz
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Log.e("Error: ", e.getMessage());
        }
    }

    // Metodo encargado de reconocer la voz y pasarla a texto al edit text de texto a traducir
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etTextoATraducir.setText(result.get(0));
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // CUando se destruya la activity hacemos shutdown del tts manager
        ttsManager.shutDown();
    }
}