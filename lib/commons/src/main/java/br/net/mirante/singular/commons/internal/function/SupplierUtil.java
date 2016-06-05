package br.net.mirante.singular.commons.internal.function;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

import static org.apache.commons.lang3.ObjectUtils.NULL;

/**
 * CLASSE APENAS PARA USO INTERNO DO SINGULAR, método utilitários para a classe
 * {@link java.util.function.Supplier}.
 *
 * @author Daniel C. Bordin
 */
public final class SupplierUtil {

    private SupplierUtil() {
    }

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

        private SoftReference<T> reference;

        public SoftReferenceCacheSupplier(Supplier<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public synchronized T get() {
            T value = null;
            if (reference != null) {
                value = reference.get();
            }
            if (value == null) {
                if (delegate != null) {
                    value = delegate.get();
                }
                if (value == null) {
                    value = (T) NULL;
                }
                reference = new SoftReference<>(value);
            }
            if (NULL.equals(value)) {
                return null;
            }
            return value;
        }
    }
}
