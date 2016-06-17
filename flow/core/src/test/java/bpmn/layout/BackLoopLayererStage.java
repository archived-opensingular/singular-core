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

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.INodeCursor;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.NodeList;
import com.yworks.yfiles.layout.hierarchic.EdgeDataType;
import com.yworks.yfiles.layout.hierarchic.IEdgeData;
import com.yworks.yfiles.layout.hierarchic.ILayerer;
import com.yworks.yfiles.layout.hierarchic.ILayers;
import com.yworks.yfiles.layout.hierarchic.ILayoutDataProvider;
import com.yworks.yfiles.layout.LayoutGraph;
import java.util.ArrayList;

/**
 * A layerer stage that pulls back loop components to earlier layers to reduce the spanned layers of back edges.
 * <p>
 * A back loop component is a set of connected nodes satisfying the following rules:
 * 
 * <ul>
 * <li>the set contains no sink node, i.e. no node with out degree 0</li>
 * <li>all outgoing edges to nodes outside of this set are back edges.</li>
 * </ul>
 * </p>
 */
class BackLoopLayererStage implements ILayerer {
  /**
   * The data provider key used to look up edges that shall be ignored by this stage.
   */
  public static final String EDGES_TO_IGNORE_DP_KEY = "com.yworks.yfiles.bpmn.layout.BackLoopLayererStage.EDGES_TO_IGNORE_DPKEY";

  private static final int STATUS_FIXED = 1;

  private static final int STATUS_BACK_LOOPING = 2;

  private static final int STATUS_BACK_LOOPING_CANDIDATE = 3;

  private final ILayerer coreLayerer;

  private BackLoopLayererMode mode = BackLoopLayererMode.REVERSING_EDGES;

  /**
   * The working mode of this stage.
   * <p>
   * Specifies the working mode of this stage. Default setting is {@link BackLoopLayererMode#REVERSING_EDGES} .
   * </p>
   * @return The LayererMode.
   * @see BackLoopLayererMode#REVERSING_EDGES
   * @see BackLoopLayererMode#REASSIGNING_LAYERS
   * @see #setLayererMode(BackLoopLayererMode)
   */
  public BackLoopLayererMode getLayererMode() {
    return this.mode;
  }

  /**
   * The working mode of this stage.
   * <p>
   * Specifies the working mode of this stage. Default setting is {@link BackLoopLayererMode#REVERSING_EDGES} .
   * </p>
   * @param value The LayererMode to set.
   * @see BackLoopLayererMode#REVERSING_EDGES
   * @see BackLoopLayererMode#REASSIGNING_LAYERS
   * @see #getLayererMode()
   */
  public void setLayererMode( BackLoopLayererMode value ) {
    this.mode = value;
  }

  /**
   * Creates a new instance with the specified core layerer.
   * @param coreLayerer The core layerer used for the initial layering.
   */
  public BackLoopLayererStage( ILayerer coreLayerer ) {
    this.coreLayerer = coreLayerer;
  }

  public void assignLayers( LayoutGraph graph, ILayers layers, ILayoutDataProvider ldp ) {
    // get core layer assignment
    this.coreLayerer.assignLayers(graph, layers, ldp);
    IDataProvider edgesToIgnore = graph.getDataProvider(EDGES_TO_IGNORE_DP_KEY);
    // determine current layer of all nodes
    int[] currentLayers = new int[graph.nodeCount()];
    for (int i = 0; i < layers.size(); i++) {
      for (INodeCursor nc = layers.getLayer(i).getList().nodes(); nc.ok(); nc.next()) {
        currentLayers[nc.node().index()] = i;
      }
    }
    // mark nodes on a back-loop and candidates that may be on a back loop if other back-loop nodes are reassigned
    int[] status = new int[graph.nodeCount()];
    NodeList candidates = new NodeList();
    NodeList backLoopNodes = new NodeList();

    {
      for (int i = layers.size() - 1; i >= 0; i--) {
        // check from last to first layer to detect candidates as well
        NodeList nodes = layers.getLayer(i).getList();
        for (INodeCursor nc = nodes.nodes(); nc.ok(); nc.next()) {
          Node node = nc.node();
          int nodeStatus = this.getStatus(node, currentLayers, status, edgesToIgnore, ldp);
          if (nodeStatus == STATUS_BACK_LOOPING) {
            backLoopNodes.addFirst(node);
          } else {
            if (nodeStatus == STATUS_BACK_LOOPING_CANDIDATE) {
              candidates.addFirst(node);
            }
          }
          status[node.index()] = nodeStatus;
        }
      }
      // store reversedEdges only for MODE_REVERSING_EDGES
      ArrayList<Edge> reversedEdges = this.mode == BackLoopLayererMode.REVERSING_EDGES ? new ArrayList<>() : null;
      // swap layer for back-loop nodes
      while (backLoopNodes.size() > 0) {
        for (INodeCursor nc = backLoopNodes.nodes(); nc.ok(); nc.next()) {
          Node node = nc.node();
          int currentLayer = currentLayers[node.index()];
          // the target layer is the next layer after the highest fixed target node layer
          int targetLayer = 0;
          for (Edge edge = node.firstOutEdge(); edge != null; edge = edge.nextOutEdge()) {
            if (this.ignoreEdge(edge, edgesToIgnore, ldp)) {
              continue;
            }
            int targetNodeIndex = edge.target().index();
            if (status[targetNodeIndex] == STATUS_FIXED) {
              targetLayer = Math.max(targetLayer, currentLayers[targetNodeIndex] + 1);
            }
          }
          if (targetLayer == 0) {
            // no fixed target found, so all targets must be candidates
            // -> we skip the node as we don't know where the candidates will be placed at the end
            continue;
          }
          if (targetLayer < currentLayer) {
            if (this.mode == BackLoopLayererMode.REVERSING_EDGES) {
              this.reverseEdges(node, targetLayer, currentLayers, graph, reversedEdges, edgesToIgnore, ldp);
            } else {
              layers.getLayer(currentLayer).remove(node);
              layers.getLayer(targetLayer).add(node);
            }
            currentLayers[node.index()] = targetLayer;
            status[node.index()] = STATUS_FIXED;
          }
        }
        backLoopNodes.clear();
        // update states of the candidates
        NodeList newCandidates = new NodeList();

        {
          for (INodeCursor nc = candidates.nodes(); nc.ok(); nc.next()) {
            Node node = nc.node();
            int newStatus = this.getStatus(node, currentLayers, status, edgesToIgnore, ldp);
            if (newStatus == STATUS_BACK_LOOPING) {
              backLoopNodes.add(node);
            } else {
              if (newStatus == STATUS_BACK_LOOPING_CANDIDATE) {
                newCandidates.add(node);
              }
            }
            status[node.index()] = newStatus;
          }
          candidates = newCandidates;
        }
      }
      if (this.mode == BackLoopLayererMode.REVERSING_EDGES) {
        // clear layers and rerun core layerer
        for (int i = layers.size() - 1; i >= 0; i--) {
          layers.remove(i);
        }
        this.coreLayerer.assignLayers(graph, layers, ldp);

        {
          // re-reverse edge again
          for (int i = 0; i < reversedEdges.size(); i++) {
            graph.reverseEdge(reversedEdges.get(i));
          }
        }
      } else {
        // remove empty layers
        for (int i = layers.size() - 1; i >= 0; i--) {
          if (layers.getLayer(i).getList().size() == 0) {
            layers.remove(i);
          }
        }
      }
    }
  }

  private void reverseEdges( Node node, int nodeTargetLayer, int[] currentLayers, LayoutGraph graph, ArrayList<Edge> reversedEdges, IDataProvider edgesToIgnore, ILayoutDataProvider ldp ) {
    // remember the initial size of the reversed edges list to only reverse edges added to this list in this method call
    int firstNewIndex = reversedEdges.size();
    for (Edge edge = node.firstOutEdge(); edge != null; edge = edge.nextOutEdge()) {
      if (this.ignoreEdge(edge, edgesToIgnore, ldp)) {
        continue;
      }
      int edgeTargetLayer = currentLayers[edge.target().index()];
      if (edgeTargetLayer < nodeTargetLayer) {
        reversedEdges.add(edge);
      }
    }

    {
      for (Edge edge = node.firstInEdge(); edge != null; edge = edge.nextInEdge()) {
        if (this.ignoreEdge(edge, edgesToIgnore, ldp)) {
          continue;
        }
        int edgeSourceLayer = currentLayers[edge.source().index()];
        if (edgeSourceLayer > nodeTargetLayer) {
          reversedEdges.add(edge);
        }
      }
      for (int i = firstNewIndex; i < reversedEdges.size(); i++) {
        graph.reverseEdge(reversedEdges.get(i));
      }
    }
  }

  private int getStatus( Node node, int[] currentLayers, int[] status, IDataProvider edgesToIgnore, ILayoutDataProvider ldp ) {
    int nodeLayer = currentLayers[node.index()];
    if (nodeLayer == 0) {
      // nodes in the first layer can't have any back edges
      return STATUS_FIXED;
    }
    int nodeStatus = STATUS_FIXED;
    for (Edge edge = node.firstOutEdge(); edge != null; edge = edge.nextOutEdge()) {
      if (this.ignoreEdge(edge, edgesToIgnore, ldp)) {
        continue;
      }
      int targetIndex = edge.target().index();
      if (currentLayers[targetIndex] >= nodeLayer) {
        // no back-looping edge...
        if (status[targetIndex] == STATUS_BACK_LOOPING || status[targetIndex] == STATUS_BACK_LOOPING_CANDIDATE) {
          // ...but target is back-looping, so this one might be as well
          nodeStatus = STATUS_BACK_LOOPING_CANDIDATE;
        } else {
          // ... and target is fixed -> this node is fixed as well.
          nodeStatus = STATUS_FIXED;
          break;
        }
      } else {
        if (nodeStatus == STATUS_FIXED) {
          // no back looping candidate -> back-looping
          nodeStatus = STATUS_BACK_LOOPING;
        }
      }
    }
    return nodeStatus;
  }

  /**
   * Returns whether an edge is a redirected group edge or explicitly marked to be ignored.
   */
  private boolean ignoreEdge( Edge e, IDataProvider edgesToIgnore, ILayoutDataProvider ldp ) {
    IEdgeData edgeData = ldp.getEdgeData(e);
    boolean isRedirectedGroupEdge = edgeData != null && edgeData.getType() == EdgeDataType.REDIRECTED_GROUP_EDGE;
    boolean markedToIgnore = edgesToIgnore != null && edgesToIgnore.getBool(e);
    return isRedirectedGroupEdge || markedToIgnore;
  }
}
