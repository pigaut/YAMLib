package io.github.pigaut.lib.yaml;

import io.github.pigaut.lib.yaml.config.*;

import java.io.*;

public class SpigotConfig extends FileConfig {

    public SpigotConfig(File file) {
        super(file, new SpigotConfigurator(), new SpigotParser());
    }

}
