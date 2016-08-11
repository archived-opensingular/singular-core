package br.net.mirante.singular.form.persistence;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static br.net.mirante.singular.form.persistence.SPackageFormPersistence.ATR_FORM_KEY;

/**
 * @author Daniel C. Bordin
 */
public abstract class AbstractBasicFormPersistence<INSTANCE extends SInstance, KEY extends FormKey>
        implements BasicFormPersistence<INSTANCE> {

    private final Class<KEY> keyClass;

    private final Constructor<KEY> keyConstructor;

    private final Method convertMethod;

    private static final String CONVERTER_METHOD_NAME = "convertToKey";

    public AbstractBasicFormPersistence(Class<KEY> keyClass) {
        this.keyClass = Objects.requireNonNull(keyClass);
        this.keyConstructor = findConstructorString(keyClass);
        this.convertMethod = findConvertMethod(keyClass);
    }

    private Method findConvertMethod(Class<KEY> keyClass) {
        Method method;
        try {
            method = keyClass.getMethod(CONVERTER_METHOD_NAME, Object.class);
        } catch (Exception e) {
            throw addInfo(new SingularFormPersistenceException(
                    "Erro tentando obter o metodo " + CONVERTER_METHOD_NAME + "(Object) na classe " +
                            keyClass.getName(), e));
        }
        if (!Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers()) ||
                !keyClass.isAssignableFrom(method.getReturnType())) {
            throw addInfo(new SingularFormPersistenceException(
                    "O metodo " + CONVERTER_METHOD_NAME + "(Object) encontrado na classe " + keyClass.getName() +
                            " não é compatível com a assintura de método esperado, que seria 'public static " +
                            keyClass.getSimpleName() + " " + CONVERTER_METHOD_NAME + "(Object)'"));
        }
        return method;
    }

    private Constructor<KEY> findConstructorString(Class<KEY> keyClass) {
        try {
            return keyClass.getConstructor(String.class);
        } catch (Exception e) {
            throw addInfo(new SingularFormPersistenceException(
                    "Erro tentando obter o construtor " + keyClass.getSimpleName() + "(String) na classe " +
                            keyClass.getName(), e));
        }
    }

    @Override
    public KEY keyFromString(String persistenceString) {
        try {
            return keyConstructor.newInstance(persistenceString);
        } catch (Exception e) {
            throw addInfo(new SingularFormPersistenceException(
                    "Erro criando FormKey para o valor string da chave '" + persistenceString + "'", e));
        }
    }

    @Override
    public KEY keyFromObject(Object objectValueToBeConverted) {
        if (objectValueToBeConverted == null) {
            return null;
        } else if (keyClass.isInstance(objectValueToBeConverted)) {
            return keyClass.cast(objectValueToBeConverted);
        }
        Object result;
        try {
            result = convertMethod.invoke(null, objectValueToBeConverted);
        } catch (Exception e) {
            throw addInfo(new SingularFormPersistenceException("Erro chamado método " + CONVERTER_METHOD_NAME).add(
                    "value", objectValueToBeConverted));
        }
        return keyClass.cast(result);
    }

    @Override
    public void delete(FormKey key) {
        deleteInternal(checkKey(key, null, " o parâmetro key não fosse null"));
    }

    @Override
    public void update(INSTANCE instance) {
        KEY key = readKeyAttribute(instance, " a instância tivesse o atributo FormKey preenchido");
        updateInternal(key, instance);
    }

    @Override
    public FormKey insertOrUpdate(INSTANCE instance) {
        KEY key = readKeyAttribute(instance, null);
        if (key == null) {
            key = insertImpl(instance);
        } else {
            updateInternal(key, instance);
        }
        return key;
    }

    @Override
    public FormKey insert(INSTANCE instance) {
        if (instance == null) {
            throw addInfo(new SingularFormPersistenceException("O parâmetro instance está null")).add(this);
        }
        return insertImpl(instance);
    }

    private KEY insertImpl(INSTANCE instance) {
        KEY key = insertInternal(instance);
        checkKey(key, instance, " o insert interno gerasse uma FormKey, mas retornou null");
        instance.setAttributeValue(ATR_FORM_KEY, key);
        return key;
    }

    protected abstract void updateInternal(KEY key, INSTANCE instance);

    protected abstract void deleteInternal(KEY key);

    protected abstract KEY insertInternal(INSTANCE instance);

    //-------------------------------------------------
    // Métodos de apoio
    //-------------------------------------------------

    /**
     * Lê o {@link FormKey} de uma instância e verifica se é da classe esperada. Se for diferente de null e não for da
     * classe espera, então dispara uma Exception.
     */
    protected KEY readKeyAttribute(INSTANCE instance, String msgRequired) {
        if (instance == null) {
            throw addInfo(new SingularFormPersistenceException("O parâmetro instance está null"));
        }
        FormKey key = instance.getAttributeValue(ATR_FORM_KEY);
        return checkKey(key, instance, msgRequired);
    }

    protected KEY checkKey(FormKey key, INSTANCE instance, String msgRequired) {
        if (key == null) {
            if (msgRequired != null) {
                throw addInfo(new SingularFormPersistenceException("Era esperado que " + msgRequired)).add("key", null)
                        .add(instance);
            }
        } else if (!keyClass.isInstance(key)) {
            throw addInfo(new SingularFormPersistenceException(
                    "A chave encontrada incompatível: (key= " + key + ") é da classe " + key.getClass().getName() +
                            " mas era esperado que fosse da classe " + keyClass.getName())).add(instance);
        }
        return (KEY) key;
    }

    @Override
    public boolean isPersistent(INSTANCE instance) {
        return readKeyAttribute(instance, null) != null;
    }

    /**
     * Método chamado para adicionar informção do serviço de persistência à exception. Pode ser ser sobreescito para
     * acrescimo de maiores informações.
     */
    protected SingularFormPersistenceException addInfo(SingularFormPersistenceException exception) {
        exception.add("persitence", toString());
        return exception;
    }
}


