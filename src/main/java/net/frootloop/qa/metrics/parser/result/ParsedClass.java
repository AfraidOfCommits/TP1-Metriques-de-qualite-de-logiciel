package net.frootloop.qa.metrics.parser.result;

import java.util.ArrayList;

public class ParsedClass {

    public ParsedRepository repo;

    private String filePath;
    private String packageName;
    private Visibility visibility;
    private String className;
    private ArrayList<String> classesInherited;
    private ArrayList<String> classesImported;
    private ArrayList<String> classesReferencingMeDirectly;
    private ArrayList<String> classesReferencingMyChildren;

    private int numLines;
    private int numLinesComments;
    private int numLinesEmpty;

    public ParsedClass(String filePath, String packageName, String className, Visibility v){
        this.filePath = filePath;
        this.packageName = packageName;
        this.className = className;
        this.visibility = v;
        this.classesInherited = new ArrayList<>();
        this.classesImported = new ArrayList<>();
        this.numLines = 0;
        this.numLinesComments = 0;
        this.numLinesEmpty = 0;
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

    public String getFilePath() {
        return "./" + this.getSignature().replace(".", "/") + ".java";
    }

    public Visibility getVisibility(){
        return visibility;
    }

    public int getNumLines(){
        return numLines;
    }

    public int getNumLinesComments(){
        return numLinesComments;
    }

    public int getNumLinesEmpty() {
        return numLinesEmpty;
    }

    public ArrayList<String> getParents() {
        return this.classesInherited;
    }

    public void addReferenceTo(String classSignatureReferenced) {
        classesImported.add(classSignatureReferenced);
    }

    public void addReferenceFrom(String classSignatureOrigin) {
        classesReferencingMeDirectly.add(classSignatureOrigin);
    }

    public void addIndirectReferenceFrom(String classSignatureOrigin) {
        classesReferencingMyChildren.add(classSignatureOrigin);
    }
}
