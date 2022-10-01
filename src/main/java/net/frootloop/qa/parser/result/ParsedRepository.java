package net.frootloop.qa.parser.result;

import java.nio.file.Path;
import java.util.HashMap;

public class ParsedRepository {

    public Path rootFilePath;
    public int totalLines;
    public int totalLinesComments;
    public int totalLinesEmpty;

    /***
     * Map to store and fetch ParsedClass instances by their signature.
     */
    private HashMap<String, ParsedClass> classMap;

    /***
     * Map to store and fetch the amount of times a class has been directly referred to by
     * another class, with the key being the class's signature.
     */
    private HashMap<String, Integer> numTimesReferenced;

    /***
     * Map to store and fetch the amount of times a parent class has been indirectly referred to by
     * a child class, with the key being the parent's signature.
     */
    private HashMap<String, Integer> NumTimesReferencedIndirectly;

    public ParsedRepository(Path filePath){
        this.rootFilePath = filePath;
        this.classMap = new HashMap<>();
        this.numTimesReferenced = new HashMap<>();
        this.NumTimesReferencedIndirectly = new HashMap<>();
    }

    public void addParsedFile(ParsedSourceFile parsedFile) {
        for (ParsedClass c: parsedFile.classes) {
            classMap.put(c.getSignature(), c);
        }
        this.totalLines += parsedFile.numLines;
        this.totalLinesComments += parsedFile.numLinesComments;
        this.totalLinesEmpty += parsedFile.numLinesEmpty;
    }

    public ParsedClass getClassDataOf(String signature){
        return classMap.get(signature);
    }

    public void buildReferenceMaps() {
        // Add number of times each class is referenced by cycling through all ParsedClasses
        for (ParsedClass c : this.classMap.values()) {
            for (String referencedClass : c.getClassesReferencedDirectly()) {
                this.addDirectReferenceTo(referencedClass);
            }
        }
    }

    public int getNumTimesReferenced(ParsedClass parsedClass) {
        if(!this.numTimesReferenced.containsKey(parsedClass.getSignature()))
            return 0;
        else
            return this.numTimesReferenced.get(parsedClass.getSignature());
    }

    private void addDirectReferenceTo(String classSignature) {
        if(!this.numTimesReferenced.containsKey(classSignature))
            this.numTimesReferenced.put(classSignature, 1);
        else {
            // Increment number of direct references;
            this.numTimesReferenced.put(classSignature, this.numTimesReferenced.get(classSignature) + 1);

            // Increment parents' number of indirect references;
            if(classMap.containsKey(classSignature)) {
                for (String parentSignature : this.classMap.get(classSignature).getParentSignatures()) {
                    this.addIndirectReferenceTo(parentSignature);
                }
            }
        }
    }

    private void addIndirectReferenceTo(String classSignature) {
        if(!this.NumTimesReferencedIndirectly.containsKey(classSignature))
            this.NumTimesReferencedIndirectly.put(classSignature, 1);
        else
            this.NumTimesReferencedIndirectly.put(classSignature, this.NumTimesReferencedIndirectly.get(classSignature) + 1);

        if(classMap.containsKey(classSignature)) {
            for (String parentSignature : this.classMap.get(classSignature).getParentSignatures()) {
                this.addIndirectReferenceTo(parentSignature);
            }
        }
    }

    public ParsedClass[] getClasses(){
        return classMap.values().toArray(new ParsedClass[classMap.size()]);
    }
}
