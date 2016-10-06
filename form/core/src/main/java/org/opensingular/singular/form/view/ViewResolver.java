/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.view;

import br.net.mirante.singular.form.*;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.STypeSimple;

import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;

/**
 * <p>
 * Implementa o algorítmo de resolução da view para uma instância de acordo com
 * o tipo de dado, sua estrutura e conteúdo, bem como, armazena o métadado das
 * regras necessário para a decisão de qual view é adequada.
 * </p>
 * <p>
 * Essa classe é basicamente de uso interno. Permitie decidir por formas de
 * organizar a interface ({@link SView}) independente de qual tecnologia
 * (framework) web está sendo utilizada. Ou seja, decide pela organização da
 * tela e cada ao gerador da interface específica implementar os diversos
 * padrões de componentes e disposição ({@link SView}).
 * </p>
 *
 * @author Daniel C. Bordin
 * @see SView
 */
public class ViewResolver {

    private int nextId = 1;

    private final HashMap<Class<? extends SType>, TreeSet<ViewRuleRef>> rules = new HashMap<>();

    public ViewResolver() {
        // Registro das regas de escolha de view.
        addRule(STypeList.class, SViewListByForm.class);
        addRule(STypeList.class, new ViewRuleTypeListOfTypeSimpleSelectionOf());
        addRule(STypeSimple.class, new ViewRuleTypeSimpleSelectionOf());
        addRule(STypeComposite.class, new ViewRuleTypeSimpleSelectionOf());
        addRule(STypeList.class, new ViewRuleTypeListOfAttachment());
    }

    /**
     * Definie a view default para um dado componente. Essa regra tem menor
     * prioridade em relação com lógica explicitas.
     */
    public void addRule(Class<? extends SType> type, Class<? extends SView> view) {
        addRule(type, 100, new ViewRuleSimple(view));
    }

    /**
     * Adiciona uma regra de mapeamento de view com lógia explicita.
     * Provavelmente é um algorítmo que ira inspecionar a estrutura do tipo ou
     * da instância antes de decidir a melhor view. Essa regra tem maior
     * prioriade em relação as regras de componentes default.
     */
    public void addRule(Class<? extends SType> type, ViewRule viewRule) {
        addRule(type, 1000, viewRule);
    }

    private void addRule(Class<? extends SType> type, int priority, ViewRule viewRule) {
        ViewRuleRef rule = new ViewRuleRef(nextId++, priority, Objects.requireNonNull(viewRule));

        TreeSet<ViewRuleRef> list = rules.get(Objects.requireNonNull(type));
        if (list == null) {
            list = new TreeSet<>();
            rules.put(type, list);
        }
        list.add(rule);
    }

    /**
     * Calcula a view mais adequeada para instancia informada. Retorna
     * MView.DEFAULT, se não houver nenhum direcionamento específico e nesse
     * caso então cabe a cada gerador decidir como criar o componente na tela.
     */
    public static SView resolve(SInstance instance) {
        return instance.getDictionary().getViewResolver().resolveInternal(instance);
    }

    private SView resolveInternal(SInstance instance) {
        SType type = instance.getType();
        SView view = null;
        while (type != null) {
            view = type.getView();
            if (view != null) { return view;    }
            type = type.getSuperType();
        }
        Class<?> classType = instance.getType().getClass();
        int priority = -1;
        while (classType != SType.class) {
            TreeSet<ViewRuleRef> list = rules.get(classType);
            if (list != null) {
                for (ViewRuleRef rule : list) {
                    if (view == null || rule.getPriority() > priority) {
                        SView novo = rule.apply(instance);
                        if (novo != null) {
                            view = novo;
                            priority = rule.getPriority();
                        }
                    }
                }
            }
            classType = classType.getSuperclass();
        }
        return view != null ? view : SView.DEFAULT;
    }

    private static class ViewRuleSimple extends ViewRule {

        private final Class<? extends SView> view;

        ViewRuleSimple(Class<? extends SView> view) {
            this.view = view;
        }

        @Override
        public SView apply(SInstance instance) {
            return newInstance(view);
        }
    }

    /** Representa uma regra de mapeamento de view. */
    private static class ViewRuleRef extends ViewRule implements Comparable<ViewRuleRef> {

        private final int priority;

        private final int order;

        private final ViewRule viewRule;

        ViewRuleRef(int order, int priority, ViewRule viewRule) {
            this.order = order;
            this.priority = priority;
            this.viewRule = viewRule;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public SView apply(SInstance instance) {
            return viewRule.apply(instance);
        }

        @Override
        public int compareTo(ViewRuleRef vr) {
            if (priority != vr.priority) {
                return priority - vr.priority;
            }
            return order - vr.order;
        }

        @Override
        public int hashCode() {
            return 31 + order;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ViewRuleRef other = (ViewRuleRef) obj;
            if (order != other.order)
                return false;
            return true;
        }

    }

}
