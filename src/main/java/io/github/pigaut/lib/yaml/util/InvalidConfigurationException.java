package io.github.pigaut.lib.yaml.util;

import io.github.pigaut.lib.yaml.*;

public class InvalidConfigurationException extends RuntimeException {

    public InvalidConfigurationException(ConfigSection section, String message) {
        this(section.getRoot(), section.getPath(), message);
    }

    public InvalidConfigurationException(ConfigSection section, String path, String message) {
        this(section.getRoot(), section.isRoot() ? path : String.join(".", section.getPath(), path), message);
    }

    public InvalidConfigurationException(Config config, String path, String message) {
        this(config.getFile().getName(), path, message);
    }

    public InvalidConfigurationException(String fileName, String path, String message) {
        super("Error found in \"" + fileName + "\" -> '" + path + "' " + message + ".");
    }

}
