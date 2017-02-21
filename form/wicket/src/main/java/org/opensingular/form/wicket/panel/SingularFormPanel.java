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

package org.opensingular.form.wicket.panel;

import org.opensingular.form.SInstance;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefSDocumentFactory;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.document.TypeLoader;
import org.opensingular.form.wicket.SingularFormContextWicket;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Painel que encapusla a lógica de criação de forms dinâmicos
 */
public abstract class SingularFormPanel<FORM_KEY extends Serializable> extends SingularFormPanelBasic {

    private final RefSDocumentFactory documentFactoryRef;

    private transient SFormConfig<FORM_KEY> singularFormConfig;

    /**
     * Construtor do painel
     *
     * @param id                 o markup id wicket
     * @param singularFormConfig configuração para manipulação do documento a ser criado ou
     *                           recuperado.
     */
    public SingularFormPanel(String id, SFormConfig<FORM_KEY> singularFormConfig) {
        this(id, singularFormConfig, false);
    }

    /**
     * Construtor do painel
     *
     * @param id                 o markup id wicket
     * @param singularFormConfig configuração para manipulação do documento a ser criado ou
     *                           recuperado.
     */
    public SingularFormPanel(String id, SFormConfig<FORM_KEY> singularFormConfig, boolean nested) {
        super(id, nested);
        this.singularFormConfig = Objects.requireNonNull(singularFormConfig);
        this.documentFactoryRef = singularFormConfig.getDocumentFactory().getDocumentFactoryRef();
        setFormContextWicketSupplier(
                () -> documentFactoryRef.get().getServiceRegistry().lookupService(SingularFormContextWicket.class));
    }

    /**
     * <p>
     * Cria ou recupera a instancia a ser trabalhada no painel.
     * </p>
     * <p>
     * A instância deve ser criada utilizando {@link TypeLoader} e
     * {@link SDocumentFactory} de modo a viabilizar recuperar a instância
     * corretamente no caso de deserialização. Para tando, deve ser utilizada as
     * objetos passados no parâmetro singularFormConfig.
     * </p>
     *
     * @param singularFormConfig Configuração do formulário em termos de recuperação de
     *                           referências e configurador inicial da instancia e SDocument
     * @return Não pode ser Null
     */
    @Nonnull
    protected abstract SInstance createInstance(SFormConfig<FORM_KEY> singularFormConfig);

    /**
     * Método wicket, local onde os componentes são adicionados
     */
    @Override
    protected void onInitialize() {
        SInstance instance = createInstance(singularFormConfig);
        setInstance(instance);
        super.onInitialize();
    }

    public final SFormConfig<FORM_KEY> getSingularFormConfig() {
        return singularFormConfig;
    }

}
