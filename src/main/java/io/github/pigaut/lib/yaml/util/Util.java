package io.github.pigaut.lib.yaml.util;

import java.util.regex.*;

public class Util {

    public static String[] getSplitPath(String path) {
        return streamlinePath(path).split("\\.");
    }

    public static String streamlinePath(String path) {
        Pattern pattern = Pattern.compile("\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(path);
        String result = matcher.replaceAll(".$1");
        if (result.startsWith(".")) {
            result = result.replaceFirst("\\.", "");
        }
        return result.toLowerCase();
    }

}
