package io.github.pigaut.yamlib;

import io.github.pigaut.lib.yaml.*;
import io.github.pigaut.lib.yaml.util.*;
import org.bukkit.*;
import org.bukkit.plugin.java.*;

import java.io.*;
import java.time.*;
import java.util.*;

public class YAMLibPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        File configFile = new File(getDataFolder(), "config.yml");
        Config config = new YamlConfig(configFile);

        config.delete();
        saveDefaultConfig();
        config.load();

        Debug.send(config.getLong("int-as-long", null) != null);

        Debug.send(config.getBigInteger("big-int").longValue() == 1902312837418902743L);

        Debug.send(config.getString("name").equals("ExampleApp"));
        Debug.send(config.getString("version").equals("1.0"));

        Debug.send(config.getString("path.that.does.not.exist", null) == null);

        Debug.send(config.isRoot());
        Debug.send(!config.isSequence());

        Debug.send(config.getString("fields[0].key").equals("max_connections"));
        Debug.send(config.getInt("fields[0].value") == 100);

        List<Object> fields = config.getValues().toList();
        Debug.send(!(fields.get(0) instanceof ConfigSection));
        Debug.send(!(fields.get(1) instanceof ConfigSection));
        Debug.send(fields.get(4) instanceof ConfigSection section && section.isSequence());
        Debug.send(fields.get(5) instanceof ConfigSection section && section.isSequence());

        ConfigSection sequence = config.getSection("fields");
        List<ConfigSection> sections = sequence.nestedSections().toList();
        Debug.send(sections.size() == 3);

        // Field checks
        Debug.send(sequence.getSection("0").getInt("value") == 100);
        Debug.send(sequence.getSection("1").getDouble("value") == 30.5);
        Debug.send(!sequence.getSection("2").getBoolean("value"));

        // Getters with defaults
        Debug.send(config.getString("non.existent.path", "default").equals("default"));
        Debug.send(config.getInt("non.existent.path", 42) == 42);
        Debug.send(config.getBoolean("non.existent.path", true));
        Debug.send(config.getDouble("non.existent.path", 3.14) == 3.14);
        Debug.send(config.getLong("non.existent.path", 123456789L) == 123456789L);
        Debug.send(config.getFloat("non.existent.path", 1.23f) == 1.23f);

        // new tests

        String name = config.getString("location[0]");
        int x = config.getInt("location[1]");
        int y = config.getInt("location[2]");
        int z = config.getInt("location[3]");

        Debug.send(name.equalsIgnoreCase("monke_world"));
        Debug.send(x == 1);
        Debug.send(y == 1);
        Debug.send(z == 1);

        Debug.send(config.getString("fields[0].key").equalsIgnoreCase("max_connections"));

        int[][] matrix = config.getIntMatrix("matrix", 3, 3);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Debug.send("Matrix elements: " + matrix[row][column]);
            }
        }

        Debug.send(config.getInt("matrix[0][0]") == 0);

        Debug.send(config.getEnum("materials[0][1]", Material.class));

        Material[][] materials = config.getEnumMatrix("materials", Material.class, 2, 2);
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 2; column++) {
                Debug.send("Materials matrix: " + materials[row][column]);
            }
        }

        Debug.send(config.parseOrDefault("date", LocalDate.class, null) != null);
        Debug.send(config.parseOrDefault("time", LocalTime.class, null) != null);
        Debug.send(config.getDateTime("date-time", null) != null);




        configFile.delete();

        config.set("section.stringWith", "stringWith($*&(#@!+\\//");

        config.set("array", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));

        config.set("optional.null", Optional.empty());
        config.set("optional.not-null", Optional.of(10));

        ConfigSection section1 = config.getSectionOrCreate("test");
        ConfigSection section2 = config.getSectionOrCreate("test");

        section1.set("wow", 1);
        section2.set("nah", 2);

        config.set("set", Set.of("key1", "key2", "key3"));
        config.set("list", List.of("key1", "key2", "key3"));

        config.save();
//
//        config.reset();
//
//        config.delete();

    }

}
