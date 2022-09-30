package net.frootloop.qa.metrics;

import net.frootloop.qa.metrics.parser.JavaSourceFileParser;
import net.frootloop.qa.metrics.parser.result.ParsedSourceFile;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends JavaSourceFileParser {

    public class Maine {
        // Nested class
    }

    private static void TestRegexes(String statement){

        // Regex precompiled patterns:
        Pattern classNamePattern = Pattern.compile("(class|interface|enum)\\s(\\w+)");
        Matcher classNameMatcher = null;

        Pattern inheritedClassesPattern = Pattern.compile("(extends|implements)\\s(\\w+((\\s)*,\\s\\w+)*)*");
        Matcher inheritedClassesMatcher = null;

        Pattern newClassObjectPattern = Pattern.compile(".*new ([A-Z]\\w*)\\(.*\\).*");
        Matcher newClassObjectMatcher = null;

        Pattern classVariablePattern = Pattern.compile("(|\\s|\\(|,)([A-Z]\\w*)\\s\\w*");
        Matcher classVariableMatcher = null;

        if(false && newClassObjectPattern.matcher(statement).find()){
            newClassObjectMatcher = newClassObjectPattern.matcher(statement);
            while (newClassObjectMatcher.find()) {
                System.out.println(newClassObjectMatcher.group(1));
            }
        }

        // Check for classes referenced as variables, and add them as a reference of the main class:
        if(classVariablePattern.matcher(statement).find()) {

            classVariableMatcher = classVariablePattern.matcher(statement);
            while (classVariableMatcher.find()) {
                System.out.println(classVariableMatcher.group(2));
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        TestRegexes("ObjectOne test = (ObjectOne)(new Object());");

        // Test!
        ParsedSourceFile f = JavaSourceFileParser.parse("/C:/Users/Alex/Desktop/IFT3913 - Qualité Logiciel/TP1/TP1 Metriques de qualite de logiciel/src/main/java/net/frootloop/qa/metrics/Main.java");

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
