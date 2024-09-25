package io.github.pigaut.lib.yaml;

import java.io.*;

public class Yamlib {

    private Yamlib() {}


    public static String getFileName(String nameWithExtension) {
        int pos = nameWithExtension.lastIndexOf(".");
        return pos == -1 ? nameWithExtension : nameWithExtension.substring(0, pos);
    }

    public static void createFile(File file) {
        if (file.exists()) return;
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
        }
    }

}
