package net.frootloop.qa.parser.result.internal;

import net.frootloop.qa.parser.StringParser;
import net.frootloop.qa.parser.result.ParsedClass;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeTree implements StringParser {

    protected BlockOfCode root;

    /**
     * The source code is organized as a tree, where blocks of code (i.e. curly braces) are the nodes and where each block is represented by
     * its contained statements, and its leading statement. This allows us to attribute proper class/method ownership, and do fancy things like
     * print a .java file's entire cleaned up source code with proper indentation.
     *
     * @param cleanSourceFileTextData Assumes that the input text has already been cleaned up.
     */
    public CodeTree(String cleanSourceFileTextData) {

        String[] codeStatements = cleanSourceFileTextData.split("(;|\\{|\\})");
        String statement;
        int statementIndex = 0;

        BlockOfCode newBlock= new BlockOfCode();
        BlockOfCode currentCodeBlock = new BlockOfCode();
        this.root = currentCodeBlock;
        this.root.leadingStatement = codeStatements[0];

        ArrayList<BlockOfCode> blocksBackup = new ArrayList<>();
        blocksBackup.add(this.root);

        for(int i = 0; i < cleanSourceFileTextData.length(); i++) {
            char c = cleanSourceFileTextData.charAt(i);

            // Ending a statement:
            if(c == ';' || c == '{' || c == '}') {
                if(statementIndex < codeStatements.length) statement = codeStatements[statementIndex++];
                else statement = "";

                if(c == '{') {

                    // Start a new code block, imbedded in the previous one:
                    newBlock = new BlockOfCode();
                    blocksBackup.add(newBlock);

                    currentCodeBlock.children.add(newBlock);
                    newBlock.parent = currentCodeBlock;
                    currentCodeBlock = newBlock;

                    // Add the current statement to the current code block as its leading statement;
                    currentCodeBlock.leadingStatement = statement;
                }
                else {
                    // Add the statement to the current block of code:
                    if(!statement.matches("\\s*"))
                        currentCodeBlock.codeStatements.add(statement);

                    // End the current code block:
                    if(currentCodeBlock == null) System.out.println("WTF!! " + this.root.getCodeAsString());
                    if(c == '}')
                        currentCodeBlock = currentCodeBlock.parent;
                }
            }
        }
    }

    protected CodeTree(BlockOfCode root) {
        this.root = root;
    }

    public void print() {
        System.out.println(root.toString());
    }

    public String toString() {return this.root.toString();}

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

        public ArrayList<String> getDeclaredVariables() {
            ArrayList<String> listOfVariables = new ArrayList<>();
            for (String codeStatement:this.codeStatements)
                if(StringParser.isVariableDeclaration(codeStatement))
                    listOfVariables.addAll(Arrays.asList(StringParser.getDeclaredVariableNames(codeStatement)));

            return listOfVariables;
        }

        public int getCyclomaticComplexity() {
            int complexity = StringParser.isBranchingStatement(leadingStatement) ? 1 : 0;

            for(String codeLine : codeStatements) {
                if(StringParser.isBranchingStatement(codeLine) || StringParser.isTernaryStatement(codeLine))
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
            if(StringParser.isClassDeclaration(this.leadingStatement)) {
                listOfClasses.add(new ParsedClass(this, packageName, importStatements, filePath));
                packageName = packageName + "." + StringParser.getDeclaredClassName(this.leadingStatement);
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

            if(shouldBeautify && StringParser.isClassDeclaration(this.leadingStatement)){
                str += "\n\n" + indentation + "(CLASS: " + StringParser.getDeclaredClassName(leadingStatement);

                List<String> inheritance = StringParser.getDeclaredClassInheritance(leadingStatement);
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
