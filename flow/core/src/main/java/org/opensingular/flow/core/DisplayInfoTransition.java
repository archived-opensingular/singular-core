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

import javax.annotation.Nullable;

/**
 * This contains display information about the transition to help the diagram generation. It doesn't affect the runtime
 * of the flow.
 *
 * @author Daniel C. Bordin
 * @since 2018-04-29
 */
public class DisplayInfoTransition {

    private final STransition transition;
    private EventType displayEventType;
    private String displayAsLinkName;
    private int displayAsLinkGroupIndex = -1;
    private boolean criticalPath = false;

    public DisplayInfoTransition(STransition transition) {this.transition = transition;}

    /**
     * Return the BPMN type of event that triggers the execution of this transition for use when generating a diagram
     * of the flow. When null, it usually means that this a transition manually executed by the user.
     * <p> This method first user the value set by {@link #setDisplayEventType(EventType)}. If it isn't explicit set and
     * {@link STransition#getPredicate()} is not null, then returns {@link ITaskPredicate#getDisplayEventType()}.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    @Nullable
    public EventType getDisplayEventType() {
        if (displayEventType == null && transition.getPredicate() != null) {
            return transition.getPredicate().getDisplayEventType();
        }
        return displayEventType;
    }

    /**
     * Defines, for the purpose of generating a diagram of the flow, the BPMN type of the event that triggers the
     * execution of this transition, if not null. When it's null on a human task, usually means that this a transition
     * manually executed by the user.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    public void setDisplayEventType(@Nullable EventType displayEventType) {
        this.displayEventType = displayEventType;
    }

    /**
     * Define that, when generating a diagram of the flow, this transition should be preferred displayed as link
     * event instead of a direct line to the destination.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     *
     * @param displayAsLinkName The name of the link. If not null, then this transition will be displayed as link.
     */
    public void setDisplayAsLink(@Nullable String displayAsLinkName) {
        setDisplayAsLink(displayAsLinkName, -1);
    }

    /**
     * Define that, when generating a diagram of the flow, this transition should be preferred displayed as link
     * event instead of a direct line to the destination.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     *
     * @param displayAsLinkName The name of the link. If not null, then this transition will be displayed as link.
     * @param linkGroupIndex    If there is two links for the same destination with the same index, it
     *                          will be rendered as just one visual component.
     */
    public void setDisplayAsLink(@Nullable String displayAsLinkName, int linkGroupIndex) {
        this.displayAsLinkName = displayAsLinkName;
        this.displayAsLinkGroupIndex = linkGroupIndex;
    }

    /**
     * If not null, it means that this transition should be preferred displayed as link event instead of a direct line
     * to the destination when generating a diagram of the flow.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    @Nullable
    public String getDisplayAsLinkName() {
        return displayAsLinkName;
    }

    /**
     * Two transition marked to be rendered as link and that also have the same non negative index, it means that both
     * transition should be point to the same outgoing link. This information is meaningful only if {@link
     * #getDisplayAsLinkName()} is not null.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     *
     * @return negative number, if the link shouldn't be grouped. A non negative number, if this transition is part of
     * the same link group.
     */
    public int getDisplayAsLinkGroupIndex() {
        return displayAsLinkGroupIndex;
    }

    /** Checks if the current transition is part of the critical path of the flow. By default, it is not. */
    public boolean isCriticalPath() {
        return criticalPath;
    }

    /**
     * Sets the transition as critical path of the flow, so the origin task and the destination task should be preferred
     * displayed together in the same visual level.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    public void setCriticalPath(boolean criticalPath) {
        this.criticalPath = criticalPath;
    }
}
