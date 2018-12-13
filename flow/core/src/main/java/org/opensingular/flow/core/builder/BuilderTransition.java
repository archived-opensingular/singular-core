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

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.DisplayInfoTransition;
import org.opensingular.flow.core.EventType;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.ITaskDefinition;
import org.opensingular.flow.core.ITransitionContext;
import org.opensingular.flow.core.ITransitionListener;
import org.opensingular.flow.core.SParametersEnabled;
import org.opensingular.flow.core.STransition;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.UITransitionAccessStrategy;
import org.opensingular.flow.core.UITransitionAccessStrategyImplUI;
import org.opensingular.flow.core.property.MetaDataKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface BuilderTransition<SELF extends BuilderTransition<SELF>> extends BuilderParametersEnabled<SELF> {

    public abstract STransition getTransition();

    public default SParametersEnabled getParametersEnabled() {
        return getTransition();
    }

    public FlowBuilder getFlowBuilder();

    public default SELF setAsDefaultTransition() {
        getTransition().getOrigin().setDefaultTransition(getTransition());
        return (SELF) self();
    }

    public default SELF thenGo(ITaskDefinition destination) {
        STransition transition = getTransition().thenGo(destination);
        return (SELF) getFlowBuilder().newTransition(transition);
    }

    public default SELF thenGo(String actionName, ITaskDefinition destination) {
        STransition transition = getTransition().thenGo(actionName, destination);
        return (SELF) getFlowBuilder().newTransition(transition);
    }

    public default SELF uiAccess(UITransitionAccessStrategy<? extends TaskInstance> accessStrategy) {
        getTransition().withAccessControl(accessStrategy);
        return self();
    }

    public default SELF defineBusinessRoleInTransition(BuilderBusinessRole<?> businessRole) {
        getTransition().defineBusinessRoleInTransition(businessRole.getBusinessRole());
        return self();
    }

    public default <K extends ITransitionContext> SELF withListener(ITransitionListener<K> iTransitionListener) {
        getTransition().addListener(iTransitionListener);
        return self();
    }

    public default <K extends FlowInstance> SELF setParametersInitializer(STransition.ITransitionParametersInitializerWithFlowInstance<K> parametrosInicializer) {
        getTransition().setParametersInitializer(parametrosInicializer);
        return self();
    }

    public default <K extends FlowInstance> SELF setParametersValidator(STransition.ITransitionParametersValidatorWithFlowInstance<K> parametrosValidator) {
        getTransition().setParametersValidator(parametrosValidator);
        return self();
    }

    @Nonnull
    public default <T extends Serializable> SELF setMetaDataValue(@Nonnull MetaDataKey<T> key, T value) {
        getTransition().setMetaDataValue(key, value);
        return self();
    }

    public default SELF uiHidden(){
        return uiAccess(UITransitionAccessStrategyImplUI.visible(false));
    }

    /**
     * Defines, for the purpose of generating a diagram of the flow, the BPMN type of the event that triggers the
     * execution of this transition, if not null. When it's null on a human task, usually means that this a transition
     * manually executed by the user.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    public default SELF setDisplayEventType(EventType eventType) {
        getTransition().getDisplayInfo().setDisplayEventType(eventType);
        return self();
    }

    public default SELF uiDisabled(){
        return uiAccess(UITransitionAccessStrategyImplUI.enabled(false, null));

    }
    public default SELF uiDisabled(String message){
        return uiAccess(UITransitionAccessStrategyImplUI.enabled(false, message));
    }

    /**
     * Define that, when generating a diagram of the flow, this transition should be preferred displayed as link
     * event instead of a direct line to the destination.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     *
     * @param displayAsLinkName The name of the link. If not null, then this transition will be displayed as link.
     */
    default SELF setDisplayAsLink(@Nullable String displayAsLinkName) {
        getTransition().getDisplayInfo().setDisplayAsLink(displayAsLinkName);
        return self();
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
    default SELF setDisplayAsLink(@Nullable String displayAsLinkName, int linkGroupIndex) {
        getTransition().getDisplayInfo().setDisplayAsLink(displayAsLinkName, linkGroupIndex);
        return self();
    }

    /**
     * Sets the transition as critical path of the flow, so the origin task and the destination task should be preferred
     * displayed together in the same visual level.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    @Nonnull
    default SELF setDisplayAsCriticalPath() {
        getTransition().getDisplayInfo().setCriticalPath(true);
        return self();
    }

    /**
     * Returns the display information of the transition.
     * <p>This information doesn't affect the runtime of the flow. The only affect is on the diagram generation.</p>
     */
    @Nonnull
    default DisplayInfoTransition getDisplayInfo() {
        return getTransition().getDisplayInfo();
    }
}