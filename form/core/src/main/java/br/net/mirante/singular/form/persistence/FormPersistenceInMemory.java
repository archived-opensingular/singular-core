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
        extends AbstractFormPersistence<INSTANCE, FormKeyInteger> {

    private final Map<FormKeyInteger, INSTANCE> collection = new LinkedHashMap<>();

    private int id;

    public FormPersistenceInMemory(SDocumentFactory documentFactory, RefType refType) {
        super(FormKeyInteger.class);
    }

    @Override
    protected void updateInternal(FormKeyInteger key, INSTANCE instance) {
        if (!collection.containsKey(key)) {
            throw new SingularFormPersistenceException("Não existe uma isntância com a chave informada").add(this).add(
                    "key", key);
        }
        collection.put(key, instance);
    }

    @Override
    protected void deleteInternal(FormKeyInteger key) {
        collection.remove(key);
    }

    @Override
    protected FormKeyInteger insertInternal(INSTANCE instance) {
        FormKeyInteger key = new FormKeyInteger(++id);
        collection.put(key, instance);
        return key;
    }

    @Override
    protected INSTANCE loadInternal(FormKeyInteger key) {
        return collection.get(key);
    }

    @Override
    protected Iterable<INSTANCE> loadAllAsIterableInternal() {
        return collection.values();
    }

    @Override
    public Collection<INSTANCE> loadAllAsCollectionInternal() {
        return collection.values();
    }

    @Override
    protected List<INSTANCE> loadAllAsListInternal() {
        return Lists.newArrayList(collection.values());
    }
}
