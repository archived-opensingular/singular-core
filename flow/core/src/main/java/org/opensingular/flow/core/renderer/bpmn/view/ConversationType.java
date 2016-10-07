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
package org.opensingular.flow.core.renderer.bpmn.view;

import com.yworks.yfiles.annotations.Obfuscation;
import com.yworks.yfiles.graphml.GraphML;

/**
 * Specifies the type of a Conversation according to BPMN.
 * @see ConversationNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum ConversationType {
  /**
   * Specifies that a Conversation is a plain Conversation according to BPMN.
   * @see ConversationNodeStyle
   */
  @GraphML(name = "Conversation")
  CONVERSATION(0),

  /**
   * Specifies that a Conversation is a Sub-Conversation according to BPMN.
   * @see ConversationNodeStyle
   */
  @GraphML(name = "SubConversation")
  SUB_CONVERSATION(1),

  /**
   * Specifies that a Conversation is a Call Conversation according to BPMN where a Global Conversation is called.
   * @see ConversationNodeStyle
   */
  @GraphML(name = "CallingGlobalConversation")
  CALLING_GLOBAL_CONVERSATION(2),

  /**
   * Specifies that a Conversation is a Call Conversation according to BPMN where a Collaboration is called.
   * @see ConversationNodeStyle
   */
  @GraphML(name = "CallingCollaboration")
  CALLING_COLLABORATION(3);

  private final int value;

  ConversationType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final ConversationType fromOrdinal( int ordinal ) {
    for (ConversationType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
