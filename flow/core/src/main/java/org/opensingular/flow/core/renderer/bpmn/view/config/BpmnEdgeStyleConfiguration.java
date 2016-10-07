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

import org.opensingular.flow.core.renderer.bpmn.view.BpmnEdgeStyle;
import org.opensingular.flow.core.renderer.bpmn.view.EdgeType;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.EnumValueAnnotation;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.Label;

/**
 * Configuration class for BpmnEdgeStyle option pane.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
@Label("Bpmn Edge")
public class BpmnEdgeStyleConfiguration extends EdgeStyleConfiguration<BpmnEdgeStyle> {
  //region Properties

  @Label("Type")
  @EnumValueAnnotation(label = "Sequence Flow", value = "SEQUENCE_FLOW")
  @EnumValueAnnotation(label = "Default Flow", value = "DEFAULT_FLOW")
  @EnumValueAnnotation(label = "Conditional Flow", value = "CONDITIONAL_FLOW")
  @EnumValueAnnotation(label = "Message Flow", value = "MESSAGE_FLOW")
  @EnumValueAnnotation(label = "Association", value = "ASSOCIATION")
  @EnumValueAnnotation(label = "Directed Association", value = "DIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Bidirected Association", value = "BIDIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Conversation", value = "CONVERSATION")
  public final EdgeType getType() {
    return getStyleTemplate().getType();
  }

  @Label("Type")
  @EnumValueAnnotation(label = "Sequence Flow", value = "SEQUENCE_FLOW")
  @EnumValueAnnotation(label = "Default Flow", value = "DEFAULT_FLOW")
  @EnumValueAnnotation(label = "Conditional Flow", value = "CONDITIONAL_FLOW")
  @EnumValueAnnotation(label = "Message Flow", value = "MESSAGE_FLOW")
  @EnumValueAnnotation(label = "Association", value = "ASSOCIATION")
  @EnumValueAnnotation(label = "Directed Association", value = "DIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Bidirected Association", value = "BIDIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Conversation", value = "CONVERSATION")
  public final void setType( EdgeType value ) {
    getStyleTemplate().setType(value);
  }

  @Override
  protected BpmnEdgeStyle createDefault() {
    return new BpmnEdgeStyle();
  }
}
