package net.frootloop.qa.parser.result.internal;

import net.frootloop.qa.parser.util.strings.CodeParser;
import net.frootloop.qa.parser.result.ParsedClass;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeTree implements CodeParser {

    protected BlockOfCode root;

    /**
     * The source code is organized as a tree, where blocks of code (i.e. curly braces) are the nodes and where each block is represented by
     * its contained statements, and its leading statement. This allows us to attribute proper class/method ownership, and do fancy things like
     * print a .java file's entire cleaned up source code with proper indentation.
     *
     * @param codeStatements : List of CodeStatement objects containing filtered data on the line of code, its comments, and its closing char ('{', '}', or ';')
     */
    public CodeTree(List<CodeStatement> codeStatements) {

        BlockOfCode currentCodeBlock = new BlockOfCode();
        this.root = currentCodeBlock;
        this.root.leadingStatement = codeStatements.get(0).code;
        this.root.numLines += codeStatements.get(0).numLines;
        this.root.numLinesCode += codeStatements.get(0).numLinesCode;
        this.root.numLinesComments += codeStatements.get(0).numLinesComments;

        // Serves no purpose besides making sure that the Garbage collector
        ArrayList<BlockOfCode> cache = new ArrayList<>();
        cache.add(currentCodeBlock);

        BlockOfCode newBlock;
        CodeStatement codeStatement;
        for(int i = 1; i < codeStatements.size(); i++) {

            codeStatement = codeStatements.get(i);

            // If the current code statement opens a pair of curly braces, we set that as a new block of code:
            if(codeStatement.isLeadingStatement) {

                // Start a new code block, imbedded in the previous one:
                newBlock = new BlockOfCode();
                newBlock.parent = currentCodeBlock;
                currentCodeBlock.children.add(newBlock);
                currentCodeBlock = newBlock;

                // Add the current statement to the current code block as its leading statement;
                currentCodeBlock.leadingStatement = codeStatement.code;
            }

            // If the current code statement CLOSES a pair of curly braces, we return to the previous scope:
            else {
                // Add the statement to the current block of code:
                if(codeStatement.numLinesCode > 0 && !codeStatement.code.equals("")) currentCodeBlock.codeStatements.add(codeStatement.code);

                // If the current code statement CLOSES a pair of curly braces, we return to the previous scope:
                if(codeStatement.isClosingStatement) currentCodeBlock = currentCodeBlock.parent;
            }

            // Increment the current code block's line counts with its new statement:
            currentCodeBlock.numLines += codeStatement.numLines;
            currentCodeBlock.numLinesCode += codeStatement.numLinesCode;
            currentCodeBlock.numLinesComments += codeStatement.numLinesComments;
        }
    }

    protected CodeTree(BlockOfCode root) {
        this.root = root;
    }

    public void print() {
        System.out.println(root.toString());
        System.out.println("Has " + this.getNumLines() + " lines; " + this.getNumLinesCode() + " of which are code, " + this.getNumLinesComments() + " of which are comments, and " + this.getNumLinesEmpty() + " of which are empty.");
    }

    public String toString() {return this.root.toString();}

    public int getNumLines() {
        return this.root.getNumLines();
    }

    public int getNumLinesEmpty() {
        return this.root.getNumLinesEmpty();
    }

    public int getNumLinesCode() {
        return this.root.getNumLinesCode();
    }

    public int getNumLinesComments() {
        return this.root.getNumLinesComments();
    }

    public int getCyclomaticComplexity() {
        return 1 + root.getCyclomaticComplexity();
    }

    public ArrayList<ParsedClass> getListOfClasses(String packageName, Path filePath, String[] importStatements) {
        return this.root.generateParsedClasses(packageName, filePath, importStatements);
    }

    public class BlockOfCode {

        public BlockOfCode parent = null;
        public ArrayList<BlockOfCode> children = new ArrayList<>();
        public String leadingStatement;
        public ArrayList<String> codeStatements = new ArrayList<>();

        public int numLines = 0, numLinesCode = 0, numLinesComments = 0;

        public int getNumChildren(){
            int numChildren = children.size(); // i.e. degree
            for (BlockOfCode child : children) numChildren += child.getNumChildren();
            return numChildren;
        }

        public int getNumStatements(){
            int numStatements = 1 + this.codeStatements.size();
            for (BlockOfCode child : children) numStatements += child.getNumStatements();
            return numStatements;
        }

        public int getNumLines(){
            int numLines = this.numLines;
            for (BlockOfCode child : children) numLines += child.getNumLines();
            return numLines;
        }

        public int getNumLinesCode(){
            int numLines = this.numLinesCode;
            for (BlockOfCode child : children) numLines += child.getNumLinesCode();
            return numLines;
        }

        public int getNumLinesComments(){
            int numLines = this.numLinesComments;
            for (BlockOfCode child : children) numLines += child.getNumLinesComments();
            return numLines;
        }

        public int getNumLinesEmpty(){
            int numLines = this.numLines - this.numLinesCode - this.numLinesComments;
            for (BlockOfCode child : children) numLines += child.getNumLinesEmpty();
            return numLines;
        }

        public ArrayList<String> getDeclaredVariables() {
            ArrayList<String> listOfVariables = new ArrayList<>();
            for (String codeStatement:this.codeStatements)
                if(CodeParser.isVariableDeclaration(codeStatement))
                    listOfVariables.addAll(Arrays.asList(CodeParser.getDeclaredVariableNames(codeStatement)));

            return listOfVariables;
        }

        public int getCyclomaticComplexity() {
            int complexity = CodeParser.isBranchingStatement(leadingStatement) ? 1 : 0;

            for(String codeLine : codeStatements) {
                if(CodeParser.isBranchingStatement(codeLine) || CodeParser.isTernaryStatement(codeLine))
                    complexity += 1;
                else if(leadingStatement.matches("switch.*") && codeLine.matches("(default|case\\s*\\w+\\s*):.*"))
                    complexity += 1;
            }

            for (BlockOfCode child : children) complexity += child.getCyclomaticComplexity();
            return complexity;
        }

        protected ArrayList<ParsedClass> generateParsedClasses(String packageName, Path filePath, String[] importStatements) {
            ArrayList<ParsedClass> listOfClasses = new ArrayList<>();
            root.generateParsedClasses(listOfClasses, packageName, filePath, importStatements);
            return listOfClasses;
        }

        private void generateParsedClasses(ArrayList<ParsedClass> listOfClasses, String packageName, Path filePath, String[] importStatements) {

            // If the current block is a class:
            if(CodeParser.isClassDeclaration(this.leadingStatement)) {
                listOfClasses.add(new ParsedClass(this, packageName, importStatements, filePath));
                packageName = packageName + "." + CodeParser.getDeclaredClassName(this.leadingStatement);
            }

            // Recursive call:
            for(BlockOfCode child: this.children)
                child.generateParsedClasses(listOfClasses, packageName, filePath, importStatements);
        }


        public String getCodeAsString() {
            return this.toString("", false);
        }


        public String getCodeAsString(boolean shouldBeautify) {
            return this.toString("", shouldBeautify);
        }


        public String toString() {
            return this.toString("", true);
        }

        private String toString(String indentation, boolean shouldBeautify) {
            String str = "";

            if(shouldBeautify && CodeParser.isClassDeclaration(this.leadingStatement)){
                str += "\n\n" + indentation + "(CLASS: " + CodeParser.getDeclaredClassName(leadingStatement);

                List<String> inheritance = CodeParser.getDeclaredClassInheritance(leadingStatement);
                if(inheritance.size() > 0) str += "\n" + indentation + " --> with parents: " + String.join(",", inheritance);

                List<String> attributes = this.getDeclaredVariables();
                if(attributes.size() > 0) str += "\n" + indentation + " --> with attributes: " + String.join(",", attributes);

                str += ")";
            }

            String childIndentation = shouldBeautify ? indentation + "    " : "";
            indentation = shouldBeautify ? indentation : "";

            str += "\n" + indentation + this.leadingStatement + " {";
            for(String s : this.codeStatements) str += "\n" + childIndentation  + s + ";";
            for(BlockOfCode c : this.children) str += c.toString(childIndentation, shouldBeautify);
            str += "\n" + indentation + "}";
            return str;
        }
    }
}
