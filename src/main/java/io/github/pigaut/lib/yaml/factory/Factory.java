package io.github.pigaut.lib.yaml.factory;

import io.github.pigaut.lib.yaml.serialize.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

public interface Factory<T> extends ConfigLoader<T> {

    @NotNull
    default T create() {
        return null;
    }

    @NotNull
    default T create(String value) throws IllegalArgumentException {
        return null;
    }

    @NotNull
    default T create(char value) {
        return null;
    }

    @NotNull
    default T create(int value) {
        return null;
    }

    @NotNull
    default T create(boolean value) {
        return null;
    }

    @NotNull
    default T create(double value) {
        return null;
    }

    @NotNull
    default T create(long value) {
        return null;
    }

    @NotNull
    default T create(float value) {
        return null;
    }

    @NotNull
    default T createFromStrings(List<String> elements) {
        return null;
    }

    default Stream<T> createFromStrings(Stream<String> stream) {
        return stream.map(this::create);
    }

    @NotNull
    default T createFromCharacters(List<Character> elements) {
        return null;
    }

    @NotNull
    default T createFromIntegers(List<Integer> elements) {
        return null;
    }

    @NotNull
    default T createFromBooleans(List<Boolean> elements) {
        return null;
    }

    @NotNull
    default T createFromDoubles(List<Double> elements) {
        return null;
    }

    @NotNull
    default T createFromLongs(List<Long> elements) {
        return null;
    }

    @NotNull
    default T createFromFloats(List<Float> elements) {
        return null;
    }

}
