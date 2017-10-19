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

import org.opensingular.flow.core.property.MetaData;
import org.opensingular.flow.core.property.MetaDataEnabled;
import org.opensingular.flow.core.variable.ValidationResult;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.lib.commons.base.SingularUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class STransition extends SParametersEnabled implements MetaDataEnabled {

    private final STask<?> origin;
    private final String name;
    private final STask<?> destination;
    private final String         abbreviation;

    private UITransitionAccessStrategy<TaskInstance> accessStrategy;
    private List<SBusinessRole> rolesToDefineUser;

    private MetaData metaData;

    private ITransitionParametersInitializer parametersInitializer;
    private ITransitionParametersValidator   parametersValidator;

    private ITaskPredicate predicate;
    private EventType displayEventType;
    private String displayAsLinkName;
    private int displayAsLinkGroupIndex = -1;

    protected STransition(STask<?> origin, String name, @Nonnull STask<?> destination) {
        this.origin = origin;
        this.name = name;
        this.destination = Objects.requireNonNull(destination);
        this.abbreviation = SingularUtil.convertToJavaIdentity(name, true);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public STransition withAccessControl(UITransitionAccessStrategy<? extends TaskInstance> accessStrategy) {
        if (this.accessStrategy != null) {
            throw new SingularFlowException("Access strategy already defined");
        }
        this.accessStrategy = (UITransitionAccessStrategy<TaskInstance>) inject(accessStrategy);
        return this;
    }

    public TransitionAccess getAccessFor(TaskInstance taskInstance) {
        if (accessStrategy == null) {
            return new TransitionAccess(TransitionAccess.TransitionVisibilityLevel.ENABLED_AND_VISIBLE, null);
        }
        return accessStrategy.getAccess(taskInstance);
    }

    public boolean hasRoleUsersToSet() {
        if (rolesToDefineUser != null) {
            for (SBusinessRole processRole : rolesToDefineUser) {
                if (!processRole.isAutomaticBusinessRoleAllocation()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAutomaticRoleUsersToSet() {
        if (rolesToDefineUser != null) {
            return rolesToDefineUser.stream().anyMatch(SBusinessRole::isAutomaticBusinessRoleAllocation);
        }
        return false;
    }

    public STransition defineBusinessRoleInTransition(SBusinessRole papel) {
        if (origin.isPeople() || papel.isAutomaticBusinessRoleAllocation()) {
            if (this.rolesToDefineUser == null) {
                this.rolesToDefineUser = new ArrayList<>();
            }
            this.rolesToDefineUser.add(papel);
            return this;
        } else {
            throw new SingularFlowException(
                    "Only automatic user allocation is allowed in " + origin.getTaskType() + " tasks", origin);
        }
    }

    public List<SBusinessRole> getRolesToDefine() {
        if (rolesToDefineUser == null) {
            return Collections.emptyList();
        }
        return rolesToDefineUser;
    }

    @Override
    @Nonnull
    public Optional<MetaData> getMetaDataOpt() {
        return Optional.ofNullable(metaData);
    }

    @Override
    @Nonnull
    public MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    public STask<?> getOrigin() {
        return origin;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public STask<?> getDestination() {
        return destination;
    }

    public STransition thenGo(ITaskDefinition destination) {
        return thenGo(getFlowMap().getTask(destination));
    }

    public STransition thenGo(String actionName, ITaskDefinition destination) {
        return thenGo(actionName, getFlowMap().getTask(destination));
    }

    public STransition thenGo(STask<?> destination) {
        return this.destination.addTransition(destination);
    }

    public STransition thenGo(String actionName, STask<?> destination) {
        return this.destination.addTransition(actionName, destination);
    }

    @Nonnull
    public STransition setParametersInitializer(@Nonnull ITransitionParametersInitializer parametersInitializer) {
        if(this.parametersInitializer != null){
            throw new SingularFlowException("Parameters Initializer already set");
        }
        this.parametersInitializer = inject(parametersInitializer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <K extends FlowInstance> STransition setParametersInitializer(
            @Nonnull ITransitionParametersInitializerProcess<K> initializerByProcess) {
        inject(initializerByProcess);
        return setParametersInitializer((ITransitionParametersInitializer) (params, ctx) -> initializerByProcess.init(params, (K) ctx.getFlowInstance()));
    }

    @Nonnull
    public STransition setParametersValidator(@Nonnull ITransitionParametersValidator parametersValidator) {
        if(this.parametersValidator != null){
            throw new SingularFlowException("Parameters Validator already set");
        }
        this.parametersValidator = inject(parametersValidator);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <K extends FlowInstance> STransition setParametersValidator(
            @Nonnull ITransitionParametersValidatorProcess<K> validatorByProcess) {
        inject(validatorByProcess);
        return setParametersValidator((ITransitionParametersValidator) (params, result, ctx) -> validatorByProcess
            .validate(params, result, (K) ctx.getFlowInstance()));
    }

    @Nonnull
    final VarInstanceMap<?,?> newTransitionParameters(@Nonnull RefTransition refTransition) {
        Objects.requireNonNull(refTransition);
        VarInstanceMap<?,?> params = getParameters().newInstanceMap();
        if (parametersInitializer != null) {
            parametersInitializer.init(params, refTransition);
        }
        return params;
    }

    @Nonnull
    final ValidationResult validate(@Nonnull RefTransition refTransition, VarInstanceMap<?,?> parameters) {
        ValidationResult validationResult = new ValidationResult();
        if (parametersValidator != null) {
            parametersValidator.validate(parameters, validationResult, refTransition);
        }
        return validationResult;
    }

    @Nonnull
    final ValidationResult validate(@Nonnull TaskInstance instance, VarInstanceMap<?,?> parameters) {
        return validate(new RefTransition(instance, this), parameters);
    }

    @Override
    public STransition addParamBindedToProcessVariable(String ref, boolean required) {
        super.addParamBindedToProcessVariable(ref, required);
        return this;
    }

    final void setPredicate(ITaskPredicate predicate) {
        this.predicate = predicate;
    }

    public ITaskPredicate getPredicate() {
        return predicate;
    }


    @Override
    FlowMap getFlowMap() {
        return destination.getFlowMap();
    }

    @Override
    public String toString() {
        return name + "(go to task:" + destination.getName() + ")";
    }

    /**
     * Return the BPMN type of event that triggers the execution of this transition for use when generating a diagram
     * of the process. When null, it usually means that this a transition manually executed by the user.
     * <p> This method first user the value set by {@link #setDisplayEventType(EventType)}. If null and {@link
     * #getPredicate()}
     * is not null, then returns {@link ITaskPredicate#getDisplayEventType()}.
     * <p>This information doesn't affect the runtime of the process. The only affect is on the diagram generation.</p>
     */
    @Nullable
    public EventType getDisplayEventType() {
        if (displayEventType == null && getPredicate() != null) {
            return getPredicate().getDisplayEventType();
        }
        return displayEventType;
    }

    /**
     * Defines, for the purpose of generating a diagram of the process, the BPMN type of the event that triggers the
     * execution of this transition, if not null. When it's null on a human task, usually means that this a transition
     * manually executed by the user.
     * <p>This information doesn't affect the runtime of the process. The only affect is on the diagram generation.</p>
     */
    public void setDisplayEventType(@Nullable EventType displayEventType) {
        this.displayEventType = displayEventType;
    }

    /**
     * Define that, when generating a diagram of the process, this transition should be preferred displayed as link
     * event instead of a direct line to the destination.
     * <p>This information doesn't affect the runtime of the process. The only affect is on the diagram generation.</p>
     *
     * @param displayAsLinkName The name of the link. If not null, then this transition will be displayed as link.
     */
    public void setDisplayAsLink(@Nullable String displayAsLinkName) {
        setDisplayAsLink(displayAsLinkName, -1);
    }

    /**
     * Define that, when generating a diagram of the process, this transition should be preferred displayed as link
     * event instead of a direct line to the destination.
     * <p>This information doesn't affect the runtime of the process. The only affect is on the diagram generation.</p>
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
     * to the destination when generating a diagram of the process.
     * <p>This information doesn't affect the runtime of the process. The only affect is on the diagram generation.</p>
     */
    @Nullable
    public String getDisplayAsLinkName() {
        return displayAsLinkName;
    }

    /**
     * Two transition marked to be rendered as link and that also have the same non negative index, it means that both
     * transition should be point to the same outgoing link. This information is meaningful only if {@link
     * #getDisplayAsLinkName()} is not null.
     * <p>This information doesn't affect the runtime of the process. The only affect is on the diagram generation.</p>
     *
     * @return negative number, if the link shouldn't be grouped. A non negative number, if this transition is part of
     * the same link group.
     */
    public int getDisplayAsLinkGroupIndex() {
        return displayAsLinkGroupIndex;
    }

    @FunctionalInterface
    public interface ITransitionParametersInitializer extends Serializable {
        void init(VarInstanceMap<?,?> params, RefTransition context);
    }

    @FunctionalInterface
    public interface ITransitionParametersInitializerProcess<K extends FlowInstance> extends Serializable {
        void init(VarInstanceMap<?,?> params, K flowInstance);
    }

    @FunctionalInterface
    public interface ITransitionParametersValidator extends Serializable {
        public void validate(VarInstanceMap<?,?> params, ValidationResult validationResult, RefTransition context);
    }

    @FunctionalInterface
    public interface ITransitionParametersValidatorProcess<K extends FlowInstance> extends Serializable {
        void validate(VarInstanceMap<?,?> params, ValidationResult validationResult, K flowInstance);
    }

}
