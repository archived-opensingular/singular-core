package br.net.mirante.singular.form.persistence;

import br.net.mirante.singular.form.SISimple;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeSimple;

import java.lang.reflect.Constructor;
import java.util.Optional;

/**
 * @author Daniel C. Bordin
 */
@SInfoType(spackage = SPackageFormPersistence.class)
public class STypeFormKey extends STypeSimple<SISimple<FormKey>, FormKey> {

    private static final char SEPARATOR = '@';

    public STypeFormKey() {
        super((Class<SISimple<FormKey>>) (Class) SISimple.class, FormKey.class);
    }

    @Override
    protected String toStringPersistence(FormKey originalValue) {
        if (originalValue == null){
            return null;
        }
        return originalValue .getClass().getName() + SEPARATOR + originalValue.toStringPersistence();
    }

    @Override
    public FormKey fromStringPersistence(String originalValue) {
        if (originalValue == null) {
            return null;
        }
        int pos = originalValue.indexOf(SEPARATOR);
        if (pos == -1 || pos == 0 || pos == originalValue.length() - 1) {
            throw new SingularFormPersistenceException("Erro convertando string em FormKey: formato inválido").add(
                    "value", originalValue);
        }
        String className = originalValue.substring(0, pos);
        String keyValue = originalValue.substring(pos + 1);

        Class<?> c;
        try {
            c = Class.forName(className);
        } catch (Exception e) {
            throw new SingularFormPersistenceException(
                    "Erro convertando string em FormKey: erro carregando classe " + className, e).add("value",
                    originalValue);
        }
        if (!FormKey.class.isAssignableFrom(c)) {
            throw new SingularFormPersistenceException(
                    "Erro convertando string em FormKey: " + className + " não extende " +
                            FormKey.class.getSimpleName()).add("value", originalValue);
        }
        Constructor<?> constructor;
        try {
            constructor = c.getConstructor(String.class);
        } catch (Exception e) {
            throw new SingularFormPersistenceException(
                    "Erro convertando string em FormKey: não foi localizado cosntrutor " + className + "(String)", e)
                    .add("value", originalValue);
        }
        try {
            return (FormKey) constructor.newInstance(keyValue);
        } catch (Exception e) {
            throw new SingularFormPersistenceException(
                    "Erro convertando string em FormKey: erro chamando método new " + className + "(String)", e).add(
                    "value", originalValue);
        }
    }

    @Override
    public FormKey fromString(String value) {
        return fromStringPersistence(value);
    }
}