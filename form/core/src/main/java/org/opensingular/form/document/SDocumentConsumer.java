/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

import com.google.common.collect.ImmutableList;
import org.opensingular.lib.commons.lambda.IConsumer;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Representa uma lista imutável de passos de configuração para um {@link SDocument}.
 *
 * @author Daniel C. Bordin on 26/02/2017.
 */
public final class SDocumentConsumer implements IConsumer<SDocument> {

    private final ImmutableList<IConsumer<SDocument>> setupSteps;

    private SDocumentConsumer(ImmutableList<IConsumer<SDocument>> setupSteps) {
        this.setupSteps = setupSteps;
    }

    /** Criar um novo setuper contendo o passo informado. */
    @Nonnull
    public static SDocumentConsumer of(@Nonnull IConsumer<SDocument> setupStep) {
        Objects.requireNonNull(setupStep);
        return new SDocumentConsumer(ImmutableList.of(setupStep));
    }

    /**
     * Criar um novo {@link SDocumentConsumer} mais incuindo o novo passo adicional que será executado depois dos já
     * existentes.
     */
    @Nonnull
    public SDocumentConsumer extendWith(IConsumer<SDocument> setupStep) {
        if (setupStep == null) {
            return this;
        }
        ImmutableList.Builder<IConsumer<SDocument>> builder = ImmutableList.builder();
        builder.addAll(setupSteps);
        if (setupStep instanceof SDocumentConsumer) {
            builder.addAll(((SDocumentConsumer) setupStep).setupSteps);
        } else {
            builder.add(setupStep);
        }
        return new SDocumentConsumer(builder.build());
    }

    /** Executa todos os passos de setup no documento em questao. */
    @Override
    public void accept(@Nonnull SDocument document) {
        setupSteps.forEach(step -> step.accept(document));
    }
}
