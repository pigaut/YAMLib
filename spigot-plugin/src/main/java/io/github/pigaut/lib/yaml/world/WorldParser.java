package io.github.pigaut.lib.yaml.world;

import io.github.pigaut.lib.yaml.*;
import io.github.pigaut.lib.yaml.config.parser.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class WorldParser implements Deserializer<World>, Serializer<World> {

    @Override
    public @NotNull World deserialize(@NotNull String worldName) throws DeserializationException {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new DeserializationException("world not found");
        }

        return world;
    }

    @Override
    public @NotNull String serialize(@NotNull World world) {
        return world.getName();
    }

}
