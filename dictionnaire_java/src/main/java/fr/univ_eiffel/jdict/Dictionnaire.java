package fr.univ_eiffel.jdict;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Un dictionnaire déjà créé : nom.dict (définitions) et nom.index (index).
 * RandomAccessFile permet d'aller lire directement à une position du fichier.
 */
public class Dictionnaire {

    private RandomAccessFile definitions;
    private RandomAccessFile index;
    private int nombreDeMots;

    public Dictionnaire(String nom) throws IOException {
        definitions = new RandomAccessFile(nom + ".dict", "r");
        index = new RandomAccessFile(nom + ".index", "r");

        byte[] entete = new byte[8];
        index.readFully(entete);
        if (!new String(entete, "US-ASCII").equals(Index.ENTETE)) {
            throw new IOException(nom + ".index n'est pas un fichier d'index");
        }
        nombreDeMots = (int) ((index.length() - 8) / 8);
    }

    /** Ligne JSON du mot numéro i (dans l'ordre de l'index). */
    public String ligne(int i) throws IOException {
        index.seek(8 + 8L * i);       // 8 octets d'en-tête, puis 8 octets par mot
        int debut = index.readInt();
        int fin = index.readInt();

        byte[] octets = new byte[fin - debut + 1];
        definitions.seek(debut);
        definitions.readFully(octets);
        return new String(octets, "UTF-8").trim();
    }

    /** Le mot de la ligne numéro i. */
    public String mot(int i) throws IOException {
        JsonObject json = JsonParser.parseString(ligne(i)).getAsJsonObject();
        return json.get("title").getAsString();
    }

    /** Ligne JSON du mot cherché, ou null s'il n'existe pas. */
    public String chercher(String mot) throws IOException {
        int i = dichotomie(mot, 0, nombreDeMots - 1);
        if (i == -1) {
            return null;
        }
        return ligne(i);
    }

    /**
     * Recherche par dichotomie entre les mots numéro bas et haut.
     * On compare le mot du milieu au mot cherché, puis on recommence
     * sur la moitié où il peut se trouver.
     */
    private int dichotomie(String mot, int bas, int haut) throws IOException {
        if (bas > haut) {
            return -1; // plus rien à regarder : le mot n'est pas là
        }
        int milieu = (bas + haut) / 2;
        int comparaison = Normalisation.comparer(mot(milieu), mot);

        if (comparaison == 0) {
            return milieu;
        } else if (comparaison < 0) {
            return dichotomie(mot, milieu + 1, haut); // le mot du milieu est avant : on va à droite
        } else {
            return dichotomie(mot, bas, milieu - 1);  // le mot du milieu est après : on va à gauche
        }
    }

    public void fermer() throws IOException {
        definitions.close();
        index.close();
    }
}
