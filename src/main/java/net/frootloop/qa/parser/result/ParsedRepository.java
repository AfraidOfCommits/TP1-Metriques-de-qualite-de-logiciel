package net.frootloop.qa.parser.result;

import net.frootloop.qa.parser.util.GitGudder;
import net.frootloop.qa.parser.util.stats.commits.ParsedClassCommitComparator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ParsedRepository {

    private Path rootFilePath;
    private int totalLines, totalLinesComments, totalLinesEmpty, totalLinesCode, numAssertStatements, numSourceFiles, numMethods, numClasses, cyclomaticComplexity = 1;
    private ParsedClass mostComplexClass, mostReferencedClass, mostDirectlyReferencedClass, mostIndirectlyReferencedClass, leastCohesiveClass, classWithHighestMethodComplexity;
    private int mostAmountReferences, mostAmountDirectReferences, mostAmountIndirectReferences;

    /***
     * Map to store and fetch ParsedClass instances by their signature.
     */
    private HashMap<String, ParsedClass> classMap;

    /***
     * Map to store and fetch the amount of times a class has been referred (directly or indirectly) to by
     * another class, with the key being the class's signature.
     */
    private HashMap<String, Integer> mapNumTimesReferenced;

    /***
     * Map to store and fetch the amount of times a class has been directly referred to by
     * another class, with the key being the class's signature.
     */
    private HashMap<String, Integer> mapNumTimesReferencedDirectly;

    /***
     * Map to store and fetch the amount of times a parent class has been indirectly referred to by
     * a child class, with the key being the parent's signature.
     */
    private HashMap<String, Integer> mapNumTimesReferencedIndirectly;

    public ParsedRepository(Path filePath){
        this.rootFilePath = filePath;
        this.classMap = new HashMap<String, ParsedClass>();
        this.mapNumTimesReferenced = new HashMap<String, Integer>();
        this.mapNumTimesReferencedDirectly = new HashMap<String, Integer>();
        this.mapNumTimesReferencedIndirectly = new HashMap<String, Integer>();
    }

    public void addParsedFile(ParsedSourceFile parsedFile) {
        for (ParsedClass c: parsedFile.getClasses())
            this.addParsedClass(c);

        // Useful stats for LOC, CLOC, NLOC, NVLOC, NOM, and Testability:
        this.numSourceFiles += 1;
        this.totalLines += parsedFile.getNumLines();
        this.totalLinesComments += parsedFile.getNumLinesComments();
        this.totalLinesEmpty += parsedFile.getNumLinesEmpty();
        this.totalLinesCode += parsedFile.getNumLinesCode();
    }

    private void addParsedClass(ParsedClass parsedClass) {
        classMap.put(parsedClass.getSignature(), parsedClass);

        // Set the class' commit count:
        parsedClass.setNumCommits(GitGudder.getCommitCountTo(this, parsedClass));

        // Add to repo's stats (NOM, NOC):
        this.numClasses += 1;
        this.numMethods += parsedClass.getNumMethods() + parsedClass.getNumFunctions();
        this.numAssertStatements += parsedClass.getNumAssertStatements();

        // (Complexity) Add class' cyclomatic complexity:
        this.cyclomaticComplexity += parsedClass.getCyclomaticComplexity() - 1;
        if(mostComplexClass == null || mostComplexClass.getCyclomaticComplexity() < parsedClass.getCyclomaticComplexity())
            mostComplexClass = parsedClass;

        // (Complexity) Add class' WMC:
        if(classWithHighestMethodComplexity == null || parsedClass.getWeightedMethods() > this.classWithHighestMethodComplexity.getWeightedMethods())
            this.classWithHighestMethodComplexity = parsedClass;

        // (Modularity) Add class' LCOM:
        if(leastCohesiveClass == null || parsedClass.getLackOfCohesionInMethods() > this.leastCohesiveClass.getLackOfCohesionInMethods())
            this.leastCohesiveClass = parsedClass;
    }

    public ParsedClass getClassDataOf(String signature){
        return classMap.get(signature);
    }

    public void buildReferences() {
        // Add number of times each class is referenced by cycling through all ParsedClasses
        for (ParsedClass c : this.classMap.values())
            for (String referencedClass : c.getClassesReferencedDirectly())
                this.addDirectReferenceTo(referencedClass);

        // Add number of times each method was given a dedicated unit test:
        for (ParsedClass c : this.classMap.values()) {
            if(c.getMethodNamesTestedOutsideClass().size() == 0) continue;

            for (ParsedMethod m : c.getMethods()) {
                ArrayList<String> testedMethodNames = m.getTestedMethodNamesOutsideClass();
                ArrayList<ParsedClass> referencedClasses = this.getAllReferencedClassesOf(m);

                for (ParsedClass referencedClass : referencedClasses) {
                    for (String methodName : testedMethodNames) {
                        ParsedMethod referencedMethod = referencedClass.getMethodByName(methodName, false);
                        if(referencedMethod != null) referencedMethod.numDedicatedUnitTests++;
                    }
                }
            }
        }
    }

    private ArrayList<ParsedClass> getAllParentsOf(ParsedClass c) {
        ArrayList<ParsedClass> parents = new ArrayList<>();

        for (String parentSignature: c.getParentSignatures()) {
            if(this.classMap.containsKey(parentSignature)) {
                parents.add(this.classMap.get(parentSignature));
                parents.addAll(this.getAllParentsOf(this.classMap.get(parentSignature)));
            }
        }
        return parents;
    }

    private ArrayList<ParsedClass> getAllReferencedClassesOf(ParsedClass c) {
        ArrayList<ParsedClass> referenced = new ArrayList<>();
        for (String classSignature: c.getAllClassesReferenced()) {
            if (this.classMap.containsKey(classSignature)) {
                referenced.add(this.classMap.get(classSignature));
                referenced.addAll(this.getAllParentsOf(this.classMap.get(classSignature)));
            }
        }
        return referenced;
    }

    private ArrayList<ParsedClass> getAllReferencedClassesOf(ParsedMethod m) {
        ArrayList<ParsedClass> referenced = new ArrayList<>();
        for (String classSignature: m.getReferencedClasses()) {
            if (this.classMap.containsKey(classSignature)) {
                referenced.add(this.classMap.get(classSignature));
                referenced.addAll(this.getAllParentsOf(this.classMap.get(classSignature)));
            }
        }
        return referenced;
    }

    private void incrementNumReferencesTotal(String classSignature) {

        // Increment total number of references:
        int numReferences = 1;
        if(this.mapNumTimesReferenced.containsKey(classSignature))
            numReferences = this.mapNumTimesReferenced.get(classSignature) + 1;

        this.mapNumTimesReferenced.put(classSignature, numReferences);

        // Update the repo's class with the most amount of references:
        if(numReferences > mostAmountReferences && classMap.containsKey(classSignature)) {
            mostAmountReferences = numReferences;
            mostReferencedClass = this.classMap.get(classSignature);
        }
    }

    private void addDirectReferenceTo(String classSignature) {

        // Increment number of direct references:
        int numReferences = 1;
        if(this.mapNumTimesReferencedDirectly.containsKey(classSignature)) {
            numReferences = this.mapNumTimesReferencedDirectly.get(classSignature) + 1;

            // Increment parents' number of indirect references:
            if(classMap.containsKey(classSignature)) {
                for (String parentSignature : this.classMap.get(classSignature).getParentSignatures())
                    this.addIndirectReferenceTo(parentSignature);
            }
        }
        this.mapNumTimesReferencedDirectly.put(classSignature, numReferences);
        this.incrementNumReferencesTotal(classSignature);

        // Update the repo's class with the most amount of references:
        if(numReferences > mostAmountDirectReferences && classMap.containsKey(classSignature)) {
            mostAmountDirectReferences = numReferences;
            mostDirectlyReferencedClass = this.classMap.get(classSignature);
        }
    }

    private void addIndirectReferenceTo(String classSignature) {

        // Increment number of indirect references:
        int numReferences = 1;
        if(this.mapNumTimesReferencedIndirectly.containsKey(classSignature))
            numReferences = this.mapNumTimesReferencedIndirectly.get(classSignature) + 1;

        this.mapNumTimesReferencedIndirectly.put(classSignature, numReferences);
        this.incrementNumReferencesTotal(classSignature);

        // Recursively add indirect references to the class's ancestors:
        if(classMap.containsKey(classSignature)) {
            for (String parentSignature : this.classMap.get(classSignature).getParentSignatures())
                this.addIndirectReferenceTo(parentSignature);
        }

        // Update the repo's class with the most amount of indirect references:
        if(numReferences > mostAmountIndirectReferences  && classMap.containsKey(classSignature)) {
            mostAmountIndirectReferences = numReferences;
            mostIndirectlyReferencedClass = this.classMap.get(classSignature);
        }
    }

    public int getNumTimesReferenced(ParsedClass parsedClass) {
        if(!this.mapNumTimesReferenced.containsKey(parsedClass.getSignature())) return 0;
        else return this.mapNumTimesReferenced.get(parsedClass.getSignature());
    }

    public int getNumTimesReferencedDirectly(ParsedClass parsedClass) {
        if(!this.mapNumTimesReferencedDirectly.containsKey(parsedClass.getSignature())) return 0;
        else return this.mapNumTimesReferencedDirectly.get(parsedClass.getSignature());
    }

    public int getNumTimesReferencedIndirectly(ParsedClass parsedClass) {
        if(!this.mapNumTimesReferencedIndirectly.containsKey(parsedClass.getSignature())) return 0;
        else return this.mapNumTimesReferencedIndirectly.get(parsedClass.getSignature());
    }

    public ParsedClass[] getClasses(){
        return classMap.values().toArray(new ParsedClass[classMap.size()]);
    }

    public ParsedClass getMostComplexClass() {
        return mostComplexClass;
    }

    public ParsedClass getMostReferencedClass() {
        return mostReferencedClass;
    }

    public ParsedClass getMostDirectlyReferencedClass() {
        return mostDirectlyReferencedClass;
    }

    public ParsedClass getMostIndirectlyReferencedClass() {
        return mostIndirectlyReferencedClass;
    }

    public ParsedClass getLeastCohesiveClass() {
        return leastCohesiveClass;
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

    public Path getFilePath() {
        return this.rootFilePath;
    }

    public ParsedClass getMostCommittedClass() {
        ParsedClass mostCommitted = null;
        int max = Integer.MIN_VALUE;
        for (ParsedClass c : this.classMap.values())
            if(c.getNumCommits() > max) mostCommitted = c;
        return mostCommitted;
    }

    public ParsedClass getLeastCommittedClass() {
        ParsedClass leastCommitted = null;
        int min = Integer.MAX_VALUE;
        for (ParsedClass c : this.classMap.values())
            if(c.getNumCommits() < min) leastCommitted = c;
        return leastCommitted;
    }

    public List<ParsedClass> getClassesSortedByCommits() {
        return this.classMap.values().stream().sorted(new ParsedClassCommitComparator()).collect(Collectors.toList());
    }

    public float getAverageCommitsPerClass() {
        float average = 0;
        for (ParsedClass c : this.classMap.values())
            average += c.getNumCommits();

        return average / (float)this.numClasses;
    }

    public float getAverageLackOfCohesionInMethods() {
        float average = 0;
        for (ParsedClass c : this.classMap.values())
            average += c.getLackOfCohesionInMethods();

        return average / (float)this.numClasses;
    }

    public float getAverageUnitTestsPerMethod() {
        int sum = 0;
        for (ParsedClass c : this.classMap.values())
            for (ParsedMethod m : c.getMethods())
                sum += m.numDedicatedUnitTests;

        return (float) sum / (float) this.numMethods;
    }

    public float getAverageWeightedMethods() {
        float average = 0;
        for (ParsedClass c : this.classMap.values())
            average += c.getWeightedMethods();

        return average / (float)this.numClasses;
    }

    public float getAverageCouplageBetweenClasses() {
        float average = 0;
        for (ParsedClass c : this.classMap.values())
            if(this.mapNumTimesReferenced.containsKey(c.getSignature()))
                average += this.mapNumTimesReferenced.get(c.getSignature());

        return average / (float)this.numClasses;
    }

    public float getPercentageMethodsUntested() {
        int numMethodsUntested = 0;
        for (ParsedClass c : this.classMap.values())
            for(ParsedMethod m : c.getMethods())
                if(!m.isTest() && !m.isPrivate() && !m.isAbstract() && m.numDedicatedUnitTests == 0)
                    numMethodsUntested++;

        return 100 * (float)numMethodsUntested / (float)this.numMethods;
    }

    public float getPercentageCodeDedicatedForTests() {
        float percentage = 0;
        for (ParsedClass c : this.classMap.values())
            percentage += c.getPercentageCodeDedicatedToTests();

        return percentage / (float)this.numClasses;
    }

    public float getCommentDensity() {
        return (float)this.totalLinesComments / (float)(this.totalLinesCode + totalLinesComments);
    }

    public int getNumClasses() {
        return numClasses;
    }

    public int getNumMethods() {
        return numMethods;
    }

    public int getNumSourceFiles(){
        return numSourceFiles;
    }
}
