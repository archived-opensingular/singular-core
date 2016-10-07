package org.opensingular.form.persistence;

import org.opensingular.form.SIComposite;

import java.util.List;
import java.util.Optional;

/**
 * @author Daniel C. Bordin
 */
public abstract class AbstractFormPersistence<INSTANCE extends SIComposite, KEY extends FormKey>
        extends AbstractBasicFormPersistence<INSTANCE, KEY> implements FormPersistence<INSTANCE> {

    private String name;

    public AbstractFormPersistence(Class<KEY> keyClass) {
        super(keyClass);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public INSTANCE load(FormKey key) {
        INSTANCE instance = loadImpl(key);
        if (instance == null) {
            throw addInfo(new SingularFormPersistenceException("Não foi encontrada a instância")).add("Key", key);
        }
        return instance;
    }

    public Optional<INSTANCE> loadOpt(FormKey key) {
        return Optional.ofNullable(loadImpl(key));
    }

    private INSTANCE loadImpl(FormKey key) {
        INSTANCE instance = loadInternal(checkKey(key, null, " o parâmetro key não fosse null"));
        if (instance != null) {
            KEY key2 = readKeyAttribute(instance, " a instância carregada tivesse o atributo FormKey preenchido");
            if (!key2.equals(key)) {
                throw addInfo(new SingularFormPersistenceException(
                        "FormKey da instância encontrada, não é igual à solicitada")).add("Key Esperado", key).add(
                        "Key Encontado", key2).add(instance);
            }
        }
        return instance;
    }

    @Override
    public List<INSTANCE> loadAll(long first, long max) {
        return loadAllInternal(first, max);
    }

    @Override
    public List<INSTANCE> loadAll() {
        return loadAllInternal();
    }

    protected abstract INSTANCE loadInternal(KEY key);

    protected abstract List<INSTANCE> loadAllInternal();

    protected abstract List<INSTANCE> loadAllInternal(long first, long max);


    //-------------------------------------------------
    // Métodos de apoio
    //-------------------------------------------------

    @Override
    public String toString() {
        String s = getClass().getName();
        if (name != null) {
            s += "( name=" + name + ")";
        }
        return s;
    }
}
