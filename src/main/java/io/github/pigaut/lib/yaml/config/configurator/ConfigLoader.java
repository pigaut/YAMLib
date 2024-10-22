package io.github.pigaut.lib.yaml.config.configurator;

import io.github.pigaut.lib.yaml.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface ConfigLoader<T> {

    @NotNull
    T load(@NotNull ConfigSection section) throws InvalidConfigurationException;

}
