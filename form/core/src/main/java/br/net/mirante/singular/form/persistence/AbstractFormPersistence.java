package br.net.mirante.singular.form.persistence;

import br.net.mirante.singular.form.SIComposite;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static br.net.mirante.singular.form.persistence.SPackageFormPersistence.ATR_FORM_KEY;

/**
 * @author Daniel C. Bordin
 */
public abstract class AbstractFormPersistence<INSTANCE extends SIComposite, KEY extends FormKey>
        implements FormPersistence<INSTANCE> {

    private final Class<KEY> keyClass;

    private final Constructor<KEY> keyConstructor;

    private String name;

    public AbstractFormPersistence(Class<KEY> keyClass) {
        this.keyClass = keyClass;
        this.keyConstructor = findConstructorString(keyClass);
    }

    private Constructor<KEY> findConstructorString(Class<KEY> keyClass) {
        try {
            return keyClass.getConstructor(String.class);
        } catch (Exception e) {
            throw new SingularFormPersistenceException(
                    "Erro tentando obter o construtor " + keyClass.getSimpleName() + "(String) na classe " +
                            keyClass.getName(), e).add(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public KEY keyFromString(String persistenceString) {
        try {
            return keyConstructor.newInstance(persistenceString);
        } catch (Exception e) {
            throw new SingularFormPersistenceException(
                    "Erro criando FormKey para o valor string da chave '" + persistenceString + "'", e).add(this);
        }
    }

    @Override
    public final FormKey insert(INSTANCE instance) {
        if (instance == null) {
            throw new SingularFormPersistenceException("O parâmetro instance está null").add(this);
        }
        return insertImpl(instance);
    }

    @Override
    public final INSTANCE load(FormKey key) {
        INSTANCE instance = loadImpl(key);
        if (instance == null) {
            throw new SingularFormPersistenceException("Não foi encontrada a instância").add(this).add("Key", key);
        }
        return instance;
    }

    public final Optional<INSTANCE> loadOpt(FormKey key) {
        return Optional.ofNullable(loadImpl(key));
    }

    private final INSTANCE loadImpl(FormKey key) {
        INSTANCE instance = loadInternal(checkKey(key, null, " o parâmetro key não fosse null"));
        if (instance != null) {
            KEY key2 = readKeyAttribute(instance, " a instância carregada tivesse o atributo FormKey preenchido");
            if (!key2.equals(key)) {
                throw new SingularFormPersistenceException("FormKey da instância encontrada, não é igual à solicitada")
                        .add(this).add("Key Esperado", key).add("Key Encontado", key2).add(instance);
            }
        }
        return instance;
    }

    @Override
    public final void delete(FormKey key) {
        deleteInternal(checkKey(key, null, " o parâmetro key não fosse null"));
    }

    @Override
    public final void update(INSTANCE instance) {
        KEY key = readKeyAttribute(instance, " a instância tivesse o atributo FormKey preenchido");
        updateInternal(key, instance);
    }

    @Override
    public final FormKey insertOrUpdate(INSTANCE instance) {
        KEY key = readKeyAttribute(instance, null);
        if (key == null) {
            key = insertImpl(instance);
        } else {
            updateInternal(key, instance);
        }
        return key;
    }

    private KEY insertImpl(INSTANCE instance) {
        KEY key = insertInternal(instance);
        checkKey(key, instance, " o insert interno gerasse uma FormKey, mas retornou null");
        instance.setAttributeValue(ATR_FORM_KEY, key);
        return key;
    }

    @Override
    public final Iterable<INSTANCE> loadAllAsIterable() {
        return loadAllAsIterableInternal();
    }

    @Override
    public final Collection<INSTANCE> loadAllAsCollection() {
        return loadAllAsCollectionInternal();
    }

    @Override
    public final List<INSTANCE> loadAllAsList() {
        return loadAllAsListInternal();
    }

    protected abstract void updateInternal(KEY key, INSTANCE instance);

    protected abstract void deleteInternal(KEY key);

    protected abstract KEY insertInternal(INSTANCE instance);

    protected abstract INSTANCE loadInternal(KEY key);

    protected abstract Iterable<INSTANCE> loadAllAsIterableInternal();

    protected abstract Collection<INSTANCE> loadAllAsCollectionInternal();

    protected abstract List<INSTANCE> loadAllAsListInternal();


    //-------------------------------------------------
    // Métodos de apoio
    //-------------------------------------------------

    /**
     * Lê o {@link FormKey} de uma instância e verifica se é da classe esperada. Se for diferente de null e não for da
     * classe espera, então dispara uma Exception.
     */
    protected final KEY readKeyAttribute(INSTANCE instance, String msgRequired) {
        if (instance == null) {
            throw new SingularFormPersistenceException("O parâmetro instance está null").add(this);
        }
        FormKey key = instance.getAttributeValue(ATR_FORM_KEY);
        return checkKey(key, instance, msgRequired);
    }

    protected final <KEY extends FormKey> KEY checkKey(FormKey key, INSTANCE instance, String msgRequired) {
        if (key == null) {
            if (msgRequired != null) {
                throw new SingularFormPersistenceException("Era esperado que " + msgRequired).add(this).add("key", key)
                        .add(instance);
            }
        } else if (!keyClass.isInstance(key)) {
            throw new SingularFormPersistenceException(
                    "A chave encontrada incompatível: (key= " + key + ") é da classe " + key.getClass().getName() +
                            " mas era esperado que fosse da classe " + keyClass.getName()).add(this).add(instance);
        }
        return (KEY) key;
    }

    protected void addExceptionInfo(SingularFormPersistenceException exception) {
        exception.add("persitence", toString());
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        if (name != null) {
            s += "( name=" + name + ")";
        }
        return s;
    }
}
