package io.github.pigaut.lib.yaml.location;

import io.github.pigaut.lib.yaml.*;
import io.github.pigaut.lib.yaml.config.configurator.*;
import io.github.pigaut.lib.yaml.util.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class LocationLoader implements ConfigLoader<Location> {

    private final World defaultWorld;

    public LocationLoader(@Nullable World defaultWorld) {
        this.defaultWorld = defaultWorld;
    }

    @Override
    public @NotNull Location load(@NotNull ConfigSection config) throws InvalidConfigurationException {
        World world = config.getOptional("world", World.class).orElse(defaultWorld);
        double x = config.getDouble("x");
        double y = config.getDouble("y");
        double z = config.getDouble("z");
        float yaw = config.getOptionalFloat("yaw").orElse(0f);
        float pitch = config.getOptionalFloat("pitch").orElse(0f);

        return new Location(world, x, y, z, yaw, pitch);
    }

}
