package br.net.mirante.singular.form.mform.document;

import java.util.Objects;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;

/**
 * <p>
 * Constroi novos documentos ou intancias já configurando-os com os serviços e
 * ligações necessárias. Cada sistema deve prover ao menos uma implementação de
 * fábrica de acordo com suas necessidades.
 * </p>
 * <p>
 * O método {@link #setupDocument(SDocument)} deve ser implementando com as
 * configurações necessárias pela aplicação. Na maior parte dos casos
 * provavelmente será utilizada a class
 * {@link br.net.mirante.singular.form.spring.SpringSDocumentFactory}.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public abstract class SDocumentFactory {

    /**
     * Cria um novo documento configurado pela fábrica e cuja a raiz é do tipo
     * informado.
     */
    public final SDocument create(SType<?> rootType) {
        return createInstance(rootType).getDocument();
    }

    /**
     * Cria um nova instancia configurada pela fábrica.
     */
    public final <T extends SInstance> T createInstance(SType<T> rootType) {
        T instance = Objects.requireNonNull(rootType).novaInstancia();
        SDocument document = instance.getDocument();
        document.setDocumentFactory(this);
        setupDocument(document);
        return instance;
    }

    /**
     * Retorna uma referência serializável a fábrica atual, de modo que a mesma
     * possa ser serializada (provavelmente durante uma tela de edição) e
     * posteriomente recupere a referência a fábrica a atual, que em geral não
     * faz sentido ser serializada em sim.
     */
    public abstract SDocumentFactoryRef getDocumentFactoryRef();

    /**
     * Retorna o registro de serviços para busca de serviços pelo documento que
     * por acaso não estejam configurados no próprio documento durante o seu
     * setup inicial. Por exemplo, pode ser procurado um bean provedor para o
     * conteúdo (lista) de uma seleção (combo ou outro tipo).
     *
     * @return Pode ser null
     */
    public abstract ServiceRegistry getServiceRegistry();

    // public abstract SingularFormConfig<?> getFormConfig();

    /**
     * Método a ser sobreescrito com o objetivo de configurar um novo documento
     * criado. Por configuração, entende-se o registro de serviços no documento
     * e outros parâmetros do mesmo.
     */
    protected abstract void setupDocument(SDocument document);
}
