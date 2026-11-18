package fr.univ_eiffel.jdict;

import java.text.Normalizer;

/** Forme normalisée d'un mot et ordre des mots dans l'index. */
public class Normalisation {

    /** "congrès" -> "CONGRES" : majuscules et sans accents. */
    public static String normaliser(String mot) {
        // NFD coupe "è" en deux caractères : "e" puis l'accent seul
        String decoupe = Normalizer.normalize(mot, Normalizer.Form.NFD);
        // \p{M} = les accents seuls, on les enlève
        String sansAccents = decoupe.replaceAll("\\p{M}", "");
        return sansAccents.toUpperCase();
    }

    /**
     * Ordre du sujet : d'abord la forme normalisée, puis en cas d'égalité
     * l'ordre normal des String. Négatif si mot1 est avant mot2.
     */
    public static int comparer(String mot1, String mot2) {
        int resultat = normaliser(mot1).compareTo(normaliser(mot2));
        if (resultat == 0) {
            resultat = mot1.compareTo(mot2);
        }
        return resultat;
    }
}
