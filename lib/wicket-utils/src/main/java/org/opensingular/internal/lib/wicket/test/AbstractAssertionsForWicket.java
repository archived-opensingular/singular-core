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

package org.opensingular.internal.lib.wicket.test;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.opensingular.lib.commons.test.AssertionsBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represent a basic implementation of assertions for Wicket's components.
 *
 * @author Daniel C. Bordin
 * @since 2017-10-30
 */
public abstract class AbstractAssertionsForWicket<SELF extends AbstractAssertionsForWicket<SELF, T,
        ASSERTIONS_COMPONENT, ASSERTIONS_LIST>, T extends Component, ASSERTIONS_COMPONENT extends AbstractAssertionsForWicket<ASSERTIONS_COMPONENT,
        Component,?,?>, ASSERTIONS_LIST extends AbstractAssertionsForWicketList<ASSERTIONS_LIST, ASSERTIONS_COMPONENT>>
        extends AssertionsBase<SELF, T> {

    public AbstractAssertionsForWicket(T c) {
        super(c);
    }

    @Override
    @Nonnull
    protected final Optional<String> generateDescriptionForCurrentTarget(@Nonnull Optional<T> current) {
        return current.map(component -> component.getPageRelativePath());
    }

    @Nonnull
    protected abstract ASSERTIONS_COMPONENT toAssertionsComponent(@Nullable Component component);

    @Nonnull
    protected abstract ASSERTIONS_LIST toAssertionsList(@Nonnull List<Component> list);

    /** Busca um sub componente do componente atual com o ID informado e retonar o resultado. */
    @Nonnull
    public final ASSERTIONS_COMPONENT getSubComponentWithId(String componentId) {
        return findSubComponent(component -> componentId.equals(component.getId()));
    }

    /**
     * Busca um sub componente do componente atual que atenda ao critério informado. Para no primeiro que atender.
     * Mesmo senão encontrar, retorna uma assertiva com conteúdo null.
     */
    @Nonnull
    public final ASSERTIONS_COMPONENT findSubComponent(Predicate<Component> predicate) {
        return createAssertionForSubComponent(getTarget(), predicate);
    }

    /**
     * Cria um objeto de assertivas para o sub componente na hierarquia que for encontrado com o id informado ou dispara
     * exception senão encontrar o componente.
     */
    private final ASSERTIONS_COMPONENT createAssertionForSubComponent(Component parent,
            Predicate<Component> predicate) {
        Objects.requireNonNull(parent);
        return toAssertionsComponent(findSubComponentImpl(parent, predicate));
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
    public final ASSERTIONS_LIST getSubComponents( @Nonnull Class<?> targetClass) {
        return getSubComponents(component -> targetClass.isInstance(component));
    }

    /**
     * Retornar uma lista com todos os sub componentes com o ID informado.
     * <p>A lista pode ser de tamanho zero.</p>
     */
    @Nonnull
    public final ASSERTIONS_LIST getSubComponentsWithId( @Nonnull String componentId) {
        return getSubComponents(component -> componentId.equals(component.getId()));
    }

    /**
     * Retornar uma lista dos sub componentes que atendem a critério informado. Ao encontrar um componente que atende
     * ao critério, não continua procurando nos subComponentes desse.
     * <p>A lista pode ser de tamanho zero.</p>
     */
    @Nonnull
    public final ASSERTIONS_LIST getSubComponents(@Nonnull Predicate<Component> predicate) {
        List<Component> result = new ArrayList<>();
        findSubComponentsImpl(getTarget(), predicate, result);
        return toAssertionsList(result);
    }

    private static void findSubComponentsImpl(Component parent, Predicate<Component> predicate,
            List<Component> components) {
        if (parent instanceof MarkupContainer) {
            for (Component component : (MarkupContainer) parent) {
                if (predicate.test(component)) {
                    components.add(component);
                } else {
                    findSubComponentsImpl(component, predicate, components);
                }
            }
        }
    }

    public final AbstractObjectAssert<?, Serializable> assertDefaultModelObject() {
        return Assertions.assertThat((Serializable) getTarget().getDefaultModelObject());
    }


    /** Gera para o console a árvore de componentes a partir do elemento atual. */
    public final SELF debugComponentTree() {
        debugComponentTree(0, getTarget());
        return (SELF) this;
    }

    protected final void debugComponentTree(int level, Component component) {
        if (component == null) {
            return;
        }
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }

        String descr = component.getId() + ":" + resolveClassName(component.getClass());
        descr = debugAddDetailsToLine(component, descr);

        System.out.println(descr);
        if (component instanceof MarkupContainer) {
            for (Component sub : (MarkupContainer) component) {
                debugComponentTree(level + 1, sub);
            }
        }
    }

    @Nonnull
    protected String debugAddDetailsToLine(@Nonnull Component component, @Nonnull String currentTextLine) {
        String result = currentTextLine;
        if (component instanceof Label) {
            result += ": \"" + component.getDefaultModelObjectAsString() + '"';
        }
        return result;
    }

    private String resolveClassName(Class<?> aClass) {
        String name = aClass.getSimpleName();
        if (name.length() == 0) {
            return resolveClassName(aClass.getSuperclass());
        }
        return name;
    }
}
