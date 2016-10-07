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

import org.opensingular.flow.core.renderer.bpmn.view.EventCharacteristic;
import org.opensingular.flow.core.renderer.bpmn.view.EventNodeStyle;
import org.opensingular.flow.core.renderer.bpmn.view.EventType;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.EnumValueAnnotation;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.Label;

/**
 * Configuration class for EventNodeStyle option pane.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
@Label("Event Node")
public class EventNodeStyleConfiguration extends NodeStyleConfiguration<EventNodeStyle> {
  //region Properties

  @Label("Event Type")
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
  public final EventType getType() {
    return getStyleTemplate().getType();
  }

  @Label("Event Type")
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
  public final void setType( EventType value ) {
    getStyleTemplate().setType(value);
  }

  @Label("Event Characteristic")
  @EnumValueAnnotation(label = "Start", value = "START")
  @EnumValueAnnotation(label = "Subprocess Interrupting", value = "SUB_PROCESS_INTERRUPTING")
  @EnumValueAnnotation(label = "Subprocess Non Interrupting", value = "SUB_PROCESS_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Catching", value = "CATCHING")
  @EnumValueAnnotation(label = "Boundary Interrupting", value = "BOUNDARY_INTERRUPTING")
  @EnumValueAnnotation(label = "Boundary Non Interrupting", value = "BOUNDARY_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Throwing", value = "THROWING")
  @EnumValueAnnotation(label = "End", value = "END")
  public final EventCharacteristic getCharacteristic() {
    return getStyleTemplate().getCharacteristic();
  }

  @Label("Event Characteristic")
  @EnumValueAnnotation(label = "Start", value = "START")
  @EnumValueAnnotation(label = "Subprocess Interrupting", value = "SUB_PROCESS_INTERRUPTING")
  @EnumValueAnnotation(label = "Subprocess Non Interrupting", value = "SUB_PROCESS_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Catching", value = "CATCHING")
  @EnumValueAnnotation(label = "Boundary Interrupting", value = "BOUNDARY_INTERRUPTING")
  @EnumValueAnnotation(label = "Boundary Non Interrupting", value = "BOUNDARY_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Throwing", value = "THROWING")
  @EnumValueAnnotation(label = "End", value = "END")
  public final void setCharacteristic( EventCharacteristic value ) {
    getStyleTemplate().setCharacteristic(value);
  }

  @Override
  protected EventNodeStyle createDefault() {
    return new EventNodeStyle();
  }
}
