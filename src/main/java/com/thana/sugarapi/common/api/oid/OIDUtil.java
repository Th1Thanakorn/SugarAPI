package com.thana.sugarapi.common.api.oid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class OIDUtil {

    private static final ArrayList<Method> SYNC_MAP = new ArrayList<>();

    public static void scanPackage(String packageName) {
        try {
            for (Class<?> c : getClasses(packageName)) {
                if (c.isAnnotationPresent(OIDClass.class)) {
                    for (Method method : c.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Synchronized.class)) {
                            SYNC_MAP.add(method);
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        send();
    }

    public static void send() {
        for (Method method : SYNC_MAP) {
            try {
                Object instance = method.getDeclaringClass().getConstructor().newInstance();
                method.setAccessible(true);
                method.invoke(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
