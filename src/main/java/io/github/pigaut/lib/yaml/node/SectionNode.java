package io.github.pigaut.lib.yaml.node;

import io.github.pigaut.lib.yaml.*;
import io.github.pigaut.lib.yaml.serialize.*;
import io.github.pigaut.lib.yaml.util.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public abstract class SectionNode implements ConfigSection {

    public static final List<Class<?>> SCALAR_TYPES = List.of(Boolean.class, Character.class, String.class, Integer.class, Long.class,
            Float.class, Double.class, BigInteger.class, BigDecimal.class);

    protected final Map<String, Object> children = new LinkedHashMap<>();
    private boolean sequence;

    protected SectionNode() {
        this(false);
    }

    protected SectionNode(boolean sequence) {
        this.sequence = sequence;
    }

    protected void setScalar(String path, Object value) {
        String formattedPath = path.replace("][", ".").replace("[", ".").replace("]", "");

        SectionNode section = this;
        String key = formattedPath;

        int lastKeyIndex = formattedPath.lastIndexOf(".");
        if (lastKeyIndex != -1) {
            section = getSectionOrCreate(formattedPath.substring(0, lastKeyIndex));
            key = formattedPath.substring(lastKeyIndex + 1);
        }

        section.children.put(key, value);
    }

    @Override
    public Set<String> getKeys() {
        return new HashSet<>(children.keySet());
    }

    @Override
    public Stream<Object> getValues() {
        return children.values().stream();
    }

    @Override
    public boolean isSequence() {
        return sequence;
    }

    @Override
    public void isSequenceOrThrow() throws InvalidConfigurationException {
        if (!sequence) {
            throw new InvalidConfigurationException(this, "is not a sequence");
        }
    }

    @Override
    public void setSequence(boolean sequence) {
        if (!this.sequence && sequence) {
            List<Object> elements = getValues().toList();
            children.clear();

            int index = 0;
            for (Object element : elements) {
                children.put(Integer.toString(index++), element);
            }
        }

        this.sequence = sequence;
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public boolean contains(@NotNull String path) {
        String[] keys = Util.getSplitPath(path);

        Object currentNode = this;
        for (String key : keys) {
            if (currentNode instanceof SectionNode section) {
                currentNode = section.children.get(key);
                continue;
            }
            return false;
        }

        return true;
    }

    @Override
    public void set(@NotNull String path, Object value) {
        Preconditions.checkNotNull(path, "Path cannot be null.");
        Preconditions.checkArgument(!path.isBlank(), "Path cannot be empty.");

        if (value instanceof Optional<?> optional) {
            value = optional.orElse(null);
        }

        if (value == null) {
            setScalar(path, null);
            return;
        }

        Class<?> classType = value.getClass();
        if (SCALAR_TYPES.contains(classType)) {
            setScalar(path, value);
            return;
        }

        StringSerializer serializer = getRoot().getConfiguration().getSerializer(classType);
        if (serializer != null) {
            setScalar(path, serializer.serialize(value));
            return;
        }

        ConfigMapper mapper = getRoot().getConfiguration().getMapper(classType);
        if (mapper != null) {
            mapper.map(getSectionOrCreate(path), value);
            return;
        }

        setScalar(path, value.toString());
    }

    @Override
    public void add(Object value) {
        set(Integer.toString(children.size()), value);
    }

    @Override
    public void remove(@NotNull String key) {
        children.remove(key);
    }

    @Override
    public void clear() {
        children.clear();
    }

    @Override
    public void map(Object value) throws IllegalArgumentException {
        if (value == null) {
            return;
        }

        Class<?> classType = value.getClass();
        ConfigMapper mapper = getRoot().getConfiguration().getMapper(classType);

        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper exists for " + classType.getSimpleName());
        }

        mapper.map(this, value);
    }

    @Override
    public <T> @NotNull T parse(String path, Class<T> type) throws InvalidConfigurationException {
        return parse(path, type, type.getSimpleName());
    }

    private <T> @NotNull T parse(String path, Class<T> type, String name) throws InvalidConfigurationException {
        StringParser<T> parser = getRoot().getConfiguration().getExactParser(type);
        Preconditions.checkNotNull(parser, "No parser found for class " + type.getSimpleName());

        String stringToParse = getString(path);
        T parsedValue = parser.parse(stringToParse);

        if (parsedValue == null) {
            throw new InvalidConfigurationException(this, path, "string could not be parsed as a " + type.getSimpleName());
        }

        return parsedValue;
    }

    @Override
    public <T> @Nullable T parseOrDefault(String path, Class<T> type, T def) {
        StringParser<T> parser = getRoot().getConfiguration().getExactParser(type);
        Preconditions.checkNotNull(parser, "No parser found for class " + type.getSimpleName());

        String stringToParse = getStringOrNull(path);
        if (stringToParse == null) {
            return def;
        }

        T parsedValue = parser.parse(stringToParse);
        return parsedValue != null ? parsedValue : def;
    }

    @Override
    public <T> @NotNull T load(@NotNull Class<T> type) throws InvalidConfigurationException {
        ConfigLoader<T> loader = getRoot().getConfiguration().getExactLoader(type);

        if (loader == null) {
            throw new IllegalStateException("No section loader exists for config id %s and type %s");
        }

        return loader.load(this);
    }

    @Override
    public <T> @NotNull T loadOrDefault(@NotNull Class<T> type, @Nullable T def) {
        ConfigLoader<T> loader = getRoot().getConfiguration().getExactLoader(type);

        if (loader == null) {
            throw new IllegalStateException("No section loader exists for config id %s and type %s");
        }

        try {
            return loader.load(this);
        } catch (InvalidConfigurationException e) {
            return def;
        }
    }

    @Override
    public ConfigSection getSection(String path) throws InvalidConfigurationException {
        ConfigSection section = getSectionOrNull(path);
        if (section == null) {
            throw new InvalidConfigurationException(this, path, "is not a section");
        }
        return section;
    }

    @Override
    public SectionNode getSectionOrCreate(String path) {
        SectionNode section = getSectionOrNull(path);

        if (section != null) {
            return section;
        }

        String[] keys = path.split("\\.");
        SectionNode currentSection = this;

        for (String key : keys) {
            Pattern indexPattern = Pattern.compile("\\[(\\d+)]");
            Matcher indicesMatcher = indexPattern.matcher(key);

            String cleanKey = indicesMatcher.replaceAll("");

            SectionNode foundSection = currentSection.getSectionOrNull(cleanKey);
            if (foundSection == null) {
                foundSection = new ChildSection(currentSection, cleanKey);
            }
            currentSection = foundSection;

            while (indicesMatcher.find()) {
                String indexKey = indicesMatcher.group(1);
                currentSection.setSequence(true);

                SectionNode indexedSection = currentSection.getSectionOrNull(indexKey);
                if (indexedSection == null) {
                    indexedSection = new ChildSection(currentSection, indexKey);
                }
                currentSection = indexedSection;
            }
        }

        return currentSection;
    }

    @Override
    public SectionNode getSectionOrNull(String path) {
        Object node = getNodeOrNull(path);
        return node instanceof SectionNode section ? section : null;
    }

    @Override
    public Stream<ConfigSection> nestedSections() {
        return getValues().filter(SectionNode.class::isInstance).map(SectionNode.class::cast);
    }

    @Override
    public Stream<Object> nestedFields() {
        return getValues().filter(node -> !(node instanceof SectionNode));
    }

    @Override
    public Stream<String> nestedStrings() {
        return nestedFields().filter(String.class::isInstance).map(String.class::cast);
    }

    @Override
    public Stream<Character> nestedCharacters() {
        return nestedFields().filter(Character.class::isInstance).map(Character.class::cast);
    }

    @Override
    public Stream<Integer> nestedIntegers() {
        return nestedFields().filter(Integer.class::isInstance).map(Integer.class::cast);
    }

    @Override
    public Stream<Boolean> nestedBooleans() {
        return nestedFields().filter(Boolean.class::isInstance).map(Boolean.class::cast);
    }

    @Override
    public Stream<Double> nestedDoubles() {
        return nestedFields().filter(Double.class::isInstance).map(Double.class::cast);
    }

    @Override
    public Stream<Long> nestedLongs() {
        return nestedFields().filter(Long.class::isInstance).map(Long.class::cast);
    }

    @Override
    public Stream<Float> nestedFloats() {
        return nestedFields().filter(Float.class::isInstance).map(Float.class::cast);
    }

    @Override
    public <E extends Enum<E>> Stream<E> nestedEnums(@NotNull Class<E> enumType) {
        return nestedStrings()
                .map(StringParser.enumParser(enumType)::parse)
                .filter(Objects::nonNull);
    }

    @Override
    public Stream<ConfigSection> getSections(@NotNull String path) {
        ConfigSection section = getSectionOrNull(path);
        return section != null ? section.nestedSections() : Stream.empty();
    }

    @Override
    public Stream<String> getStrings(@NotNull String path) {
        ConfigSection section = getSectionOrNull(path);
        return section != null ? section.nestedStrings() : Stream.empty();
    }

    @Override
    public Stream<Character> getCharacters(@NotNull String path) {
        ConfigSection section = getSectionOrNull(path);
        return section != null ? section.nestedCharacters() : Stream.empty();
    }

    @Override
    public Stream<Integer> getIntegers(@NotNull String path) {
        ConfigSection section = getSectionOrNull(path);
        return section != null ? section.nestedIntegers() : Stream.empty();
    }

    @Override
    public Stream<Boolean> getBooleans(@NotNull String path) {
        ConfigSection section = getSectionOrNull(path);
        return section != null ? section.nestedBooleans() : Stream.empty();
    }

    @Override
    public Stream<Double> getDoubles(@NotNull String path) {
        ConfigSection section = getSectionOrNull(path);
        return section != null ? section.nestedDoubles() : Stream.empty();
    }

    @Override
    public Stream<Long> getLongs(@NotNull String path) {
        ConfigSection section = getSectionOrNull(path);
        return section != null ? section.nestedLongs() : Stream.empty();
    }

    @Override
    public Stream<Float> getFloats(@NotNull String path) {
        ConfigSection section = getSectionOrNull(path);
        return section != null ? section.nestedFloats() : Stream.empty();
    }

    @Override
    public <E extends Enum<E>> Stream<E> getEnums(@NotNull String path, @NotNull Class<E> enumType) {
        ConfigSection section = getSectionOrNull(path);
        return section != null ? section.nestedEnums(enumType) : Stream.empty();
    }

    @Override
    public boolean getBoolean(@NotNull String path) throws InvalidConfigurationException {
        Object node = getNode(path);
        if (!(node instanceof Boolean bool)) {
            throw new InvalidConfigurationException(this, path, "is not a boolean");
        }
        return bool;
    }

    @Override
    public @Nullable Boolean getBoolean(@NotNull String path, Boolean def) {
        Object node = getNodeOrNull(path);
        return node instanceof Boolean bool ? bool : def;
    }

    @Override
    public char getChar(@NotNull String path) throws InvalidConfigurationException {
        Object node = getNode(path);
        if (!(node instanceof Character character)) {
            throw new InvalidConfigurationException(this, path, "is not a character");
        }
        return character;
    }

    @Override
    public @Nullable Character getChar(@NotNull String path, Character def) {
        Object node = getNodeOrNull(path);
        return node instanceof Character character ? character : def;
    }

    @Override
    public @NotNull String getString(@NotNull String path) throws InvalidConfigurationException {
        return getNode(path).toString();
    }

    @Override
    public @Nullable String getString(@NotNull String path, String def) {
        Object node = getNodeOrNull(path);
        return node != null ? node.toString() : def;
    }

    @Override
    public int getInt(@NotNull String path) throws InvalidConfigurationException {
        Object node = getNode(path);
        if (!(node instanceof Integer integer)) {
            throw new InvalidConfigurationException(this, path, "is not an integer");
        }
        return integer;
    }

    @Override
    public Integer getInt(@NotNull String path, Integer def) {
        Object node = getNodeOrNull(path);
        return node instanceof Integer integer ? integer : def;
    }

    @Override
    public long getLong(@NotNull String path) throws InvalidConfigurationException {
        Object node = getNode(path);
        if (!(node instanceof Number number)) {
            throw new InvalidConfigurationException(this, path, "is not a float");
        }
        return number.longValue();
    }

    @Override
    public Long getLong(@NotNull String path, Long def) {
        Object node = getNodeOrNull(path);
        return node instanceof Number number ? number.longValue() : def;
    }

    @Override
    public float getFloat(@NotNull String path) throws InvalidConfigurationException {
        Object node = getNode(path);
        if (!(node instanceof Number number)) {
            throw new InvalidConfigurationException(this, path, "is not a float");
        }
        return number.floatValue();
    }

    @Override
    public Float getFloat(@NotNull String path, Float def) {
        Object node = getNodeOrNull(path);
        return node instanceof Number number ? number.floatValue() : def;
    }

    @Override
    public double getDouble(@NotNull String path) throws InvalidConfigurationException {
        Object node = getNode(path);
        if (!(node instanceof Number number)) {
            throw new InvalidConfigurationException(this, path, "is not a double");
        }
        return number.doubleValue();
    }

    @Override
    public Double getDouble(@NotNull String path, Double def) {
        Object node = getNodeOrNull(path);
        return node instanceof Number number ? number.doubleValue() : def;
    }

    @Override
    public @NotNull BigInteger getBigInteger(@NotNull String path) throws InvalidConfigurationException {
        Object node = getNode(path);
        if (!(node instanceof Number number)) {
            throw new InvalidConfigurationException(this, path, "is not a big integer");
        }
        return BigInteger.valueOf(number.longValue());
    }

    @Override
    public BigInteger getBigInteger(@NotNull String path, BigInteger def) {
        Object node = getNodeOrNull(path);
        return node instanceof Number number ? BigInteger.valueOf(number.longValue()) : def;
    }

    @Override
    public @NotNull BigDecimal getBigDecimal(@NotNull String path) throws InvalidConfigurationException {
        Object node = getNode(path);
        if (!(node instanceof Number number)) {
            throw new InvalidConfigurationException(this, path, "is not a big decimal");
        }
        return BigDecimal.valueOf(number.doubleValue());
    }

    @Override
    public BigDecimal getBigDecimal(@NotNull String path, BigDecimal def) {
        Object node = getNodeOrNull(path);
        return node instanceof Number number ? BigDecimal.valueOf(number.doubleValue()) : def;
    }

    @Override
    public @NotNull LocalDate getDate(@NotNull String path) throws InvalidConfigurationException {
        return parse(path, LocalDate.class, "date");
    }

    @Override
    public LocalDate getDate(@NotNull String path, LocalDate def) {
        return parseOrDefault(path, StringParser.LOCAL_DATE, def);
    }

    @Override
    public @NotNull LocalTime getTime(@NotNull String path) throws InvalidConfigurationException {
        return parse(path, LocalTime.class, "time");
    }

    @Override
    public LocalTime getTime(@NotNull String path, @NotNull LocalTime def) {
        return parseOrDefault(path, StringParser.LOCAL_TIME, def);
    }

    @Override
    public @NotNull LocalDateTime getDateTime(@NotNull String path) throws InvalidConfigurationException {
        return parse(path, LocalDateTime.class, "date and time");
    }

    @Override
    public LocalDateTime getDateTime(@NotNull String path, LocalDateTime def) {
        return parseOrDefault(path, StringParser.LOCAL_DATE_TIME, def);
    }

    @Override
    public @NotNull Duration getDuration(@NotNull String path) throws InvalidConfigurationException {
        return parse(path, Duration.class, "duration");
    }

    @Override
    public Duration getDuration(@NotNull String path, Duration def) {
        return parseOrDefault(path, StringParser.DURATION, def);
    }

    @Override
    public @NotNull File getFile(@NotNull String path) throws InvalidConfigurationException {
        return parse(path, File.class, "file");
    }

    @Override
    public File getFile(@NotNull String path, File def) {
        return parseOrDefault(path, StringParser.FILE, def);
    }

    @Override
    public @NotNull Locale getLocale(@NotNull String path) throws InvalidConfigurationException {
        return parse(path, Locale.class, "locale");
    }

    @Override
    public Locale getLocale(@NotNull String path, Locale def) {
        return parseOrDefault(path, StringParser.LOCALE, def);
    }

    @Override
    public @NotNull UUID getUUID(@NotNull String path) throws InvalidConfigurationException {
        return parse(path, UUID.class, "uuid");
    }

    @Override
    public UUID getUUID(@NotNull String path, UUID def) {
        return parseOrDefault(path, StringParser.UUID, def);
    }

    @Override
    public @NotNull URL getURL(@NotNull String path) throws InvalidConfigurationException {
        return parse(path, URL.class, "url");
    }

    @Override
    public URL getURL(@NotNull String path, URL def) {
        return parseOrDefault(path, StringParser.URL, def);
    }

    @Override
    public <E extends Enum<E>> @NotNull E getEnum(@NotNull String path, @NotNull Class<E> enumType) throws InvalidConfigurationException {
        E enumValue = getEnum(path, enumType, null);
        if (enumValue == null) {
            throw new InvalidConfigurationException(this, path, "is not an enum of type " + enumType.getSimpleName());
        }
        return enumValue;
    }

    @Override
    public <E extends Enum<E>> E getEnum(@NotNull String path, @NotNull Class<E> enumType, @Nullable E def) {
        return parseOrDefault(path, StringParser.enumParser(enumType), def);
    }

    public Object[][] getMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Preconditions.checkArgument(rowCount > 0 && columnCount > 0, "Matrix must have at least one row and one column.");

        Object[][] matrix = new Object[rowCount][columnCount];
        ConfigSection rowSection = getSection(path);

        if (!rowSection.isSequence()) {
            throw new InvalidConfigurationException(this, path, "invalid matrix: section must be a sequence.");
        }

        List<ConfigSection> rowSections = rowSection.nestedSections().toList();

        if (rowSections.size() != rowCount) {
            throw new InvalidConfigurationException(this, path, String.format("invalid matrix: expected %d rows, but got %d", rowCount, rowSections.size()));
        }

        for (int row = 0; row < rowCount; row++) {
            ConfigSection columnSection = rowSections.get(row);

            if (!columnSection.isSequence()) {
                throw new InvalidConfigurationException(this, path, String.format("invalid matrix: row %d must be a sequence", row + 1));
            }

            List<Object> columnFields = columnSection.nestedFields().toList();

            if (columnFields.size() != columnCount) {
                throw new InvalidConfigurationException(this, path, String.format("invalid matrix: row %d must have %d columns, but got %d", row + 1, columnCount, columnFields.size()));
            }

            for (int column = 0; column < columnCount; column++) {
                matrix[row][column] = columnFields.get(column);
            }

        }

        return matrix;
    }

    @Override
    public String[][] getStringMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        String[][] stringMatrix = new String[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof String string)) {
                    throw new InvalidConfigurationException(this, path, String.format("invalid matrix: element at row %d and column %d is not a string", row + 1, column + 1));
                }

                stringMatrix[row][column] = string;
            }
        }

        return stringMatrix;
    }

    @Override
    public char[][] getCharMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        char[][] charMatrix = new char[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Character character)) {
                    throw new InvalidConfigurationException(this, path, String.format("invalid matrix: element at row %d and column %d is not a single character", row + 1, column + 1));
                }

                charMatrix[row][column] = character;
            }
        }

        return charMatrix;
    }

    @Override
    public int[][] getIntMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        int[][] intMatrix = new int[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Integer intNum)) {
                    throw new InvalidConfigurationException(this, path, String.format("invalid matrix: element at row %d and column %d is not an integer", row + 1, column + 1));
                }

                intMatrix[row][column] = intNum;
            }
        }

        return intMatrix;
    }

    @Override
    public boolean[][] getBooleanMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        boolean[][] booleanMatrix = new boolean[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Boolean bool)) {
                    throw new InvalidConfigurationException(this, path, String.format("invalid matrix: element at row %d and column %d is not a boolean", row + 1, column + 1));
                }

                booleanMatrix[row][column] = bool;
            }
        }

        return booleanMatrix;
    }

    @Override
    public double[][] getDoubleMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        double[][] doubleMatrix = new double[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Double doubleNum)) {
                    throw new InvalidConfigurationException(this, path, String.format("invalid matrix: element at row %d and column %d is not a double", row + 1, column + 1));
                }

                doubleMatrix[row][column] = doubleNum;
            }
        }

        return doubleMatrix;
    }

    @Override
    public long[][] getLongMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        long[][] longMatrix = new long[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Long longNum)) {
                    throw new InvalidConfigurationException(this, path, String.format("invalid matrix: element at row %d and column %d is not a long", row + 1, column + 1));
                }

                longMatrix[row][column] = longNum;
            }
        }

        return longMatrix;
    }

    @Override
    public float[][] getFloatMatrix(String path, int rowCount, int columnCount) throws InvalidConfigurationException {
        Object[][] matrix = getMatrix(path, rowCount, columnCount);
        float[][] floatMatrix = new float[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Object element = matrix[row][column];

                if (!(element instanceof Float floatNum)) {
                    throw new InvalidConfigurationException(this, path, String.format("invalid matrix: element at row %d and column %d is not a float", row + 1, column + 1));
                }

                floatMatrix[row][column] = floatNum;
            }
        }

        return floatMatrix;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> E[][] getEnumMatrix(String path, Class<E> type, int rowCount, int columnCount) {
        String[][] stringMatrix = getStringMatrix(path, rowCount, columnCount);
        E[][] enumMatrix = (E[][]) Array.newInstance(type, rowCount, columnCount);

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                String value = stringMatrix[row][column];

                E enumValue = StringParser.enumParser(type).parse(value);
                if (enumValue == null) {
                    throw new InvalidConfigurationException(this, path, String.format("invalid matrix: element at row %d and column %d is not a %s", type.getSimpleName(), row + 1, column + 1));
                }

                enumMatrix[row][column] = enumValue;
            }
        }

        return enumMatrix;
    }

    @Override
    public @NotNull Map<String, Object> toMap() {
        Map<String, Object> data = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : children.entrySet()) {
            Object value = entry.getValue();
            data.put(entry.getKey(), value instanceof ConfigSection section ? (section.isSequence() ? section.toList() : section.toMap()) : value);
        }

        return data;
    }

    @Override
    public List<Object> toList() {
        return children.values().stream()
                .map(value -> value instanceof ConfigSection section ? (section.isSequence() ? section.toList() : section.toMap()) : value)
                .toList();
    }

    private <T> T parseOrDefault(String path, StringParser<T> parser, T def) {
        String stringToParse = getStringOrNull(path);
        if (stringToParse == null) return def;
        T parsedValue = parser.parse(stringToParse);
        return parsedValue != null ? parsedValue : def;
    }

    private String getStringOrNull(String path) {
        return getString(path, null);
    }

    private Object getNode(String path) throws InvalidConfigurationException {
        Object node = getNodeOrNull(path);
        if (node == null) {
            throw new InvalidConfigurationException(this, path, "is not set");
        }
        return node;
    }

    private Object getNodeOrNull(String path) {
        String[] keys = Util.getSplitPath(path);

        Object currentNode = this;

        for (String key : keys) {
            if (currentNode instanceof SectionNode section) {
                currentNode = section.children.get(key);
                continue;
            }
            return null;
        }

        return currentNode;
    }

    @Override
    public String toString() {
        return toMap().toString();
    }

}
