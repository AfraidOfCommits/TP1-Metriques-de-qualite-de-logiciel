package net.frootloop.qa.parser.result;

import java.nio.file.Path;
import java.util.HashMap;

public class ParsedRepository {

    private Path rootFilePath;
    private int totalLines;
    private int totalLinesComments;
    private int totalLinesEmpty;
    private int totalLinesCode;
    private int numAssertStatements;
    private int cyclomaticComplexity;
    private ParsedClass mostComplexClass;
    private ParsedClass mostReferencedClass;
    private int mostAmountReferences;
    private ParsedClass mostIndirectlyReferencedClass;
    private int mostAmountIndirectReferences;

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
        this.classMap = new HashMap<String, ParsedClass>();
        this.numTimesReferenced = new HashMap<String, Integer>();
        this.NumTimesReferencedIndirectly = new HashMap<String, Integer>();
    }

    public void addParsedFile(ParsedSourceFile parsedFile) {
        for (ParsedClass c: parsedFile.getClasses()) {
            classMap.put(c.getSignature(), c);
            this.cyclomaticComplexity += c.getCyclomaticComplexity();
            if(mostComplexClass == null || mostComplexClass.getCyclomaticComplexity() < c.getCyclomaticComplexity())
                mostComplexClass = c;
        }
        this.totalLines += parsedFile.getNumLines();
        this.totalLinesComments += parsedFile.getNumLinesComments();
        this.totalLinesEmpty += parsedFile.getNumLinesEmpty();
        this.totalLinesCode += parsedFile.getNumLinesCode();
        this.numAssertStatements += parsedFile.getNumAssertStatements();
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

        // Increment number of direct references:
        int numReferences;
        if(!this.numTimesReferenced.containsKey(classSignature))
            numReferences = 1;
        else {
            numReferences = this.numTimesReferenced.get(classSignature) + 1;

            // Increment parents' number of indirect references:
            if(classMap.containsKey(classSignature)) {
                for (String parentSignature : this.classMap.get(classSignature).getParentSignatures())
                    this.addIndirectReferenceTo(parentSignature);
            }
        }
        this.numTimesReferenced.put(classSignature, numReferences);

        // Update the class with the most amount of references:
        if(numReferences > mostAmountReferences) {
            mostAmountReferences = numReferences;
            mostReferencedClass = this.classMap.get(classSignature);
        }
    }

    private void addIndirectReferenceTo(String classSignature) {

        // Increment number of indirect references:
        int numReferences;
        if(!this.NumTimesReferencedIndirectly.containsKey(classSignature)) numReferences = 1;
        else numReferences = this.NumTimesReferencedIndirectly.get(classSignature) + 1;
        this.NumTimesReferencedIndirectly.put(classSignature, numReferences);

        // Recursively add indirect references to the class's ancestors:
        if(classMap.containsKey(classSignature)) {
            for (String parentSignature : this.classMap.get(classSignature).getParentSignatures())
                this.addIndirectReferenceTo(parentSignature);
        }

        // Update the class with the most amount of indirect references:
        if(numReferences > mostAmountIndirectReferences) {
            mostAmountIndirectReferences = numReferences;
            mostIndirectlyReferencedClass = this.classMap.get(classSignature);
        }
    }

    public ParsedClass[] getClasses(){
        return classMap.values().toArray(new ParsedClass[classMap.size()]);
    }

    public ParsedClass getMostComplexClass() {
        return mostComplexClass;
    }

    public int getTotalCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public int getTotalLines() {
        return totalLines;
    }

    public int getTotalLinesCode() {
        return totalLinesCode;
    }

    public int getTotalLinesComments() {
        return totalLinesComments;
    }

    public int getTotalLinesEmpty() {
        return totalLinesEmpty;
    }

    public int getNumAssertStatements() {
        return numAssertStatements;
    }
}
