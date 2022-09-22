package net.frootloop.qa.metrics.parser.result;

import java.util.ArrayList;

public class ParsedClassData {

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
    private int numLinesEmpty;

    public ParsedClassData(ParsedRepository repo, String filePath, String packageName, String className, Visibility v){
        this.repo = repo;
        this.filePath = filePath;
        this.packageName = packageName;
        this.className = className;
        this.visibility = v;
        this.classesInherited = new ArrayList<>();
        this.classesImported = new ArrayList<>();
    }

    public void addReferenceFrom(String classSignature) {
        if(this.visibility == Visibility.PRIVATE) return;
        classesReferencingMeDirectly.add(classSignature);
        for (String parentSignature : classesInherited)
            repo.getClassDataOf(parentSignature).addChildReferenceFrom(classSignature);
    }

    public void addChildReferenceFrom(String classSignature){
        if(this.visibility == Visibility.PRIVATE) return;
        classesReferencingMyChildren.add(classSignature);
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

    public int getNumLinesEmpty() {
        return numLinesEmpty;
    }
}
