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

package org.opensingular.form.wicket.helpers;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.string.Strings;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.wicket.AjaxUpdateListenersFactory;
import org.opensingular.form.wicket.util.WicketFormUtils;
import org.opensingular.lib.commons.lambda.IBiConsumer;
import org.opensingular.lib.commons.lambda.IFunction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class STypeTester<T extends SType<?>> {

    private AjaxUpdateListenersFactory ajaxUpdateListenersFactory = new AjaxUpdateListenersFactory();
    private SingularWicketTester singularWicketTester;
    private Class<? extends T>   stypeClass;
    private Map<String, SingularFormTester> testers = new HashMap<>();

    public STypeTester(SingularWicketTester singularWicketTester, Class<? extends T> stypeClass) {
        this.singularWicketTester = singularWicketTester;
        this.stypeClass = stypeClass;
    }


    public <ST extends SType<?>> void submitValue(IFunction<T, ST> typeFinder, Object value) {
        SInstance subInstance          = resolveTypeFinderInstance(typeFinder);
        Component component            = getComponentForSType(subInstance.getType());
        String    wicketConvertedValue = getWicketConvertedValue(component, value);
        submitValue(subInstance, component, wicketConvertedValue);
    }

    private <ST extends SType<?>> void submitValue(SInstance subInstance, Component component, String wicketConvertedValue) {
        SingularFormTester singularFormTester = getCurrentFormTester(component);
        executeSingularEvents(singularFormTester::setValue, component, wicketConvertedValue);
    }


    @SuppressWarnings("unchecked")
    public <ST extends SType<?>> List<SInstance> getChoices(IFunction<T, ST> typeFinder) {
        SInstance          subInstance = resolveTypeFinderInstance(typeFinder);
        ST                 subtype     = (ST) subInstance.getType();
        Component          component   = getComponentForSType(subtype);
        SInstanceConverter converter   = subInstance.asAtrProvider().getConverter();
        return (List<SInstance>) ((AbstractChoice) component).getChoices().stream().map(serializable -> {
            SInstance choiceInstance = subtype.newInstance();
            converter.fillInstance(choiceInstance, (Serializable) serializable);
            return choiceInstance;
        }).collect(Collectors.toList());
    }

    public <ST extends SType<?>> AssertionsSInstance getAssertionsSInstance(IFunction<T, ST> typeFinder) {
        return new AssertionsWComponent(getComponentForSType(resolveTypeFinderInstance(typeFinder).getType())).assertSInstance();
    }

    public <ST extends SType<?>> void submitSelection(IFunction<T, ST> typeFinder, BiPredicate<SInstance, Integer> selection) {
        List<SInstance> choices = getChoices(typeFinder);
        submitSelection(typeFinder,
                choices
                        .stream()
                        .filter(c -> selection.test(c, choices.indexOf(c)))
                        .map(choices::indexOf)
                        .findFirst()
                        .orElse(null));
    }

    public <ST extends SType<?>> void submitSelection(IFunction<T, ST> typeFinder, int choiceIndex) {
        SInstance          subInstance        = resolveTypeFinderInstance(typeFinder);
        ST                 subtype            = (ST) subInstance.getType();
        Component          component          = getComponentForSType(subtype);
        SingularFormTester singularFormTester = getCurrentFormTester(component);
        executeSingularEvents(singularFormTester::select, component, choiceIndex);
    }

    private <T> void executeSingularEvents(IBiConsumer<String, T> valueSetter, Component component, T value) {
        Set<String>    eventSet     = new HashSet<>();
        List<Behavior> behaviorList = new ArrayList<>();
        component.getBehaviors().stream().filter(ajaxUpdateListenersFactory::isSingularBehavior).forEach(b -> {
            if (b instanceof AjaxEventBehavior) {
                eventSet.addAll(collectEvents((AjaxEventBehavior) b));
            } else {
                behaviorList.add(b);
            }
        });
        executarBehavior(valueSetter, component, value, behaviorList);
        executarEventos(valueSetter, component, value, eventSet);
    }

    private <T> void executarEventos(IBiConsumer<String, T> valueSetter, Component component, T value, Set<String> eventSet) {
        for (String e : eventSet) {
            valueSetter.accept(component.getId(), value);
            singularWicketTester.executeAjaxEvent(component, e);
        }
    }

    private <T> void executarBehavior(IBiConsumer<String, T> valueSetter, Component component, T value, List<Behavior> behaviorList) {
        for (Behavior b : behaviorList) {
            valueSetter.accept(component.getId(), value);
            singularWicketTester.executeBehavior((AbstractAjaxBehavior) b);
        }
    }

    private List<String> collectEvents(AjaxEventBehavior behavior) {
        List<String> events             = new ArrayList<>();
        String       behaviorEvent      = behavior.getEvent();
        String[]     behaviorEventNames = Strings.split(behaviorEvent, ' ');
        for (String behaviorEventName : behaviorEventNames) {
            if (behaviorEventName.startsWith("on")) {
                behaviorEventName = behaviorEventName.substring(2);
            }
            events.add(behaviorEventName);
        }

        return events;
    }

    @SuppressWarnings("unchecked")
    private <ST extends SType<?>, V extends Object> String getWicketConvertedValue(Component value, V o) {
        if (o == null) {
            return null;
        }
        return value.getConverter((Class<V>) o.getClass()).convertToString(o, Locale.getDefault());
    }

    private SingularFormTester getCurrentFormTester(Component c) {
        String relativePath = findNearestEnclosingForm(c).getPageRelativePath();
        if (!testers.containsKey(relativePath)) {
            testers.put(relativePath, singularWicketTester.newSingularFormTester(relativePath, false));
        }
        return testers.get(relativePath);
    }

    private Component getComponentForSType(SType<?> sType) {
        return this.singularWicketTester.getAssertionsPage().getSubCompomentWithId(sType.getNameSimple()).getTarget();
    }

    private <ST extends SType<?>> SInstance resolveTypeFinderInstance(IFunction<T, ST> typeFinder) {
        SInstance instance = getRootSInstanceForGivenSTypeClass();
        ST        subtype  = typeFinder.apply((T) instance.getType());
        return instance.findNearest((SType<SInstance>) subtype).orElse(null);
    }

    private SInstance getRootSInstanceForGivenSTypeClass() {
        SInstance instance = singularWicketTester.getAssertionsInstance().getTarget();
        if (instance.getType().getName().equals(SFormUtil.getTypeName(this.stypeClass))) {
            return instance;
        } else {
            throw new SingularFormException(
                    String.format(
                            "A classe do tipo informado (%s) não corresponde ao tipo encontrado: %s ",
                            SFormUtil.getTypeName(stypeClass),
                            instance.getType().getName()));
        }
    }

    private Form<?> findNearestEnclosingForm(Component c) {
        return (Form<?>) WicketFormUtils
                .streamAscendants(c)
                .filter(a -> a instanceof Form)
                .findFirst()
                .orElseThrow(() -> new SingularFormException(
                                "Não foi possível encontrar um Form (wicket) ao redor do campo informado!"
                        )
                );
    }
}
