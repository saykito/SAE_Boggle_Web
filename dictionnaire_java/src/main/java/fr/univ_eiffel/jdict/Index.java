package fr.univ_eiffel.jdict;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Fichier d'index : "DICTINDX", puis pour chaque mot (triés) deux entiers sur 4 octets :
 * le début et la fin de sa ligne. Chaque mot prend donc 8 octets dans l'index.
 */
public class Index {

    public static final String ENTETE = "DICTINDX";

    public static void ecrire(String chemin, ArrayList<EntreeIndex> entrees) throws IOException {
        entrees.sort(new Comparator<EntreeIndex>() {
            public int compare(EntreeIndex a, EntreeIndex b) {
                return Normalisation.comparer(a.mot, b.mot);
            }
        });

        // writeInt écrit les 4 octets en grand-boutiste (octet de poids fort en premier)
        DataOutputStream sortie = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(chemin)));
        sortie.writeBytes(ENTETE);
        for (EntreeIndex entree : entrees) {
            sortie.writeInt((int) entree.debut);
            sortie.writeInt((int) entree.fin);
        }
        sortie.close();
    }
}
