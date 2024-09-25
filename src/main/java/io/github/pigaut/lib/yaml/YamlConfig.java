package io.github.pigaut.lib.yaml;

import io.github.pigaut.lib.yaml.node.*;
import io.github.pigaut.lib.yaml.serialize.*;
import io.github.pigaut.lib.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;

import java.io.*;

public class YamlConfig extends RootSection implements Config {

    private final File file;
    private final String name;
    private Configuration configuration;

    public YamlConfig(File file) {
        this(file, new Configuration());
    }

    public YamlConfig(File file, Configuration configuration) {
        Preconditions.checkNotNull(file, "File cannot be null");
        Preconditions.checkNotNull(configuration, "Configuration cannot be null");
        this.file = file;
        this.name = Yamlib.getFileName(file.getName());
        this.configuration = configuration;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull File getFile() {
        return file;
    }

    @Override
    public @NotNull Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        Preconditions.checkNotNull(configuration, "Configuration cannot be null");
        this.configuration = configuration;
    }

    @Override
    public void save() {
        DumpSettings settings = DumpSettings.builder()
                .setDumpComments(true)
                .setIndentWithIndicator(true)
                .setIndicatorIndent(2)
                .build();

        Dump dumper = new Dump(settings, new ConfigRepresenter(settings));
        String yaml = dumper.dumpToString(this);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(yaml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load() {
        this.clear();

        if (!file.exists()) {
            Yamlib.createFile(file);
            return;
        }

        try (Reader reader = new BufferedReader(new FileReader(file))) {
            Load loader = new Load(LoadSettings.builder().setParseComments(true).build());
            Object data = loader.loadFromReader(reader);

            ConfigMapper mapper = configuration.getMapper(data.getClass());

            if (mapper == null) {
                throw new IllegalStateException("No mapper exists for class " + data.getClass().getSimpleName());
            }

            mapper.map(this, data);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete() {
        this.clear();
        file.delete();
    }

}
