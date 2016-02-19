package br.net.mirante.singular.form.mform;

import java.io.Serializable;
import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.io.ServiceRefTransientValue;

/**
 * <p>
 * Prove acesso a um serviço e ao mesmo tempo permite serializar essa referência
 * sem necessáriamente implicar na serialização de todo o serviço. É capaz de
 * recuperar a referência ao serviço depois de ser deserializado.
 * </p>
 * <p>
 * É um solução semelhante a {@link org.apache.wicket.model.IModel}.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public interface ServiceRef<T> extends Serializable, Supplier<T> {

    /**
     * Recupera a referência do serviço desejado. Garantido a disponibilidade
     * mesmo depois de serializado e deserializado.
     */
    @Override
    public T get();

    /**
     * Retorna um novo Supplier que guarda o valor retornado do supplier
     * original entre chamadas. Dessa forma evita tentativas repetidas de
     * recuperar o serviço. Faz sentido o seu uso se houver algum custo de
     * performance em chamar get() no supplier original.
     */
    @SuppressWarnings("serial")
    public static <T> ServiceRef<T> cached(ServiceRef<T> supplier) {
        return new ServiceRef<T>() {

            private transient T value;

            @Override
            public T get() {
                if (value == null) {
                    value = supplier.get();
                }
                return value;
            }
        };
    }

    /**
     * Retorna um novo Supplier que sempre retorna o valor informado.
     */
    @SuppressWarnings("serial")
    public static <T extends Serializable> ServiceRef<T> of(T value) {
        return new ServiceRef<T>() {
            @Override
            public T get() {
                return value;
            }
        };
    }

    /**
     * Cria uma ServiceRef para o valor informado mas que descarta o valor caso
     * a refência seja serializada. Tipicamente é utilizado para referência do
     * tipo cache ou que pode ser recalculada depois.
     */
    public static <T> ServiceRefTransientValue<T> ofToBeDescartedIfSerialized(T value) {
        return new ServiceRefTransientValue<T>(value);
    }
}
