package fr.univ_eiffel.jdict;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TimeZone;

/**
 * Écrit le fichier de définitions : une ligne JSON d'en-tête, puis une ligne JSON par mot.
 * On compte les octets écrits pour savoir où commence et où finit chaque ligne.
 */
public class FichierDefinitions {

    // disableHtmlEscaping : sinon Gson écrit ' sous la forme '
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private BufferedOutputStream sortie;
    private long position = 0; // nombre d'octets déjà écrits

    public FichierDefinitions(String chemin, String langue) throws IOException {
        sortie = new BufferedOutputStream(new FileOutputStream(chemin));

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        JsonObject entete = new JsonObject();
        entete.addProperty("description", "definition file");
        entete.addProperty("created_on", format.format(new Date()));
        entete.addProperty("language", langue);
        ecrireLigne(GSON.toJson(entete));
    }

    /** Écrit un mot et ses définitions, renvoie sa place pour l'index. */
    public EntreeIndex ajouter(String mot, LinkedHashMap<String, ArrayList<String>> definitions) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("title", mot);
        json.add("definitions", GSON.toJsonTree(definitions));

        long debut = position;
        ecrireLigne(GSON.toJson(json));
        long fin = position - 1;
        return new EntreeIndex(mot, debut, fin);
    }

    private void ecrireLigne(String ligne) throws IOException {
        // On compte en octets, pas en caractères : "é" prend 2 octets en UTF-8
        byte[] octets = (ligne + "\n").getBytes("UTF-8");
        sortie.write(octets);
        position = position + octets.length;
    }

    public void fermer() throws IOException {
        sortie.close();
    }
}
