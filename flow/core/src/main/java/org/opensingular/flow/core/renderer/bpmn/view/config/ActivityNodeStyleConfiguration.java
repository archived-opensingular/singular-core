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
package org.opensingular.flow.core.renderer.bpmn.view.config;

import org.opensingular.flow.core.renderer.bpmn.view.ActivityNodeStyle;
import org.opensingular.flow.core.renderer.bpmn.view.ActivityType;
import org.opensingular.flow.core.renderer.bpmn.view.EventCharacteristic;
import org.opensingular.flow.core.renderer.bpmn.view.EventType;
import org.opensingular.flow.core.renderer.bpmn.view.LoopCharacteristic;
import org.opensingular.flow.core.renderer.bpmn.view.SubState;
import org.opensingular.flow.core.renderer.bpmn.view.TaskType;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.EnumValueAnnotation;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.Label;

/**
 * Configuration class for ActivityNodeStyle option pane.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
@Label("Activity Node")
public class ActivityNodeStyleConfiguration extends NodeStyleConfiguration<ActivityNodeStyle> {
  //region Properties

  @Label("Activity Type")
  @EnumValueAnnotation(label = "Task", value = "TASK")
  @EnumValueAnnotation(label = "Subprocess", value = "SUB_PROCESS")
  @EnumValueAnnotation(label = "Transaction", value = "TRANSACTION")
  @EnumValueAnnotation(label = "Event Subprocess", value = "EVENT_SUB_PROCESS")
  @EnumValueAnnotation(label = "Call Activity", value = "CALL_ACTIVITY")
  public final ActivityType getActivityType() {
    return getStyleTemplate().getActivityType();
  }

  @Label("Activity Type")
  @EnumValueAnnotation(label = "Task", value = "TASK")
  @EnumValueAnnotation(label = "Subprocess", value = "SUB_PROCESS")
  @EnumValueAnnotation(label = "Transaction", value = "TRANSACTION")
  @EnumValueAnnotation(label = "Event Subprocess", value = "EVENT_SUB_PROCESS")
  @EnumValueAnnotation(label = "Call Activity", value = "CALL_ACTIVITY")
  public final void setActivityType( ActivityType value ) {
    getStyleTemplate().setActivityType(value);
  }

  @Label("Task Type")
  @EnumValueAnnotation(label = "Abstract", value = "ABSTRACT")
  @EnumValueAnnotation(label = "Send", value = "SEND")
  @EnumValueAnnotation(label = "Receive", value = "RECEIVE")
  @EnumValueAnnotation(label = "User", value = "USER")
  @EnumValueAnnotation(label = "Manual", value = "MANUAL")
  @EnumValueAnnotation(label = "Business Rule", value = "BUSINESS_RULE")
  @EnumValueAnnotation(label = "Service", value = "SERVICE")
  @EnumValueAnnotation(label = "Script", value = "SCRIPT")
  @EnumValueAnnotation(label = "Event Triggered", value = "EVENT_TRIGGERED")
  public final TaskType getTaskType() {
    return getStyleTemplate().getTaskType();
  }

  @Label("Task Type")
  @EnumValueAnnotation(label = "Abstract", value = "ABSTRACT")
  @EnumValueAnnotation(label = "Send", value = "SEND")
  @EnumValueAnnotation(label = "Receive", value = "RECEIVE")
  @EnumValueAnnotation(label = "User", value = "USER")
  @EnumValueAnnotation(label = "Manual", value = "MANUAL")
  @EnumValueAnnotation(label = "Business Rule", value = "BUSINESS_RULE")
  @EnumValueAnnotation(label = "Service", value = "SERVICE")
  @EnumValueAnnotation(label = "Script", value = "SCRIPT")
  @EnumValueAnnotation(label = "Event Triggered", value = "EVENT_TRIGGERED")
  public final void setTaskType( TaskType value ) {
    getStyleTemplate().setTaskType(value);
  }

  @Label("Trigger Event Type")
  @EnumValueAnnotation(label = "Plain", value = "PLAIN")
  @EnumValueAnnotation(label = "Message", value = "MESSAGE")
  @EnumValueAnnotation(label = "Timer", value = "TIMER")
  @EnumValueAnnotation(label = "Escalation", value = "ESCALATION")
  @EnumValueAnnotation(label = "Conditional", value = "CONDITIONAL")
  @EnumValueAnnotation(label = "Link", value = "LINK")
  @EnumValueAnnotation(label = "Error", value = "ERROR")
  @EnumValueAnnotation(label = "Cancel", value = "CANCEL")
  @EnumValueAnnotation(label = "Compensation", value = "COMPENSATION")
  @EnumValueAnnotation(label = "Signal", value = "SIGNAL")
  @EnumValueAnnotation(label = "Multiple", value = "MULTIPLE")
  @EnumValueAnnotation(label = "Parallel Multiple", value = "PARALLEL_MULTIPLE")
  @EnumValueAnnotation(label = "Terminate", value = "TERMINATE")
  public final EventType getTriggerEventType() {
    return getStyleTemplate().getTriggerEventType();
  }

  @Label("Trigger Event Type")
  @EnumValueAnnotation(label = "Plain", value = "PLAIN")
  @EnumValueAnnotation(label = "Message", value = "MESSAGE")
  @EnumValueAnnotation(label = "Timer", value = "TIMER")
  @EnumValueAnnotation(label = "Escalation", value = "ESCALATION")
  @EnumValueAnnotation(label = "Conditional", value = "CONDITIONAL")
  @EnumValueAnnotation(label = "Link", value = "LINK")
  @EnumValueAnnotation(label = "Error", value = "ERROR")
  @EnumValueAnnotation(label = "Cancel", value = "CANCEL")
  @EnumValueAnnotation(label = "Compensation", value = "COMPENSATION")
  @EnumValueAnnotation(label = "Signal", value = "SIGNAL")
  @EnumValueAnnotation(label = "Multiple", value = "MULTIPLE")
  @EnumValueAnnotation(label = "Parallel Multiple", value = "PARALLEL_MULTIPLE")
  @EnumValueAnnotation(label = "Terminate", value = "TERMINATE")
  public final void setTriggerEventType( EventType value ) {
    getStyleTemplate().setTriggerEventType(value);
  }

  @Label("Trigger Event Characteristic")
  @EnumValueAnnotation(label = "Start", value = "START")
  @EnumValueAnnotation(label = "Subprocess Interrupting", value = "SUB_PROCESS_INTERRUPTING")
  @EnumValueAnnotation(label = "Subprocess Non Interrupting", value = "SUB_PROCESS_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Catching", value = "CATCHING")
  @EnumValueAnnotation(label = "Boundary Interrupting", value = "BOUNDARY_INTERRUPTING")
  @EnumValueAnnotation(label = "Boundary Non Interrupting", value = "BOUNDARY_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Throwing", value = "THROWING")
  @EnumValueAnnotation(label = "End", value = "END")
  public final EventCharacteristic getTriggerEventCharacteristic() {
    return getStyleTemplate().getTriggerEventCharacteristic();
  }

  @Label("Trigger Event Characteristic")
  @EnumValueAnnotation(label = "Start", value = "START")
  @EnumValueAnnotation(label = "Subprocess Interrupting", value = "SUB_PROCESS_INTERRUPTING")
  @EnumValueAnnotation(label = "Subprocess Non Interrupting", value = "SUB_PROCESS_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Catching", value = "CATCHING")
  @EnumValueAnnotation(label = "Boundary Interrupting", value = "BOUNDARY_INTERRUPTING")
  @EnumValueAnnotation(label = "Boundary Non Interrupting", value = "BOUNDARY_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Throwing", value = "THROWING")
  @EnumValueAnnotation(label = "End", value = "END")
  public final void setTriggerEventCharacteristic( EventCharacteristic value ) {
    getStyleTemplate().setTriggerEventCharacteristic(value);
  }

  @Label("Loop Characteristic")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Loop", value = "LOOP")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Sequential", value = "SEQUENTIAL")
  public final LoopCharacteristic getLoopCharacteristic() {
    return getStyleTemplate().getLoopCharacteristic();
  }

  @Label("Loop Characteristic")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Loop", value = "LOOP")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Sequential", value = "SEQUENTIAL")
  public final void setLoopCharacteristic( LoopCharacteristic value ) {
    getStyleTemplate().setLoopCharacteristic(value);
  }

  @Label("Substate")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Expanded", value = "EXPANDED")
  @EnumValueAnnotation(label = "Collapsed", value = "COLLAPSED")
  @EnumValueAnnotation(label = "Dynamic", value = "DYNAMIC")
  public final SubState getSubState() {
    return getStyleTemplate().getSubState();
  }

  @Label("Substate")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Expanded", value = "EXPANDED")
  @EnumValueAnnotation(label = "Collapsed", value = "COLLAPSED")
  @EnumValueAnnotation(label = "Dynamic", value = "DYNAMIC")
  public final void setSubState( SubState value ) {
    getStyleTemplate().setSubState(value);
  }

  @Label("Ad Hoc")
  public final boolean isAdHoc() {
    return getStyleTemplate().isAdHoc();
  }

  @Label("Ad Hoc")
  public final void setAdHoc( boolean value ) {
    getStyleTemplate().setAdHoc(value);
  }

  @Label("Compensation")
  public final boolean isCompensation() {
    return getStyleTemplate().isCompensation();
  }

  @Label("Compensation")
  public final void setCompensation( boolean value ) {
    getStyleTemplate().setCompensation(value);
  }

  @Override
  protected ActivityNodeStyle createDefault() {
    return new ActivityNodeStyle();
  }
}
