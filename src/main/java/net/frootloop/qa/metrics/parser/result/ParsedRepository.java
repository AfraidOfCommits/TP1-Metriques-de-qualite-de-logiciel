package net.frootloop.qa.metrics.parser.result;

import java.util.HashMap;

public class ParsedRepository {

    public String rootFilePath;
    public int totalLines;
    public int totalEmptyLines;

    private HashMap<String,ParsedClassData> classMap;

    public ParsedRepository(String filePath){
        this.rootFilePath = filePath;
        this.totalLines = 0;
        this.totalEmptyLines = 0;
        this.classMap = new HashMap<String,ParsedClassData>();
    }

    public boolean isInRepo(String signature){
        return classMap.containsKey(signature);
    }

    public void addToRepo(String signature, ParsedClassData data){
        classMap.put(signature, data);
    }

    public ParsedClassData getClassDataOf(String signature){
        return classMap.get(signature);
    }
}
