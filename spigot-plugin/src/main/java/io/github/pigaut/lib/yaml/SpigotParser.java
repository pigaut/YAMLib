package io.github.pigaut.lib.yaml;

import io.github.pigaut.lib.yaml.config.parser.*;
import io.github.pigaut.lib.yaml.world.*;
import org.bukkit.*;

public class SpigotParser extends StandardParser {

    public SpigotParser() {
        WorldParser worldParser = new WorldParser();
        registerDeserializer(World.class, worldParser);
        registerSerializer(World.class, worldParser);

    }

}
