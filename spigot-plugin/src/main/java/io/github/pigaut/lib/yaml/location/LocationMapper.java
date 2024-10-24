package io.github.pigaut.lib.yaml.location;

import io.github.pigaut.lib.yaml.*;
import io.github.pigaut.lib.yaml.config.configurator.*;
import org.bukkit.*;
import org.snakeyaml.engine.v2.common.*;

public class LocationMapper implements ConfigMapper<Location> {

    private final boolean compact;

    public LocationMapper(boolean compact) {
        this.compact = compact;
    }

    @Override
    public void map(ConfigSection config, Location location) {
        World world = location.getWorld();

        if (world != null) {
            config.set("world", world.getName());
        }

        config.set("x", location.getX());
        config.set("y", location.getY());
        config.set("z", location.getZ());

        float yaw = location.getYaw();
        if (yaw != 0) {
            config.set("yaw", yaw);
        }

        double pitch = location.getPitch();
        if (pitch != 0) {
            config.set("pitch", pitch);
        }

        config.setKeyless(false);
        if (compact) {
            config.setFlowStyle(FlowStyle.FLOW);
        }
    }

}
