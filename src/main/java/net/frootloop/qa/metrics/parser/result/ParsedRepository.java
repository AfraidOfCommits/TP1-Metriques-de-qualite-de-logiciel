package net.frootloop.qa.metrics.parser.result;

import java.util.HashMap;

public class ParsedRepository {

    public String rootFilePath;
    public int totalLines;
    public int totalLinesComments;
    public int totalLinesEmpty;

    private HashMap<String, ParsedClass> classMap;
    private HashMap<String, Integer> numTimesReferenced;
    private HashMap<String, Integer> NumTimesReferencedIndirectly;

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

    private void buildReferenceMaps() {
        // Add number of times each class is referenced by cycling through all ParsedClasses
        for (ParsedClass c : this.classMap.values()) {
            for (String referencedClass : c.getClassesReferencedDirectly()) {
                this.addDirectReferenceTo(referencedClass);
            }
        }
    }

    private void addDirectReferenceTo(String classSignature) {
        if(!this.numTimesReferenced.containsKey(classSignature))
            this.numTimesReferenced.put(classSignature, 1);
        else {
            // Increment number of direct references;
            this.numTimesReferenced.put(classSignature, this.numTimesReferenced.get(classSignature) + 1);

            // Increment parents' number of indirect references;
            for (String parentSignature : this.classMap.get(classSignature).getParentSignatures()) {
                this.addIndirectReferenceTo(parentSignature);
            }
        }
    }

    private void addIndirectReferenceTo(String classSignature) {
        if(!this.NumTimesReferencedIndirectly.containsKey(classSignature))
            this.NumTimesReferencedIndirectly.put(classSignature, 1);
        else
            this.NumTimesReferencedIndirectly.put(classSignature, this.NumTimesReferencedIndirectly.get(classSignature) + 1);

        for (String parentSignature : this.classMap.get(classSignature).getParentSignatures()) {
            this.addIndirectReferenceTo(parentSignature);
        }
    }

}
