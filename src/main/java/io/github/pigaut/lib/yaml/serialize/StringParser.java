package io.github.pigaut.lib.yaml.serialize;

import io.github.pigaut.lib.yaml.util.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public interface StringParser<T> {

    T parse(@NotNull String string);

    StringParser<Character> CHAR = string -> string.length() == 1 ? string.charAt(0) : null;

    StringParser<Integer> INT = string -> {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return null;
        }
    };

    StringParser<Boolean> BOOLEAN = string -> {
        if ("true".equalsIgnoreCase(string)) return true;
        if ("false".equalsIgnoreCase(string)) return false;
        return null;
    };

    StringParser<Double> DOUBLE = string -> {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return null;
        }
    };

    StringParser<Long> LONG = string -> {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            return null;
        }
    };

    StringParser<Float> FLOAT = string -> {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException e) {
            return null;
        }
    };

    static <E extends Enum<E>> StringParser<E> enumParser(Class<E> type) {
        return string -> {
            try {
                return Enum.valueOf(type, StringFormatter.CONSTANT.format(string));
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    StringParser<LocalDate> LOCAL_DATE = string -> {
        try {
            return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            return null;
        }
    };

    StringParser<LocalTime> LOCAL_TIME = string -> {
        try {
            return LocalTime.parse(string, DateTimeFormatter.ofPattern("HH:mm[:ss]"));
        } catch (DateTimeParseException e) {
            return null;
        }
    };

    StringParser<LocalDateTime> LOCAL_DATE_TIME = string -> {
        try {
            return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm[:ss]"));
        } catch (DateTimeParseException e) {
            return null;
        }
    };

    StringParser<BigInteger> BIG_INTEGER = string -> {
        try {
            return new BigInteger(string);
        } catch (NumberFormatException e) {
            return null;
        }
    };

    StringParser<BigDecimal> BIG_DECIMAL = string -> {
        try {
            return new BigDecimal(string);
        } catch (NumberFormatException e) {
            return null;
        }
    };

    StringParser<File> FILE = File::new;

    StringParser<Locale> LOCALE = Locale::forLanguageTag;

    StringParser<UUID> UUID = string -> {
        try {
            return java.util.UUID.fromString(string);
        } catch (IllegalArgumentException e) {
            return null;
        }
    };

    StringParser<URL> URL = string -> {
        try {
            return new java.net.URL(string);
        } catch (MalformedURLException e) {
            return null;
        }
    };

    StringParser<Duration> DURATION = new DurationFormatter();

    class ParserContainer {

        private final Map<Class<?>, StringParser<?>> parserByType = new HashMap<>();

        protected ParserContainer() {
            registerParser(Boolean.class, BOOLEAN);
            registerParser(Character.class, CHAR);
            registerParser(Integer.class, INT);
            registerParser(Long.class, LONG);
            registerParser(Double.class, DOUBLE);
            registerParser(Float.class, FLOAT);
            registerParser(BigInteger.class, BIG_INTEGER);
            registerParser(BigDecimal.class, BIG_DECIMAL);
            registerParser(LocalDate.class, LOCAL_DATE);
            registerParser(LocalTime.class, LOCAL_TIME);
            registerParser(LocalDateTime.class, LOCAL_DATE_TIME);
            registerParser(Duration.class, DURATION);
            registerParser(UUID.class, UUID);
            registerParser(Locale.class, LOCALE);
            registerParser(File.class, FILE);
            registerParser(URL.class, URL);
        }

        public boolean contains(Class<?> classType) {
            return parserByType.containsKey(classType);
        }

        public StringParser getParser(Class<?> classType) {
            if (classType.isEnum()) {
                return enumParser((Class<? extends Enum>) classType);
            }
            return parserByType.get(classType);
        }

        public <T> StringParser<T> getExactParser(Class<T> classType) {
            if (classType.isEnum()) {
                return enumParser((Class<? extends Enum>) classType);
            }
            return (StringParser<T>) parserByType.get(classType);
        }

        public <T> void registerParser(Class<T> classType, StringParser<T> parser) {
            parserByType.put(classType, parser);
        }

    }

}
