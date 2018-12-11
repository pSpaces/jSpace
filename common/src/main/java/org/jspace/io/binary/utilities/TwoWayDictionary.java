package org.jspace.io.binary.utilities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TwoWayDictionary<K, V> implements Map<K, V> {
    private Map<K, V> dictionary = new HashMap<K, V>();
    private Map<V, K> rDictionary = new HashMap<V, K>();

    public TwoWayDictionary(){}

    public V get(Object key){
        return dictionary.get(key);
    }
    public K rGet(Object key){
        return rDictionary.get(key);
    }

    public V getOrDefault(Object key, V defaultValue){
        return dictionary.getOrDefault(key, defaultValue);
    }
    public K rGetOrDefault(Object key, K defaultValue){
        return rDictionary.getOrDefault(key, defaultValue);
    }

    public V put(K key, V value){
        rDictionary.put(value, key);
        return dictionary.put(key, value);
    }
    public K rPut(V key, K value){
        dictionary.put(value, key);
        return rDictionary.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> m){
        for (Map.Entry<? extends K,? extends V> entry : m.entrySet()){
            dictionary.put(entry.getKey(), entry.getValue());
            rDictionary.put(entry.getValue(), entry.getKey());
        }
    }
    public void rPutAll(Map<? extends V, ? extends K> m){
        for (Map.Entry<? extends V,? extends K> entry : m.entrySet()){
            rDictionary.put(entry.getKey(), entry.getValue());
            dictionary.put(entry.getValue(), entry.getKey());
        }
    }

    public Set<Map.Entry<K,V>> entrySet(){
        return dictionary.entrySet();
    }
    public Set<K> keySet() {
        return dictionary.keySet();
    }
    public Collection<V> values(){
        return dictionary.values();
    }

    public int size(){
        return dictionary.size();
    }

    public void clear(){
        dictionary.clear();
        rDictionary.clear();
    }

    public boolean containsKey(Object key){
        return dictionary.containsKey(key);
    }
    public boolean rContainsKey(Object key){
        return rDictionary.containsKey(key);
    }
    public boolean containsValue(Object key){
        return dictionary.containsValue(key);
    }
    public boolean rContainsValue(Object key){
        return rDictionary.containsValue(key);
    }

    public boolean isEmpty(){
        return dictionary.isEmpty();
    }

    public V remove(Object key){
        V value = dictionary.remove(key);
        rDictionary.remove(value);
        return value;
    }
    public K rRemove(Object key){
        K value = rDictionary.remove(key);
        dictionary.remove(value);
        return value;
    }

    public V replace(K key, V value){
        if (dictionary.containsKey(key)){
            V oldValue = dictionary.put(key, value);
            rDictionary.remove(oldValue);
            rDictionary.put(value, key);
            return oldValue;
        }
        else
            return null;
    }
    public K rReplace(V key, K value){
        if (rDictionary.containsKey(key)){
            K oldValue = rDictionary.put(key, value);
            dictionary.remove(oldValue);
            dictionary.put(value, key);
            return oldValue;
        }
        else
            return null;
    }

    public boolean equals(Object o){
        if (o instanceof TwoWayDictionary)
            return ((TwoWayDictionary<K,V>)o).dictionary.equals(this.dictionary);
        else
            return false;
    }
}
