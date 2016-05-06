/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.document;

import java.util.Objects;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;

/**
 * <p>
 * Constroi novos documentos ou instâncias já configurando-os com os serviços e
 * ligações necessárias. Cada sistema deve prover ao menos uma implementação de
 * fábrica de acordo com suas necessidades ou usar
 * {@link SDocumentFactory#empty()}.
 * </p>
 * >p> As instâncias criadas já estarão preparadas para serialização se
 * necessário (ver {@link FormSerializationUtil#checkIfSerializable(SInstance)}
 * ).
 * </p>
 * <p>
 * O método {@link #setupDocument(SDocument)} deve ser implementando com as
 * configurações necessárias pela aplicação. Na maior parte dos casos
 * provavelmente será utilizada a classe
 * {@link br.net.mirante.singular.form.spring.SpringSDocumentFactory}.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public abstract class SDocumentFactory {

    /**
     * Cria um nova instancia configurada pela fábrica e já preparada para
     * serialização se necessário (ver
     * {@link FormSerializationUtil#checkIfSerializable(SInstance)}).
     */
    public final SInstance createInstance(RefType rootType) {
        SInstance instance = createIntanceWithoutSetup(rootType);
        setupDocument(instance.getDocument());
        SType type = instance.getType();
        type.init(instance);
        return instance;
    }

    private final SInstance createIntanceWithoutSetup(RefType rootType) {
        SInstance instance = Objects.requireNonNull(rootType).get().newInstance();
        instance.getDocument().setRootRefType(rootType);
        instance.getDocument().setDocumentFactory(this);
        return instance;
    }

    @Deprecated
    public final <T extends SInstance> T createInstance(SType<T> rootType) {
        T instance = Objects.requireNonNull(rootType).newInstance();
        SDocument document = instance.getDocument();
        document.setDocumentFactory(this);
        setupDocument(document);
        return instance;
    }

    /**
     * Retorna uma referência serializável a fábrica atual, de modo que a mesma
     * possa ser serializada (provavelmente durante uma tela de edição) e
     * posteriomente recupere a referência à fábrica a atual, que em geral não
     * faz sentido ser serializada em sim.
     */
    public abstract RefSDocumentFactory getDocumentFactoryRef();

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

    /**
     * Fábrica de conveniência que não faz nenhuma configuração na instância ou
     * documento ao criá-los.
     */
    public static final SDocumentFactory empty() {
        return new SDocumentFactoryEmpty();
    }
}
