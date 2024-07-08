package org.example;

import org.opencv.core.Core;
/**
 * Main class
 */
public class Main {
    /**
     * Methode starts program
     * @param args ..
     */
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Handler.createDirectory();
        App.main(args);
    }
}
