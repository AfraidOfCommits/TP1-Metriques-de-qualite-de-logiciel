package net.frootloop.qa.parser.result;

import net.frootloop.qa.parser.StringParser;
import net.frootloop.qa.parser.result.internal.CodeTree;
import net.frootloop.qa.parser.result.internal.Visibility;

import java.nio.file.Path;
import java.util.ArrayList;

public class ParsedClass extends CodeTree {

    private Path filePath;
    private String packageName;
    private Visibility visibility;
    private String className;
    private int cyclomaticComplexity = 1;
    private ArrayList<String> parentClasses = new ArrayList<>();
    private ArrayList<String> classesReferenced = new ArrayList<>();

    public ParsedClass(BlockOfCode classCodeBlock, String packageName, String[] importStatements){
        super(classCodeBlock);
        this.packageName = packageName;
        this.className = StringParser.getDeclaredClassName(this.root.leadingStatement);
        this.visibility = StringParser.getDeclaredClassVisibility(this.root.leadingStatement);
        this.cyclomaticComplexity = this.root.getCyclomaticComplexity();

        // Check if the package name we're given refers to a class we'd be imbedded in:
        String enclosingClass = StringParser.getPackageClass(packageName);
        if(enclosingClass != null) this.addParent(packageName);

        // Set inheritance:
        ArrayList<String> parentClassNames = StringParser.getDeclaredClassInheritance(this.root.leadingStatement);
        for(String name : parentClassNames)
            this.addParent(this.getSignatureOfReferencedClass(name, importStatements));

        // Set references to other classes:
        ArrayList<String> referencedClassNames = StringParser.getInitializedClassNames(this.root.codeStatements);
        for(String name : referencedClassNames)
            this.addReferenceTo(this.getSignatureOfReferencedClass(name, importStatements));
    }

    public ParsedClass(BlockOfCode classCodeBlock, String packageName, Path filePath, String[] importStatements){
        this(classCodeBlock,packageName,importStatements);
        this.filePath = filePath;
    }

    private String getSignatureOfReferencedClass(String className, String[] importStatements) {
        // Check if the class was imported from another package:
        for(String importedClassSignature : importStatements)
            if(StringParser.isStatementImportingClass(importedClassSignature, className))
                return importedClassSignature;
        // Otherwise, we can assume the class shares the same package as us:
        return this.packageName + "." + className;
    }

    public String getSignature() {
        return packageName + "." + className;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public Path getFilePath() {
        if(this.filePath == null) return Path.of("./" + this.getSignature().replace(".", "/") + ".java");
        return filePath;
    }

    public ArrayList<String> getParentSignatures() {
        return this.parentClasses;
    }

    public void addParent(String classSignatureOfParent) {
        if(this.getSignature() != classSignatureOfParent && !parentClasses.contains(classSignatureOfParent))
            parentClasses.add(classSignatureOfParent);
    }

    public void addReferenceTo(String classSignatureReferenced) {
        if(this.getSignature() != classSignatureReferenced && !classesReferenced.contains(classSignatureReferenced))
            classesReferenced.add(classSignatureReferenced);
    }

    public ArrayList<String> getClassesReferencedDirectly() {
        return classesReferenced;
    }

    public int getCyclomatcComplexity() {return cyclomaticComplexity;}
}
