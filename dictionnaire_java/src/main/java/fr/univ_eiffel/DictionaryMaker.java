package fr.univ_eiffel;

import fr.univ_eiffel.jdict.EntreeIndex;
import fr.univ_eiffel.jdict.ExtracteurDefinitions;
import fr.univ_eiffel.jdict.FichierDefinitions;
import fr.univ_eiffel.jdict.Index;
import fr.univ_eiffel.jdict.Langue;
import fr.univ_eiffel.jdict.LangueAnglaise;
import fr.univ_eiffel.jdict.LangueFrancaise;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * cat frwiktionary.xml.bz2 | java fr.univ_eiffel.DictionaryMaker fr definitions
 *
 * Lit le dump sur l'entrée standard, et crée definitions.dict et definitions.index.
 */
public class DictionaryMaker {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage : cat dump.xml.bz2 | java fr.univ_eiffel.DictionaryMaker <fr|en> <nom>");
            System.exit(1);
        }

        // 1. Choix de la langue
        Langue langue;
        if (args[0].equals("fr")) {
            langue = new LangueFrancaise();
        } else if (args[0].equals("en")) {
            langue = new LangueAnglaise();
        } else {
            System.err.println("Langue non gérée : " + args[0]);
            return;
        }
        String nom = args[1];

        ExtracteurDefinitions extracteur = new ExtracteurDefinitions(langue);
        FichierDefinitions fichier = new FichierDefinitions(nom + ".dict", langue.code());
        ArrayList<EntreeIndex> entrees = new ArrayList<>();

        // 2. Lecture du dump : on décompresse le bzip2, puis on lit le XML avec StAX
        // Java limite par défaut les entités XML (&lt; &amp;...) : un dump en a beaucoup trop
        System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
        BZip2CompressorInputStream dump = new BZip2CompressorInputStream(new BufferedInputStream(System.in), true);
        XMLStreamReader xml = XMLInputFactory.newInstance().createXMLStreamReader(dump, "UTF-8");

        String titre = null;
        String namespace = null;

        // StAX donne les balises une par une. On garde le titre et le namespace de la page,
        // et quand on arrive au texte d'une page du namespace 0, on extrait les définitions.
        while (xml.hasNext()) {
            xml.next();
            if (xml.getEventType() == XMLStreamConstants.START_ELEMENT) {
                String balise = xml.getLocalName();

                if (balise.equals("title")) {
                    titre = xml.getElementText();
                } else if (balise.equals("ns")) {
                    namespace = xml.getElementText();
                } else if (balise.equals("text") && namespace.equals("0")) {
                    LinkedHashMap<String, ArrayList<String>> definitions = extracteur.extraire(xml.getElementText());
                    if (definitions != null) {
                        entrees.add(fichier.ajouter(titre, definitions));
                    }
                }
            }
        }
        xml.close();
        fichier.fermer();

        // 3. Une fois tous les mots écrits, on trie et on écrit l'index
        Index.ecrire(nom + ".index", entrees);
        System.out.println(entrees.size() + " mots écrits dans " + nom + ".dict et " + nom + ".index");
    }
}
