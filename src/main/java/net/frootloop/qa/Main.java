package net.frootloop.qa;

import net.frootloop.qa.parser.JavaRepositoryParser;
import net.frootloop.qa.parser.JavaSourceFileParser;
import net.frootloop.qa.parser.StringParser;
import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.result.ParsedRepository;
import net.frootloop.qa.parser.result.ParsedSourceFile;
import net.frootloop.qa.parser.result.internal.CodeTree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

// test;

public class Main extends JavaSourceFileParser implements StringParser {

    private String test1;
    private String test2;

    private class Pouet {
        private class PouetSquared {

        }
    }

    public static void main(String[] args) throws IOException {

        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\parser\\StringParser.java";
        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\Main.java";
        String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\parser\\result\\internal\\CodeTree.java";

        //ParsedSourceFile parsedFile = JavaSourceFileParser.parse(pathString);
        //parsedFile.print();

        ParsedRepository repo = JavaRepositoryParser.parse("C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\");
    }
}