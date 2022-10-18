package net.frootloop.qa.parser;

import net.frootloop.qa.parser.result.internal.Visibility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface StringParser {

    Pattern rxAssertStatements = Pattern.compile("(^|;|})(assert\\([^;]*\\));");
    Pattern rxImportStatements = Pattern.compile("(^|;)\\n*\\s*(import\\s+((\\w+\\.)*([A-Z]\\w+)))");
    Pattern rxPackageStatement = Pattern.compile("(^|;)(package\\s+((\\w+\\.)*\\w+));");
    Pattern rxImbeddedPackage = Pattern.compile("(\\w+\\.)*([A-Z]\\w+)");
    Pattern rxNewClassObject = Pattern.compile(".*new ([A-Z]\\w*)\\(.*\\).*");
    Pattern rxClassVariable = Pattern.compile("(^|\\s+|\\(|,)([A-Z]\\w*)\\s\\w*");
    Pattern rxInheritedClasses = Pattern.compile("(extends|implements)\\s(\\w+((\\s)*,\\s\\w+)*)*");
    Pattern rxDeclaredClassName = Pattern.compile("((final|public|abstract)\\s+)*(class|interface|enum)\\s+([A-Z]\\w+)");

    /***
     * Remove unnecessary spaces and symbols, null chars, normalize line breaks, and replace string values with "text".
     * @param sourceFileTextData
     * @return Clean version of source file's text data.
     */
    static String cleanUpSource(String sourceFileTextData) {
        sourceFileTextData = StringParser.getWithNormalizedLineBreakChars(sourceFileTextData);
        sourceFileTextData = StringParser.getWithoutNullChars(sourceFileTextData);
        sourceFileTextData = StringParser.getWithoutExtraSpaces(sourceFileTextData);
        sourceFileTextData = StringParser.getWithoutUnnecessarySemicolons(sourceFileTextData);
        sourceFileTextData = StringParser.getWithGenericStringValues(sourceFileTextData, "\"text\"");
        return StringParser.getWithSingleBracketTryCatch(sourceFileTextData);
    }

    static boolean isClassDeclaration(String codeStatement) {
        return rxDeclaredClassName.matcher(codeStatement).find();
    }

    static int getLineCountOf(String inputStr) {
        return inputStr.split("\r\n|\r|\n").length;
    }

    static String getWithNormalizedLineBreakChars(String inputStr) {
        return inputStr.replaceAll("\r\n|\r|\n", "\n");
    }

    static String getWithoutNullChars(String inputStr) {
        return inputStr.replaceAll("\0", "");
    }

    static String getWithoutExtraSpaces(String inputStr) {
        inputStr = inputStr.replaceAll("( |\t)+", " "); // Replaces multiple spaces by only a single
        inputStr = inputStr.replaceAll("^( |\t)+", ""); // Removes spaces at the beginning of inputStr
        inputStr = inputStr.replaceAll("( |\t)+$", ""); // Removes spaces at the end of inputStr
        inputStr = inputStr.replaceAll("( |\t)*\n( |\t)*", "\n"); // Removes spaces around line breaks
        inputStr = inputStr.replaceAll("( |\t)*\\{( |\t)*", "{"); // Removes spaces that surround a '{' char
        return inputStr.replaceAll("( |\t)*;( |\t)*", ";"); // Removes spaces that surround a ';' char
    }

    static String getWithGenericStringValues(String inputStr, String valueToReplaceWith) {
        return inputStr.replaceAll("\\\"(\\/\\\"|.)*\\\"", valueToReplaceWith); // Replace strings by a generic value, i.e. "text"
    }

    static String getWithoutComments(String inputStr) {
        // Order is important here:
        inputStr = inputStr.replaceAll("\\/\\/.*(\n|$)", ""); // Remove single-line
        inputStr = inputStr.replaceAll("\\/\\*([^\\*]|\\*[^\\/]|[^\\*]\\/)*\\*\\/", ""); // Remove multiline comments & docstrings
        inputStr = inputStr.replaceAll("\\/\\/((?!\\*\\/).)*\n", "\n"); // Remove comments appended to code (like this one!)
        return inputStr.replaceAll("( |\t);", ";");
    }

    static String getWithoutEmptyLines(String inputStr) {
        return inputStr.replaceAll("\n+", "\n");
    }

    static String getWithoutLineBreaks(String inputStr) {
        return inputStr.replaceAll("\r\n|\r|\n", "");
    }

    static String getWithSingleBracketTryCatch(String inputStr){
        return inputStr.replaceAll("\\}\\s*catch\\s*(.*)\\s*\\{", "catch -> ");
    }

    static String getWithoutUnnecessarySemicolons(String inputStr) {
        return inputStr.replaceAll("\\};", "}");
    }

    /**
     * Assumes that the input text has already been cleaned up.
     * @param cleanSourceFileTextData
     * @return List of assert statements, i.e. assert(predicate);
     */
    static String[] getAssertStatementsOf(String cleanSourceFileTextData) {
        ArrayList<String> assertStatements = new ArrayList<>();
        Matcher regexAssertDetector = rxAssertStatements.matcher(cleanSourceFileTextData);
        while (regexAssertDetector.find()) assertStatements.add(regexAssertDetector.group(1));
        return assertStatements.toArray(new String[0]);
    }

    /**
     * Assumes that the input text has already been cleaned up.
     * @param cleanSourceFileTextData
     * @return List of import statements, such as 'import java.util.List';
     */
    static String[] getImportStatementsOf(String cleanSourceFileTextData) {
        ArrayList<String> importStatements = new ArrayList<>();
        Matcher regexImportDetector = rxImportStatements.matcher(cleanSourceFileTextData);
        while (regexImportDetector.find()) importStatements.add(regexImportDetector.group(3));
        return importStatements.toArray(new String[0]);
    }

    /***
     * Takes in an import statement and returns whether it's referring to a given class name.
     * @param importStatement
     * @param className
     * @return If the statement is importing the class 'className'
     */
    static boolean isStatementImportingClass(String importStatement, String className) {
        Matcher regexImportDetector = rxImportStatements.matcher(importStatement);
        while (regexImportDetector.find()) return className.equals(regexImportDetector.group(5));
        return false;
    }


    /***
     * Takes in a package name and returns the name of the class it inherits from, if any!
     * For instance, BlockOfCode's package is "net.frootloop.qa.parser.result.internal.CodeTree", so we return "CodeTree".
     * @param packageName
     * @return Class name of direct inheritance. Returns 'null' if the packageName is not a class.
     */
    static String getPackageClass(String packageName) {
        Matcher regexPackageDetector = rxImbeddedPackage.matcher(packageName);
        while (regexPackageDetector.find()) return regexPackageDetector.group(2);
        return null;
    }

    /**
     * Assumes that the input text has already been cleaned up.
     * @param cleanSourceFileTextData
     * @return Name of the package, such as 'net.frootloop.qa';
     */
    static String getPackageNameOf(String cleanSourceFileTextData) {
        Matcher regexPackageDetector = rxPackageStatement.matcher(cleanSourceFileTextData);
        while (regexPackageDetector.find()) return regexPackageDetector.group(1);
        return "";
    }

    static String getDeclaredClassName(String codeStatement) {

        System.out.println("[ Fetching class name... ]");

        Matcher regexClassNameDetector = rxDeclaredClassName.matcher(codeStatement);
        while(regexClassNameDetector.find()) return regexClassNameDetector.group(4);
        return "";
    }

    static ArrayList<String> getDeclaredClassInheritance(String codeStatement) {

        System.out.println("[ Fetching class inheritance... ]");

        ArrayList<String> inherited = new ArrayList<>();
        Matcher regexInheritedClassesDetector = rxInheritedClasses.matcher(codeStatement);
        while(regexInheritedClassesDetector.find()) {
            for(String className : regexInheritedClassesDetector.group(2).split(";"))
                inherited.add(className.replace(" ", ""));
        }
        return inherited;
    }

    static Visibility getDeclaredClassVisibility(String codeStatement) {
        Matcher regexClassNameDetector = rxDeclaredClassName.matcher(codeStatement);
        while(regexClassNameDetector.find()) {
            String v = regexClassNameDetector.group(2);
            if(v.equals("final")) return Visibility.FINAL;
            if(v.equals("abstract")) return Visibility.ABSTRACT;
        }
        return Visibility.PUBLIC;
    }

    static ArrayList<String> getInitializedClassNames(ArrayList<String> codeStatements) {
        ArrayList<String> referenced = new ArrayList<>();

        String code = String.join(";", codeStatements);

        Matcher regexNewKeywordDetector = rxNewClassObject.matcher(code);
        while(regexNewKeywordDetector.find()) referenced.add(regexNewKeywordDetector.group(1));

        Matcher regexClassVariableDetector = rxClassVariable.matcher(code);
        while(regexClassVariableDetector.find()) referenced.add(regexClassVariableDetector.group(2));

        return referenced;
    }
}
