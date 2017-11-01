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

package org.opensingular.form.wicket.helpers;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.internal.lib.wicket.test.AbstractAssertionsForWicket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Classe base de apoio a construção de assertivas para componentes Wicket.
 *
 * @author Daniel Bordin on 12/02/2017.
 */
public abstract class AssertionsWComponentBase<SELF extends AssertionsWComponentBase<SELF, T>, T extends Component>
        extends AbstractAssertionsForWicket<SELF, T, AssertionsWComponent, AssertionsWComponentList> {

    public AssertionsWComponentBase(T c) {
        super(c);
    }

    @Override
    @Nonnull
    protected AssertionsWComponent toAssertionsComponent(@Nullable Component component) {
        return new AssertionsWComponent(component);
    }

    @Override
    protected final AssertionsWComponentList toAssertionsList(@Nonnull List<Component> list) {
        return new AssertionsWComponentList(list);
    }

    /** Busca um sub componente do componente atual com o tipo informado e retonar o resultado. */
    @Nonnull
    public final AssertionsWComponent getSubComponentWithType(SType<?> type) {
        return findSubComponent(component -> ISInstanceAwareModel.optionalSInstance(component)
                .map(SInstance::getType)
                .map(type::equals).orElse(Boolean.FALSE));
    }

    @Nonnull
    public final <T extends SType<?>> AssertionsWComponent getSubComponentWithType(Class<? extends T> typeClass) {
        return findSubComponent(component -> ISInstanceAwareModel.optionalSInstance(component)
                .map(SInstance::getType)
                .map(SType::getName)
                .map(SFormUtil.getTypeName(typeClass)::equals)
                .orElse(Boolean.FALSE));
    }


    /** Busca um sub componente do componente atual com o nome do tipo informado e retonar o resultado. */
    @Nonnull
    public final AssertionsWComponent getSubComponentWithTypeNameSimple(String nameSimple) {
        return findSubComponent(component -> ISInstanceAwareModel.optionalSInstance(component)
                .map(SInstance::getType)
                .map(SType::getNameSimple)
                .map(nameSimple::equals).orElse(Boolean.FALSE));
    }

    /**
     * Busca um sub componente do componente atual que possua um model cujo o valor seja a {@link SInstance} informada.
     */
    public final AssertionsWComponent getSubComponentForSInstance(@Nonnull SInstance expectedInstance) {
        return findSubComponent(component -> ISInstanceAwareModel.optionalSInstance(component).orElse(null) ==
                expectedInstance);
    }

    /**
     * Busca um sub componente do componente atual que possua uma {@link SInstance} como model do componente.
     */
    public final AssertionsWComponent getSubComponentWithSInstance() {
        return findSubComponent(component ->  ISInstanceAwareModel.optionalSInstance(component).isPresent());
    }

    /**
     * Retornar uma lista com todos os sub componentes que possuem uma {@link SInstance} associada em seu model.
     * <p>A lista pode ser de tamanho zero.</p>
     */
    public final AssertionsWComponentList getSubComponentsWithSInstance() {
        return getSubComponents(component ->  ISInstanceAwareModel.optionalSInstance(component).isPresent());
    }

    /**
     * Verifica se o componente é um TextField e sendo retorna um assertiva específica para esse caso. Dispara exception
     * senão for um TextField Wicket.
     */
    @Nonnull
    public AssertionsWTextField asTextField() {
        return new AssertionsWTextField(getTarget(TextField.class));
    }

    /**
     * Verifies if the component is a Label and returns a assertive for Label components. Throws exception if it isn't a
     * Wicket Label.
     */
    @Nonnull
    public AssertionsWLabel asLabel() {
        return new AssertionsWLabel(getTarget(Label.class));
    }

    /**
     *
     * Verifica se o objeto atual possui um model que contem alguma SInstance, dispara expection caso não possua
     *
     * @return AssertionsSInstance para assertions de SInstance
     */
    @Nonnull
    public AssertionsSInstance assertSInstance() {
        isNotNull();
        return new AssertionsSInstance(ISInstanceAwareModel.optionalSInstance(getTarget())
                .orElseThrow(() -> new AssertionError(errorMsg("O Componente "+getTarget()
                        +" não possui model de SInstance "))));
    }

    @Override
    protected String debugAddDetailsToLine(Component component, String currentTextLine) {
        Optional<SInstance> instance = ISInstanceAwareModel.optionalSInstance(component);
        if (instance.isPresent()) {
            return currentTextLine + "      <" + instance.get().getPathFull() + ">";
        }
        return super.debugAddDetailsToLine(component, currentTextLine);
    }
}
