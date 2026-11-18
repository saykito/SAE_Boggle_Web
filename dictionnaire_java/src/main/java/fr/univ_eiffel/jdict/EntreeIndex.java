package fr.univ_eiffel.jdict;

/** Un mot et la position de sa ligne dans le fichier de définitions. */
public class EntreeIndex {

    public String mot;
    public long debut; // premier octet de la ligne
    public long fin;   // dernier octet de la ligne

    public EntreeIndex(String mot, long debut, long fin) {
        this.mot = mot;
        this.debut = debut;
        this.fin = fin;
    }
}
