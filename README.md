# TextToSpeech Sample (Android)

Una app pequeña de Android para practicar con reconocimiento de voz, traducción automática y texto a voz, todo en un mismo flujo. La hice como proyecto de aprendizaje, no busques nada del otro mundo, pero funciona bien y me sirvió para meterle mano a varias APIs de Android que tenía pendientes: `SpeechRecognizer`, `TextToSpeech` y ML Kit.

## Qué hace

1. Dictas o escribes un texto.
2. Eliges idioma de origen y de destino (o dejas que la app detecte el idioma automáticamente).
3. Traduce el texto con ML Kit.
4. Reproduce la traducción en voz alta.

Nada más, nada menos. La idea era tener algo tipo "traductor de bolsillo" rápido, sin depender de conexión salvo para descargar los modelos de traducción la primera vez.

## Capturas

_(pendiente de subir un par de capturas, de momento tira del código o pruébala tú mismo)_

## Stack

- Java
- Android SDK (minSdk 24, compileSdk/targetSdk 36)
- ML Kit Translate + Language Identification
- `android.speech.tts.TextToSpeech` para la síntesis de voz
- `RecognizerIntent` para captar la voz
- Material Components 3 para la interfaz
- ViewBinding

## Cómo lo monté

Es un proyecto de Android Studio estándar (Gradle con Kotlin DSL). Solo hace falta:

1. Clonar el repo.
2. Abrirlo con Android Studio.
3. Dejar que sincronice el Gradle.
4. Ejecutar en un emulador o dispositivo físico.

El dispositivo/emulador necesita tener instalado algo capaz de manejar `RecognizerIntent.ACTION_RECOGNIZE_SPEECH` (en un dispositivo normal con Google Play Services no debería haber problema) y conexión a internet la primera vez que uses un par de idiomas nuevo, porque ML Kit se descarga el modelo de traducción bajo demanda.

## Cosas que sé que se pueden mejorar

- No hay tests más allá de los que trae la plantilla por defecto de Android Studio.
- La lista de idiomas está hardcodeada en un par de arrays paralelos; en algún momento debería pasar esto a un enum o a una clase con nombre + código en vez de dos arrays que tienen que ir sincronizados a mano.
- No hay manejo de rotación de pantalla pensado en serio (el estado de los EditText y spinners se pierde en algunos casos).
- Falta feedback visual mientras se descarga el modelo de traducción o mientras habla el TTS (ahora mismo no hay ningún loader).
- El diseño es funcional, sin más pretensiones.
