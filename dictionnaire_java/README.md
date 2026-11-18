# SAE Boggle, module L1 bis : gestionnaire de dictionnaire (Java)

## Compiler (fat jar avec toutes les dépendances)
    mvn package
    export CLASSPATH=target/jdict.jar          (Windows : set CLASSPATH=target\jdict.jar)

## Créer le dictionnaire
    cat frwiktionary-latest-pages-articles.xml.bz2 | java fr.univ_eiffel.DictionaryMaker fr definitions
    cat test/mini_fr.xml.bz2 | java fr.univ_eiffel.DictionaryMaker fr definitions    (petit test)

## Chercher un mot
    java fr.univ_eiffel.jdict.DictionarySearcher definitions congres

## Avancement
- Fait : extraction (fr et en), fichier de définitions JSONL, index DICTINDX
- En cours : DictionarySearcher (recherche exacte faite ; reste MAJUSCULES et yaml:)
- En cours : compression zlib (classe Compression faite et testée seule ; pas encore branchée, ni préfixe compressed: ni en-tête "compression")
- À faire : NormalizedExtractor, CharTableMaker, InteractiveUI
