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
 * Specifies the type of an edge according to BPMN.
 * @see BpmnEdgeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum EdgeType {
  /**
   * Specifies an edge to be a Sequence Flow according to BPMN.
   * @see BpmnEdgeStyle
   */
  @GraphML(name = "SequenceFlow")
  SEQUENCE_FLOW(0),

  /**
   * Specifies an edge to be a Default Flow according to BPMN.
   * @see BpmnEdgeStyle
   */
  @GraphML(name = "DefaultFlow")
  DEFAULT_FLOW(1),

  /**
   * Specifies an edge to be a Conditional Flow according to BPMN.
   * @see BpmnEdgeStyle
   */
  @GraphML(name = "ConditionalFlow")
  CONDITIONAL_FLOW(2),

  /**
   * Specifies an edge to be a Message Flow according to BPMN.
   * @see BpmnEdgeStyle
   */
  @GraphML(name = "MessageFlow")
  MESSAGE_FLOW(3),

  /**
   * Specifies an edge to be an undirected Association according to BPMN.
   * @see BpmnEdgeStyle
   */
  @GraphML(name = "Association")
  ASSOCIATION(4),

  /**
   * Specifies an edge to be a directed Association according to BPMN.
   * @see BpmnEdgeStyle
   */
  @GraphML(name = "DirectedAssociation")
  DIRECTED_ASSOCIATION(5),

  /**
   * Specifies an edge to be a bidirected Association according to BPMN.
   * @see BpmnEdgeStyle
   */
  @GraphML(name = "BidirectedAssociation")
  BIDIRECTED_ASSOCIATION(6),

  /**
   * Specifies an edge to be a Conversation according to BPMN.
   * @see BpmnEdgeStyle
   */
  @GraphML(name = "Conversation")
  CONVERSATION(7);

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
