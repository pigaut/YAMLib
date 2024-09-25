package io.github.pigaut.lib.yaml.serialize;

import io.github.pigaut.lib.yaml.*;
import io.github.pigaut.lib.yaml.util.*;

import java.util.*;

public interface ConfigLoader<T> {

    T load(ConfigSection section) throws InvalidConfigurationException;

    class LoaderContainer {
        private final Map<Class<?>, ConfigLoader<?>> loadersByType = new HashMap<>();

        public LoaderContainer() {}

        public <T> ConfigLoader<T> getExactLoader(Class<T> type) {
            return (ConfigLoader<T>) loadersByType.get(type);
        }

        public ConfigLoader getLoader(Class<?> type) {
            if (loadersByType.containsKey(type)) {
                return loadersByType.get(type);
            }
            for (Class<?> key : loadersByType.keySet()) {
                if (key.isAssignableFrom(type)) {
                    return loadersByType.get(key);
                }
            }
            return null;
        }

        public <T> void registerLoader(Class<T> type, ConfigLoader<T> loader) {
            loadersByType.put(type, loader);
        }

    }

}
