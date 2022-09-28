package net.frootloop.qa.metrics.parser.result;

import java.util.HashMap;

public class ParsedRepository {

    public String rootFilePath;
    public int totalLines;
    public int totalLinesComments;
    public int totalLinesEmpty;

    private HashMap<String, ParsedClass> classMap;

    public ParsedRepository(String filePath){
        this.rootFilePath = filePath;
        this.totalLines = 0;
        this.totalLinesComments = 0;
        this.totalLinesEmpty = 0;
        this.classMap = new HashMap<String, ParsedClass>();
    }

    public boolean isInRepo(String signature){
        return classMap.containsKey(signature);
    }

    public void addToRepo(String signature, ParsedClass data){
        if(!classMap.containsKey(signature)) {
            classMap.put(signature, data);
            this.totalLines += data.getNumLines();
            this.totalLinesComments += data.getNumLinesComments();
            this.totalLinesEmpty += data.getNumLinesEmpty();
        }
    }

    public ParsedClass getClassDataOf(String signature){
        return classMap.get(signature);
    }

    public void addReferenceFromTo(String classSignatureReferenced, String classSignatureOrigin) {

        ParsedClass referenced = classMap.get(classSignatureReferenced);
        ParsedClass origin = classMap.get(classSignatureOrigin);
        if(referenced == null || origin == null || referenced.getVisibility() == Visibility.PRIVATE) return;

        // Add class references:
        origin.addReferenceTo(classSignatureReferenced);
        referenced.addReferenceFrom(classSignatureOrigin);
        ParsedClass parent;
        for (String parentSignature : referenced.getParentSignatures()) {
            parent = classMap.get(parentSignature);
            if(parent != null) {
                parent.addIndirectReferenceFrom(classSignatureOrigin);
            }
        }
    }
}
