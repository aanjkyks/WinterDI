package winter.di.context;

import java.util.*;

public class SnowdropContainer {
    private final Map<SnowdropIdentity, Object> fullIdentitySnowdrops = new HashMap<>();
    private final Map<Class<?>, List<Object>> snowdropsByClass = new HashMap<>();

    public Object byClass(Class<?> type) {
        var drops = snowdropsByClass.get(type);
        if (drops == null) return null;
        if (drops.size() > 1) throw new RuntimeException("too many drops for class %s".formatted(type));
        return drops.getFirst();
    }

    public Object byClassAndName(Class<?> type, String name) {
        return fullIdentitySnowdrops.get(new SnowdropIdentity(name, type));
    }

    public SnowdropContainer registerSnowdrop(String name, Object instance) {
        fullIdentitySnowdrops.put(new SnowdropIdentity(name, instance.getClass()), instance);
        var dropsList = Optional.ofNullable(snowdropsByClass.get(instance.getClass()))
                .orElseGet(ArrayList::new);
        dropsList.add(instance);
        snowdropsByClass.put(instance.getClass(), dropsList);

        return this;
    }

    public record SnowdropIdentity(String name, Class<?> clazz) {
    }
}
