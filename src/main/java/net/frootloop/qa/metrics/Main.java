package net.frootloop.qa.metrics;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        String text = "public class Main extends Something, SomethingElse, Blub implements Babyyyy";

        Pattern pattern = Pattern.compile("(extends|implements)\\s(\\w+((\\s)*,\\s\\w+)*)*");
        Matcher matcher = pattern.matcher(text);
        // Check all occurrences

        int i = 0;
        while (matcher.find()) {
            System.out.println(i + " : " + matcher.group(2));
        }
    }
}
