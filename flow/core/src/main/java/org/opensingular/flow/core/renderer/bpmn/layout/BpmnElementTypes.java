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
package org.opensingular.flow.core.renderer.bpmn.layout;

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.Graph;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.Node;

/**
 * BPMN element types that are relevant for the {@link BpmnLayout}.
 */
public class BpmnElementTypes {
  /**
   * Returns true if the specified type is different to {@link NodeType#INVALID}.
   * @param type The type to check.
   * @return {@code true} if the specified type is valid.
   */
  public static final boolean isValidNodeType( NodeType type ) {
    return type != NodeType.INVALID;
  }

  /**
   * Returns true if the specified type is {@link NodeType#INVALID}.
   * @param type The type to check.
   * @return {@code true} if the specified type is invalid.
   */
  public static final boolean isInvalidNodeType( NodeType type ) {
    return type == NodeType.INVALID;
  }

  /**
   * Returns true if the specified type is different to {@link EdgeType#INVALID}.
   * @param type The type to check.
   * @return {@code true} if the specified type is valid.
   */
  public static final boolean isValidEdgeType( EdgeType type ) {
    return type != EdgeType.INVALID;
  }

  /**
   * Returns true if the specified type is {@link EdgeType#INVALID}.
   * @param type The type to check.
   * @return {@code true} if the specified type is invalid.
   */
  public static final boolean isInvalidEdgeType( EdgeType type ) {
    return type == EdgeType.INVALID;
  }

  /**
   * Returns true if the specified type is {@link NodeType#START_EVENT}.
   * @param type The type to check.
   * @return {@code true} if the specified type is a start event.
   */
  public static final boolean isStartNode( NodeType type ) {
    return type == NodeType.START_EVENT;
  }

  /**
   * Returns true if the specified type is {@link NodeType#END_EVENT}.
   * @param type The type to check.
   * @return {@code true} if the specified type is an end event.
   */
  public static final boolean isEndNode( NodeType type ) {
    return type == NodeType.END_EVENT;
  }

  /**
   * Returns true if the specified type is {@link NodeType#TASK} or {@link NodeType#SUB_PROCESS}.
   * @param type The type to check.
   * @return {@code true} if the specified type is a task or sub process.
   */
  public static final boolean isActivityNode( NodeType type ) {
    return (type == NodeType.TASK) || (type == NodeType.SUB_PROCESS);
  }

  /**
   * Returns true if the specified type is {@link NodeType#TASK}.
   * @param type The type to check.
   * @return {@code true} if the specified type is a task.
   */
  public static final boolean isTask( NodeType type ) {
    return (type == NodeType.TASK);
  }

  /**
   * Returns true if the specified type is {@link NodeType#SUB_PROCESS}.
   * @param type The type to check.
   * @return {@code true} if the specified type is a sub process.
   */
  public static final boolean isSubProcess( NodeType type ) {
    return (type == NodeType.SUB_PROCESS);
  }

  /**
   * Returns true if the specified type is {@link NodeType#GATEWAY}
   * @param type The type to check.
   * @return {@code true} if the specified type is a gateway.
   */
  public static final boolean isGatewayNode( NodeType type ) {
    return (type == NodeType.GATEWAY);
  }

  /**
   * Returns true if the specified type is {@link NodeType#START_EVENT} , {@link NodeType#EVENT} or {@link NodeType#END_EVENT}.
   * @param type The type to check.
   * @return {@code true} if the specified type is an event.
   */
  public static final boolean isEventNode( NodeType type ) {
    return (type == NodeType.START_EVENT) || (type == NodeType.EVENT) || (type == NodeType.END_EVENT);
  }

  /**
   * Returns true if the specified type is {@link NodeType#ARTIFACT} or {@link NodeType#ANNOTATION}.
   * @param type The type to check.
   * @return {@code true} if the specified type is an artifact or annotation.
   */
  public static final boolean isArtifact( NodeType type ) {
    return (type == NodeType.ARTIFACT) || (type == NodeType.ANNOTATION);
  }

  /**
   * Returns true if the specified type is {@link EdgeType#SEQUENCE_FLOW}.
   * @param type The type to check.
   * @return {@code true} if the specified type is a sequence flow.
   */
  public static final boolean isSequenceFlow( EdgeType type ) {
    return type == EdgeType.SEQUENCE_FLOW;
  }

  /**
   * Returns true if the specified type is {@link EdgeType#MESSAGE_FLOW}.
   * @param type The type to check.
   * @return {@code true} if the specified type is a message flow.
   */
  public static final boolean isMessageFlow( EdgeType type ) {
    return type == EdgeType.MESSAGE_FLOW;
  }

  /**
   * Returns true if the specified type is {@link EdgeType#ASSOCIATION}.
   * @param type The type to check.
   * @return {@code true} if the specified type is an association.
   */
  public static final boolean isAssociation( EdgeType type ) {
    return type == EdgeType.ASSOCIATION;
  }

  /**
   * Returns true if the specified type is {@link EdgeType#CONVERSATION_LINK}.
   * @param type The type to check.
   * @return {@code true} if the specified type is a conversation link.
   */
  public static final boolean isConversationLink( EdgeType type ) {
    return type == EdgeType.CONVERSATION_LINK;
  }

  /**
   * Returns the BPMN type of the specified node.
   * <p>
   * The type is retrieved from the {@link IDataProvider IDataProvider} that is returned by the specified graph using the
   * data provider key {@link BpmnLayout#BPMN_NODE_TYPE_DP_KEY} .
   * </p>
   * @param n The node to lookup the BPMN type for.
   * @param graph The graph containing the node.
   * @return The BPMN type of the specified node as returned by the {@code DataProvider} or {@link NodeType#INVALID} if no provider
   * can be found.
   */
  public static final NodeType getType( Node n, Graph graph ) {
    IDataProvider node2Type = graph.getDataProvider(BpmnLayout.BPMN_NODE_TYPE_DP_KEY);
    if (node2Type == null || node2Type.get(n) == null) {
      return NodeType.INVALID;
    } else {
      return (NodeType) node2Type.get(n);
    }
  }

  /**
   * Returns the BPMN type of the specified edge.
   * <p>
   * The type is retrieved from the {@link IDataProvider IDataProvider} that is returned by the specified graph using the
   * data provider key {@link BpmnLayout#BPMN_EDGE_TYPE_DP_KEY} .
   * </p>
   * @param e The edge to lookup the BPMN type for.
   * @param graph The graph containing the edge.
   * @return The BPMN type of the specified edge as returned by the {@code DataProvider} or {@link EdgeType#INVALID} if no provider
   * can be found.
   */
  public static final EdgeType getType( Edge e, Graph graph ) {
    IDataProvider edge2Type = graph.getDataProvider(BpmnLayout.BPMN_EDGE_TYPE_DP_KEY);
    if (edge2Type == null || edge2Type.get(e) == null ) {
      return EdgeType.INVALID;
    } else {
      return (EdgeType) edge2Type.get(e);
    }
  }

  private BpmnElementTypes() {
  }
}
