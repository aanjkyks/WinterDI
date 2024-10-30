package winter.di.context;

import winter.di.annotationprocessor.SnowdropProcessor;
import winter.di.annotations.Snowdrop;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

public class WinterDIContext {
    private final SnowdropContainer container = new SnowdropContainer();

    public static WinterDIContext buildContext(Class<?> mainClass) {
        List<Class<?>> snowdropClasses = findSnowdropClasses(mainClass.getPackageName());
        WinterDIContext ctx = new WinterDIContext();
        SnowdropProcessor processor = new SnowdropProcessor(ctx);
        snowdropClasses
                .forEach(processor::instantiate);
        return ctx;
    }

    public SnowdropContainer getContainer() {
        return container;
    }

    private static List<Class<?>> findSnowdropClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        if (stream == null) throw new RuntimeException("could not read package with name %s".formatted(packageName));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        return reader.lines()
                .map(line -> dfs(line, packageName))
                .flatMap(Collection::stream)
                .filter(clazz -> clazz.isAnnotationPresent(Snowdrop.class))
                .toList();
    }

    private static List<Class<?>> dfs(String line, String basePackage) {
        if (line.endsWith(".class"))
            return List.of(findClass(line, basePackage));
        return findSnowdropClasses(basePackage + "." + line);
    }

    private static Class<?> findClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("could not find single class", e);
        }
    }
}
