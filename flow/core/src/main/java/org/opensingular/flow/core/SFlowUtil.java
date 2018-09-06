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

package org.opensingular.flow.core;

import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.variable.VarService;
import org.opensingular.flow.core.view.IViewLocator;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.lib.commons.context.RefService;
import org.opensingular.lib.commons.context.ServiceRegistry;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;
import org.opensingular.lib.commons.net.Lnk;
import org.opensingular.lib.commons.net.WebRef;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;



public class SFlowUtil {

    private static final int WEIGHT_PESO_TASK_JAVA = 100;
    private static final int WEIGHT_TASK_WAIT = 300;
    private static final int WEIGHT_TASK_HUMAN = 1000;
    private static final int WEIGHT_TASK_FIM = 100000;

    private SFlowUtil() {}

    public static void sortInstancesByDistanceFromBeginning(List<? extends FlowInstance> instances,
            FlowDefinition<?> definition) {
        instances.sort((s1, s2) -> compareByDistanceFromBeginning(
                s1.getLastTaskOrException().getEntityTaskInstance().getTaskVersion(),
                s2.getLastTaskOrException().getEntityTaskInstance().getTaskVersion(), definition));
    }

    private static int compareByDistanceFromBeginning(IEntityTaskVersion s1, IEntityTaskVersion s2,
            FlowDefinition<?> definition) {
        int ordem1 = calculateTaskOrder(s1, definition);
        int ordem2 = calculateTaskOrder(s2, definition);
        if (ordem1 != ordem2) {
            return ordem1 - ordem2;
        }
        return s1.getName().compareTo(s2.getName());
    }

    public static <T> void sortByDistanceFromBeginning(List<? extends T> list,
            Function<T, IEntityTaskVersion> converter, FlowDefinition<?> definition) {
        list.sort(getDistanceFromBeginningComparator(converter, definition));
    }

    private static <T> Comparator<T> getDistanceFromBeginningComparator(Function<T, IEntityTaskVersion> converter,
            FlowDefinition<?> definition) {
        return (o1, o2) -> compareByDistanceFromBeginning(converter.apply(o1), converter.apply(o2), definition);
    }

    public static <X extends IEntityTaskVersion> List<X> getSortedByDistanceFromBeginning(List<X> situations,
            FlowDefinition<?> definition) {
        List<X> list = new ArrayList<>(situations);
        list.sort((s1, s2) -> compareByDistanceFromBeginning(s1, s2, definition));
        return list;
    }

    @Nonnull
    public static List<STask<?>> getSortedTasksByDistanceFromBeginning(@Nonnull FlowDefinition<?> definition) {
        FlowMap flowMap = definition.getFlowMap();
        calculateTaskOrder(flowMap);
        List<STask<?>> list = new ArrayList<>(flowMap.getTasks());
        list.sort((t1, t2) -> {
            int order1 = t1.getOrder();
            int order2 = t2.getOrder();
            if (order1 != order2) {
                return order1 - order2;
            }
            return t1.getName().compareTo(t2.getName());
        });
        return list;
    }

    static void calculateTaskOrder(@Nonnull FlowMap flowMap) {
        for (STask<?> task : flowMap.getTasks()) {
            task.setOrder(0);
        }
        Deque<STask<?>> deque = new ArrayDeque<>();
        int initialValue = 0;
        for(SStart start :  flowMap.getStarts()) {
            deque.clear();
            orderedVisit(initialValue, start.getTask(), deque);
            initialValue = start.getTask().getOrder() + 2 * WEIGHT_PESO_TASK_JAVA + 1;
        }
        flowMap.getTasks().stream().filter(task -> task.getOrder() == 0)
                .forEach(task -> task.setOrder(calculateWeight(task) + 1000000));
    }

    private static void orderedVisit(int previousValue, STask<?> task, Deque<STask<?>> deque) {
        int newValue = previousValue + calculateWeight(task);
        int currentOrder = task.getOrder();
        if (currentOrder == 0 || (currentOrder < newValue && !deque.contains(task))) {
            task.setOrder(newValue);
            deque.add(task);
            for (STransition transition : task.getTransitions()) {
                if (task.getDefaultTransition() == transition) {
                    orderedVisit(newValue, transition.getDestination(), deque);
                } else {
                    orderedVisit(newValue + 1, transition.getDestination(), deque);
                }
            }
            deque.removeLast();
        }
    }

    private static int calculateTaskOrder(IEntityTaskVersion entityTaskDefinition, FlowDefinition<?> flowDefinition) {
        if (!flowDefinition.getEntityFlowDefinition().equals(
                entityTaskDefinition.getFlowVersion().getFlowDefinition())) {
            throw new SingularFlowException("Mistura de situações de definições diferrentes");
        }
        Optional<STask<?>> task = flowDefinition.getFlowMap().getTaskByAbbreviation(entityTaskDefinition.getAbbreviation());
        if (task.isPresent()) {
            return task.get().getOrder();
        }
        if (entityTaskDefinition.isPeople()) {
            return 10000000 + WEIGHT_TASK_HUMAN;
        } else if (entityTaskDefinition.isWait()) {
            return 10000000 + WEIGHT_TASK_WAIT;
        } else if (entityTaskDefinition.isEnd()) {
            return 10000000 + WEIGHT_TASK_FIM;
        } else {
            return 10000000 + WEIGHT_PESO_TASK_JAVA;
        }
    }

    private static int calculateWeight(STask<?> task) {
        IEntityTaskType tt = task.getTaskType();
        if (tt.isHuman()) {
            return WEIGHT_TASK_HUMAN;
        } else if (tt.isJava()) {
            return WEIGHT_PESO_TASK_JAVA;
        } else if (tt.isWait()) {
            return WEIGHT_TASK_WAIT;
        } else if (tt.isEnd()) {
            return WEIGHT_TASK_FIM;
        }
        throw new SingularFlowException(task.getTaskType() + " não tratado", task);
    }

    /** Faz a injeção de beans no objeto informado, se o mesmo necessitar. */
    @Nonnull
    public static <V> V inject(@Nonnull STask<?> task, @Nonnull V target) {
        return task.inject(target);
    }


    /** Creates a {@link TaskJavaCall} that doesn't do nothing. Useful mainly for implementing tests. */
    public static <T extends FlowInstance> TaskJavaCall<T> dummyTaskJavaCall() {
        return new TaskJavaCall<T>() {
            @Override
            public void call(ExecutionContext<T> context) {
            }
        };
    }

    /** Creates a {@link TaskJavaBatchCall} that doesn't do nothing. Useful mainly for implementing tests. */
    public static <T extends FlowInstance> TaskJavaBatchCall<T> dummyTaskJavaBatchCall() {
        return new TaskJavaBatchCall<T>() {
            @Override
            public String call(Collection<T> flowInstances) {
                return null;
            }
        };
    }

    /** Creates a {@link TaskAccessStrategy} that doesn't do nothing. Useful mainly for implementing tests. */
    @Nonnull
    public static TaskAccessStrategy dummyTaskAccessStrategy() {
        return new DummyTaskAccessStrategy();
    }

    private static class DummyTaskAccessStrategy extends TaskAccessStrategy {
        @Override
        public boolean canExecute(FlowInstance instance, SUser user) {
            return false;
        }

        @Override
        public Set<Integer> getFirstLevelUsersCodWithAccess(FlowInstance instance) {
            return Collections.emptySet();
        }

        @Override
        @Nonnull
        public List<? extends SUser> listAllowedUsers(@Nonnull FlowInstance instance) {
            return Collections.emptyList();
        }

        @Nonnull
        @Override
        public List<String> getExecuteRoleNames(FlowDefinition definition, STask task) {
            return Collections.emptyList();
        }
    }

    /** Creates a {@link FlowInstanceListener} that doesn't do nothing. Useful mainly for implementing tests. */
    @Nonnull
    public static FlowInstanceListener dummyFlowInstanceListener() { return new DummyInstanceListener(); }

    private static class DummyInstanceListener implements FlowInstanceListener {
        @Override
        public void notifyUserTaskRelocation(TaskInstance taskInstance, SUser responsibleUser, SUser userToNotify,
                SUser allocatedUser, SUser removedUser) {}

        @Override
        public void notifyUserTaskAllocation(TaskInstance taskInstance, SUser responsibleUser, SUser userToNotify,
                SUser allocatedUser, SUser removedUser, String justification) {}

        @Override
        public void notifyStartToResponsibleUser(TaskInstance taskInstance, ExecutionContext executionContext) {}

        @Override
        public void notifyStartToInterestedUser(TaskInstance taskInstance, ExecutionContext executionContext) {}

        @Override
        public <X extends SUser> void notifyLogToUsers(TaskHistoricLog taskHistoricLog, List<X> usersToNotify) {}

        @Override
        public void notifyStateUpdate(FlowInstance instance) {}
    }

    /** Creates a {@link ITaskPageStrategy} that doesn't do nothing. Useful mainly for implementing tests. */
    @Nonnull
    public static ITaskPageStrategy dummyITaskPageStrategy() { return new NullPageStrategy(); }

    private static class NullPageStrategy implements ITaskPageStrategy {
        @Override
        public WebRef getPageFor(TaskInstance taskInstance, SUser user) {
            return null;
        }
    }

    /** Creates a {@link IViewLocator} that doesn't do nothing. Useful mainly for implementing tests. */
    @Nonnull
    public static IViewLocator dummyIViewLocator() { return new NullViewLocator(); }

    private static class NullViewLocator implements IViewLocator {
        @Override
        public Lnk getDefaultHrefFor(FlowInstance flowInstance) {
            return null;
        }

        @Override
        public Lnk getDefaultHrefFor(TaskInstance taskInstance) {
            return null;
        }
    }

    /** Creates a {@link BusinessRoleStrategy} that doesn't do nothing. Useful mainly for implementing tests. */
    @Nonnull
    public static BusinessRoleStrategy<FlowInstance> dummyBusinessRoleStrategy() { return new EmptyBusinessRoleStrategy(); }

    private static class EmptyBusinessRoleStrategy extends BusinessRoleStrategy<FlowInstance> {
        @Override
        public List<? extends SUser> listAllowedUsers(FlowInstance instance) {
            return Collections.emptyList();
        }
    }

    /**
     * Instantiates a flow definition ignoring any bean injection dependency.
     * <p>IT MUST NOT BE USE IN PRODUCTION ENVIRONMENT. IT SHOULD ONLY BE USED FOR DEBUG AND JUNIT IMPLEMENTATIONS.</p>
     */
    @Nonnull
    public static <T extends FlowDefinition<?>> T instanceForDebug(@Nonnull Class<T> definitionClass) {
        Optional<SingularInjector> injector = ServiceRegistryLocator.locate().lookupSingularInjectorOpt();
        if (!injector.isPresent()) {
            ServiceRegistry registry = ServiceRegistryLocator.locate();
            //Creates a injector that doesn't do nothing and also doesn't about complain missing injections
            registry.bindService(SingularInjector.class,
                    RefService.ofToBeDescartedIfSerialized((SingularInjector) obj -> {
                    }));
        }
        try {
            return definitionClass.cast(definitionClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SingularFlowException(e);
        }
    }

    /**
     * Create a flow definition and calls the informed flowCreator to build the flow structure.
     * <p>This method isn't supposed to be used in production environments, but rather for debugging or JUnit testing
     * during developing.</p>
     */
    @Nonnull
    public static FlowDefinition<?> instanceForDebug(@Nonnull Consumer<FlowBuilderImpl> flowCreator) {
        return new BaseFlowTestDefinition(flowCreator);
    }

    private static final class BaseFlowTestDefinition extends FlowDefinition<FlowInstance> {

        private Consumer<FlowBuilderImpl> flowCreator;

        public BaseFlowTestDefinition(@Nonnull Consumer<FlowBuilderImpl> flowCreator) {
            this("dummyFlow", flowCreator);
        }

        public BaseFlowTestDefinition(@Nonnull String flowKey, @Nonnull Consumer<FlowBuilderImpl> flowCreator) {
            super(FlowInstance.class, VarService.basic(), flowKey);
            this.flowCreator = flowCreator;
        }

        @Nonnull
        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);
            flowCreator.accept(f);
            flowCreator = null;
            return f.build();
        }
    }

}
