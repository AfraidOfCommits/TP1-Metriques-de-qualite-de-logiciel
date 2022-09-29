package net.frootloop.qa.metrics;

import net.frootloop.qa.metrics.parser.JavaSourceFileParser;
import net.frootloop.qa.metrics.parser.SourceFileData;
import net.frootloop.qa.metrics.parser.result.ParsedClass;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends JavaSourceFileParser {

    public class Maine {
        // Nested class
    }

    public static void main(String[] args) throws FileNotFoundException {

        Object test = new Object();

        Pattern newClassObjectPattern = Pattern.compile(".*new ([A-Z]\\w*)\\(.*\\).*");
        Matcher m = newClassObjectPattern.matcher("Object test = new Object();");
        if(m.find())
            System.out.println(m.group(1));

        // Test!
        SourceFileData f = JavaSourceFileParser.parse("/C:/Users/Alex/Desktop/IFT3913 - Qualité Logiciel/TP1/TP1 Metriques de qualite de logiciel/src/main/java/net/frootloop/qa/metrics/Main.java");

        /*System.out.println("Number of empty lines : " + f.numLinesEmpty);
        System.out.println("Number of single-lined comments : " + f.numLinesComments);
        System.out.println("Number of other lines (code, docstring, etc.) : " + (f.numLines - f.numLinesEmpty - f.numLinesComments));
        System.out.println("In total, there are " + f.numLines + " lines in this file.");

        System.out.println();
        System.out.println("Package : " + f.packageName);
        System.out.println("Main class name : " + f.mainClass.getClassName());
        System.out.println("Import statements : " + String.join("; ", f.importStatements));

        System.out.println();
        for(int i = 0; i < f.classes.size(); i++) {
            ParsedClass c = f.classes.get(i);
            System.out.println("The class " + c.getClassName() + " ("+ c.getSignature() + ") inherits from " + String.join(", ", c.getParentSignatures()));
        }
         */
    }
}
