package net.frootloop.qa;

import net.frootloop.qa.inputhandling.FilePathHandler;
import net.frootloop.qa.inputhandling.GitGudder;
import net.frootloop.qa.inputhandling.InputHandler;
import net.frootloop.qa.parser.JavaSourceFileParser;
import net.frootloop.qa.parser.StringParser;

import java.io.IOException;

// test;

public class Test extends JavaSourceFileParser implements StringParser, GitGudder, FilePathHandler, InputHandler {

    private String test1;
    private String test2;

    private class Pouet {
        private class PouetSquared {

        }
    }

    public static void main(String[] args) throws IOException {

        String method = "/* ===========================================================\n" +
                "* JFreeChart : a free chart library for the Java(tm) platform\n" +
                "* ===========================================================\n" +
                "*\n" +
                "* (C) Copyright 2000-2022, by David Gilbert and Contributors.\n" +
                "*\n" +
                "* Project Info: http:*\n" +
                "* This library is free software;you can redistribute it and/or modify it\n" +
                "* under the terms of the GNU Lesser General Public License as published by\n" +
                "* the Free Software Foundation;either version 2.1 of the License, or\n" +
                "* (at your option) any later version.\n" +
                "*\n" +
                "* This library is distributed in the hope that it will be useful, but\n" +
                "* WITHOUT ANY WARRANTY;without even the implied warranty of MERCHANTABILITY\n" +
                "* or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public\n" +
                "* License for more details.\n" +
                "*\n" +
                "* You should have received a copy of the GNU Lesser General Public\n" +
                "* License along with this library;if not, write to the Free Software\n" +
                "* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,\n" +
                "* USA.\n" +
                "*\n" +
                "* [Oracle and Java are registered trademarks of Oracle and/or its affiliates.\n" +
                "* Other names may be trademarks of their respective owners.]\n" +
                "*\n" +
                "* -----------------------\n" +
                "* AbstractAnnotation.java\n" +
                "* -----------------------\n" +
                "* (C) Copyright 2009-2022, by David Gilbert and Contributors.\n" +
                "*\n" +
                "* Original Author: Peter Kolb (see patch 2809117);\n" +
                "* Contributor(s): -;\n" +
                "*\n" +
                "*/\n" +
                "package org.jfree.chart.annotations;\n" +
                "import java.io.IOException;\n" +
                "import java.io.ObjectInputStream;\n" +
                "import java.io.ObjectOutputStream;\n" +
                "import java.io.Serializable;\n" +
                "import java.util.Arrays;\n" +
                "import java.util.EventListener;\n" +
                "import java.util.List;\n" +
                "import javax.swing.event.EventListenerList;\n" +
                "import org.jfree.chart.event.AnnotationChangeEvent;\n" +
                "import org.jfree.chart.event.AnnotationChangeListener;\n" +
                "/**\n" +
                "* An abstract implementation of the{@link Annotation} interface, containing a\n" +
                "* mechanism for registering change listeners.\n" +
                "*/\n" +
                "public abstract class AbstractAnnotation implements Annotation, Cloneable,\n" +
                "Serializable{\n" +
                "/** Storage for registered change listeners. */\n" +
                "private transient EventListenerList listenerList;\n" +
                "/**\n" +
                "* A flag that indicates whether listeners should be notified\n" +
                "* about changes of the annotation.\n" +
                "*/\n" +
                "private boolean notify = true;\n" +
                "/**\n" +
                "* Constructs an annotation.\n" +
                "*/\n" +
                "protected AbstractAnnotation(){\n" +
                "this.listenerList = new EventListenerList();\n" +
                "}\n" +
                "/**\n" +
                "* Registers an object to receive notification of changes to the\n" +
                "* annotation.\n" +
                "*\n" +
                "* @param listener the object to register.\n" +
                "*\n" +
                "* @see #removeChangeListener(AnnotationChangeListener)\n" +
                "*/\n" +
                "@Override\n" +
                "public void addChangeListener(AnnotationChangeListener listener){\n" +
                "this.listenerList.add(AnnotationChangeListener.class, listener);\n" +
                "}\n" +
                "/**\n" +
                "* Deregisters an object so that it no longer receives notification of\n" +
                "* changes to the annotation.\n" +
                "*\n" +
                "* @param listener the object to deregister.\n" +
                "*\n" +
                "* @see #addChangeListener(AnnotationChangeListener)\n" +
                "*/\n" +
                "@Override\n" +
                "public void removeChangeListener(AnnotationChangeListener listener){\n" +
                "this.listenerList.remove(AnnotationChangeListener.class, listener);\n" +
                "}\n" +
                "/**\n" +
                "* Returns{@code true} if the specified object is registered with\n" +
                "* the annotation as a listener. Most applications won't need to call this\n" +
                "* method, it exists mainly for use by unit testing code.\n" +
                "*\n" +
                "* @param listener the listener.\n" +
                "*\n" +
                "* @return A boolean.\n" +
                "*\n" +
                "* @see #addChangeListener(AnnotationChangeListener)\n" +
                "* @see #removeChangeListener(AnnotationChangeListener)\n" +
                "*/\n" +
                "public boolean hasListener(EventListener listener){\n" +
                "List list = Arrays.asList(this.listenerList.getListenerList());\n" +
                "return list.contains(listener);\n" +
                "}\n" +
                "/**\n" +
                "* Notifies all registered listeners that the annotation has changed.\n" +
                "*\n" +
                "* @see #addChangeListener(AnnotationChangeListener)\n" +
                "*/\n" +
                "protected void fireAnnotationChanged(){\n" +
                "if (notify){\n" +
                "notifyListeners(new AnnotationChangeEvent(this, this));\n" +
                "}\n" +
                "}\n" +
                "/**\n" +
                "* Notifies all registered listeners that the annotation has changed.\n" +
                "*\n" +
                "* @param event contains information about the event that triggered the\n" +
                "* notification.\n" +
                "*\n" +
                "* @see #addChangeListener(AnnotationChangeListener)\n" +
                "* @see #removeChangeListener(AnnotationChangeListener)\n" +
                "*/\n" +
                "protected void notifyListeners(AnnotationChangeEvent event){\n" +
                "Object[] listeners = this.listenerList.getListenerList();\n" +
                "for (int i = listeners.length - 2;i >= 0;i -= 2){\n" +
                "if (listeners[i] == AnnotationChangeListener.class){\n" +
                "((AnnotationChangeListener) listeners[i + 1]).annotationChanged(\n" +
                "event);\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "/**\n" +
                "* Returns a flag that indicates whether listeners should be\n" +
                "* notified about changes to the annotation.\n" +
                "*\n" +
                "* @return the flag.\n" +
                "*\n" +
                "* @see #setNotify(boolean)\n" +
                "*/\n" +
                "public boolean getNotify(){\n" +
                "return this.notify;\n" +
                "}\n" +
                "/**\n" +
                "* Sets a flag that indicates whether listeners should be notified about\n" +
                "* changes of an annotation.\n" +
                "*\n" +
                "* @param flag the flag\n" +
                "*\n" +
                "* @see #getNotify()\n" +
                "*/\n" +
                "public void setNotify(boolean flag){\n" +
                "this.notify = flag;\n" +
                "if (notify){\n" +
                "fireAnnotationChanged();\n" +
                "}\n" +
                "}\n" +
                "/**\n" +
                "* Returns a clone of the annotation. The cloned annotation will NOT\n" +
                "* include the{@link AnnotationChangeListener} references that have been\n" +
                "* registered with this annotation.\n" +
                "*\n" +
                "* @return A clone.\n" +
                "*\n" +
                "* @throws CloneNotSupportedException if the annotation does not support\n" +
                "* cloning.\n" +
                "*/\n" +
                "@Override\n" +
                "public Object clone() throws CloneNotSupportedException{\n" +
                "AbstractAnnotation clone = (AbstractAnnotation) super.clone();\n" +
                "clone.listenerList = new EventListenerList();\n" +
                "return clone;\n" +
                "}\n" +
                "/**\n" +
                "* Handles serialization.\n" +
                "*\n" +
                "* @param stream the output stream.\n" +
                "*\n" +
                "* @throws IOException if there is an I/O problem.\n" +
                "*/\n" +
                "private void writeObject(ObjectOutputStream stream) throws IOException{\n" +
                "stream.defaultWriteObject();\n" +
                "}\n" +
                "/**\n" +
                "* Restores a serialized object.\n" +
                "*\n" +
                "* @param stream the input stream.\n" +
                "*\n" +
                "* @throws IOException if there is an I/O problem.\n" +
                "* @throws ClassNotFoundException if there is a problem loading a class.\n" +
                "*/\n" +
                "private void readObject(ObjectInputStream stream)\n" +
                "throws IOException, ClassNotFoundException{\n" +
                "stream.defaultReadObject();\n" +
                "this.listenerList = new EventListenerList();\n" +
                "}\n" +
                "}";


        System.out.println(method.indexOf("*/"));


        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\parser\\StringParser.java";
        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\Test.java";
        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\parser\\result\\internal\\CodeTree.java";

        //ParsedSourceFile parsedFile = JavaSourceFileParser.parse(pathString);
        //parsedFile.print();

        //Path root = new File("C:\\").toPath();
        //System.out.println("Path: "  + root.toString());

        //ArrayList<Path> occurrencesOf = FilePathHandler.getPathsToFile("picture");

        //System.out.println(FilePathHandler.getWorkingDirectoryRoot());

        //GitGudder.getLocalGitRepositories();

        //InputHandler.promptWelcome();
        //InputHandler.promptForRepositoryPath();

        //Path testPath = Path.of("C:\\Users\\Alex\\Documents\\GitHub\\TP1-Metriques-de-qualite-de-logiciel");

        /*
        if(1 != 2) return;
        ParsedRepository repo = JavaRepositoryParser.parse("C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel");
        System.out.println("\n[ STATISTICS OF REPOSITORY ]");
        System.out.println("Total Cyclomatic Complexity of the Project: " + repo.getTotalCyclomaticComplexity());
        System.out.println("Number of classes: " + repo.getClasses().length);
        System.out.println("Number of tests: " + repo.getNumAssertStatements());
        System.out.println("Number of lines:\n    " + repo.getTotalLinesCode() + " are code, " + repo.getTotalLinesComments() + " are comments, " + repo.getTotalLinesEmpty() + " are empty.\n    " + repo.getTotalLines() + " in total.");

        System.out.println("\n\n[ CLASSES ]");
        System.out.println("Most Complex: " + repo.getMostComplexClass().getSignature() + " with a cyclomatic complexity of " + repo.getMostComplexClass().getCyclomaticComplexity() + ".");
        System.out.println("Most Referenced (Total): " + repo.getMostReferencedClass().getSignature() + ", referenced by " + repo.getNumTimesReferenced(repo.getMostReferencedClass()) + " other classes.");
        System.out.println("Most Referenced (Directly): " + repo.getMostDirectlyReferencedClass().getSignature() + ", referenced by " + repo.getNumTimesReferenced(repo.getMostDirectlyReferencedClass()) + " other classes.");
        System.out.println("Most Referenced (Indirectly): " + repo.getMostIndirectlyReferencedClass().getSignature() + ", referenced by " + repo.getNumTimesReferenced(repo.getMostIndirectlyReferencedClass()) + " other classes.");


        System.out.println(GitGudder.getCommitCountTo(repo.getFilePath()));*/
    }
}