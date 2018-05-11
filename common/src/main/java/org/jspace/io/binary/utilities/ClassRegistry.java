package org.jspace.io.binary.utilities;

import org.jspace.io.binary.exceptions.ClassRegistryException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassRegistry {
    private static TwoWayDictionary<Class, String> registry = new TwoWayDictionary<Class, String>();
    private static HashSet<Class> primitives = initializePrimitives();
    private static HashSet<Class> initializePrimitives(){
        HashSet<Class> set = new HashSet<Class>();
        set.add(Boolean.class);
        set.add(Short.class);
        set.add(Integer.class);
        set.add(Long.class);
        set.add(Float.class);
        set.add(Double.class);
        set.add(Character.class);
        return set;
    }

    public static String get(Class key) throws Exception {
        if (registry.containsKey(key))
            return registry.get(key);
        else
            return null;
    }
    public static Class get(String value){
        if (registry.containsValue(value))
            return registry.rGet(value);
        else
            return null;
    }

    public static void register(Class key, String value) throws ClassRegistryException {
        put(key, value);
    }
    public static void put(Class key, String value) throws ClassRegistryException {
        if (primitives.contains(key))
            throw new ClassRegistryException("Cannot add primitive type to the Class Dictionary. '" + key + "' is considered a primitive type.");
        registry.put(key, value);
    }

    public static void remove(Class key){
        registry.remove(key);
    }
    public static void remove(String value){
        registry.remove(value);
    }

    public static boolean containsClass(Class key){
        return registry.containsKey(key);
    }
    public static boolean containsString(String value){
        return registry.containsValue(value);
    }

    public static boolean isPrimitive(Class key){
        return primitives.contains(key) || key.isEnum();
    }

    public static void clear(){
        registry.clear();
    }

    public static int size(){
        return registry.size();
    }

    public static Set<Map.Entry<Class,String>> entrySet(){
        return registry.entrySet();
    }
    public static Set<Class> keySet(){
        return registry.keySet();
    }
    public static Collection<String> values(){
        return registry.values();
    }
}
