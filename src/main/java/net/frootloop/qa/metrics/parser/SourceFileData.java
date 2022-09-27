package net.frootloop.qa.metrics.parser;

import java.util.regex.Pattern;

public class SourceFileData {

    static Pattern rxMultilineComment = Pattern.compile("/(\\/\\*).*(\\*\\/)/gm",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    static Pattern rxEmptySpaces = Pattern.compile("/[[:blank:]]{2,}/gm",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    public int numLines, numLinesEmpty, numLinesComments;
    public String data;

    public void cleanUpData(){
        this.removeNullChars();
        this.removeMultilineComments();
        this.removeExtraSpaces();
    }

    private void removeNullChars(){
        this.data = data.replaceAll("null", " ");
        this.data = data.replaceAll("\0", " ");
    }

    private void removeMultilineComments(){
        this.data = data.replaceAll("(\\/\\*).*(\\*\\/)", " ");
    }

    private void removeExtraSpaces(){
        this.data = data.replaceAll("[\\t\\s]+", " ");
    }
}
