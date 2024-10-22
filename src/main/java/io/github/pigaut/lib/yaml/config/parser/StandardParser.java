package io.github.pigaut.lib.yaml.config.parser;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class StandardParser extends Parser {

    public StandardParser() {
        registerSerializer(LocalDate.class, Serializer.LOCAL_DATE);
        registerSerializer(LocalTime.class, Serializer.LOCAL_TIME);
        registerSerializer(LocalDateTime.class, Serializer.LOCAL_DATE_TIME);
        registerSerializer(UUID.class, Serializer.defaultSerializer());
        registerSerializer(Locale.class, Serializer.LOCALE);
        registerSerializer(File.class, Serializer.defaultSerializer());
        registerSerializer(URL.class, Serializer.defaultSerializer());

        registerDeserializer(LocalDate.class, Deserializer.LOCAL_DATE);
        registerDeserializer(LocalTime.class, Deserializer.LOCAL_TIME);
        registerDeserializer(LocalDateTime.class, Deserializer.LOCAL_DATE_TIME);
        registerDeserializer(UUID.class, Deserializer.UUID);
        registerDeserializer(Locale.class, Deserializer.LOCALE);
        registerDeserializer(File.class, Deserializer.FILE);
        registerDeserializer(URL.class, Deserializer.URL);
    }

}
