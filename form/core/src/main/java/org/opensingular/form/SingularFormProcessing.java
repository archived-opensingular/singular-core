package org.opensingular.form;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.lambda.IConsumer;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     * @param
     *  i the instance from which all dependents types must be notified
     * @see <a href="https://www.pivotaltracker.com/story/show/131103577">[#131103577]</a>
     * @return
     *  List of dependants SInstances
     */
    protected static Set<SInstance> evaluateUpdateListeners(SInstance i) {
        return SInstances
                .streamDescendants(i.getRoot(), true)
                .filter(isDependantOf(i))
                .filter(SingularFormProcessing::isNotOrphan)
                .filter(dependant -> isNotInListOrIsBothInSameList(i, dependant))
                .map(dependant -> {
                    List<SInstance> instances = new ArrayList<>();
                    IConsumer<SInstance> updateListener = dependant.asAtr().getUpdateListener();
                    if (updateListener != null) {
                        updateListener.accept(dependant);
                    }
                    instances.add(dependant);
                    if (!dependant.equals(i)) {
                        instances.addAll(SingularFormProcessing.evaluateUpdateListeners(dependant));
                    }
                    return instances;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
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

        final Pattern indexFinder = Pattern.compile("(\\[\\d\\])");
        final Pattern bracketsFinder = Pattern.compile("[\\[\\]]");

        final Matcher matcher = indexFinder.matcher(path);
        final StringBuilder key = new StringBuilder();

        while (matcher.find()) {
            key.append(bracketsFinder.matcher(matcher.group()).replaceAll(StringUtils.EMPTY));
        }

        return key.toString();
    }

}
