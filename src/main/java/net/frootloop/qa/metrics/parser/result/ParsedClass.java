package net.frootloop.qa.metrics.parser.result;

import java.nio.file.Path;
import java.util.ArrayList;

public class ParsedClass {

    private Path filePath;
    private String packageName;
    private Visibility visibility;
    private String className;
    private ArrayList<String> classesInherited = new ArrayList<>();
    private ArrayList<String> classesImported = new ArrayList<>();

    public ParsedClass(String className){
        this.className = className;
    }

    public ParsedClass(String className, Visibility v){
        this.className = className;
        this.visibility = v;
    }

    public ParsedClass(String className, Visibility v, String packageName){
        this.className = className;
        this.packageName = packageName;
        this.visibility = v;
    }

    public ParsedClass(String className, Visibility v, String packageName, Path filePath){
        this.className = className;
        this.packageName = packageName;
        this.filePath = filePath;
        this.visibility = v;
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
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Path getFilePath() {
        return filePath;
        //return "./" + this.getSignature().replace(".", "/") + ".java";
    }
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public Visibility getVisibility(){
        return visibility;
    }
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public ArrayList<String> getParentSignatures() {
        return this.classesInherited;
    }

    public void addParent(String classSignatureOfParent) {
        if(this.getSignature() == classSignatureOfParent)
            return;
        else if(!classesInherited.contains(classSignatureOfParent))
            classesInherited.add(classSignatureOfParent);
    }

    public void addReferenceTo(String classSignatureReferenced) {
        if(this.getSignature() == classSignatureReferenced)
            return;
        else if(!classesImported.contains(classSignatureReferenced))
            classesImported.add(classSignatureReferenced);
    }

    public ArrayList<String> getClassesReferencedDirectly() {
        return classesImported;
    }
}
