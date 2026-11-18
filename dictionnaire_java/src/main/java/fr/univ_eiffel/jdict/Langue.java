package fr.univ_eiffel.jdict;

/**
 * Ce qui change d'une langue à l'autre dans le Wiktionary.
 * Pour ajouter une langue, on écrit une nouvelle classe qui implémente cette interface.
 */
public interface Langue {

    /** Code de la langue : "fr" ou "en". */
    String code();

    /** Vrai si le titre d'une section de niveau 2 (== ... ==) est celui de notre langue. */
    boolean estSectionLangue(String titre);

    /** Le type de mot (nom, verbe...) si le titre de section en est un, sinon null. */
    String typeDeMot(String titre);
}
