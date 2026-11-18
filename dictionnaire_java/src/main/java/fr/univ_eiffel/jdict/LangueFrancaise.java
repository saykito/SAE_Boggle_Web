package fr.univ_eiffel.jdict;

/**
 * Wiktionary français.
 * Section de la langue : == {{langue|fr}} ==
 * Section d'un type    : === {{S|nom|fr}} ===
 */
public class LangueFrancaise implements Langue {

    private static final String[] TYPES = {
        "nom", "verbe", "adjectif", "adverbe", "pronom", "préposition", "conjonction",
        "interjection", "article", "locution", "onomatopée", "préfixe", "suffixe", "prénom"
    };

    public String code() {
        return "fr";
    }

    public boolean estSectionLangue(String titre) {
        return titre.equals("{{langue|fr}}");
    }

    public String typeDeMot(String titre) {
        if (!titre.startsWith("{{S|")) {
            return null;
        }
        // "{{S|nom propre|fr}}" -> on garde ce qu'il y a entre "{{S|" et le "|" suivant
        String nom = titre.substring(4);
        int fin = nom.indexOf('|');
        if (fin == -1) {
            fin = nom.indexOf('}');
        }
        nom = nom.substring(0, fin);

        for (String type : TYPES) {
            if (nom.startsWith(type)) { // "nom propre" commence par "nom"
                return nom;
            }
        }
        return null; // étymologie, traductions... : ce n'est pas un type de mot
    }
}
