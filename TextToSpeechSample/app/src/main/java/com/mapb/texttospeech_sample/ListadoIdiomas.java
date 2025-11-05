package com.mapb.texttospeech_sample;

public class ListadoIdiomas {
    // Array con nombres de los idioma disponibles del ML Kit
    public static String[] idiomasDisponibles = {
            "Afrikáans",
            "Árabe",
            "Bielorruso",
            "Búlgaro",
            "Bengalí",
            "Catalán",
            "Checo",
            "Galés",
            "Danés",
            "Alemán",
            "Griego",
            "Inglés",
            "Esperanto",
            "Español",
            "Estonio",
            "Persa",
            "Finés",
            "Francés",
            "Irlandés",
            "Gallego",
            "Guyaratí",
            "Hebreo",
            "Hindi",
            "Croata",
            "Haitiano",
            "Húngaro",
            "Indonesio",
            "Islandés",
            "Italiano",
            "Japonés",
            "Georgiano",
            "Canarés",
            "Coreano",
            "Lituano",
            "Letón",
            "Macedonio",
            "Maratí",
            "Malayo",
            "Maltés",
            "Neerlandés",
            "Noruego",
            "Polaco",
            "Portugués",
            "Rumano",
            "Ruso",
            "Eslovaco",
            "Esloveno",
            "Albanés",
            "Sueco",
            "Suajili",
            "Tamil",
            "Telugu",
            "Tailandés",
            "Tagalo",
            "Turco",
            "Ucraniano",
            "Urdu",
            "Vietnamita",
            "Chino"
    };
    // Array con los codigos de idioma disponibles del ML Kit
    public static String[] codIdiomasDisponibles = {
            "af",
            "ar",
            "be",
            "bg",
            "bn",
            "ca",
            "cs",
            "cy",
            "da",
            "de",
            "el",
            "en",
            "eo",
            "es",
            "et",
            "fa",
            "fi",
            "fr",
            "ga",
            "gl",
            "gu",
            "he",
            "hi",
            "hr",
            "ht",
            "hu",
            "id",
            "is",
            "it",
            "ja",
            "ka",
            "kn",
            "ko",
            "lt",
            "lv",
            "mk",
            "mr",
            "ms",
            "mt",
            "nl",
            "no",
            "pl",
            "pt",
            "ro",
            "ru",
            "sk",
            "sl",
            "sq",
            "sv",
            "sw",
            "ta",
            "te",
            "th",
            "tl",
            "tr",
            "uk",
            "ur",
            "vi",
            "zh"
    };

    // Metodo estatico que nos devuelve la lista de nombre de idiomas
    public static String[] getListaIdiomasDisponibles() {
        return idiomasDisponibles;
    }

    // Metodo estatico que nos devuelve el tamano el array
    public static int getSize() {
        return idiomasDisponibles.length;
    }

    // Metodo estatico que nos devuelve el codigo de idioma del indice pasado por parametro
    public static String getCodIdioma(int indice) {
        return codIdiomasDisponibles[indice];
    }

    // Metodo estatico que nos el indice el codigo de idioma del indice pasado por parametro
    public static int getIndicePorIdioma(String idioma) {
        for (int i = 0; i < getSize(); i++) {
            if (idioma.equals(idiomasDisponibles[i])) {
                return i;
            }
        }
        return 0;
    }

    // Metodo estatico que nos devuelve el indice en el array del codigo de idioma pasado por parametro
    public static int getIndicePorCodIdioma(String codIdioma) {
        for (int i = 0; i < getSize(); i++) {
            if (codIdioma.equals(codIdiomasDisponibles[i])) {
                return i;
            }
        }
        return 0;
    }

}
