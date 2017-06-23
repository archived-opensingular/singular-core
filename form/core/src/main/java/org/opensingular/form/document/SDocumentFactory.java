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

import org.opensingular.form.InternalAccess;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.io.FormSerializationUtil;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.ISupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

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
 * {@link org.opensingular.form.spring.SpringSDocumentFactory}.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public abstract class SDocumentFactory {

    private RefSDocumentFactory factoryReference;

    /**
     * Cria um nova instancia configurada pela fábrica e já preparada para
     * serialização se necessário (ver
     * {@link FormSerializationUtil#checkIfSerializable(SInstance)}).
     */
    @Nonnull
    public final SInstance createInstance(@Nonnull RefType rootType) {
        return createInstance(rootType, true);
    }

    /**
     * USO INTERNO: cria uma instancia usando a factory atual
     * @param executeInitTypeSetup Se true, dispara as inicializações automáticas implementadas em
     * {@link SInstance#init()}. Usar como false quando a instância está sendo recuperada da persistência.
     */
    @Nonnull
    public final SInstance createInstance(@Nonnull RefType rootType, boolean executeInitTypeSetup) {
        SType type = Objects.requireNonNull(rootType).get();

        SDocument owner = new SDocument();
        owner.setRootRefType(rootType);
        owner.setDocumentFactory(this);

        SInstance instance = InternalAccess.INTERNAL.newInstance(type, false, owner);
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
    @Nonnull
    public final RefSDocumentFactory getDocumentFactoryRef() {
        if (factoryReference == null) {
            factoryReference = createDocumentFactoryRef();
            if (factoryReference.get() != this) {
                throw new SingularFormException(
                        "A refencia a criada em RefSDocumentFactory.createDocumentFactoryRef() deveria retornar a " +
                                "mesma instância atual");
            }
        }
        return factoryReference;
    }

    /**
     * Deve ser implementada para conter a lógica de recuperação da fábrica conforme descrito em {@link
     * #getDocumentFactoryRef()}.
     */
    @Nonnull
    protected abstract RefSDocumentFactory createDocumentFactoryRef();


    /**
     * Método a ser sobreescrito com o objetivo de configurar um novo documento
     * criado. Por configuração, entende-se o registro de serviços no documento
     * e outros parâmetros do mesmo.
     */
    protected abstract void setupDocument(@Nonnull SDocument document);

    /**
     * Fábrica de conveniência que não faz nenhuma configuração na instância ou
     * documento ao criá-los.
     */
    @Nonnull
    public static SDocumentFactory empty() {
        return SDocumentFactoryEmpty.getEmptyInstance();
    }

    /**
     * Cria um nova fábrica com o passo de configuração indicado.
     */
    @Nonnull
    public static SDocumentFactory of(@Nonnull IConsumer<SDocument> setupStep) {
        return new SDocumentFactoryExtended(Objects.requireNonNull(setupStep));
    }

    /**
     * Cria uma nova fábrica de documento que além das configurações originais, aplicará os passos adicionais
     * informados. A nova configuração será executada na sequencia da configuração original.
     *
     * @param extraSetupStep Passo adicional de configuração a se executado no documento em complemento a factory
     *                       atual. Por ser null.
     * @return Nova fábrica com o passo novo. Retorna a mesma fábrica original se o extraSetupStep for null.
     */
    @Nonnull
    public SDocumentFactory extendAddingSetupStep(@Nullable IConsumer<SDocument> extraSetupStep) {
        return SDocumentFactoryExtended.extend(this, extraSetupStep);
    }
}
