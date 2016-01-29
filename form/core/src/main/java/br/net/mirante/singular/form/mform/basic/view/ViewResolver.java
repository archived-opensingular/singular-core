package br.net.mirante.singular.form.mform.basic.view;

import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;

import br.net.mirante.singular.form.mform.*;

/**
 * <p>
 * Implementa o algorítmo de resolução da view para uma instância de acordo com
 * o tipo de dado, sua estrutura e conteúdo, bem como, armazena o métadado das
 * regras necessário para a decisão de qual view é adequada.
 * </p>
 * <p>
 * Essa classe é basicamente de uso interno. Permitie decidir por formas de
 * organizar a interface ({@link MView}) independente de qual tecnologia
 * (framework) web está sendo utilizada. Ou seja, decide pela organização da
 * tela e cada ao gerador da interface específica implementar os diversos
 * padrões de componentes e disposição ({@link MView}).
 * </p>
 *
 * @author Daniel C. Bordin
 * @see MView
 */
public class ViewResolver {

    private int nextId = 1;

    private final HashMap<Class<? extends SType>, TreeSet<ViewRuleRef>> rules = new HashMap<>();

    public ViewResolver() {
        // Registro das regas de escolha de view.
        addRule(STypeLista.class, MPanelListaView.class);
        addRule(STypeLista.class, new ViewRuleTypeListOfTypeSimpleSelectionOf());
        addRule(STypeSimple.class, new ViewRuleTypeSimpleSelectionOf());
        addRule(STypeComposite.class, new ViewRuleTypeSimpleSelectionOf());
    }

    /**
     * Definie a view default para um dado componente. Essa regra tem menor
     * prioridade em relação com lógica explicitas.
     */
    public void addRule(Class<? extends SType> type, Class<? extends MView> view) {
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
    public static MView resolve(SInstance instance) {
        return instance.getDicionario().getViewResolver().resolveInternal(instance);
    }

    private MView resolveInternal(SInstance instance) {
        MView view = instance.getMTipo().getView();
        if (view != null) {
            return view;
        }
        Class<?> classType = instance.getMTipo().getClass();
        int priority = -1;
        while (classType != SType.class) {
            TreeSet<ViewRuleRef> list = rules.get(classType);
            if (list != null) {
                for (ViewRuleRef rule : list) {
                    if (view == null || rule.getPriority() > priority) {
                        MView novo = rule.apply(instance);
                        if (novo != null) {
                            view = novo;
                            priority = rule.getPriority();
                        }
                    }
                }
            }
            classType = classType.getSuperclass();
        }
        return view != null ? view : MView.DEFAULT;
    }

    private static class ViewRuleSimple extends ViewRule {

        private final Class<? extends MView> view;

        ViewRuleSimple(Class<? extends MView> view) {
            this.view = view;
        }

        @Override
        public MView apply(SInstance instance) {
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
        public MView apply(SInstance instance) {
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
