package io.github.pigaut.lib.yaml;

import io.github.pigaut.lib.yaml.config.configurator.*;
import io.github.pigaut.lib.yaml.itemstack.*;
import io.github.pigaut.lib.yaml.location.*;
import io.github.pigaut.lib.yaml.world.*;
import org.bukkit.*;
import org.bukkit.inventory.*;

public class SpigotConfigurator extends StandardConfigurator {

    public SpigotConfigurator() {
        this(true);
    }

    public SpigotConfigurator(boolean compact) {
        this(compact, Bukkit.getWorlds().get(0));
    }

    public SpigotConfigurator(boolean compact, World defaultWorld) {
        registerMapper(ItemStack.class, new ItemStackMapper(compact));
        registerMapper(Location.class, new LocationMapper(compact));

        registerLoader(ItemStack.class, new ItemStackLoader());
        registerLoader(Location.class, new LocationLoader(defaultWorld));
    }

}
