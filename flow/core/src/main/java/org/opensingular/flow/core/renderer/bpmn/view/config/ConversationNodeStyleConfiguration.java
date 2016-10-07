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

import org.opensingular.flow.core.renderer.bpmn.view.ConversationNodeStyle;
import org.opensingular.flow.core.renderer.bpmn.view.ConversationType;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.EnumValueAnnotation;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.Label;

/**
 * Configuration class for ConversationNodeStyle option pane.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
@Label("Conversation Node")
public class ConversationNodeStyleConfiguration extends NodeStyleConfiguration<ConversationNodeStyle> {
  //region Properties

  @Label("Conversation Type")
  @EnumValueAnnotation(label = "Conversation", value = "CONVERSATION")
  @EnumValueAnnotation(label = "Sub Conversation", value = "SUB_CONVERSATION")
  @EnumValueAnnotation(label = "Calling Global Conversation", value = "CALLING_GLOBAL_CONVERSATION")
  @EnumValueAnnotation(label = "Calling Collaboration", value = "CALLING_COLLABORATION")
  public final ConversationType getType() {
    return getStyleTemplate().getType();
  }

  @Label("Conversation Type")
  @EnumValueAnnotation(label = "Conversation", value = "CONVERSATION")
  @EnumValueAnnotation(label = "Sub Conversation", value = "SUB_CONVERSATION")
  @EnumValueAnnotation(label = "Calling Global Conversation", value = "CALLING_GLOBAL_CONVERSATION")
  @EnumValueAnnotation(label = "Calling Collaboration", value = "CALLING_COLLABORATION")
  public final void setType( ConversationType value ) {
    getStyleTemplate().setType(value);
  }

  @Override
  protected ConversationNodeStyle createDefault() {
    return new ConversationNodeStyle();
  }
}
