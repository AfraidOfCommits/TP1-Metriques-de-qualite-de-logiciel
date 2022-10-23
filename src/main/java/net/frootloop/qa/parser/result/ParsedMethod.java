package net.frootloop.qa.parser.result;

import net.frootloop.qa.parser.StringParser;
import net.frootloop.qa.parser.result.internal.CodeTree;
import net.frootloop.qa.parser.result.internal.Visibility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParsedMethod extends CodeTree {

    private ParsedClass homeClass;
    private Visibility visibility;
    private Boolean isStatic;
    private String methodName;
    private String returnType;

    private ArrayList<String> arguments;

    public ParsedMethod(BlockOfCode blockOfCode, ParsedClass parsedClass) {
        super(blockOfCode);
        this.homeClass = parsedClass;
        this.methodName = StringParser.getDeclaredMethodName(blockOfCode.leadingStatement);
        this.visibility = StringParser.getDeclaredMethodVisibility(blockOfCode.leadingStatement);
        this.returnType = StringParser.getDeclaredMethodReturnType(blockOfCode.leadingStatement);
        this.arguments = StringParser.getDeclaredMethodArguments(blockOfCode.leadingStatement);
        this.isStatic = StringParser.isMethodDeclarationStatic(blockOfCode.leadingStatement);
    }

    public List<String> getReferencedAttributes() {
        if(isStatic) return new ArrayList<>();

        String code = this.root.getCodeAsString();

        // Get a list of our class' attributes that were referred to by using the "this.attributeName" format;
        ArrayList<String> referencedAttributeList = StringParser.getObviousReferencedAttributes(code);
        for(String attribute: referencedAttributeList) {
            if(!this.homeClass.hasAttributeCalled(attribute))
                referencedAttributeList.remove(attribute);
        }

        // Try to find other times the class
        ArrayList<String> declaredVariables = this.root.getDeclaredVariables();
        for(String word:StringParser.getLowerCaseWordsOf(code)) {
            if(this.homeClass.hasAttributeCalled(word)) {
                boolean isDeclaredInMethod = declaredVariables.contains(word);
                boolean isArgument = this.arguments.contains(word);
                if(!isArgument & isDeclaredInMethod) referencedAttributeList.add(word);
            }
        }

        // Filter out duplicates and empty matches, then return:
        return referencedAttributeList.stream().distinct().filter(item-> item != null && !item.isEmpty()).collect(Collectors.toList());
    }

    public boolean isStatic() {
        return this.isStatic;
    }
}
