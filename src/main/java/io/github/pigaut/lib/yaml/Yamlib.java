package io.github.pigaut.lib.yaml;

import java.io.*;

public class Yamlib {

    private Yamlib() {}

    public static String getFileName(String nameWithExtension) {
        int pos = nameWithExtension.lastIndexOf(".");
        return pos == -1 ? nameWithExtension : nameWithExtension.substring(0, pos);
    }

    public static boolean createFile(File file) {
        if (file.exists()) return false;
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
