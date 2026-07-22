package com.mapb.texttospeech_sample;

/**
 * Catalogo de idiomas soportados por ML Kit (traduccion e identificacion de idioma).
 * Los arrays son privados: el acceso externo se hace siempre a traves de los
 * metodos estaticos, para no exponer la representacion interna.
 */
public class ListadoIdiomas {

    private static final String[] NOMBRES_IDIOMAS = {
            "Afrikáans", "Árabe", "Bielorruso", "Búlgaro", "Bengalí", "Catalán",
            "Checo", "Galés", "Danés", "Alemán", "Griego", "Inglés", "Esperanto",
            "Español", "Estonio", "Persa", "Finés", "Francés", "Irlandés", "Gallego",
            "Guyaratí", "Hebreo", "Hindi", "Croata", "Haitiano", "Húngaro", "Indonesio",
            "Islandés", "Italiano", "Japonés", "Georgiano", "Canarés", "Coreano",
            "Lituano", "Letón", "Macedonio", "Maratí", "Malayo", "Maltés", "Neerlandés",
            "Noruego", "Polaco", "Portugués", "Rumano", "Ruso", "Eslovaco", "Esloveno",
            "Albanés", "Sueco", "Suajili", "Tamil", "Telugu", "Tailandés", "Tagalo",
            "Turco", "Ucraniano", "Urdu", "Vietnamita", "Chino"
    };

    // Mismo orden que NOMBRES_IDIOMAS: codigo ISO 639-1 de cada idioma.
    private static final String[] CODIGOS_IDIOMAS = {
            "af", "ar", "be", "bg", "bn", "ca", "cs", "cy", "da", "de", "el", "en",
            "eo", "es", "et", "fa", "fi", "fr", "ga", "gl", "gu", "he", "hi", "hr",
            "ht", "hu", "id", "is", "it", "ja", "ka", "kn", "ko", "lt", "lv", "mk",
            "mr", "ms", "mt", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sq",
            "sv", "sw", "ta", "te", "th", "tl", "tr", "uk", "ur", "vi", "zh"
    };

    private ListadoIdiomas() {
        // Clase de utilidades: no se instancia.
    }

    /** Devuelve el listado de nombres de idioma, en el mismo orden que sus codigos. */
    public static String[] getListaIdiomasDisponibles() {
        return NOMBRES_IDIOMAS;
    }

    public static int getSize() {
        return NOMBRES_IDIOMAS.length;
    }

    /** Codigo ISO del idioma que ocupa la posicion indicada. */
    public static String getCodIdioma(int indice) {
        return CODIGOS_IDIOMAS[indice];
    }

    /** Posicion en el listado del codigo de idioma indicado, o 0 si no se encuentra. */
    public static int getIndicePorCodIdioma(String codIdioma) {
        for (int i = 0; i < CODIGOS_IDIOMAS.length; i++) {
            if (CODIGOS_IDIOMAS[i].equals(codIdioma)) {
                return i;
            }
        }
        return 0;
    }
}
