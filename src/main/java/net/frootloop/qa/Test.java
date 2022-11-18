package net.frootloop.qa;

import net.frootloop.qa.parser.JavaSourceFileParser;
import net.frootloop.qa.parser.util.InputHandler;
import net.frootloop.qa.parser.util.files.FilePathHandler;
import net.frootloop.qa.parser.util.files.GitGudder;
import net.frootloop.qa.parser.util.strings.CodeParser;
import net.frootloop.qa.parser.util.strings.SourceCodeFixerUpper;

import java.io.IOException;

// test;

public class Test extends JavaSourceFileParser implements CodeParser, GitGudder, FilePathHandler, InputHandler {

    private String test1;
    private String test2;

    public static void main(String[] args) throws IOException {



        String sourceFileText = getString();

        System.out.println("\n============================================");
        System.out.println("                AFTER CLEANUP               ");
        System.out.println("============================================");
        sourceFileText = SourceCodeFixerUpper.cleanUpSource(sourceFileText);
        //System.out.println(sourceFileText);

        System.out.println("\n============================================");
        System.out.println("                AFTER SPLITTING             ");
        System.out.println("============================================");
        SourceCodeFixerUpper.getCodeStatementsOf(sourceFileText);

        String[] split = sourceFileText.split("(;|\\{|\\})");


        String x = "\n" +
                "}";

        System.out.println(SourceCodeFixerUpper.getLineCountOf(x));

    }


    /**
     * This is just
     * a way to check
     * the CodeParser
     */
    private class Pouet {
        private class PouetSquared {

        }
    }

    private static String getString(){
        return "package net.frootloop.qa.parser.util.strings;\n" +
                "\n" +
                "import net.frootloop.qa.parser.result.internal.Visibility;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "import java.util.regex.Matcher;\n" +
                "import java.util.regex.Pattern;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "public interface CodeParser {\n" +
                "    Pattern rxAssertStatements = Pattern.compile(\"(^|;|})(\\\\n\\\\s*)?((Assert\\\\.)?(assert[A-Z]\\\\w+)\\\\([^;]*\\\\));\");\n" +
                "    Pattern rxImportStatements = Pattern.compile(\"(^|;)?\\\\n*\\\\s*(import\\\\s+((\\\\w+\\\\.)*([A-Z]\\\\w+)))\");\n" +
                "    Pattern rxPackageStatement = Pattern.compile(\"(^|;)\\\\s*\\\\n*\\\\s*(package\\\\s+(((\\\\w+\\\\.)*[a-z]\\\\w+)(.([A-Z]\\\\w+))?))(\\\\s*;)\");\n" +
                "    Pattern rxImbeddedPackage = Pattern.compile(\"(\\\\w+\\\\.)*([A-Z]\\\\w+)\");\n" +
                "    Pattern rxNewClassObject = Pattern.compile(\".*new ([A-Z]\\\\w*)\\\\(.*\\\\).*\");\n" +
                "    Pattern rxClassVariable = Pattern.compile(\"(^|\\\\s+|\\\\(|,)([A-Z]\\\\w*)\\\\s*(<(([A-Z]\\\\w*)(,([A-Z]\\\\w*))*)>)?\\\\s+(\\\\w*)\\\\s*(,\\\\s*(\\\\w+))?(,\\\\s*(\\\\w+))?(,\\\\s*(\\\\w+))*\\\\s*(,|=|\\\\*=|\\\\-=|\\\\+=|\\\\|!=|\\\\^=|;|\\\\{|\\\\))\");\n" +
                "    Pattern rxInheritedClasses = Pattern.compile(\"(extends|implements)\\\\s(\\\\w+((\\\\s)*,\\\\s\\\\w+)*)*\");\n" +
                "    Pattern rxDeclaredClass = Pattern.compile(\"((final|public|abstract)\\\\s+)*(class|interface|enum)\\\\s+([A-Z]\\\\w+)\");\n" +
                "    Pattern rxDeclaredMethod = Pattern.compile(\"(@[A-Z]\\\\w+)?(?:((?:public|private|protected|static|final|native|synchronized|abstract|transient)+)\\\\s+)+(([$_\\\\w<>\\\\[\\\\]\\\\s]*)\\\\s+([\\\\$_\\\\w]+)\\\\(([^\\\\)]*)\\\\)?\\\\s*)\");\n" +
                "    Pattern rxDeclaredVariable = Pattern.compile(\"(((public|private|protected|final)\\\\s+)?)(int|short|long|float|double|byte|boolean|char|[A-Z]\\\\w+(\\\\[\\\\s*\\\\]|\\\\.\\\\w+|<\\\\w+(\\\\s*,\\\\s*\\\\w+)*>)?)\\\\s+((\\\\w+)\\\\s*((\\\\s*,\\\\s*\\\\w+)*))($|[;=])\");\n" +
                "    Pattern rxReferencedMethod = Pattern.compile(\"[\\\\. \\\\(\\\\{]([a-z_][A-z_]*)\\\\(\");\n" +
                "    Pattern rxReferencedAttributeWithThis = Pattern.compile(\"this\\\\.([a-z]\\\\w+)\");\n" +
                "    Pattern rxLowerCaseWords = Pattern.compile(\"(?=[^\\\\w]([a-z]\\\\w+)[^\\\\(\\\\{\\\\w])\");\n" +
                "\n" +
                "    /**\n" +
                "     * Assumes that the input text has already been cleaned up.\n" +
                "     * @param cleanSourceFileTextData\n" +
                "     * @return List of assert statements, i.e. assert(predicate);\n" +
                "     */\n" +
                "    static String[] getAssertStatementsOf(String cleanSourceFileTextData) {\n" +
                "        ArrayList<String> assertStatements = new ArrayList<>();\n" +
                "        Matcher regexAssertDetector = rxAssertStatements.matcher(cleanSourceFileTextData);\n" +
                "        while (regexAssertDetector.find()) assertStatements.add(regexAssertDetector.group(3));\n" +
                "        return assertStatements.toArray(new String[0]);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Assumes that the input text has already been cleaned up.\n" +
                "     * @param cleanSourceFileTextData\n" +
                "     * @return List of import statements, such as 'import java.util.List';\n" +
                "     */\n" +
                "    static String[] getImportStatementsOf(String cleanSourceFileTextData) {\n" +
                "        ArrayList<String> importStatements = new ArrayList<>();\n" +
                "        Matcher regexImportDetector = rxImportStatements.matcher(cleanSourceFileTextData);\n" +
                "        while (regexImportDetector.find()) importStatements.add(regexImportDetector.group(3));\n" +
                "        return importStatements.toArray(new String[0]);\n" +
                "    }\n" +
                "\n" +
                "    /***\n" +
                "     * Takes in an import statement and returns whether it's referring to a given class name.\n" +
                "     * @param importStatement\n" +
                "     * @param className\n" +
                "     * @return If the statement is importing the class 'className'\n" +
                "     */\n" +
                "    static boolean isStatementImportingClass(String importStatement, String className) {\n" +
                "        if(importStatement.matches(\"^(\\\\w+\\\\.)*\\\\w+$\"))\n" +
                "            importStatement = \"import \" + importStatement;\n" +
                "\n" +
                "        Matcher regexImportDetector = rxImportStatements.matcher(importStatement);\n" +
                "        while (regexImportDetector.find()) return className.equals(regexImportDetector.group(5));\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    /***\n" +
                "     * Takes in a package name and returns the name of the class it inherits from, if any!\n" +
                "     * For instance, BlockOfCode's package is \"net.frootloop.qa.parser.result.internal.CodeTree\", so we return \"CodeTree\".\n" +
                "     * @param packageName\n" +
                "     * @return Class name of direct inheritance. Returns 'null' if the packageName is not a class.\n" +
                "     */\n" +
                "    static String getPackageClass(String packageName) {\n" +
                "        Matcher regexPackageDetector = rxImbeddedPackage.matcher(packageName);\n" +
                "        while (regexPackageDetector.find()) return regexPackageDetector.group(2);\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Assumes that the input text has already been cleaned up.\n" +
                "     * @param cleanSourceFileTextData\n" +
                "     * @return Name of the package, such as 'net.frootloop.qa';\n" +
                "     */\n" +
                "    static String getPackageNameOf(String cleanSourceFileTextData) {\n" +
                "        Matcher regexPackageDetector = rxPackageStatement.matcher(cleanSourceFileTextData);\n" +
                "        while (regexPackageDetector.find()) return regexPackageDetector.group(3);\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    static boolean isClassDeclaration(String codeStatement) {\n" +
                "        return rxDeclaredClass.matcher(codeStatement).find();\n" +
                "    }\n" +
                "\n" +
                "    static String getDeclaredClassName(String codeStatement) {\n" +
                "        Matcher regexClassNameDetector = rxDeclaredClass.matcher(codeStatement);\n" +
                "        while(regexClassNameDetector.find()) return regexClassNameDetector.group(4);\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    static List<String> getDeclaredClassInheritance(String codeStatement) {\n" +
                "        List<String> inherited = new ArrayList<>();\n" +
                "        Matcher regexInheritedClassesDetector = rxInheritedClasses.matcher(codeStatement);\n" +
                "        while(regexInheritedClassesDetector.find()) {\n" +
                "            for(String className : regexInheritedClassesDetector.group(2).split(\";\"))\n" +
                "                inherited.add(className.replace(\" \", \"\"));\n" +
                "        }\n" +
                "        // Filter out duplicates and empty matches, then return:\n" +
                "        return inherited.stream().distinct().filter(item-> item != null && !item.isEmpty()).collect(Collectors.toList());\n" +
                "    }\n" +
                "\n" +
                "    static Visibility getDeclaredClassVisibility(String codeStatement) {\n" +
                "        Matcher regexClassNameDetector = rxDeclaredClass.matcher(codeStatement);\n" +
                "        while(regexClassNameDetector.find()) {\n" +
                "            if(regexClassNameDetector.group(2) == \"private\") return Visibility.PRIVATE;\n" +
                "            if(regexClassNameDetector.group(2) == \"abstract\") return Visibility.PROTECTED; // Functionally the same thing\n" +
                "        }\n" +
                "        return Visibility.PUBLIC;\n" +
                "    }\n" +
                "\n" +
                "    static List<String> getInitializedClassNames(String code) {\n" +
                "        List<String> referenced = new ArrayList<>();\n" +
                "\n" +
                "        // Get names of classes that were referenced by a 'new' heap allocation:\n" +
                "        Matcher regexNewKeywordDetector = rxNewClassObject.matcher(code);\n" +
                "        while(regexNewKeywordDetector.find()) referenced.add(regexNewKeywordDetector.group(1));\n" +
                "\n" +
                "        // Get names of classes that were references as variable types, or in a type <T> collection:\n" +
                "        Matcher regexClassVariableDetector = rxClassVariable.matcher(code);\n" +
                "        while(regexClassVariableDetector.find()) {\n" +
                "            referenced.add(regexClassVariableDetector.group(2));\n" +
                "            referenced.add(regexClassVariableDetector.group(5));\n" +
                "            referenced.add(regexClassVariableDetector.group(7));\n" +
                "        }\n" +
                "\n" +
                "        // Filter out duplicates and empty matches, then return:\n" +
                "        return referenced.stream().distinct().filter(item-> item != null && !item.isEmpty()).collect(Collectors.toList());\n" +
                "    }\n" +
                "\n" +
                "    static boolean isVariableDeclaration(String codeStatement) {\n" +
                "         return rxDeclaredVariable.matcher(codeStatement).find();\n" +
                "    }\n" +
                "\n" +
                "    static String[] getDeclaredVariableNames(String codeStatement) {\n" +
                "        Matcher regexAttributeNameDetector = rxDeclaredVariable.matcher(codeStatement);\n" +
                "        while(regexAttributeNameDetector.find())\n" +
                "            if(regexAttributeNameDetector.group(7) != null)\n" +
                "                return regexAttributeNameDetector.group(7).split(\", *\");\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    static String getDeclaredVariableType(String codeStatement) {\n" +
                "        Matcher regexAttributeNameDetector = rxDeclaredVariable.matcher(codeStatement);\n" +
                "        while(regexAttributeNameDetector.find()) return regexAttributeNameDetector.group(4);\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    static boolean isMethodDeclaration(String codeStatement) {\n" +
                "        return rxDeclaredMethod.matcher(codeStatement).find();\n" +
                "    }\n" +
                "\n" +
                "    static boolean isMethodDeclarationStatic(String codeStatement) {\n" +
                "        return codeStatement.matches(\"([^(]+\\\\s+)*static(\\\\s+[^(]+)*\\\\(.*\\\\)\\\\s*\");\n" +
                "    }\n" +
                "\n" +
                "    static boolean isMethodDeclarationAbstract(String codeStatement) {\n" +
                "        return codeStatement.matches(\"([^(]+\\\\s+)*abstract(\\\\s+[^(]+)*\\\\(.*\\\\)\\\\s*\");\n" +
                "    }\n" +
                "\n" +
                "    static boolean isMethodDeclarationTest(String codeStatement) {\n" +
                "        return codeStatement.matches(\"^(@[A-Z]\\\\w+\\\\s)*@Test[\\\\s\\\\n]*.*\");\n" +
                "    }\n" +
                "\n" +
                "    static String getDeclaredMethodName(String codeStatement) {\n" +
                "        Matcher regexMethodNameDetector = rxDeclaredMethod.matcher(codeStatement);\n" +
                "        while(regexMethodNameDetector.find()) return regexMethodNameDetector.group(5);\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    static String getDeclaredMethodReturnType(String codeStatement) {\n" +
                "        Matcher regexMethodNameDetector = rxDeclaredMethod.matcher(codeStatement);\n" +
                "        while(regexMethodNameDetector.find()) return regexMethodNameDetector.group(4);\n" +
                "        return \"void\";\n" +
                "    }\n" +
                "\n" +
                "    static ArrayList<String> getDeclaredMethodArguments(String codeStatement) {\n" +
                "        Matcher regexMethodNameDetector = rxDeclaredMethod.matcher(codeStatement);\n" +
                "        while(regexMethodNameDetector.find()) {\n" +
                "\n" +
                "            // If the method declaration has arguments, we add their names individually to the list:\n" +
                "            if (regexMethodNameDetector.group(6) != null && !regexMethodNameDetector.group(6).matches(\"\\\\s*\")) {\n" +
                "\n" +
                "                // Split the arguments by commas:\n" +
                "                String[] arguments = regexMethodNameDetector.group(6).replaceAll(\"\\\\s*,\\\\s*\", \",\").split(\",\");\n" +
                "                ArrayList<String> argumentTypes = new ArrayList<>();\n" +
                "\n" +
                "                // For every argument, such as \"String codeStatement\", keep only the types, such as \"String\"\n" +
                "                for (String arg : arguments)\n" +
                "                    argumentTypes.add(arg.replaceAll(\" .*\", \"\"));\n" +
                "\n" +
                "                return argumentTypes;\n" +
                "            }\n" +
                "        }\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    static Visibility getDeclaredMethodVisibility(String codeStatement) {\n" +
                "        Matcher regexMethodNameDetector = rxDeclaredMethod.matcher(codeStatement);\n" +
                "        while(regexMethodNameDetector.find()) {\n" +
                "            if(regexMethodNameDetector.group(2) == null)\n" +
                "                return Visibility.PUBLIC;\n" +
                "\n" +
                "            switch(regexMethodNameDetector.group(2)) {\n" +
                "                case \"private\":\n" +
                "                    return Visibility.PRIVATE;\n" +
                "                case \"protected\":\n" +
                "                    return Visibility.PROTECTED;\n" +
                "            }\n" +
                "        }\n" +
                "        return Visibility.PUBLIC;\n" +
                "    }\n" +
                "\n" +
                "    static ArrayList<String> getReferencedMethodNames(String unitTest) {\n" +
                "        ArrayList<String> methodNames = new ArrayList<>();\n" +
                "        Matcher regexReferencedMethodDetector = rxReferencedMethod.matcher(unitTest);\n" +
                "        while(regexReferencedMethodDetector.find())\n" +
                "            if(regexReferencedMethodDetector.group(1) != null && !methodNames.contains(regexReferencedMethodDetector.group(1)))\n" +
                "                methodNames.add(regexReferencedMethodDetector.group(1));\n" +
                "\n" +
                "        return methodNames;\n" +
                "    }\n" +
                "\n" +
                "    static List<String> getLowerCaseWordsOf(String code) {\n" +
                "        ArrayList<String> words = new ArrayList<>();\n" +
                "        Matcher regexLowerCaseWordsDetector = rxLowerCaseWords.matcher(code);\n" +
                "        while(regexLowerCaseWordsDetector.find()) {\n" +
                "            boolean isLikelyVariable = !regexLowerCaseWordsDetector.group(1).matches(\"(int|short|long|float|double|byte|boolean|char|for|if|else|do|while|public|private|protected|static|final|abstract)\");\n" +
                "            if (isLikelyVariable) words.add(regexLowerCaseWordsDetector.group(1));\n" +
                "        }\n" +
                "\n" +
                "        // Filter out duplicates and empty matches, then return:\n" +
                "        return words.stream().distinct().filter(item-> item != null && !item.isEmpty()).collect(Collectors.toList());\n" +
                "    }\n" +
                "\n" +
                "    static ArrayList<String> getObviousReferencedAttributes(String code) {\n" +
                "        ArrayList<String> referencedAttributes = new ArrayList<>();\n" +
                "        Matcher regexThisKeyordDetector = rxReferencedAttributeWithThis.matcher(code);\n" +
                "        while(regexThisKeyordDetector.find()) referencedAttributes.add(regexThisKeyordDetector.group(1));\n" +
                "        return referencedAttributes;\n" +
                "    }\n" +
                "\n" +
                "    /***\n" +
                "     * @param codeStatement\n" +
                "     * @return Whether it's a conditional branching statement ( for, if, else, while, etc. )\n" +
                "     */\n" +
                "    static boolean isBranchingStatement(String codeStatement) {\n" +
                "        return codeStatement.matches(\"(if|else if|while|for).*\");\n" +
                "    }\n" +
                "\n" +
                "    /***\n" +
                "     * @param codeStatement\n" +
                "     * @return Whether it's a ternary operator ( condition ? if-true : if-false; )\n" +
                "     */\n" +
                "    static boolean isTernaryStatement(String codeStatement) {\n" +
                "        return codeStatement.matches(\"\\\\w+ +\\\\w+ +=.+\\\\?.+:.+\");\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * @param codeStatement\n" +
                "     * @return Whether the statement contains simple predicates ( ==, !=, >=, <=, |=, &=, etc. )\n" +
                "     */\n" +
                "    static boolean containsBooleanOperator(String codeStatement) {\n" +
                "        return codeStatement.matches(\".*(.*(==|!=|>=|<=|&&|\\\\|\\\\||&=|\\\\|\\\\^).*).*\");\n" +
                "    }\n" +
                "}\n";
    }

}