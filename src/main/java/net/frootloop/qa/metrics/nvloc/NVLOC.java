package net.frootloop.qa.metrics.nvloc;

import net.frootloop.qa.parser.JavaSourceFileParser;
import net.frootloop.qa.parser.result.ParsedSourceFile;

import java.nio.file.Path;

public class NVLOC {

    /***
     * Etant donné un fichier source d'une classe java, calcule la métrique de taille
     * NVLOC : nombre de lignes de code non-vides.
     *
     * @param filePathString : (String) chemin du fichier à aller évaluer.
     */
    public static int getAmountOfEmptyLines(String filePathString){
        Path path = Path.of(filePathString.replace('/', '\\').replace(":", ""));
        return NVLOC.getAmountOfEmptyLines(path);
    }

    /***
     * Etant donné un fichier source d'une classe java, calcule la métrique de taille
     * NVLOC : nombre de lignes de code non-vides.
     *
     * @param filePath : (Path) chemin du fichier à aller évaluer.
     */
    public static int getAmountOfEmptyLines(Path filePath){
        ParsedSourceFile parsedFile = JavaSourceFileParser.parse(filePath);
        if(parsedFile == null) {
            System.out.println("Le fichier donné en input à nvloc n'est pas un fichier .java : " + filePath);
            return 0;
        }
        else return parsedFile.numLinesEmpty;
    }

    /***
     * Etant donné un fichier source d'une classe java, imprime la métrique de taille
     * NVLOC : nombre de lignes de code non-vides.
     *
     * @param filePathString : (String) chemin du fichier à aller évaluer.
     */
    public static int printAmountOfEmptyLines(String filePathString){
       return NVLOC.getAmountOfEmptyLines(filePathString);
    }

}
