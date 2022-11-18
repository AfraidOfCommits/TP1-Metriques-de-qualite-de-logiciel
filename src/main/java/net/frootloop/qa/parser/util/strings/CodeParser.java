package net.frootloop.qa.parser.util.strings;

import net.frootloop.qa.parser.result.internal.Visibility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface CodeParser {
    Pattern rxAssertStatements = Pattern.compile("(^|;|})(\\n\\s*)?((Assert\\.)?(assert[A-Z]\\w+)\\([^;]*\\));");
    Pattern rxImportStatements = Pattern.compile("(^|;)?\\n*\\s*(import\\s+((\\w+\\.)*([A-Z]\\w+)))");
    Pattern rxPackageStatement = Pattern.compile("(^|;)\\s*\\n*\\s*(package\\s+(((\\w+\\.)*[a-z]\\w+)(.([A-Z]\\w+))?))(\\s*;)");
    Pattern rxImbeddedPackage = Pattern.compile("(\\w+\\.)*([A-Z]\\w+)");
    Pattern rxNewClassObject = Pattern.compile(".*new ([A-Z]\\w*)\\(.*\\).*");
    Pattern rxClassVariable = Pattern.compile("(^|\\s+|\\(|,)([A-Z]\\w*)\\s*(<(([A-Z]\\w*)(,([A-Z]\\w*))*)>)?\\s+(\\w*)\\s*(,\\s*(\\w+))?(,\\s*(\\w+))?(,\\s*(\\w+))*\\s*(,|=|\\*=|\\-=|\\+=|\\|!=|\\^=|;|\\{|\\))");
    Pattern rxInheritedClasses = Pattern.compile("(extends|implements)\\s(\\w+((\\s)*,\\s\\w+)*)*");
    Pattern rxDeclaredClass = Pattern.compile("((final|public|abstract)\\s+)*(class|interface|enum)\\s+([A-Z]\\w+)");
    Pattern rxDeclaredMethod = Pattern.compile("(@[A-Z]\\w+)?(?:((?:public|private|protected|static|final|native|synchronized|abstract|transient)+)\\s+)+(([$_\\w<>\\[\\]\\s]*)\\s+([\\$_\\w]+)\\(([^\\)]*)\\)?\\s*)");
    Pattern rxDeclaredVariable = Pattern.compile("(((public|private|protected|final)\\s+)?)(int|short|long|float|double|byte|boolean|char|[A-Z]\\w+(\\[\\s*\\]|\\.\\w+|<\\w+(\\s*,\\s*\\w+)*>)?)\\s+((\\w+)\\s*((\\s*,\\s*\\w+)*))($|[;=])");
    Pattern rxReferencedMethod = Pattern.compile("[\\. \\(\\{]([a-z_][A-z_]*)\\(");
    Pattern rxReferencedAttributeWithThis = Pattern.compile("this\\.([a-z]\\w+)");
    Pattern rxLowerCaseWords = Pattern.compile("(?=[^\\w]([a-z]\\w+)[^\\(\\{\\w])");

    /**
     * Assumes that the input text has already been cleaned up.
     * @param cleanSourceFileTextData
     * @return List of assert statements, i.e. assert(predicate);
     */
    static String[] getAssertStatementsOf(String cleanSourceFileTextData) {
        ArrayList<String> assertStatements = new ArrayList<>();
        Matcher regexAssertDetector = rxAssertStatements.matcher(cleanSourceFileTextData);
        while (regexAssertDetector.find()) assertStatements.add(regexAssertDetector.group(3));
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
        if(importStatement.matches("^(\\w+\\.)*\\w+$"))
            importStatement = "import " + importStatement;

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
        if(packageName == null || packageName.length() == 0) return null;
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
        while (regexPackageDetector.find()) return regexPackageDetector.group(3);
        return null;
    }

    static boolean isClassDeclaration(String codeStatement) {
        return rxDeclaredClass.matcher(codeStatement).find();
    }

    static String getDeclaredClassName(String codeStatement) {
        Matcher regexClassNameDetector = rxDeclaredClass.matcher(codeStatement);
        while(regexClassNameDetector.find()) return regexClassNameDetector.group(4);
        return null;
    }

    static List<String> getDeclaredClassInheritance(String codeStatement) {
        List<String> inherited = new ArrayList<>();
        Matcher regexInheritedClassesDetector = rxInheritedClasses.matcher(codeStatement);
        while(regexInheritedClassesDetector.find()) {
            for(String className : regexInheritedClassesDetector.group(2).split(";"))
                inherited.add(className.replace(" ", ""));
        }
        // Filter out duplicates and empty matches, then return:
        return inherited.stream().distinct().filter(item-> item != null && !item.isEmpty()).collect(Collectors.toList());
    }

    static Visibility getDeclaredClassVisibility(String codeStatement) {
        Matcher regexClassNameDetector = rxDeclaredClass.matcher(codeStatement);
        while(regexClassNameDetector.find()) {
            if(regexClassNameDetector.group(2) == "private") return Visibility.PRIVATE;
            if(regexClassNameDetector.group(2) == "abstract") return Visibility.PROTECTED; // Functionally the same thing
        }
        return Visibility.PUBLIC;
    }

    static List<String> getInitializedClassNames(String code) {
        List<String> referenced = new ArrayList<>();

        // Get names of classes that were referenced by a 'new' heap allocation:
        Matcher regexNewKeywordDetector = rxNewClassObject.matcher(code);
        while(regexNewKeywordDetector.find()) referenced.add(regexNewKeywordDetector.group(1));

        // Get names of classes that were references as variable types, or in a type <T> collection:
        Matcher regexClassVariableDetector = rxClassVariable.matcher(code);
        while(regexClassVariableDetector.find()) {
            referenced.add(regexClassVariableDetector.group(2));
            referenced.add(regexClassVariableDetector.group(5));
            referenced.add(regexClassVariableDetector.group(7));
        }

        // Filter out duplicates and empty matches, then return:
        return referenced.stream().distinct().filter(item-> item != null && !item.isEmpty()).collect(Collectors.toList());
    }

    static boolean isVariableDeclaration(String codeStatement) {
         return rxDeclaredVariable.matcher(codeStatement).find();
    }

    static String[] getDeclaredVariableNames(String codeStatement) {
        Matcher regexAttributeNameDetector = rxDeclaredVariable.matcher(codeStatement);
        while(regexAttributeNameDetector.find())
            if(regexAttributeNameDetector.group(7) != null)
                return regexAttributeNameDetector.group(7).split(", *");
        return null;
    }

    static String getDeclaredVariableType(String codeStatement) {
        Matcher regexAttributeNameDetector = rxDeclaredVariable.matcher(codeStatement);
        while(regexAttributeNameDetector.find()) return regexAttributeNameDetector.group(4);
        return null;
    }

    static boolean isMethodDeclaration(String codeStatement) {
        return rxDeclaredMethod.matcher(codeStatement).find();
    }

    static boolean isMethodDeclarationStatic(String codeStatement) {
        return codeStatement.matches("([^(]+\\s+)*static(\\s+[^(]+)*\\(.*\\)\\s*");
    }

    static boolean isMethodDeclarationAbstract(String codeStatement) {
        return codeStatement.matches("([^(]+\\s+)*abstract(\\s+[^(]+)*\\(.*\\)\\s*");
    }

    static boolean isMethodDeclarationTest(String codeStatement) {
        return codeStatement.matches("^(@[A-Z]\\w+\\s)*@Test[\\s\\n]*.*");
    }

    static String getDeclaredMethodName(String codeStatement) {
        Matcher regexMethodNameDetector = rxDeclaredMethod.matcher(codeStatement);
        while(regexMethodNameDetector.find()) return regexMethodNameDetector.group(5);
        return null;
    }

    static String getDeclaredMethodReturnType(String codeStatement) {
        Matcher regexMethodNameDetector = rxDeclaredMethod.matcher(codeStatement);
        while(regexMethodNameDetector.find()) return regexMethodNameDetector.group(4);
        return "void";
    }

    static ArrayList<String> getDeclaredMethodArguments(String codeStatement) {
        Matcher regexMethodNameDetector = rxDeclaredMethod.matcher(codeStatement);
        while(regexMethodNameDetector.find()) {

            // If the method declaration has arguments, we add their names individually to the list:
            if (regexMethodNameDetector.group(6) != null && !regexMethodNameDetector.group(6).matches("\\s*")) {

                // Split the arguments by commas:
                String[] arguments = regexMethodNameDetector.group(6).replaceAll("\\s*,\\s*", ",").split(",");
                ArrayList<String> argumentTypes = new ArrayList<>();

                // For every argument, such as "String codeStatement", keep only the types, such as "String"
                for (String arg : arguments)
                    argumentTypes.add(arg.replaceAll(" .*", ""));

                return argumentTypes;
            }
        }
        return null;
    }

    static Visibility getDeclaredMethodVisibility(String codeStatement) {
        Matcher regexMethodNameDetector = rxDeclaredMethod.matcher(codeStatement);
        while(regexMethodNameDetector.find()) {
            if(regexMethodNameDetector.group(2) == null)
                return Visibility.PUBLIC;

            switch(regexMethodNameDetector.group(2)) {
                case "private":
                    return Visibility.PRIVATE;
                case "protected":
                    return Visibility.PROTECTED;
            }
        }
        return Visibility.PUBLIC;
    }

    static ArrayList<String> getReferencedMethodNames(String unitTest) {
        ArrayList<String> methodNames = new ArrayList<>();
        Matcher regexReferencedMethodDetector = rxReferencedMethod.matcher(unitTest);
        while(regexReferencedMethodDetector.find())
            if(regexReferencedMethodDetector.group(1) != null && !methodNames.contains(regexReferencedMethodDetector.group(1)))
                methodNames.add(regexReferencedMethodDetector.group(1));

        return methodNames;
    }

    static List<String> getLowerCaseWordsOf(String code) {
        ArrayList<String> words = new ArrayList<>();
        Matcher regexLowerCaseWordsDetector = rxLowerCaseWords.matcher(code);
        while(regexLowerCaseWordsDetector.find()) {
            boolean isLikelyVariable = !regexLowerCaseWordsDetector.group(1).matches("(int|short|long|float|double|byte|boolean|char|for|if|else|do|while|public|private|protected|static|final|abstract)");
            if (isLikelyVariable) words.add(regexLowerCaseWordsDetector.group(1));
        }

        // Filter out duplicates and empty matches, then return:
        return words.stream().distinct().filter(item-> item != null && !item.isEmpty()).collect(Collectors.toList());
    }

    static ArrayList<String> getObviousReferencedAttributes(String code) {
        ArrayList<String> referencedAttributes = new ArrayList<>();
        Matcher regexThisKeyordDetector = rxReferencedAttributeWithThis.matcher(code);
        while(regexThisKeyordDetector.find()) referencedAttributes.add(regexThisKeyordDetector.group(1));
        return referencedAttributes;
    }

    /***
     * @param codeStatement
     * @return Whether it's a conditional branching statement ( for, if, else, while, etc. )
     */
    static boolean isBranchingStatement(String codeStatement) {
        return codeStatement.matches("(if|else if|while|for).*");
    }

    /***
     * @param codeStatement
     * @return Whether it's a ternary operator ( condition ? if-true : if-false; )
     */
    static boolean isTernaryStatement(String codeStatement) {
        return codeStatement.matches("\\w+ +\\w+ +=.+\\?.+:.+");
    }

    /**
     * @param codeStatement
     * @return Whether the statement contains simple predicates ( ==, !=, >=, <=, |=, &=, etc. )
     */
    static boolean containsBooleanOperator(String codeStatement) {
        return codeStatement.matches(".*(.*(==|!=|>=|<=|&&|\\|\\||&=|\\|\\^).*).*");
    }
}
