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
package bpmn.layout;


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
