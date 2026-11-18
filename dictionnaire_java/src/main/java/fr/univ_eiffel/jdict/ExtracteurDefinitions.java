package fr.univ_eiffel.jdict;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Extrait les définitions d'une page du Wiktionary.
 * Résultat : type de mot -> liste de définitions, par exemple "nom" -> [...]
 */
public class ExtracteurDefinitions {

    private Langue langue;

    public ExtracteurDefinitions(Langue langue) {
        this.langue = langue;
    }

    /** Renvoie les définitions de la page, ou null s'il n'y en a aucune dans notre langue. */
    public LinkedHashMap<String, ArrayList<String>> extraire(String texte) {
        LinkedHashMap<String, ArrayList<String>> definitions = new LinkedHashMap<>();
        boolean dansLaLangue = false;  // est-ce qu'on est dans la section de notre langue ?
        String typeActuel = null;      // type de mot de la section en cours (null si aucun)

        for (String ligne : texte.split("\n")) {
            if (ligne.startsWith("=")) {
                // C'est un titre : le niveau est le nombre de = au début
                int niveau = compterEgal(ligne, 0);
                String titre = ligne.replace("=", "").trim();
                if (niveau == 2) {
                    dansLaLangue = langue.estSectionLangue(titre);
                    typeActuel = null;
                } else if (dansLaLangue) {
                    typeActuel = langue.typeDeMot(titre);
                }
            } else if (dansLaLangue && typeActuel != null && ligne.startsWith("#")) {
                // On enlève les # du début : "# texte" -> "texte", "#* exemple" -> "* exemple"
                String definition = ligne.replaceFirst("#+", "").trim();
                if (estDefinition(definition)) {
                    if (!definitions.containsKey(typeActuel)) {
                        definitions.put(typeActuel, new ArrayList<>());
                    }
                    definitions.get(typeActuel).add(definition);
                }
            }
        }

        if (definitions.isEmpty()) {
            return null;
        }
        return definitions;
    }

    /** Compte en récursif les = au début de la ligne, à partir de la position i. */
    private int compterEgal(String ligne, int i) {
        if (i == ligne.length() || ligne.charAt(i) != '=') {
            return 0;
        }
        return 1 + compterEgal(ligne, i + 1);
    }

    /** Les exemples commencent par * ou : (après les #), on ne les garde pas. */
    private boolean estDefinition(String definition) {
        return !definition.isEmpty() && !definition.startsWith("*") && !definition.startsWith(":");
    }
}
