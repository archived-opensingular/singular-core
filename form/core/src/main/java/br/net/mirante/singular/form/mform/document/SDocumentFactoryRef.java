package br.net.mirante.singular.form.mform.document;

import java.io.Serializable;
import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.SingularFormException;

/**
 * É uma referência a uma {@link SDocumentFactory} que pode ser serializada com
 * segurança e posteriormente (depois de deserializado) será capaz de localizar
 * a refência à factory, que em geral não é serializável. Particularmente útil
 * na integração do formulário com interfaces web.
 *
 * @author Daniel C. Bordin
 */
public abstract class SDocumentFactoryRef implements Supplier<SDocumentFactory>, Serializable {

    private transient SDocumentFactory documentFactory;

    public SDocumentFactoryRef() {
    }

    public SDocumentFactoryRef(SDocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    /**
     * Obtém a factory referenciada. Caso esteja nula a referência interna
     * (devido a uma deserialização), então chama {@link #reloadFactory()}.
     * 
     * @return Nunca null
     */
    @Override
    public final SDocumentFactory get() {
        if (documentFactory == null) {
            documentFactory = reloadFactory();
            if (documentFactory == null) {
                throw new SingularFormException(getClass().getName() + ".reloadFactory() retornou null");
            }
        }
        return documentFactory;
    }

    /**
     * Deve ser implementado com a lógica para recuperar a fábrica referenciada,
     * após a deserialização.
     */
    protected abstract SDocumentFactory reloadFactory();

}
