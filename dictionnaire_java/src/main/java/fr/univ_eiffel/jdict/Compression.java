package fr.univ_eiffel.jdict;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Compression zlib d'une ligne du fichier de définitions.
 *
 * EN COURS : compresser et décompresser une ligne marche.
 * Reste à faire : l'activer avec le préfixe "compressed:", écrire "compression": "zlib"
 * dans l'en-tête, et l'utiliser dans FichierDefinitions et Dictionnaire.
 */
public class Compression {

    /** Compresse des octets avec Deflater (zlib). */
    public static byte[] compresser(byte[] donnees) {
        Deflater deflater = new Deflater();
        deflater.setInput(donnees);
        deflater.finish(); // on a tout donné, il peut terminer la compression

        ByteArrayOutputStream resultat = new ByteArrayOutputStream();
        compresserMorceaux(deflater, new byte[1024], resultat);
        deflater.end();
        return resultat.toByteArray();
    }

    /** Récupère le résultat 1024 octets à la fois, en récursif, jusqu'à la fin. */
    private static void compresserMorceaux(Deflater deflater, byte[] tampon, ByteArrayOutputStream resultat) {
        if (deflater.finished()) {
            return;
        }
        int taille = deflater.deflate(tampon);
        resultat.write(tampon, 0, taille);
        compresserMorceaux(deflater, tampon, resultat);
    }

    /** Décompresse des octets avec Inflater : on retrouve les octets de départ. */
    public static byte[] decompresser(byte[] donnees) throws DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(donnees);

        ByteArrayOutputStream resultat = new ByteArrayOutputStream();
        decompresserMorceaux(inflater, new byte[1024], resultat);
        inflater.end();
        return resultat.toByteArray();
    }

    private static void decompresserMorceaux(Inflater inflater, byte[] tampon, ByteArrayOutputStream resultat)
            throws DataFormatException {
        if (inflater.finished()) {
            return;
        }
        int taille = inflater.inflate(tampon);
        if (taille == 0 && inflater.needsInput()) {
            throw new DataFormatException("Données compressées incomplètes");
        }
        resultat.write(tampon, 0, taille);
        decompresserMorceaux(inflater, tampon, resultat);
    }
}
