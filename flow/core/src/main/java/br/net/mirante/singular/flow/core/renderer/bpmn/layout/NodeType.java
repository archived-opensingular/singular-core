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
package br.net.mirante.singular.flow.core.renderer.bpmn.layout;


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
