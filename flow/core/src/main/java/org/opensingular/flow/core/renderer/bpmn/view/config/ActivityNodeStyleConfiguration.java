/****************************************************************************
 **
 ** This demo file is part of yFiles for Java 3.0.0.1.
 **
 ** Copyright (c) 2000-2016 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for Java functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for Java version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for Java powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for Java
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
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
