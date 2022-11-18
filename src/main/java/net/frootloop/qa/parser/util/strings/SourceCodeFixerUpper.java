
package net.frootloop.qa.parser.util.strings;

import net.frootloop.qa.parser.result.internal.CodeStatement;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface SourceCodeFixerUpper {

    static String removeEmptyLines(String inputStr) {
        inputStr = inputStr.replaceAll("(\r\n|\r|\n)( )*(\r\n|\r|\n)", "\n");
        inputStr = inputStr.replaceAll("(\r\n|\r|\n)+", "\n");
        return inputStr;
    }

    static int getLineCountOf(String inputStr) {
        Matcher m = Pattern.compile("\r\n|\r|\n").matcher(inputStr);
        int lines = 1;
        while (m.find()) lines ++;
        return lines;
    }

    /***
     * Remove unnecessary spaces, null chars, normalize line breaks, etc.
     * Also replace string and comments with generic values, such as '"text"' and '// // Single-line comment;' respectively.
     *
     * @param sourceFileTextData
     * @return Clean version of source file's text data.
     */
    static String cleanUpSource(String sourceFileTextData) {

        // Get with normalized line break chars:
        sourceFileTextData = sourceFileTextData.replaceAll("\r\n|\r|\n", "\n");

        // Remove null chars:
        sourceFileTextData = sourceFileTextData.replaceAll("\0", "");

        // Remove unnecessary semicolons that follow up a closed curly bracket:
        sourceFileTextData = sourceFileTextData.replaceAll("\\}( *);", "}");

        // Remove extra spaces:
        sourceFileTextData = SourceCodeFixerUpper.getWithoutExtraSpaces(sourceFileTextData);

        // Now to the meaty stuff, aka modifying values in the source's code;
        sourceFileTextData = SourceCodeFixerUpper.getWithGenericStringValues(sourceFileTextData);
        sourceFileTextData = SourceCodeFixerUpper.getWithGenericCommentValues(sourceFileTextData);
        sourceFileTextData = SourceCodeFixerUpper.getWithSingleBracketTryCatch(sourceFileTextData);

        return sourceFileTextData;
    }

    static String getWithoutExtraSpaces(String inputStr) {
        inputStr = inputStr.replaceAll(" +", " "); // Replaces multiple spaces by only a single
        inputStr = inputStr.replaceAll("^ +", ""); // Removes spaces at the beginning of inputStr
        inputStr = inputStr.replaceAll(" +$", ""); // Removes spaces at the end of inputStr
        inputStr = inputStr.replaceAll(" *\n *", "\n"); // Removes spaces around line breaks
        inputStr = inputStr.replaceAll(" *\\{ *", "{"); // Removes spaces that surround a '{' char
        inputStr = inputStr.replaceAll(" *; *", ";"); // Removes spaces that surround a ';' char
        return inputStr;
    }

    static String getWithGenericStringValues(String inputStr) {
        inputStr = inputStr.replaceAll("\\\'(.)\\\'", "\'char\'");
        return inputStr.replaceAll("\\\"([^\\\"]*(\\\\\\\")*)*\\\"", "\"text\"");
    }

    static String getWithoutComments(String inputStr) {
        inputStr = getWithoutSingleLineComments(inputStr);
        inputStr = getWithoutAppendedComments(inputStr);
        inputStr = getWithoutDocstrings(inputStr);
        return inputStr;
    }

    static String getWithGenericCommentValues(String inputStr) {
        inputStr = getWithoutAppendedComments(inputStr);
        inputStr = getWithGenericSingleLineComments(inputStr, "// Single-line comment\n");
        inputStr = getWithGenericDocstrings(inputStr, "*\n");
        return inputStr;
    }

    static String getWithoutAppendedComments(String inputStr) {
        return inputStr.replaceAll("\\/\\/((?!\\*\\/).)*\n", "\n"); // Remove comments appended to code (like this one!)
    }

    static String getWithoutSingleLineComments(String inputStr) {
        return SourceCodeFixerUpper.getWithGenericSingleLineComments(inputStr, "");
    }

    static String getWithGenericSingleLineComments(String inputStr, String replaceWith) {
        return inputStr.replaceAll("\\/\\/.*(\n|$)", replaceWith);
    }

    static String getWithoutDocstrings(String inputStr) {
        return SourceCodeFixerUpper.getWithGenericDocstrings(inputStr, "");
    }

    static String getWithGenericDocstrings(String inputStr, String replaceWith) {
        // Modify multiline comments & docstrings
        // Avoid a stack overflow error! Some source files are so ridiculously well documented
        // that using a replaceAll call takes up the entire goddamn stack.
        if(inputStr.length() < 3) return inputStr;
        String withGenericDocstrings = "";
        int startOfCode = 0, startOfDocstring;
        do {

            // Find the start of a /* docstring:
            startOfDocstring = inputStr.indexOf("/*", startOfCode);
            if(startOfDocstring == -1) startOfDocstring = inputStr.length();

            // Append:
            withGenericDocstrings += inputStr.substring(startOfCode, startOfDocstring);

            // Find the end of the */ docstring:
            startOfCode = inputStr.indexOf("*/", startOfDocstring);
            if(startOfCode == -1) startOfCode = inputStr.length() - 1;
            else if(startOfCode + 2 < inputStr.length() && replaceWith.equals("")) {

                // If the docstring ends with a line break, we'll remove it as well;
                String charAfterDocstring = inputStr.substring(startOfCode + 2, startOfCode + 3);
                if(charAfterDocstring.equals("\n")) startOfCode += 3;

            }
            else startOfCode += 2;

            // Append the docstring as a single line:
            if(startOfCode < inputStr.length() - 1 && !replaceWith.equals("")) {
                int numLines = Math.max(0, SourceCodeFixerUpper.getLineCountOf(inputStr.substring(startOfDocstring, startOfCode)) - 2);
                withGenericDocstrings += "/*\n" + new String(new char[numLines]).replace("\0", replaceWith) + "*/";
            }

        } while(startOfCode < inputStr.length() - 1);
        return withGenericDocstrings;
    }

    static String getWithSingleBracketTryCatch(String inputStr){
        return inputStr.replaceAll("\\}\\s*catch\\s*(.*)\\s*\\{", "catch -> ");
    }

    static String getWithoutLineBreaks(String inputStr) {
        inputStr = inputStr.replaceAll("\r\n|\r|\n", " ");
        return SourceCodeFixerUpper.getWithoutExtraSpaces(inputStr);
    }

    static ArrayList<CodeStatement> getCodeStatementsOf(String cleanSourceFileText) {

        ArrayList<CodeStatement> codeStatements = new ArrayList<>();

        int indexOfSourceFileText = -1;
        for(String statementStr : cleanSourceFileText.split(";|\\{|\\}")) {

            indexOfSourceFileText += statementStr.length() + 1;
            char endOfStatementChar = indexOfSourceFileText >= cleanSourceFileText.length() ? ' ' : cleanSourceFileText.charAt(indexOfSourceFileText);
            boolean isLeadingStatement = endOfStatementChar == '{';
            boolean isClosingStatement = endOfStatementChar == '}';

            // Count the number of line break chars in this statement;
            int numLines = SourceCodeFixerUpper.getLineCountOf(statementStr) - 1;

            // Remove comments from the statement, then get the number of lines removed and set as numLinesComments;
            statementStr = SourceCodeFixerUpper.getWithoutComments(statementStr);
            int numLinesComments = numLines - (SourceCodeFixerUpper.getLineCountOf(statementStr) - 1);

            // Remove empty lines from the statement, then get the number of lines removed and set as numLinesEmpty;
            statementStr = SourceCodeFixerUpper.removeEmptyLines(statementStr);
            int numLinesEmpty = numLines - numLinesComments - (SourceCodeFixerUpper.getLineCountOf(statementStr) - 1);

            // Get the number of lines of code in the statement;
            int numLinesCode = 0;
            if(endOfStatementChar != ' ') numLinesCode = numLines - numLinesEmpty - numLinesComments;
            else numLinesEmpty += numLines - numLinesEmpty - numLinesComments;

            // Clean up the code and create a new CodeStatement object with it;
            statementStr = SourceCodeFixerUpper.getWithoutLineBreaks(statementStr);
            statementStr = SourceCodeFixerUpper.getWithoutExtraSpaces(statementStr);
            codeStatements.add(new CodeStatement(statementStr, isLeadingStatement, isClosingStatement, numLines, numLinesCode, numLinesComments, numLinesEmpty));

        }
        return codeStatements;
    }
}
