package io.github.pigaut.lib.yaml;

import io.github.pigaut.lib.yaml.serialize.*;
import org.jetbrains.annotations.*;

import java.io.*;

public interface Config extends ConfigSection {

    @NotNull String getName();

    @NotNull File getFile();

    @NotNull Configuration getConfiguration();

    void setConfiguration(Configuration configuration);

    void save();

    void load();

    void delete();

}
