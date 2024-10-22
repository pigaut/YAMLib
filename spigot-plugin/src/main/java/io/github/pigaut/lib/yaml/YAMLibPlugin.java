package io.github.pigaut.lib.yaml;

import org.bukkit.inventory.*;
import org.bukkit.plugin.java.*;
import org.snakeyaml.engine.v2.common.*;

import java.io.*;
import java.util.*;

public class YAMLibPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();

        File file = new File(getDataFolder(), "config.yml");

        Config config = new SpigotConfig(file);
        config.load();

        ItemStack item = config.getSection("item").load(ItemStack.class);

        config.set("item2", item);

        config.getSection("locations").setDefaultFlowStyle(FlowStyle.FLOW);

        config.set("nullcheck", "");

        config.set("monos[2]", "mono3");

        config.set("monkeys.2", "mono3");

        config.set("matrix", List.of(
                        List.of(
                                List.of(0, 1, 2),
                                List.of(3, 4, 5),
                                List.of(6, 7, 8)
                        ),
                        List.of(
                                List.of(0, 1, 2),
                                List.of(3, 4, 5),
                                List.of(6, 7, 8)
                        ),
                        List.of(
                                List.of(0, 1, 2),
                                List.of(3, 4, 5),
                                List.of(6, 7, 8)
                        )
                )
        );

        config.save();

    }

}
