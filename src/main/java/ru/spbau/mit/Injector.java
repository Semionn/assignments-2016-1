package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.*;

public final class Injector {

    private Injector() {
    }

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        Map<String, Object> createdClasses = new HashMap<>();
        Set<String> resolvingClasses = new HashSet<>();
        return resolve(rootClassName, implementationClassNames, createdClasses, resolvingClasses);
    }

    private static Object resolve(String className, List<String> implementationClassNames,
                                  Map<String, Object> createdClasses, Set<String> resolvingClasses)
            throws Exception {
        if (resolvingClasses.contains(className)) {
            throw new InjectionCycleException();
        }

        Object result = null;
        if (createdClasses.containsKey(className)) {
            result = createdClasses.get(className);
        }

        if (result != null) {
            return result;
        }

        resolvingClasses.add(className);

        Constructor<?> constructor = Class.forName(className).getConstructors()[0];
        List<Object> constructorArgs = new ArrayList<>();
        for (String depImpl : getConstrDependencies(constructor, implementationClassNames, resolvingClasses)) {
            constructorArgs.add(resolve(depImpl, implementationClassNames, createdClasses, resolvingClasses));
        }
        result = constructor.newInstance(constructorArgs.toArray());
        resolvingClasses.remove(className);
        createdClasses.put(className, result);

        return result;
    }

    private static List<String> getConstrDependencies(Constructor<?> constructor,
                                                      List<String> implementationClassNames,
                                                      Set<String> resolvingClasses)
            throws Exception {
        Class<?>[] parameters = constructor.getParameterTypes();
        List<String> dependencyClasses = new ArrayList<>();
        for (Class<?> depClass : parameters) {
            String depClassName = null;

            if (resolvingClasses.contains(depClass.getCanonicalName())) {
                throw new InjectionCycleException();
            }

            for (String implementedClassName : implementationClassNames) {
                Class implementedClass = Class.forName(implementedClassName);
                if (depClass.isAssignableFrom(implementedClass)) {
                    if (depClassName == null) {
                        depClassName = implementedClassName;
                    } else {
                        throw new AmbiguousImplementationException();
                    }
                }
            }
            if (depClassName == null) {
                throw new ImplementationNotFoundException();
            }
            dependencyClasses.add(depClassName);
        }
        return dependencyClasses;
    }
}
