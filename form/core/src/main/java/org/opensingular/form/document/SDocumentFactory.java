/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.document;

import java.util.Objects;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.form.SType;
import org.opensingular.form.io.FormSerializationUtil;

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
 * {@link org.opensingular.singular.form.spring.SpringSDocumentFactory}.
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
