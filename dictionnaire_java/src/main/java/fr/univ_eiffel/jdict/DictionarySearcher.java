package fr.univ_eiffel.jdict;

/**
 * java fr.univ_eiffel.jdict.DictionarySearcher definitions congres
 *
 * EN COURS : la recherche d'un mot exact marche.
 * Reste à faire : mot en MAJUSCULES (toutes les formes), option yaml:, compression.
 */
public class DictionarySearcher {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage : java fr.univ_eiffel.jdict.DictionarySearcher <dictionnaire> <mot>");
            System.exit(1);
        }
        Dictionnaire dictionnaire = new Dictionnaire(args[0]);
        String resultat = dictionnaire.chercher(args[1]);
        dictionnaire.fermer();

        if (resultat == null) {
            System.out.println("Mot introuvable : " + args[1]);
        } else {
            System.out.println(resultat);
        }
    }
}
