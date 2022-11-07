package net.frootloop.qa;

import net.frootloop.qa.parser.inputhandling.FilePathHandler;
import net.frootloop.qa.parser.inputhandling.GitGudder;
import net.frootloop.qa.parser.inputhandling.InputHandler;
import net.frootloop.qa.parser.JavaSourceFileParser;
import net.frootloop.qa.parser.StringParser;

import java.io.IOException;

// test;

public class Test extends JavaSourceFileParser implements StringParser, GitGudder, FilePathHandler, InputHandler {

    private String test1;
    private String test2;

    private class Pouet {
        private class PouetSquared {

        }
    }

    public static void main(String[] args) throws IOException {

        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\parser\\StringParser.java";
        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\Test.java";
        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\parser\\result\\internal\\CodeTree.java";

        String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\jfreechart-master\\jfreechart-master\\src\\test\\java\\org\\jfree\\chart\\axis\\AxisLocationTest.java";


    }
}