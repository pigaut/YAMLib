package io.github.pigaut.lib.yaml;

import io.github.pigaut.lib.yaml.config.configurator.*;
import io.github.pigaut.lib.yaml.config.parser.*;
import org.jetbrains.annotations.*;

import java.io.*;

public interface Config extends ConfigSection {

    /**
     * Retrieves the name of the configuration.
     *
     * @return The name of the configuration, never {@code null}.
     */
    @NotNull String getName();

    /**
     * Retrieves the file associated with the configuration.
     *
     * @return The {@link File} object representing the configuration file, never {@code null}.
     */
    @NotNull File getFile();

    @NotNull Configurator getConfigurator();

    void setConfigurator(@NotNull Configurator configurator);

    @NotNull Parser getParser();

    void setParser(@NotNull Parser parser);

    /**
     * Saves the current state of the configuration to the associated file.
     * @returns true if config was saved successfully, false otherwise
     */
    boolean save();

    /**
     * Loads the configuration from the associated file.
     */
    void load();

    /**
     * Deletes the configuration file and removes all configuration data.
     */
    void remove();

}
