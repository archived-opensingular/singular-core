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
package org.opensingular.flow.core.renderer.bpmn.layout;


/**
 * @see BpmnElementTypes#getType(com.yworks.yfiles.algorithms.Edge, com.yworks.yfiles.algorithms.Graph)
 * @see BpmnElementTypes#isConversationLink(EdgeType)
 * @see BpmnLayerer#getWeight(EdgeType)
 * @see BpmnElementTypes#isValidEdgeType(EdgeType)
 * @see BpmnLayerer#getType(com.yworks.yfiles.algorithms.Edge, com.yworks.yfiles.layout.LayoutGraph)
 * @see BpmnElementTypes#isAssociation(EdgeType)
 * @see BpmnElementTypes#isSequenceFlow(EdgeType)
 * @see BpmnElementTypes#isInvalidEdgeType(EdgeType)
 * @see BpmnLayerer#getMinimalLength(EdgeType)
 * @see BpmnElementTypes#isMessageFlow(EdgeType)
 */
public enum EdgeType {
  /**
   * Type constant for an invalid type.
   */
  INVALID(0),

  /**
   * Type constant for a BPMN connection type (sequence flow).
   */
  SEQUENCE_FLOW(4),

  /**
   * Type constant for a BPMN connection type (message flow).
   */
  MESSAGE_FLOW(5),

  /**
   * Type constant for a BPMN connection type (association).
   */
  ASSOCIATION(6),

  /**
   * Type constant for a BPMN connection type (conversation link).
   */
  CONVERSATION_LINK(15);

  private final int value;

  EdgeType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final EdgeType fromOrdinal( int ordinal ) {
    for (EdgeType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
