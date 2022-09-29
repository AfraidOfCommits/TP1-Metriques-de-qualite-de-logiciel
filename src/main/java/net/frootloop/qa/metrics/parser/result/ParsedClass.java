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

    public ParsedClass(String className){
        this.className = className;
        this.classesInherited = new ArrayList<>();
        this.classesImported = new ArrayList<>();
        this.numLines = 0;
        this.numLinesComments = 0;
        this.numLinesEmpty = 0;
    }

    public ParsedClass(String className, Visibility v){
        this.className = className;
        this.visibility = v;
        this.classesInherited = new ArrayList<>();
        this.classesImported = new ArrayList<>();
        this.numLines = 0;
        this.numLinesComments = 0;
        this.numLinesEmpty = 0;
    }

    public ParsedClass(String className, Visibility v, String packageName){
        this.className = className;
        this.packageName = packageName;
        this.visibility = v;
        this.classesInherited = new ArrayList<>();
        this.classesImported = new ArrayList<>();
        this.numLines = 0;
        this.numLinesComments = 0;
        this.numLinesEmpty = 0;
    }

    public ParsedClass(String className, Visibility v, String packageName, String filePath){
        this.className = className;
        this.packageName = packageName;
        this.filePath = filePath;
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
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFilePath() {
        if(filePath != null || filePath != "") return filePath;
        return "./" + this.getSignature().replace(".", "/") + ".java";
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Visibility getVisibility(){
        return visibility;
    }
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public int getNumLines(){
        return numLines;
    }

    public void setNumLines(int numLines) {
        this.numLines = numLines;
    }

    public int getNumLinesComments(){
        return numLinesComments;
    }

    public void setNumLinesComments(int numLinesComments) {
        this.numLinesComments = numLinesComments;
    }

    public int getNumLinesEmpty() {
        return numLinesEmpty;
    }

    public void setNumLinesEmpty(int numLinesEmpty) {
        this.numLinesEmpty = numLinesEmpty;
    }

    public ArrayList<String> getParentSignatures() {
        return this.classesInherited;
    }

    public void addParent(String classSignatureOfParent) {
        if(!classesInherited.contains(classSignatureOfParent))
            classesInherited.add(classSignatureOfParent);
    }

    public void addReferenceTo(String classSignatureReferenced) {
        if(!classesImported.contains(classSignatureReferenced))
            classesImported.add(classSignatureReferenced);
    }

    public void addReferenceFrom(String classSignatureOrigin) {
        if(!classesReferencingMeDirectly.contains(classSignatureOrigin))
            classesReferencingMeDirectly.add(classSignatureOrigin);
    }

    public void addIndirectReferenceFrom(String classSignatureOrigin) {
        if(!classesReferencingMyChildren.contains(classSignatureOrigin))
            classesReferencingMyChildren.add(classSignatureOrigin);
    }
}
