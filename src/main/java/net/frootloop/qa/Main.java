package net.frootloop.qa;

import net.frootloop.qa.metrics.lcsec.LSEC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("SVP entrer un chemin de dossier (à partir d'ou ce fichier .JAR est localisé): ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine();


        //NVLOC.printAmountOfEmptyLines(input)
        //JLS.print(input);
        LSEC.print(input);
        //whoNeedsUnitTests();
    }
}