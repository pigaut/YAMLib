package io.github.pigaut.lib.yaml.util;

import org.jetbrains.annotations.*;

public interface StringFormatter {

    StringFormatter CONSTANT = value -> value.trim().replaceAll("\\s+|-", "_").toUpperCase();
    StringFormatter NAMESPACE = value -> value.trim().replaceAll("\\s+|_", "-").toLowerCase();

    @NotNull String format(@NotNull String value);

}
