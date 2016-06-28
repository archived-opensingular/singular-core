/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.document;

import java.util.Objects;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.io.FormSerializationUtil;

/**
 * <p>
 * Constroi novos documentos ou instâncias já configurando-os com os serviços e
 * ligações necessárias. Cada sistema deve prover ao menos uma implementação de
 * fábrica de acordo com suas necessidades ou usar
 * {@link SDocumentFactory#empty()}.
 * </p>
 * <p> As instâncias criadas já estarão preparadas para serialização se
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
        return createInstance(rootType, true);
    }

    /**
     * USO INTERNO: cria uma instancia sem disparar as inicializações
     * automáticas.
     */
    public final SInstance createInstance(RefType rootType, boolean executeInitTypeSetup) {
        SType type = Objects.requireNonNull(rootType).get();
        SInstance instance = type.newInstance(false);
        instance.getDocument().setRootRefType(rootType);
        instance.getDocument().setDocumentFactory(this);
        setupDocument(instance.getDocument());
        if (executeInitTypeSetup) {
            instance.init();
        }
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
        return SDocumentFactoryEmpty.getEmptyInstance();
    }

    /**
     * Cria uma nova fábrica de documento que além das configurações originais, aplicará os passos adicionais
     * informados. A nova configuração será executada na sequencia da configuração original.
     *
     * @param extraSetupStep Passo adicional de configuração a se executado no documento em complemento a factory
     *                       atual.
     * @return Nova fábrica com o passo novo
     */
    public SDocumentFactory extendAddingSetupStep(IConsumer<SDocument> extraSetupStep) {
        return new SDocumentExtended(this, extraSetupStep);
    }
}
