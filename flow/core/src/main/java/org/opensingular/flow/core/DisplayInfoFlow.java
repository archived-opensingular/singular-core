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

package org.opensingular.flow.core;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Represents display information about the flow in general to help the diagram generation. It doesn't affect the
 * runtime of the flow.
 *
 * @author Daniel C. Bordin
 * @since 2018-04-29
 */
public class DisplayInfoFlow {

    private final FlowMap flowMap;

    private boolean hideCancelTransitions;

    private DisplayTransitionLabelStrategy transitionLabelStrategy = DisplayTransitionLabelStrategy.SMART;

    DisplayInfoFlow(@Nonnull FlowMap flowMap) {this.flowMap = Objects.requireNonNull(flowMap);}

    /** If true, the generated diagram won't show any transaction of the type cancel (trigger by a cancelevent). */
    public boolean isHideCancelTransitions() {
        return hideCancelTransitions;
    }

    /**
     * If set to true, the generated diagram won't show any transaction of the type cancel (trigger by a cancel
     * event). By the default, it's false.
     */
    public void setHideCancelTransitions(boolean hideCancelTransitions) {
        this.hideCancelTransitions = hideCancelTransitions;
    }

    /**
     * Returns the strategy used to display transition's labels when generating the flow diagram. By default, it's
     * {@link
     * DisplayTransitionLabelStrategy#SMART}.
     */
    @Nonnull
    public DisplayTransitionLabelStrategy getTransitionLabelStrategy() {
        return transitionLabelStrategy;
    }

    /**
     * Sets the strategy used to display transition's labels when generating the flow diagram. By default, it's {@link
     * DisplayTransitionLabelStrategy#SMART}.
     */
    public void setTransitionLabelStrategy(@Nonnull DisplayTransitionLabelStrategy transitionLabelStrategy) {
        this.transitionLabelStrategy = Objects.requireNonNull(transitionLabelStrategy);
    }

    /**
     * Sets the sequence os task as a critical path of the flow. It's the same thing to call {@link
     * #addCriticalPath(ITaskDefinition, ITaskDefinition)} for each pair of tasks.
     */
    public void addCriticalPath(ITaskDefinition... taskPath) {
        for (int i = 0; i < taskPath.length - 1; i++) {
            addCriticalPath(taskPath[i], taskPath[i + 1]);
        }
    }

    /**
     * Define the transition between the two tasks as been a critical path of the flow. See {@link
     * DisplayInfoTransition#setCriticalPath(boolean)} for more information.
     * <p>Throws a exception if there isn't one transition between the two task or if there is more the one
     * transition.</p>
     */
    public void addCriticalPath(@Nonnull ITaskDefinition taskOrigin, @Nonnull ITaskDefinition taskDestination) {
        STask<?> origin = flowMap.getTask(taskOrigin);
        STask<?> destination = flowMap.getTask(taskDestination);
        origin.getTransitionToOrException(taskDestination).getDisplayInfo().setCriticalPath(true);
    }
}
