package net.frootloop.qa.parser.result;

import net.frootloop.qa.parser.StringParser;
import net.frootloop.qa.parser.result.internal.CodeTree;
import net.frootloop.qa.parser.result.internal.Visibility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParsedMethod extends CodeTree {

    public int numDedicatedUnitTests = 0;
    private ParsedClass homeClass;
    private Visibility visibility;
    private Boolean isStatic, isAbstract, isTest;
    private String methodName;
    private String returnType;
    private String[] assertStatements;
    private ArrayList<String> arguments;
    private ArrayList<ParsedMethod> methodsReferencedInScope = new ArrayList<>();
    private ArrayList<String> methodsNamesReferencedOutsideScope = new ArrayList<>();

    public ParsedMethod(BlockOfCode blockOfCode, ParsedClass parsedClass) {
        super(blockOfCode);
        this.homeClass = parsedClass;
        this.methodName = StringParser.getDeclaredMethodName(blockOfCode.leadingStatement);
        this.visibility = StringParser.getDeclaredMethodVisibility(blockOfCode.leadingStatement);
        this.returnType = StringParser.getDeclaredMethodReturnType(blockOfCode.leadingStatement);
        this.arguments = StringParser.getDeclaredMethodArguments(blockOfCode.leadingStatement);
        this.isStatic = StringParser.isMethodDeclarationStatic(blockOfCode.leadingStatement);
        this.isAbstract = StringParser.isMethodDeclarationAbstract(blockOfCode.leadingStatement);
        this.isTest = StringParser.isMethodDeclarationTest(blockOfCode.leadingStatement);
        this.assertStatements = StringParser.getAssertStatementsOf(blockOfCode.getCodeAsString());
    }

    public void setReferencedMethods() {
        for (String methodName : StringParser.getReferencedMethodNames(this.root.getCodeAsString())) {
            if(methodName.equals(this.methodName)) continue;

            ParsedMethod referenced = this.homeClass.getMethodByName(methodName, false);
            if (referenced != null) {
                if (!methodsReferencedInScope.contains(referenced)) methodsReferencedInScope.add(referenced);
            }
            else {
                methodsNamesReferencedOutsideScope.add(methodName);
            }
        }
    }

    public void incrementTestedMethodsNumTests() {
        if (this.isTest)
            for(ParsedMethod testedMethod: this.methodsReferencedInScope)
                testedMethod.numDedicatedUnitTests++;

        else if(this.assertStatements != null && this.assertStatements.length > 0)
            for (String unitTest : this.assertStatements)
                for (String name : StringParser.getReferencedMethodNames(unitTest))
                    for (ParsedMethod m : this.methodsReferencedInScope)
                        if (m.getMethodName().equals(name)) m.numDedicatedUnitTests++;
    }

    public List<String> getReferencedAttributes() {
        if (isStatic) return new ArrayList<>();

        String code = this.root.getCodeAsString();

        // Get a list of our class' attributes that were referred to by using the "this.attributeName" format;
        ArrayList<String> referencedAttributeList = new ArrayList<>();
        for (String attribute : StringParser.getObviousReferencedAttributes(code))
            if (this.homeClass.hasAttributeCalled(attribute))
                referencedAttributeList.add(attribute);

        // Try to find other times the class
        ArrayList<String> declaredVariables = this.root.getDeclaredVariables();
        for (String word : StringParser.getLowerCaseWordsOf(code)) {
            if (this.homeClass.hasAttributeCalled(word)) {
                boolean isDeclaredInMethod = declaredVariables.contains(word);
                boolean isArgument = (this.arguments != null) && (this.arguments.contains(word));
                if (!isArgument & isDeclaredInMethod) referencedAttributeList.add(word);
            }
        }

        // Filter out duplicates and empty matches, then return:
        return referencedAttributeList.stream().distinct().filter(item -> item != null && !item.isEmpty()).collect(Collectors.toList());
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public boolean isTest() {
        return this.isTest;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public boolean isPrivate() {
        return this.visibility == Visibility.PRIVATE;
    }

    public String getMethodSignature() {
        String methodSignature = "\n" + this.homeClass.getSignature() + " {\n    ";
        if (this.visibility == Visibility.PUBLIC) methodSignature += "public ";
        if (this.visibility == Visibility.PRIVATE) methodSignature += "private ";
        if (this.visibility == Visibility.PROTECTED) methodSignature += "protected ";
        if (this.isStatic) methodSignature += "static ";
        if (this.isAbstract) methodSignature += "abstract ";

        methodSignature += this.returnType + " " + this.methodName + "(" + String.join(", ", this.arguments) + ");\n}\n";
        return methodSignature;
    }

    public List<String> getReferencedClasses() {
        ArrayList<String> referencedClasses = new ArrayList<>();
        for(String className: StringParser.getInitializedClassNames(this.root.getCodeAsString()))
            referencedClasses.add(this.homeClass.getSignatureOfReferencedClass(className));
        return referencedClasses;
    }

    public ArrayList<String> getReferencedMethodNames() {
        if(this.isTest) return this.methodsNamesReferencedOutsideScope;
        else if(this.assertStatements == null || this.assertStatements.length == 0) return new ArrayList<>();

        ArrayList<String> testedMethods = new ArrayList<>();
        for(String unitTest: this.assertStatements)
            for(String name : StringParser.getReferencedMethodNames(unitTest))
                if(this.methodsNamesReferencedOutsideScope.contains(name)) testedMethods.add(name);

        return testedMethods;
    }

    public ArrayList<String> getTestedMethodNamesOutsideClass() {
        if(this.isTest) return this.methodsNamesReferencedOutsideScope;
        else if(this.assertStatements == null || this.assertStatements.length == 0) return new ArrayList<>();

        ArrayList<String> testedMethods = new ArrayList<>();
        for(String unitTest: this.assertStatements)
            for(String name : StringParser.getReferencedMethodNames(unitTest))
                if(this.methodsNamesReferencedOutsideScope.contains(name)) testedMethods.add(name);

        return testedMethods;
    }

    public int getNumAssertStatements() {
        if(this.assertStatements == null) return 0;
        return this.assertStatements.length;
    }

    public int getNumStatements() {
        return this.root.getNumStatements();
    }
}
