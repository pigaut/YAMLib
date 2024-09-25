package io.github.pigaut.lib.yaml.serialize;

public class Configuration {

    private final ConfigMapper.MapperContainer classMappers = new ConfigMapper.MapperContainer();
    private final ConfigLoader.LoaderContainer classLoaders = new ConfigLoader.LoaderContainer();
    private final StringSerializer.SerializerContainer classSerializers = new StringSerializer.SerializerContainer();
    private final StringParser.ParserContainer classParsers = new StringParser.ParserContainer();

    public <T> ConfigMapper<T> getExactMapper(Class<T> type) {
        return classMappers.getExactMapper(type);
    }

    public ConfigMapper getMapper(Class<?> type) {
        return classMappers.getMapper(type);
    }

    public <T> void registerMapper(Class<T> type, ConfigMapper<T> mapper) {
        classMappers.registerMapper(type, mapper);
    }

    public <T> ConfigLoader<T> getExactLoader(Class<T> type) {
        return classLoaders.getExactLoader(type);
    }

    public ConfigLoader getLoader(Class<?> type) {
        return classLoaders.getLoader(type);
    }

    public <T> void registerLoader(Class<T> type, ConfigLoader<T> loader) {
        classLoaders.registerLoader(type, loader);
    }

    public StringSerializer getSerializer(Class<?> classType) {
        return classSerializers.getSerializer(classType);
    }

    public <T> StringParser<T> getExactSerializer(Class<T> classType) {
        return classSerializers.getExactSerializer(classType);
    }

    public <T> void registerSerializer(Class<T> classType, StringSerializer<T> parser) {
        classSerializers.registerSerializer(classType, parser);
    }

    public StringParser getParser(Class<?> classType) {
        return classParsers.getParser(classType);
    }

    public <T> StringParser<T> getExactParser(Class<T> classType) {
        return classParsers.getExactParser(classType);
    }

    public <T> void registerParser(Class<T> classType, StringParser<T> parser) {
        classParsers.registerParser(classType, parser);
    }

}
