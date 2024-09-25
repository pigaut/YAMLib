package io.github.pigaut.lib.yaml.factory;

import io.github.pigaut.lib.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class AbstractFactory<T> implements Factory<T> {

    private final Map<String, Factory<T>> factoriesById = new HashMap<>();
    private Factory<T> defaultFactory = null; //new Factory<>() {};

    public void addFactory(String id, Factory<T> factory) {
        factoriesById.put(id, factory);
    }

    public void removeFactory(String id) {
        factoriesById.remove(id);
    }

    public Factory<T> getFactory(String id) {
        return factoriesById.getOrDefault(id, defaultFactory);
    }

    public Factory<T> getDefaultFactory() {
        return defaultFactory;
    }

    public void setDefaultFactory(@NotNull Factory<T> defaultFactory) {
        this.defaultFactory = defaultFactory;
    }

//    @Override
//    public @Nullable T createFromConfig(Config config) {
//        return defaultFactory.createFromConfig(config);
//    }
//
//    @Override
//    public @Nullable T createFromSection(ConfigSection section) {
//        return defaultFactory.createFromSection(section);
//    }
//
//    @Override
//    public @Nullable T createFromSequence(@NotNull ConfigSequence sequence) {
//        return defaultFactory.createFromSequence(sequence);
//    }
//
//    @Override
//    public @Nullable T createFromField(ConfigField field) {
//        return defaultFactory.createFromField(field);
//    }

    @Override
    public @Nullable T create() {
        return defaultFactory.create();
    }

    @Override
    public @Nullable T create(char value) {
        return defaultFactory.create(value);
    }

    @Override
    public @Nullable T create(int value) {
        return defaultFactory.create(value);
    }

    @Override
    public @Nullable T create(boolean value) {
        return defaultFactory.create(value);
    }

    @Override
    public @Nullable T create(double value) {
        return defaultFactory.create(value);
    }

    @Override
    public @Nullable T create(long value) {
        return defaultFactory.create(value);
    }

    @Override
    public @Nullable T create(float value) {
        return defaultFactory.create(value);
    }

    @Override
    public @Nullable T createFromStrings(List<String> elements) {
        return defaultFactory.createFromStrings(elements);
    }

    @Override
    public @Nullable T createFromCharacters(List<Character> elements) {
        return defaultFactory.createFromCharacters(elements);
    }

    @Override
    public @Nullable T createFromIntegers(List<Integer> elements) {
        return defaultFactory.createFromIntegers(elements);
    }

    @Override
    public @Nullable T createFromBooleans(List<Boolean> elements) {
        return defaultFactory.createFromBooleans(elements);
    }

    @Override
    public @Nullable T createFromDoubles(List<Double> elements) {
        return defaultFactory.createFromDoubles(elements);
    }

    @Override
    public @Nullable T createFromLongs(List<Long> elements) {
        return defaultFactory.createFromLongs(elements);
    }

    @Override
    public @Nullable T createFromFloats(List<Float> elements) {
        return defaultFactory.createFromFloats(elements);
    }

}
