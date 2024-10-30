package winter.di.annotationprocessor;

import winter.di.annotations.Snowdrop;
import winter.di.context.WinterDIContext;
import winter.di.util.SnowdropUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class SnowdropProcessor {
    private final WinterDIContext ctx;

    public SnowdropProcessor(WinterDIContext ctx) {
        this.ctx = ctx;
    }

    @SuppressWarnings("unchecked")
    public <T> T instantiate(Class<T> clazz) {
        T existingDrop = (T) ctx.getContainer().byClass(clazz);
        if (existingDrop != null) return existingDrop;
        var mostArgsConstructor = Arrays.stream(clazz.getConstructors())
                .max(Comparator.comparing(Constructor::getParameterCount))
                .orElseThrow(() -> new RuntimeException("class not have constructors or what? %s".formatted(clazz)));
        var args = Arrays.stream(mostArgsConstructor.getParameters())
                .map(param -> Optional.ofNullable(ctx.getContainer().byClassAndName(param.getType(), param.getName()))
                        .or(() -> Optional.ofNullable(ctx.getContainer().byClass(param.getType())))
                        .orElseGet(() -> instantiate(param.getType()))
                ).toArray();
        var annotation = clazz.getAnnotation(Snowdrop.class);
        var name = Optional.ofNullable(fromAnnotation(annotation))
                .orElseGet(() -> SnowdropUtil.decapitalize(clazz.getSimpleName()));
        try {
            T instance = (T) mostArgsConstructor.newInstance(args);
            ctx.getContainer().registerSnowdrop(name, instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException e) {
            throw new RuntimeException("something went wrong", e);
        }
    }

    private static String fromAnnotation(Snowdrop annotation) {
        if (!annotation.name().isBlank()) return annotation.name();
        if (!annotation.value().isBlank()) return annotation.value();
        return null;
    }
}
