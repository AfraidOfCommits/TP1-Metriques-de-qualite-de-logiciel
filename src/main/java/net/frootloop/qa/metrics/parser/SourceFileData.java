package net.frootloop.qa.metrics.parser;

import net.frootloop.qa.metrics.parser.result.ParsedClass;

import java.util.ArrayList;
import java.util.LinkedList;

public class SourceFileData {

        public String packageName, mainClassName, filePath;
        public int numLines, numLinesEmpty, numLinesComments;

        // List of classes found within the source file (one file can declare multiple nested classes)
        public ArrayList<ParsedClass> classes = new ArrayList<>();
        public ArrayList<String> importStatements;
        public String textData = null;
        public LinkedList<String[]> codeBlocks = null;

        public void addNewLineOfText(String lineOfText){
            this.numLines += 1;

            // Replace strings with a generic value:
            lineOfText = lineOfText.replaceAll("\\\"[^\\\"]*\\\"", "\"(string value)\"]");

            // Clean up the line a bit by removing extraneous spaces:
            lineOfText = lineOfText.replaceAll("\\s+", " ");
            lineOfText = lineOfText.replaceAll(";\\s", ";");
            lineOfText = lineOfText.replaceAll("}\\s", "}");

            // If the line is a single-line comment (like this one!):
            if(lineOfText.matches("/[[:blank:]]*\\/{2,}/gm"))
                this.numLinesComments += 1;

                // If the line is just empty:
            else if (lineOfText.matches("/\\A[[:blank:]]*\\Z/gm"))
                this.numLinesEmpty += 1;

                // Only add actual lines of code to the output:
            else {
                lineOfText = lineOfText.replaceAll("\\/\\/.*", ""); // Remove comments appended to code (like this one!)
                this.textData += lineOfText;
            }
        }

        public LinkedList<String[]> getCode(){
            if(this.codeBlocks == null && this.textData != null) {
                this.generateCodeFromTextData();
            }
            return this.codeBlocks;
        }

        private void generateCodeFromTextData(){
            this.cleanUpTextData();
            this.codeBlocks = new LinkedList<>();
            for (String nestedCodeBlock : this.textData.split("[\\{\\}]")) {
                codeBlocks.add(nestedCodeBlock.split(";"));
            }
        }

        private void cleanUpTextData(){
            // Remove null chars:
            this.textData = textData.replaceAll("\0", " ");

            // Remove multiline comments:
            this.textData = textData.replaceAll("(\\/\\*).*(\\*\\/)", " ");

            // Remove extra spaces:
            this.textData = textData.replaceAll("\\s+", " ");
        }
    }