package me.michqql.engine.util.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Registry<T> {

    private final Function<String, T> loadingFunction;
    private final Map<String, T> registryMap = new HashMap<>();
    private boolean acceptingEntries = true;

    public Registry(Function<String, T> loadingFunction) {
        this.loadingFunction = loadingFunction;
    }

    public T get(String key) {
        if(acceptingEntries)
            return registryMap.computeIfAbsent(key, loadingFunction);

        return registryMap.get(key);
    }

    public boolean contains(String key) {
        return registryMap.containsKey(key);
    }

    public boolean isAcceptingEntries() {
        return acceptingEntries;
    }

    public void stopAcceptingEntries() {
        this.acceptingEntries = false;
    }
}
