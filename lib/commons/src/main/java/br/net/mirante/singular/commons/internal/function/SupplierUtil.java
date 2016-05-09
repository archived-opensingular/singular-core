package br.net.mirante.singular.commons.internal.function;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

/**
 * CLASSE APENAS PARA USO INTERNO DO SINGULAR, método utilitários para a classe
 * {@link java.util.function.Supplier}.
 * 
 * @author Daniel C. Bordin
 */
public class SupplierUtil {

    /**
     * Retorna uma supplier baseado em {@link SoftReference} que faz cache do
     * valor criado pelo supplier informado, mas que permite a liberação da
     * memória se necessário.
     */
    public static <T> Supplier<T> cached(Supplier<T> delegate) {
        return new SoftReferenceCacheSupplier<>(delegate);
    }

    private static final class SoftReferenceCacheSupplier<T> implements Supplier<T> {

        private final Supplier<T> delegate;

        private transient SoftReference<T> reference;

        public SoftReferenceCacheSupplier(Supplier<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get() {
            T value = null;
            if (reference != null) {
                value = reference.get();
            }
            if (value == null) {
                synchronized (this) {
                    if (reference != null) {
                        value = reference.get();
                    }
                    if (value == null) {
                        value = delegate.get();
                        if (value == null) {
                            // Pequeno gato controlado. Usa a propria instância
                            // atual para indicar o valor de null.
                            value = (T) this;
                        }
                        reference = new SoftReference<>(value);
                    }
                }
            }
            if (value == this) {
                return null;
            }
            return value;
        }
    }
}
