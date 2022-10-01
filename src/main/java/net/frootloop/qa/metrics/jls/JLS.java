package net.frootloop.qa.metrics.jls;

import net.frootloop.qa.parser.JavaRepositoryParser;
import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.result.ParsedRepository;

import java.nio.file.Path;
import java.util.ArrayList;

public class JLS {

    /**
     * Prends en entrée le chemin d'accès d'un dossier qui contient du code java potentiellement
     * organisé en paquets (sous-dossiers, organisés selon les normes java) et produise en sortie en
     * format CSV (« comma separated values », valeurs séparées par des virgules) les colonnes
     * <p>
     * • chemin du fichier
     * • nom du paquet
     * • nom de la classe
     *
     * @param pathString : (String) Le dossier dans lequel on cherche des fichiers .java
     * @return Un array de String t.q. chaque valeur est separee par une virgule
     */
    public static ArrayList<String> getValuesForFolder(String pathString) {
        Path path = Path.of(pathString.replace('/', '\\').replace(":", ""));
        return JLS.getValuesForFolder(path);
    }

    /**
     * Prends en entrée le chemin d'accès d'un dossier qui contient du code java potentiellement
     * organisé en paquets (sous-dossiers, organisés selon les normes java) et produise en sortie en
     * format CSV (« comma separated values », valeurs séparées par des virgules) les colonnes
     * <p>
     * • chemin du fichier
     * • nom du paquet
     * • nom de la classe
     *
     * @param path : (Path) Le dossier dans lequel on cherche des fichiers .java
     * @return Un array de String t.q. chaque valeur est separee par une virgule
     */
    public static ArrayList<String> getValuesForFolder(Path path) {
        ParsedRepository repo = JavaRepositoryParser.parse(path);
        ParsedClass classes[] = repo.getClasses();

        ArrayList<String> csv = new ArrayList<String>();
        for (ParsedClass c : repo.getClasses())
            csv.add("." + c.getFilePath().toString().replace("\\", "/") +
                    ", " + c.getPackageName() +
                    ", " + c.getClassName());

        return csv;
    }

    public static void print(String path){
        for (String s : JLS.getValuesForFolder(path))
            System.out.println(s);
    }
}
