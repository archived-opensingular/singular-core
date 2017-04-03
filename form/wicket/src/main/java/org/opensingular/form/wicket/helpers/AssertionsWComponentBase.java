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
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.fest.assertions.api.Assertions;
import org.fest.assertions.api.ObjectAssert;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.lib.commons.test.AssertionsBase;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Classe base de apoio a construção de assertivas para componentes Wicket.
 *
 * @author Daniel Bordin on 12/02/2017.
 */
public abstract class AssertionsWComponentBase<T extends Component, SELF extends AssertionsWComponentBase<T, SELF>>
        extends AssertionsBase<T, SELF> {

    public AssertionsWComponentBase(T c) {
        super(c);
    }

    @Override
    protected String errorMsg(String msg) {
        if (! getTargetOpt().isPresent()) {
            return "null : " + msg;
        }
        return getTarget().getPageRelativePath() + " : " + msg;
    }


    /** Busca um sub componente do componente atual com o tipo informado e retonar o resultado. */
    @Nonnull
    public final AssertionsWComponent getSubCompomentWithType(SType<?> type) {
        return findSubComponent(component -> ISInstanceAwareModel.optionalSInstance(component)
                .map(SInstance::getType)
                .map(type::equals).orElse(Boolean.FALSE));
    }


    /** Busca um sub componente do componente atual com o nome do tipo informado e retonar o resultado. */
    @Nonnull
    public final AssertionsWComponent getSubCompomentWithTypeNameSimple(String nameSimple) {
        return findSubComponent(component -> ISInstanceAwareModel.optionalSInstance(component)
                .map(SInstance::getType)
                .map(SType::getNameSimple)
                .map(nameSimple::equals).orElse(Boolean.FALSE));
    }

    /** Busca um sub componente do componente atual com o ID informado e retonar o resultado. */
    @Nonnull
    public final AssertionsWComponent getSubCompomentWithId(String componentId) {
        return findSubComponent(component -> componentId.equals(component.getId()));
    }

    /**
     * Busca um sub componente do componente atual que possua um model cujo o valor seja a {@link SInstance} informada.
     */
    public final AssertionsWComponent getSubCompomentForSInstance(@Nonnull SInstance expectedInstance) {
        return findSubComponent(component -> ISInstanceAwareModel.optionalSInstance(component).orElse(null) ==
                expectedInstance);
    }

    /**
     * Busca um sub componente do componente atual que possua uma {@link SInstance} como model do componente.
     */
    public final AssertionsWComponent getSubCompomentWithSInstance() {
        return findSubComponent(component ->  ISInstanceAwareModel.optionalSInstance(component).isPresent());
    }

    /**
     * Busca um sub componente do componente atual que atenda ao critério informado. Para no primeiro que atender.
     * Mesmo senão encontrar, retorna uma assertiva com conteúdo null.
     */
    @Nonnull
    public final AssertionsWComponent findSubComponent(Predicate<Component> predicate) {
        return createAssertionForSubComponent(getTarget(), predicate);
    }

    /**
     * Cria um objeto de assertivas para o sub componente na hierarquia que for encontrado com o id informado ou dispara
     * exception senão encontrar o componente.
     */
    final static AssertionsWComponent createAssertionForSubComponent(Component parent, Predicate<Component> predicate) {
        Objects.requireNonNull(parent);
        return new AssertionsWComponent(findSubComponentImpl(parent, predicate));
    }

    private static Component findSubComponentImpl(Component parent, Predicate<Component> predicate) {
        if (parent instanceof MarkupContainer) {
            for (Component component : (MarkupContainer) parent) {
                if (predicate.test(component)) {
                    return component;
                }
                Component result = findSubComponentImpl(component, predicate);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Retornar uma lista com todos os sub componentes do atual que forem da classe informada. A lista pode ser de
     * tamanho zero.
     */
    @Nonnull
    public final <TT extends Component> AssertionsWComponentList getSubComponents(@Nonnull Class<TT> targetClass) {
        List<TT> result = Collections.emptyList();
        if (getTarget() instanceof MarkupContainer) {
            result = TestFinders.findTag((MarkupContainer) getTarget(), targetClass);
        }
        return new AssertionsWComponentList<>(result);
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

    public ObjectAssert<Serializable> assertDefaultModelObject(){
        return Assertions.assertThat((Serializable) getTarget().getDefaultModelObject());
    }


    /** Gera para o console a árvore de componentes a partir do elemento atual. */
    public final SELF debugComponentTree() {
        debugComponentTree(0, getTarget());
        return (SELF) this;
    }

    private void debugComponentTree(int level, Component component) {
        if (component == null) {
            return;
        }
        for(int i = 0; i < level; i++) {
            System.out.print("  ");
        }

        String descr = component.getId() + ":" + resolveClassName(component.getClass());
        Optional<SInstance> instance = ISInstanceAwareModel.optionalSInstance(component);
        if (instance.isPresent()) {
            descr += "      <" + instance.get().getPathFull() + ">";
        } else if (component instanceof Label) {
            descr += ": \"" + ((Label) component).getDefaultModelObjectAsString() + '"';
        }

        System.out.println(descr);
        if (component instanceof MarkupContainer) {
            for(Component sub : (MarkupContainer) component) {
                debugComponentTree(level+1, sub);
            }
        }
    }

    private String resolveClassName(Class<?> aClass) {
        String name = aClass.getSimpleName();
        while (name.length() == 0) {
            aClass = aClass.getSuperclass();
            name = aClass.getSimpleName();
        }
        return name;
    }
}
