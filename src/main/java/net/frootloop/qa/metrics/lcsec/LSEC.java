package net.frootloop.qa.metrics.lcsec;

import net.frootloop.qa.metrics.jls.JLS;
import net.frootloop.qa.parser.JavaRepositoryParser;
import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.result.ParsedRepository;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class LSEC {

    public static void main(String[] args) throws IOException {

        System.out.println("SVP entrer un chemin de dossier (à partir d'ou ce fichier .JAR est localisé): ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine();

        LSEC.print(input);
    }


    /**
     * Prend comme entrée le chemin d’un dossier, et un fichier CSV avec la sortie du jls
     * pour le même dossier, et produise en sortie les mêmes données que dans le fichier, augmentés par la métrique de
     * couplage CSEC («couplage simple entre classes», voir dessous) de chaque classe
     *
     * @param pathString : (String) Le dossier dans lequel on cherche des fichiers .java
     * @return Un array de String t.q. chaque valeur est separee par une virgule
     */
    public static ArrayList<String> getValuesForFolder(String pathString) {
        Path path = Path.of(pathString.replace('/', '\\').replace(":", ""));
        return LSEC.getValuesForFolder(path);
    }

    /**
     * Prend comme entrée le chemin d’un dossier, et un fichier CSV avec la sortie du jls
     * pour le même dossier, et produise en sortie les mêmes données que dans le fichier, augmentés par la métrique de
     * couplage CSEC («couplage simple entre classes», voir dessous) de chaque classe
     *
     * @param path : (Path) Le dossier dans lequel on cherche des fichiers .java
     * @return Un array de String t.q. chaque valeur est separee par une virgule
     */
    public static ArrayList<String> getValuesForFolder(Path path) {
        ParsedRepository repo = JavaRepositoryParser.parse(path);
        ParsedClass classes[] = repo.getClasses();

        ArrayList<String> csv = new ArrayList<String>();
        for (ParsedClass c : repo.getClasses())
            csv.add(LSEC.getValueForClass(c, repo));

        return csv;
    }

    public static void print(String path){
        for (String s : LSEC.getValuesForFolder(path))
            System.out.println(s);
    }

    public static String getValueForClass(ParsedClass parsedClass, ParsedRepository repo) {
        String jls = JLS.getValueForClass(parsedClass);
        String csec = "" + repo.getNumTimesReferencedDirectly(parsedClass);
        return jls + ", " + csec;
    }

    public static void generateCSV(String path) throws FileNotFoundException {
        File csvOutputFile = new File("lsec.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            for(String line : getValuesForFolder(path))
                pw.println(line);
        }
        if(!csvOutputFile.exists())
            System.out.println("ERROR: CSV wasn't generated!");
    }
}
