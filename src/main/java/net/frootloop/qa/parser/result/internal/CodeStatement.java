package net.frootloop.qa.parser.result.internal;

public class CodeStatement {
    public final String code;
    public final boolean isLeadingStatement, isClosingStatement;
    public final int numLines, numLinesCode, numLinesComments, numLinesEmpty;

    public CodeStatement(String code, boolean isLeadingStatement, boolean isClosingStatement, int numLines, int numLinesCode, int numLinesComments, int numLinesEmpty) {
        this.code = code;
        this.isLeadingStatement = isLeadingStatement;
        this.isClosingStatement = isClosingStatement;
        this.numLines = numLines;
        this.numLinesCode = numLinesCode;
        this.numLinesComments = numLinesComments;
        this.numLinesEmpty = numLinesEmpty;
    }
}
