package br.net.mirante.singular.form.mform.io;

import br.net.mirante.singular.form.mform.MDicionario;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Represents the storage of know dicionaries used on instance serialization.
 */
public class DictionaryCache {
    private Map<Integer, MDicionario> dictCache = newHashMap();
    private Map<MDicionario, Integer> reverseCache = newHashMap();
    private AtomicInteger lastDictKey = new AtomicInteger();

    public Integer put(MDicionario dict){
        if(reverseCache.containsKey(dict)){ return reverseCache.get(dict);  }
        return add(dict);
    }

    private Integer add(MDicionario dict){
        int id = lastDictKey.incrementAndGet();
        dictCache.put(id, dict);
        reverseCache.put(dict,id);
        return id;
    }

    public boolean has(Integer id){
        return dictCache.containsKey(id);
    }

    public MDicionario get(Integer id){
        return dictCache.get(id);
    }

}
