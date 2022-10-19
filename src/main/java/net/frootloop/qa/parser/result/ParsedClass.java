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

    public ParsedClass(BlockOfCode classCodeBlock, String packageName, String[] importStatements, Path filePath){
        super(classCodeBlock);
        this.filePath = filePath;
        this.packageName = packageName;
        this.className = StringParser.getDeclaredClassName(this.root.leadingStatement);
        this.visibility = StringParser.getDeclaredClassVisibility(this.root.leadingStatement);
        this.cyclomaticComplexity = this.root.getCyclomaticComplexity();

        // Check if the package name we're given refers to a class we'd be embedded in:
        if(StringParser.getPackageClass(packageName) != null) this.addParent(packageName);

        // Set inheritance:
        for(String name : StringParser.getDeclaredClassInheritance(this.root.leadingStatement))
            this.addParent(this.getSignatureOfReferencedClass(name, importStatements));

        // Set references to other classes:
        String codeOfClass = this.root.getCodeAsString(false).replace("\n", "");
        for(String name : StringParser.getInitializedClassNames(codeOfClass))
            this.addReferenceTo(this.getSignatureOfReferencedClass(name, importStatements));
    }

    public ParsedClass(BlockOfCode classCodeBlock, String packageName, String[] importStatements){
        this(classCodeBlock,packageName,importStatements, null);
    }

    private String getSignatureOfReferencedClass(String className, String[] importStatements) {
        // Check if the class was imported from another package:
        for(String importedClassSignature : importStatements)
            if (StringParser.isStatementImportingClass(importedClassSignature, className))
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

    public String toString() {
        return this.getSignature();
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
