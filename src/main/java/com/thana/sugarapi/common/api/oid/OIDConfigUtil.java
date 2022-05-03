package com.thana.sugarapi.common.api.oid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class OIDConfigUtil {

    private static final HashMap<Method, Method> SYNC_MAP = new HashMap<>();

    public static void scanPackage(String packageName) {
        try {
            for (Class<?> c : getClasses(packageName)) {
                if (c.isAnnotationPresent(OIDClass.class)) {
                    OIDClass annotation = c.getAnnotation(OIDClass.class);
                    for (Method method : c.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Synchronized.class)) {
                            Method targetMethod = annotation.value().getDeclaredMethod(method.getName(), method.getParameterTypes());
                            SYNC_MAP.put(targetMethod, method);
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static Set<Class<?>> getClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().filter(line -> line.endsWith(".class")).map(line -> getClass(line, packageName)).collect(Collectors.toSet());
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
