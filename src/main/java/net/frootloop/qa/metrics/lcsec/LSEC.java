package net.frootloop.qa.metrics.lcsec;

import net.frootloop.qa.metrics.parser.JavaSourceFileParser;
import net.frootloop.qa.metrics.parser.result.ParsedSourceFile;

public class LSEC {

    /***
     * Etant donné un fichier source d'une classe java, calcule la métrique de taille
     * NVLOC : nombre de lignes de code non-vides. Il doit juste sortir la valeur du NVLOC
     * à la ligne de commandes
     */
    // NVLOC : nombre de lignes de code non-vides. Il doit juste sortir la valeur du NVLOC à la ligne de commandes
    public static void printAmountOfEmptyLines(String filePath){
        ParsedSourceFile parsedFile = JavaSourceFileParser.parse(filePath);
        if(parsedFile == null)
            System.out.println("Le fichier donné en input à nvloc n'est pas un fichier .java : " + filePath);
        else
            System.out.println(parsedFile.numLinesEmpty);
    }

}
