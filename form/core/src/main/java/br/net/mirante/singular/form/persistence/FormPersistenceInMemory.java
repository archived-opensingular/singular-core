package br.net.mirante.singular.form.persistence;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * Persitencia de instância baseada em mapa em memória.
 *
 * @author Daniel C. Bordin
 */
public class FormPersistenceInMemory<INSTANCE extends SIComposite>
        extends AbstractFormPersistence<INSTANCE, FormKeyInt> {

    private final Map<FormKeyInt, INSTANCE> collection = new LinkedHashMap<>();

    private int id;

    public FormPersistenceInMemory(SDocumentFactory documentFactory, RefType refType) {
        super(FormKeyInt.class);
    }

    @Override
    protected void updateInternal(FormKeyInt key, INSTANCE instance) {
        if (!collection.containsKey(key)) {
            throw addInfo(new SingularFormPersistenceException("Não existe uma isntância com a chave informada")).add(
                    "key", key);
        }
        collection.put(key, instance);
    }

    @Override
    protected void deleteInternal(FormKeyInt key) {
        collection.remove(key);
    }

    @Override
    protected FormKeyInt insertInternal(INSTANCE instance) {
        FormKeyInt key = new FormKeyInt(++id);
        collection.put(key, instance);
        return key;
    }

    @Override
    protected INSTANCE loadInternal(FormKeyInt key) {
        return collection.get(key);
    }

    @Override
    protected List<INSTANCE> loadAllInternal() {
        return Lists.newArrayList(collection.values());
    }

    @Override
    protected List<INSTANCE> loadAllInternal(long first, long max) {
        return loadAllInternal().subList((int) first, (int) Math.min(first + max, countAll()));
    }

    @Override
    public long countAll() {
        return collection.values().size();
    }
}
