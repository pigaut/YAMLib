package io.github.pigaut.lib.yaml.util;

public interface StringFormatter {

    StringFormatter CONSTANT = value -> value.trim().replaceAll("\\s+|-", "_").toUpperCase();
    StringFormatter NAMESPACE = value -> value.trim().replaceAll("\\s+|_", "-").toLowerCase();

    String format(String value);

}
