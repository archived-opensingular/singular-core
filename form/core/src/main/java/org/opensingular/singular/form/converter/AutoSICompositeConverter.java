package org.opensingular.singular.form.converter;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SingularFormException;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.get.dsl.GetterHandler;
import net.vidageek.mirror.set.dsl.FieldSetter;
import net.vidageek.mirror.set.dsl.SetterHandler;

import java.io.Serializable;

/**
 * Conversor que monta os objetos e instancias a partir de reflection.
 *
 * @param <T>
 */
public class AutoSICompositeConverter<T extends Serializable> implements SInstanceConverter<T, SInstance> {

    private final Class<T> resultClass;

    private AutoSICompositeConverter(Class<T> resultClass) {
        this.resultClass = resultClass;
    }

    public static <X extends Serializable> AutoSICompositeConverter<X> of(Class<X> resultClass) {
        return new AutoSICompositeConverter<>(resultClass);
    }

    @Override
    public void fillInstance(SInstance ins, T obj) {
        if (!(ins instanceof SIComposite)) {
            throw new SingularFormException("AutoSICompositeConverter somente funciona com instancias compostas.");
        }
        final SIComposite   cins          = (SIComposite) ins;
        final GetterHandler getterHandler = new Mirror().on(obj).get();
        cins.getType().getFields().forEach(f -> {
            cins.setValue(f, getterHandler.field(f.getNameSimple()));
        });
    }

    @Override
    public T toObject(SInstance ins) {
        if (!(ins instanceof SIComposite)) {
            throw new SingularFormException("AutoSICompositeConverter somente funciona com instancias compostas.");
        }
        if (ins.isEmptyOfData()) {
            return null;
        }
        final SIComposite   cins          = (SIComposite) ins;
        final T             newInstance   = new Mirror().on(resultClass).invoke().constructor().withoutArgs();
        final SetterHandler setterHandler = new Mirror().on(newInstance).set();

        cins.getFields().forEach(f -> {
            final FieldSetter setter = setterHandler.field(f.getName());
            if (setter != null) {
                setter.withValue(f.getValue());
            }
        });
        return newInstance;
    }

}