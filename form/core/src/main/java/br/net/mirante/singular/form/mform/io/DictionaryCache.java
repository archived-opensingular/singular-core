package br.net.mirante.singular.form.mform.io;

import br.net.mirante.singular.form.mform.SDictionary;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Represents the storage of know dicionaries used on instance serialization.
 */
public class DictionaryCache {
    private Map<Integer, SDictionary> dictCache = newHashMap();
    private Map<SDictionary, Integer> reverseCache = newHashMap();
    private AtomicInteger lastDictKey = new AtomicInteger();

    public Integer put(SDictionary dict){
        if(reverseCache.containsKey(dict)){ return reverseCache.get(dict);  }
        return add(dict);
    }

    private Integer add(SDictionary dict){
        int id = lastDictKey.incrementAndGet();
        dictCache.put(id, dict);
        reverseCache.put(dict,id);
        return id;
    }

    public boolean has(Integer id){
        return dictCache.containsKey(id);
    }

    public SDictionary get(Integer id){
        return dictCache.get(id);
    }

}
