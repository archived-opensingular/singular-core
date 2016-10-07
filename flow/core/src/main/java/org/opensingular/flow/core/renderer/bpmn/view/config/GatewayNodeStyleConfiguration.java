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

import org.opensingular.flow.core.renderer.bpmn.view.GatewayNodeStyle;
import org.opensingular.flow.core.renderer.bpmn.view.GatewayType;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.EnumValueAnnotation;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.Label;

/**
 * Configuration class for GatewayNodeStyle option pane.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
@Label("Gateway Node")
public class GatewayNodeStyleConfiguration extends NodeStyleConfiguration<GatewayNodeStyle> {
  //region Properties

  @Label("Gateway Type")
  @EnumValueAnnotation(label = "Exclusive Without Marker", value = "EXCLUSIVE_WITHOUT_MARKER")
  @EnumValueAnnotation(label = "Exclusive With Marker", value = "EXCLUSIVE_WITH_MARKER")
  @EnumValueAnnotation(label = "Inclusive", value = "INCLUSIVE")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Complex", value = "COMPLEX")
  @EnumValueAnnotation(label = "Event Based", value = "EVENT_BASED")
  @EnumValueAnnotation(label = "Exclusive Event Based", value = "EXCLUSIVE_EVENT_BASED")
  @EnumValueAnnotation(label = "Parallel Event Based", value = "PARALLEL_EVENT_BASED")
  public final GatewayType getType() {
    return getStyleTemplate().getType();
  }

  @Label("Gateway Type")
  @EnumValueAnnotation(label = "Exclusive Without Marker", value = "EXCLUSIVE_WITHOUT_MARKER")
  @EnumValueAnnotation(label = "Exclusive With Marker", value = "EXCLUSIVE_WITH_MARKER")
  @EnumValueAnnotation(label = "Inclusive", value = "INCLUSIVE")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Complex", value = "COMPLEX")
  @EnumValueAnnotation(label = "Event Based", value = "EVENT_BASED")
  @EnumValueAnnotation(label = "Exclusive Event Based", value = "EXCLUSIVE_EVENT_BASED")
  @EnumValueAnnotation(label = "Parallel Event Based", value = "PARALLEL_EVENT_BASED")
  public final void setType( GatewayType value ) {
    getStyleTemplate().setType(value);
  }

  @Override
  protected GatewayNodeStyle createDefault() {
    return new GatewayNodeStyle();
  }
}
