package io.github.pigaut.lib.yaml;

import io.github.pigaut.lib.yaml.util.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

public interface ConfigSection {

    boolean isRoot();
    @NotNull Config getRoot();

    @NotNull String getKey() throws UnsupportedOperationException;
    @NotNull ConfigSection getParent() throws UnsupportedOperationException;
    @NotNull String getPath() throws UnsupportedOperationException;

    Set<String> getKeys();
    Stream<Object> getValues();

    boolean isSequence();
    void isSequenceOrThrow() throws InvalidConfigurationException;
    void setSequence(boolean bool);

    int size();
    boolean contains(@NotNull String path);
    void set(@NotNull String path, Object value);
    void add(Object value);

    void map(Object value) throws IllegalArgumentException;
    void remove(@NotNull String path);
    void clear();

    @NotNull <T> T parse(String path, Class<T> type) throws InvalidConfigurationException;
    @Nullable <T> T parseOrDefault(String path, Class<T> type, T def);

    @NotNull <T> T load(@NotNull Class<T> type) throws InvalidConfigurationException;
    @Nullable <T> T loadOrDefault(@NotNull Class<T> type, @Nullable T def);

    ConfigSection getSection(String path) throws InvalidConfigurationException;
    ConfigSection getSectionOrCreate(String path);
    ConfigSection getSectionOrNull(String path);

    Stream<ConfigSection> nestedSections();
    Stream<Object> nestedFields();
    Stream<String> nestedStrings();
    Stream<Character> nestedCharacters();
    Stream<Integer> nestedIntegers();
    Stream<Boolean> nestedBooleans();
    Stream<Double> nestedDoubles();
    Stream<Long> nestedLongs();
    Stream<Float> nestedFloats();
    <E extends Enum<E>> Stream<E> nestedEnums(@NotNull Class<E> enumType);

    Stream<ConfigSection> getSections(@NotNull String path);
    Stream<String> getStrings(@NotNull String path);
    Stream<Character> getCharacters(@NotNull String path);
    Stream<Integer> getIntegers(@NotNull String path);
    Stream<Boolean> getBooleans(@NotNull String path);
    Stream<Double> getDoubles(@NotNull String path);
    Stream<Long> getLongs(@NotNull String path);
    Stream<Float> getFloats(@NotNull String path);
    <E extends Enum<E>> Stream<E> getEnums(@NotNull String path, @NotNull Class<E> enumType);

    boolean getBoolean(@NotNull String path) throws InvalidConfigurationException;
    Boolean getBoolean(@NotNull String path, @Nullable Boolean def);

    char getChar(@NotNull String path) throws InvalidConfigurationException;
    Character getChar(@NotNull String path, @Nullable Character def);

    @NotNull String getString(@NotNull String path) throws InvalidConfigurationException;
    String getString(@NotNull String path, @Nullable String def);

    int getInt(@NotNull String path) throws InvalidConfigurationException;
    Integer getInt(@NotNull String path, @Nullable Integer def);

    long getLong(@NotNull String path) throws InvalidConfigurationException;
    Long getLong(@NotNull String path, @Nullable Long def);

    float getFloat(@NotNull String path) throws InvalidConfigurationException;
    Float getFloat(@NotNull String path, @Nullable Float def);

    double getDouble(@NotNull String path) throws InvalidConfigurationException;
    Double getDouble(@NotNull String path, @Nullable Double def);

    @NotNull BigInteger getBigInteger(@NotNull String path) throws InvalidConfigurationException;
    BigInteger getBigInteger(@NotNull String path, @Nullable BigInteger def);

    @NotNull BigDecimal getBigDecimal(@NotNull String path) throws InvalidConfigurationException;
    BigDecimal getBigDecimal(@NotNull String path, @Nullable BigDecimal def);

    @NotNull LocalDate getDate(@NotNull String path) throws InvalidConfigurationException;
    LocalDate getDate(@NotNull String path, @Nullable LocalDate def);

    @NotNull LocalTime getTime(@NotNull String path) throws InvalidConfigurationException;
    LocalTime getTime(@NotNull String path, @Nullable LocalTime def);

    @NotNull LocalDateTime getDateTime(@NotNull String path) throws InvalidConfigurationException;
    LocalDateTime getDateTime(@NotNull String path, @Nullable LocalDateTime def);

    @NotNull Duration getDuration(@NotNull String path) throws InvalidConfigurationException;
    Duration getDuration(@NotNull String path, @Nullable Duration def);

    @NotNull UUID getUUID(@NotNull String path) throws InvalidConfigurationException;
    UUID getUUID(@NotNull String path, @Nullable UUID def);

    @NotNull Locale getLocale(@NotNull String path) throws InvalidConfigurationException;
    Locale getLocale(@NotNull String path, @Nullable Locale def);

    @NotNull File getFile(@NotNull String path) throws InvalidConfigurationException;
    File getFile(@NotNull String path, @Nullable File def);

    @NotNull URL getURL(@NotNull String path) throws InvalidConfigurationException;
    URL getURL(@NotNull String path, @Nullable URL def);

    @NotNull <E extends Enum<E>> E getEnum(@NotNull String path, @NotNull Class<E> enumType) throws InvalidConfigurationException;
    <E extends Enum<E>> E getEnum(@NotNull String path, @NotNull Class<E> enumType, @Nullable E def);

    Object[][] getMatrix(String path, int rows, int columns) throws InvalidConfigurationException;
    String[][] getStringMatrix(String path, int rows, int columns) throws InvalidConfigurationException;
    char[][] getCharMatrix(String path, int rows, int columns) throws InvalidConfigurationException;
    int[][] getIntMatrix(String path, int rows, int columns) throws InvalidConfigurationException;
    boolean[][] getBooleanMatrix(String path, int rows, int columns) throws InvalidConfigurationException;
    double[][] getDoubleMatrix(String path, int rows, int columns) throws InvalidConfigurationException;
    long[][] getLongMatrix(String path, int rows, int columns) throws InvalidConfigurationException;
    float[][] getFloatMatrix(String path, int rows, int columns) throws InvalidConfigurationException;
    <E extends Enum<E>> E[][] getEnumMatrix(String path, Class<E> type, int rows, int columns) throws InvalidConfigurationException;

    Map<String, Object> toMap();
    List<Object> toList();

}
