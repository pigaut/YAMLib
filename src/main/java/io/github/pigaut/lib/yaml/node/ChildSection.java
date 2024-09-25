package io.github.pigaut.lib.yaml.node;

import io.github.pigaut.lib.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ChildSection extends SectionNode {

    private final SectionNode parent;
    private final String key;

    public ChildSection(SectionNode parent, String key, boolean sequence) {
        super(sequence);
        this.parent = parent;
        this.key = key;
        this.parent.children.put(key, this);
    }

    public ChildSection(SectionNode parent, String key) {
        this(parent, key, false);
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public @NotNull Config getRoot() {
        return parent.getRoot();
    }

    @Override
    public @NotNull ConfigSection getParent() {
        return parent;
    }

    @Override
    public @NotNull String getKey() {
        return key;
    }

    private ConfigSection[] getBranch() {
        List<ConfigSection> nodeTree = new ArrayList<>();

        ConfigSection currentNode = this;

        while (!currentNode.isRoot()) {
            nodeTree.add(0, currentNode);
            currentNode = currentNode.getParent();
        }

        return nodeTree.toArray(new ConfigSection[0]);
    }

    public @NotNull String getPath() {
        ConfigSection[] branch = getBranch();
        ConfigSection currentNode = this;

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < branch.length; i++) {
            currentNode = branch[i];
            String key = currentNode.getKey();

            while (currentNode.isSequence() && i < branch.length - 1) {
                ConfigSection child = branch[1 + i++];

                key = key + "[" + child.getKey() + "]";
                currentNode = child;
            }

            keys.add(key);
        }

        return String.join(".", keys);
    }

}
