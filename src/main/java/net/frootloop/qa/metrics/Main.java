package net.frootloop.qa.metrics;

import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {

        String text = "1 - aaaaa;2 - aaaaa;3 - aaaaa {1 - bbbbb;2 - bbbbb{1 - ccccc;2 = ccccc}1 - ddddd}1 - eeeee;";
        LinkedList<String[]> codeBlocks = new LinkedList<>();
        int i = 0;

        for (String nestedCode : text.split("[\\{\\}]")) {

            System.out.println("Statements: " + nestedCode + " \\ End.");
            codeBlocks.add(nestedCode.split(";"));
        }

        for (String[] block : codeBlocks) {
            for (String statement : block) {
                System.out.println(statement);
            }
            System.out.println();
        }
    }
}
