package io.github.pigaut.lib.yaml.serialize;

import io.github.pigaut.lib.yaml.*;

import java.util.*;

public interface ConfigMapper<T> {

    void map(ConfigSection section, T value);

    ConfigMapper<Map> MAP = (section, map) -> map.forEach((key, value) -> section.set(key.toString(), value));

    ConfigMapper<Iterable> SEQ = (section, elements) -> {
        section.setSequence(true);
        elements.forEach(section::add);
    };

    class MapperContainer {
        private final Map<Class<?>, ConfigMapper<?>> mappersByType = new HashMap<>();

        public MapperContainer() {
            registerMapper(Map.class, MAP);
            registerMapper(Iterable.class, SEQ);
        }

        public <T> ConfigMapper<T> getExactMapper(Class<T> type) {
            return (ConfigMapper<T>) mappersByType.get(type);
        }

        public ConfigMapper getMapper(Class<?> type) {
            if (mappersByType.containsKey(type)) {
                return mappersByType.get(type);
            }
            for (Class<?> key : mappersByType.keySet()) {
                if (key.isAssignableFrom(type)) {
                    return mappersByType.get(key);
                }
            }
            return null;
        }

        public <T> void registerMapper(Class<T> type, ConfigMapper<T> mapper) {
            mappersByType.put(type, mapper);
        }

    }

}
