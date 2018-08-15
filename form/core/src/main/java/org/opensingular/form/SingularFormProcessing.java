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

package org.opensingular.form;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.event.ISInstanceListener;
import org.opensingular.form.validation.InstanceValidationContext;
import org.opensingular.lib.commons.lambda.IConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.event.ISInstanceListener;
import org.opensingular.form.validation.InstanceValidationContext;
import org.opensingular.lib.commons.lambda.IConsumer;

import com.google.common.collect.ImmutableList;

/**
 * Singular Core processing lifecycle
 */
public class SingularFormProcessing {

    /**
     * Executa o update listener dos tipos depentens da instancia informada, sendo chamada recursivamente para os tipos
     * que foram atualizados.
     * <p>
     * Motivação: Tendo um tipo composto com tres tipos filhos (a,b e c),
     * onde "b" é dependente de "a" e "c" é dependente de "b", "b" possui update listener que modifica o seu valor,
     * e "c" será visivel se o valor de "b" não for nulo.  Ao atualizar "a" é necessario executar o listener dos seus
     * tipos dependentes("b") e também dos tipos dependentes do seu dependente("c") para que a avaliação de visibilidade
     * seja avaliada corretamente.
     *
     * @param i the instance from which all dependents types must be notified
     * @return List of dependants SInstances visited regardless whether it contains or not an update listener
     * @see <a href="https://www.pivotaltracker.com/story/show/131103577">[#131103577]</a>
     */
    public static ImmutableList<SInstance> evaluateUpdateListeners(SInstance i) {
        LinkedHashMap<String, SInstance> collected = new LinkedHashMap<>();
        circularEvaluateUpdateListeners(i, collected);
        Collection<SInstance> dependants = collected.values();
        for (SInstance dependant : dependants) {
            notifyDependentTypesBeforeRunUpdateListeners(dependant);
        }
        for (SInstance dependant : dependants) {
            IConsumer<SInstance> updateListener = dependant.asAtr().getUpdateListener();
            if (updateListener != null) {
                updateListener.accept(dependant);
            }
        }
        return ImmutableList.copyOf(dependants);
    }


    /**
     * Process execution of SType fields running validations and update listeners.
     * It visits all dependent types recursively and collect every visited type.
     * Every update listener of every collected type is executed.
     * Validation is executed for every collected type that currently contains validations errors or the types
     * that have values (regardless it contains errors or not)
     *
     * @param instance       field instance to be processed
     * @param skipValidation if true, validations are no executed
     * @return a set of processed instances
     */
    public static List<SInstance> executeFieldProcessLifecycle(SInstance instance, boolean skipValidation) {
        LinkedHashMap<String, SInstance> instancesToUpdateComponents = new LinkedHashMap<>();

        if (!skipValidation) {
            validate(instance);
        }

        ImmutableList<SInstance> updatedInstances = evaluateUpdateListeners(instance);

        ISInstanceListener.EventCollector eventCollector = new ISInstanceListener.EventCollector();
        updateAttributes(instance, eventCollector);

        if (!skipValidation) {
            revalidateInvalidOrNonEmptyInstances(updatedInstances);
        }

        for (SInstance sInstance : eventCollector.getEventSourceInstances()) {
            instancesToUpdateComponents.put(sInstance.getPathFull(), sInstance);
        }
        for (SInstance updatedInstance : updatedInstances) {
            instancesToUpdateComponents.put(updatedInstance.getPathFull(), updatedInstance);
        }

        return new ArrayList<>(instancesToUpdateComponents.values());
    }

    private static void notifyDependentTypesBeforeRunUpdateListeners(SInstance instance) {
        instance.getDocument().getInstanceListeners().fireBeforeRunUpdateListener(instance);
    }


    private static void updateAttributes(final SInstance fieldInstance, ISInstanceListener.EventCollector eventCollector) {
        final SDocument document = fieldInstance.getDocument();
        document.updateAttributes(eventCollector);
    }


    /**
     * Validates the instance {@param fieldInstance}
     * it does not validates children components or dependents ones
     *
     * @param fieldInstance
     */
    public static void validate(SInstance fieldInstance) {
        // Validação do valor do componente
        final InstanceValidationContext validationContext = new InstanceValidationContext();
        validationContext.validateSingle(fieldInstance);

    }

    /**
     * rerun validation on types that are filled with data and currently valid and the invalid ones (filled or not)
     *
     * @param updatedInstances a list of instances to rerun the validation
     * @return
     */
    private static void revalidateInvalidOrNonEmptyInstances(Iterable<SInstance> updatedInstances) {
        final InstanceValidationContext validationContext = new InstanceValidationContext();
        // limpa erros de instancias dependentes, e limpa o valor caso de este não seja válido para o provider
        for (SInstance it : updatedInstances) {
            //Executa validações que dependem do valor preenchido que não estão com valor vazio ou
            // que já haviam sido validadas anteriormente e possuem mensagens
            if (!it.isEmptyOfData() || it.hasValidationErrors()) {
                it.getDocument().clearValidationErrors(it.getId());
                validationContext.validateSingle(it);
            }
        }
    }


    private static void circularEvaluateUpdateListeners(SInstance i, final LinkedHashMap<String, SInstance> evaluated) {
        SInstances
                .streamDescendants(i.getRoot(), true)
                .filter(isDependantOf(i))
                .filter(SingularFormProcessing::isNotOrphan)
                .filter(dependant -> isNotInListOrIsBothInSameList(i, dependant))
                .filter(dependant -> !evaluated.keySet().contains(dependant.getPathFull()))
                .forEach(collectUpdateListenerCascadingCollecting(i, evaluated));
    }

    private static Consumer<SInstance> collectUpdateListenerCascadingCollecting(SInstance i, LinkedHashMap<String, SInstance> evaluated) {
        return dependant -> {
            evaluated.put(dependant.getPathFull(), dependant);
            if (dependant != i) {
                SingularFormProcessing.circularEvaluateUpdateListeners(dependant, evaluated);
            }
        };
    }

    private static Predicate<SInstance> isDependantOf(SInstance i) {
        return (x) -> i.getType().getDependentTypes().contains(x.getType());
    }

    private static boolean isOrphan(SInstance i) {
        return !(i instanceof SIComposite) && i.getParent() == null;
    }

    private static boolean isNotOrphan(SInstance i) {
        return !isOrphan(i);
    }


    private static boolean isNotInListOrIsBothInSameList(SInstance a, SInstance b) {
        final String pathA = pathFromList(a);
        final String pathB = pathFromList(b);
        return !(pathA != null && pathB != null && Objects.equals(pathA, pathB)) || Objects.equals(getIndexesKey(b.getPathFull()), getIndexesKey(a.getPathFull()));
    }

    private static String pathFromList(SInstance i) {
        return SInstances
                .findAncestor(i, STypeList.class)
                .map(SInstance::getPathFull)
                .orElse(null);
    }


    /**
     * Forma uma chava apartir dos indexes de lista
     *
     * @param path da instancia
     * @return chaves concatenadas
     */
    private static String getIndexesKey(String path) {

        final Pattern indexFinder    = Pattern.compile("(\\[\\d\\])");
        final Pattern bracketsFinder = Pattern.compile("[\\[\\]]");

        final Matcher       matcher = indexFinder.matcher(path);
        final StringBuilder key     = new StringBuilder();

        while (matcher.find()) {
            key.append(bracketsFinder.matcher(matcher.group()).replaceAll(StringUtils.EMPTY));
        }

        return key.toString();
    }

}
