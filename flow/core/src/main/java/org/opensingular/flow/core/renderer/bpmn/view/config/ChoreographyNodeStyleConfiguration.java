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

import org.opensingular.flow.core.renderer.bpmn.view.ChoreographyNodeStyle;
import org.opensingular.flow.core.renderer.bpmn.view.ChoreographyType;
import org.opensingular.flow.core.renderer.bpmn.view.LoopCharacteristic;
import org.opensingular.flow.core.renderer.bpmn.view.SubState;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.EnumValueAnnotation;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.Label;

/**
 * Configuration class for ChoreographyNodeStyle option pane.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
@Label("Choreography Node")
public class ChoreographyNodeStyleConfiguration extends NodeStyleConfiguration<ChoreographyNodeStyle> {
  //region Properties

  @Label("Choreography Type")
  @EnumValueAnnotation(label = "Task", value = "TASK")
  @EnumValueAnnotation(label = "Call", value = "CALL")
  public final ChoreographyType getType() {
    return getStyleTemplate().getType();
  }

  @Label("Choreography Type")
  @EnumValueAnnotation(label = "Task", value = "TASK")
  @EnumValueAnnotation(label = "Call", value = "CALL")
  public final void setType( ChoreographyType value ) {
    getStyleTemplate().setType(value);
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

  @Label("Initiating Message")
  public final boolean isInitiatingMessage() {
    return getStyleTemplate().isInitiatingMessage();
  }

  @Label("Initiating Message")
  public final void setInitiatingMessage( boolean value ) {
    getStyleTemplate().setInitiatingMessage(value);
  }

  @Label("Response Message")
  public final boolean isResponseMessage() {
    return getStyleTemplate().isResponseMessage();
  }

  @Label("Response Message")
  public final void setResponseMessage( boolean value ) {
    getStyleTemplate().setResponseMessage(value);
  }

  @Label("Initiating At Top")
  public final boolean isInitiatingAtTop() {
    return getStyleTemplate().isInitiatingAtTop();
  }

  @Label("Initiating At Top")
  public final void setInitiatingAtTop( boolean value ) {
    getStyleTemplate().setInitiatingAtTop(value);
  }

  @Override
  protected ChoreographyNodeStyle createDefault() {
    return new ChoreographyNodeStyle();
  }
}
