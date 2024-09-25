package io.github.pigaut.lib.yaml.serialize;

import org.jetbrains.annotations.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

public interface StringSerializer<T> {

    @NotNull String serialize(@NotNull T value);

    StringSerializer<LocalDate> LOCAL_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy")::format;

    StringSerializer<LocalTime> LOCAL_TIME = DateTimeFormatter.ofPattern("HH:mm:ss")::format;

    StringSerializer<LocalDateTime> LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")::format;

    StringSerializer<Duration> DURATION = new DurationFormatter();

    StringSerializer<Locale> LOCALE = Locale::toLanguageTag;

    class SerializerContainer {

        private final Map<Class<?>, StringSerializer<?>> serializerByType = new HashMap<>();

        protected SerializerContainer() {
            registerSerializer(LocalDate.class, LOCAL_DATE);
            registerSerializer(LocalTime.class, LOCAL_TIME);
            registerSerializer(LocalDateTime.class, LOCAL_DATE_TIME);
            registerSerializer(Duration.class, DURATION);
            registerSerializer(Locale.class, LOCALE);
        }

        public boolean contains(Class<?> classType) {
            return serializerByType.containsKey(classType);
        }

        public StringSerializer getSerializer(Class<?> classType) {
            return serializerByType.get(classType);
        }

        public <T> StringParser<T> getExactSerializer(Class<T> classType) {
            return (StringParser<T>) serializerByType.get(classType);
        }

        public <T> void registerSerializer(Class<T> classType, StringSerializer<T> parser) {
            serializerByType.put(classType, parser);
        }

    }

}
