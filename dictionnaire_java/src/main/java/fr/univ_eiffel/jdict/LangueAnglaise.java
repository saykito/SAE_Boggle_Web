package fr.univ_eiffel.jdict;

/**
 * Wiktionary anglais.
 * Section de la langue : ==English==
 * Section d'un type    : ===Noun=== ou ====Noun====
 */
public class LangueAnglaise implements Langue {

    private static final String[] TYPES = {
        "Noun", "Proper noun", "Verb", "Adjective", "Adverb", "Pronoun", "Preposition",
        "Conjunction", "Interjection", "Determiner", "Article", "Prefix", "Suffix", "Phrase"
    };

    public String code() {
        return "en";
    }

    public boolean estSectionLangue(String titre) {
        return titre.equals("English");
    }

    public String typeDeMot(String titre) {
        for (String type : TYPES) {
            if (titre.equals(type)) {
                return type.toLowerCase();
            }
        }
        return null;
    }
}
