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
 * @see BpmnElementTypes#isTask(NodeType)
 * @see BpmnElementTypes#isValidNodeType(NodeType)
 * @see BpmnElementTypes#isArtifact(NodeType)
 * @see BpmnElementTypes#isActivityNode(NodeType)
 * @see BpmnElementTypes#isGatewayNode(NodeType)
 * @see BpmnElementTypes#isSubProcess(NodeType)
 * @see BpmnElementTypes#isEndNode(NodeType)
 * @see BpmnElementTypes#isInvalidNodeType(NodeType)
 * @see BpmnElementTypes#getType(com.yworks.yfiles.algorithms.Node, com.yworks.yfiles.algorithms.Graph)
 * @see BpmnElementTypes#isEventNode(NodeType)
 * @see BpmnElementTypes#isStartNode(NodeType)
 */
public enum NodeType {
  /**
   * Type constant for an invalid type.
   */
  INVALID(0),

  /**
   * Type constant for a BPMN event type.
   */
  EVENT(1),

  /**
   * Type constant for a BPMN start event type.
   */
  START_EVENT(7),

  /**
   * Type constant for a BPMN end event type.
   */
  END_EVENT(9),

  /**
   * Type constant for a BPMN gateway type.
   */
  GATEWAY(2),

  /**
   * Type constant for a BPMN task type.
   */
  TASK(3),

  /**
   * Type constant for a BPMN sub-process type.
   */
  SUB_PROCESS(8),

  /**
   * Type constant for a BPMN annotation type.
   */
  ANNOTATION(10),

  /**
   * Type constant for a BPMN artifact type.
   */
  ARTIFACT(11),

  /**
   * Type constant for a BPMN pool type.
   */
  POOL(12),

  /**
   * Type constant for a BPMN conversation type.
   */
  CONVERSATION(13),

  /**
   * Type constant for a BPMN choreography type.
   */
  CHOREOGRAPHY(14);

  private final int value;

  NodeType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final NodeType fromOrdinal( int ordinal ) {
    for (NodeType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
