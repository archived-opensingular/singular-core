package org.opensingular.form.spring;

import java.util.ArrayList;
import java.util.List;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormPersistenceInMemory;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by ronaldtm on 16/03/17.
 */
public class SpringFormPersistenceInMemory<T extends SType<I>, I extends SIComposite>
        extends FormPersistenceInMemory<I>
        implements InitializingBean {

    private List<Class<? extends SPackage>> packageClasses = new ArrayList<>();
    private String                          typeFullName;
    private SDocumentFactory                documentFactory;

    private ISupplier<SDictionary>          dictionary;

    public SpringFormPersistenceInMemory() {}
    public SpringFormPersistenceInMemory(SDocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dictionary = new DictionarySupplier(packageClasses);
    }

    public RefType getRefType() {
        return new RefTypeImpl(dictionary, typeFullName);
    }

    @SuppressWarnings("unchecked")
    public T getType() {
        return (T) dictionary.get().getType(typeFullName);
    }

    public I createInstance() {
        return (I) documentFactory.createInstance(getRefType());
    }

    //@formatter:off
    public SpringFormPersistenceInMemory<T, I> setPackageClasses (List<Class<? extends SPackage>> packageClasses) { this.packageClasses  = packageClasses;  return this; }
    public SpringFormPersistenceInMemory<T, I> setDocumentFactory(SDocumentFactory               documentFactory) { this.documentFactory = documentFactory; return this; }
    public SpringFormPersistenceInMemory<T, I> setTypeFullName   (String                            typeFullName) { this.typeFullName    = typeFullName;    return this; }
    //@formatter:on

    /**
     * O dicionário é reconstruído toda vez, para evitar o erro de serialização.
     */
    private static class DictionarySupplier implements ISupplier<SDictionary> {
        private final List<Class<? extends SPackage>> packageClasses;
        private transient SDictionary                 dictionary;
        public DictionarySupplier(List<Class<? extends SPackage>> packageClasses) {
            this.packageClasses = packageClasses;
        }
        @Override
        public SDictionary get() {
            if (dictionary == null) {
                dictionary = SDictionary.create();
                for (Class<? extends SPackage> clazz : packageClasses)
                    dictionary.loadPackage(clazz);
            }
            return dictionary;
        }
    }

    /**
     * Implementação feita em uma classe estática, para evitar o erro de serialização.
     */
    private static class RefTypeImpl extends RefType {
        private final ISupplier<SDictionary> dict;
        private final String                 name;
        private transient SType<?>           cache;
        private RefTypeImpl(ISupplier<SDictionary> dict, String name) {
            this.dict = dict;
            this.name = name;
        }
        @Override
        protected SType<?> retrieve() {
            if (cache == null) {
                cache = dict.get().getType(name);
            }
            return cache;
        }
    }
}