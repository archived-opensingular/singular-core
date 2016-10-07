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

import com.yworks.yfiles.algorithms.Bfs;
import com.yworks.yfiles.algorithms.Cycles;
import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.EdgeList;
import com.yworks.yfiles.algorithms.Graph;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.IEdgeCursor;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.INodeCursor;
import com.yworks.yfiles.algorithms.INodeMap;
import com.yworks.yfiles.algorithms.LayoutGraphHider;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.NodeList;
import com.yworks.yfiles.algorithms.RankAssignments;
import com.yworks.yfiles.layout.hierarchic.EdgeDataType;
import com.yworks.yfiles.layout.hierarchic.IEdgeData;
import com.yworks.yfiles.layout.hierarchic.ILayerer;
import com.yworks.yfiles.layout.hierarchic.ILayers;
import com.yworks.yfiles.layout.hierarchic.ILayoutDataProvider;
import com.yworks.yfiles.layout.hierarchic.LayerType;
import com.yworks.yfiles.layout.LayoutGraph;

/**
 * Customized layering for BPMN graphs that considers the BPMN types of nodes and edge.
 */
class BpmnLayerer implements ILayerer {
  private static final int WEIGHT_SEQUENCE_FLOW = 3;

  private static final int WEIGHT_SEQUENCE_FLOW_IN_SUBPROCESS = 5;

  private static final int WEIGHT_MESSAGE_FLOW = 3;

  private static final int WEIGHT_ASSOCIATION = 2;

  private static final int MIN_LENGTH_MESSAGE_FLOW = 0;

  private static final int MIN_LENGTH_SEQUENCE_FLOW = 1;

  private static final int MIN_LENGTH_ASSOCIATION = 0;

  private static final double CYCLE_WEIGHT_BACK_EDGE = 1.0;

  private static final double CYCLE_WEIGHT_NON_BACK_EDGE = 5.0;

  private boolean assignStartNodesToLeftOrTopmostPosition = false;

  private boolean compactMessageFlowLayeringEnabled = false;

  /**
   * Determines whether or not start node are pulled to the leftmost or topmost layer.
   * <p>
   * Default value is {@code false}.
   * </p>
   * @return The AssignStartNodesToLeftOrTopmostPosition.
   * @see #setAssignStartNodesToLeftOrTopmostPosition(boolean)
   */
  public boolean isAssignStartNodesToLeftOrTopmostPosition() {
    return this.assignStartNodesToLeftOrTopmostPosition;
  }

  /**
   * Determines whether or not start node are pulled to the leftmost or topmost layer.
   * <p>
   * Default value is {@code false}.
   * </p>
   * @param value The AssignStartNodesToLeftOrTopmostPosition to set.
   * @see #isAssignStartNodesToLeftOrTopmostPosition()
   */
  public void setAssignStartNodesToLeftOrTopmostPosition( boolean value ) {
    this.assignStartNodesToLeftOrTopmostPosition = value;
  }

  /**
   * Determines whether or not message flows have only weak impact on the layering.
   * <p>
   * Having weak impact, message flows are more likely to be back edges. This often results in more compact layouts.
   * </p>
   * <p>
   * Default value is {@code false}.
   * </p>
   * @return The CompactMessageFlowLayering.
   * @see #setCompactMessageFlowLayering(boolean)
   */
  public boolean isCompactMessageFlowLayering() {
    return this.compactMessageFlowLayeringEnabled;
  }

  /**
   * Determines whether or not message flows have only weak impact on the layering.
   * <p>
   * Having weak impact, message flows are more likely to be back edges. This often results in more compact layouts.
   * </p>
   * <p>
   * Default value is {@code false}.
   * </p>
   * @param value The CompactMessageFlowLayering to set.
   * @see #isCompactMessageFlowLayering()
   */
  public void setCompactMessageFlowLayering( boolean value ) {
    this.compactMessageFlowLayeringEnabled = value;
  }

  /**
   * Makes the specified graph acyclic (ignoring BPMN Associations) by reversing cycle edges.
   */
  private void makeAcyclic( LayoutGraph g, EdgeList reversedEdges ) {
    reversedEdges.clear();
    //we ignore edges of type association
    LayoutGraphHider hider = new LayoutGraphHider(g);
    for (IEdgeCursor ec = g.getEdgeCursor(); ec.ok(); ec.next()) {
      Edge e = ec.edge();
      if (BpmnElementTypes.isAssociation(this.getType(e, g))) {
        hider.hide(e);
      }
    }
    // try to identify back edges using Bfs layering and assign lower weights to them
    IEdgeMap edge2Weight = g.createEdgeMap();
    NodeList coreNodes = new NodeList();
    for (INodeCursor nc = g.getNodeCursor(); nc.ok(); nc.next()) {
      Node n = nc.node();
      if (this.isStartNode(n, g)) {
        coreNodes.add(n);
      }
    }
    INodeMap node2Depth = g.createNodeMap();
    Bfs.getLayers(g, coreNodes, true, node2Depth);

    {
      for (IEdgeCursor ec = g.getEdgeCursor(); ec.ok(); ec.next()) {
        Edge e = ec.edge();
        if (node2Depth.getInt(e.source()) > node2Depth.getInt(e.target())) {
          //likely to be a back edge
          edge2Weight.setDouble(e, CYCLE_WEIGHT_BACK_EDGE);
        } else {
          edge2Weight.setDouble(e, CYCLE_WEIGHT_NON_BACK_EDGE);
        }
      }
      g.disposeNodeMap(node2Depth);
      //find and remove cycles
      IEdgeMap cyclingEdges = g.createEdgeMap();
      Cycles.findCycleEdges(g, cyclingEdges, edge2Weight);
      for (IEdgeCursor ec = g.getEdgeCursor(); ec.ok(); ec.next()) {
        Edge e = ec.edge();
        if (cyclingEdges.getBool(e)) {
          g.reverseEdge(e);
          reversedEdges.add(e);
        }
      }
      g.disposeEdgeMap(cyclingEdges);
      g.disposeEdgeMap(edge2Weight);
      hider.unhideAll();
    }
  }

  private boolean isStartNode( Node n, LayoutGraph g ) {
    IDataProvider startNodeDP = g.getDataProvider(BpmnLayout.IS_START_NODE_DP_KEY);
    return startNodeDP != null && startNodeDP.getBool(n);
  }

  private EdgeType getType( Edge e, LayoutGraph g ) {
    //special handling if constraint incremental layerer calls this layerer, we use the original edge for the specified one
    IDataProvider edge2OrigEdge = g.getDataProvider("y.layout.hierarchic.incremental.ConstraintIncrementalLayerer.ORIG_EDGES");
    Edge originalEdge = edge2OrigEdge != null ? (Edge)edge2OrigEdge.get(e) : null;
    Edge realEdge = originalEdge != null ? originalEdge : e;
    LayoutGraph realGraph = originalEdge != null ? (LayoutGraph)originalEdge.getGraph() : g;
    EdgeType type = BpmnElementTypes.getType(realEdge, realGraph);
    if (BpmnElementTypes.isValidEdgeType(type)) {
      // If the edge has a valid type assigned, it is returned ...
      return type;
    } else {
      // ... otherwise we check if it is an association connected to an artifact node...
      if (BpmnElementTypes.isArtifact(BpmnElementTypes.getType(realEdge.source(), g)) || BpmnElementTypes.isArtifact(BpmnElementTypes.getType(realEdge.target(), g))) {
        return EdgeType.ASSOCIATION;
      } else {
        // ... and if this isn't the case we just treat it as a normal sequence flow.
        return EdgeType.SEQUENCE_FLOW;
      }
    }
  }

  public void assignLayers( LayoutGraph g, ILayers layers, ILayoutDataProvider ldp ) {
    //remove cycles
    EdgeList reversedEdges = new EdgeList();
    this.makeAcyclic(g, reversedEdges);
    // For the later RankAssignment, we first assign weights/min length to edges and add some weak same layer constraints
    IEdgeMap weight = g.createEdgeMap();
    IEdgeMap minLength = g.createEdgeMap();
    LayoutGraphHider hider = new LayoutGraphHider(g);
    Edge[] edges = g.getEdgeArray();
    NodeList dummies = new NodeList();
    for (int i = 0; i < edges.length; i++) {
      Edge e = edges[i];
      EdgeType eType = this.getType(e, g);
      IEdgeData edgeData = ldp.getEdgeData(e);
      if (this.isRedirectedEdgeToInnerArtifact(e, edgeData, g)) {
        // e is an edge to an artifact inside a group node. We don't want the artifact to be pulled to the group
        // start or end but to be close to the node it annotates, so we hide this edge
        hider.hide(e);
      } else {
        if (edgeData.getType() == EdgeDataType.REDIRECTED_GROUP_EDGE && (edgeData.getSourcePortConstraint() != null || edgeData.getTargetPortConstraint() != null)) {
          // again e is a dummy edge representing an edge at a group node.
          // As this edge has a port constraint we want the target to be next to the group node
          this.createWeakSameLayerConstraint(e, g, weight, minLength, dummies);
          hider.hide(e);
        } else {
          if (BpmnElementTypes.isAssociation(eType)) {
            // associated nodes shall be placed on the same or on close layers
            this.createWeakSameLayerConstraint(e, g, weight, minLength, dummies);
            hider.hide(e);
          } else {
            if (BpmnElementTypes.isMessageFlow(eType)) {
              if (this.compactMessageFlowLayeringEnabled) {
                // if message flows shall have only weak impact, we use weak same layer constraints so no new layers are induced
                this.createWeakSameLayerConstraint(e, g, weight, minLength, dummies);
                hider.hide(e);
              } else {
                weight.setInt(e, WEIGHT_MESSAGE_FLOW);
                minLength.setInt(e, MIN_LENGTH_MESSAGE_FLOW);
              }
            } else {
              if (this.isContainedInSubProcess(e, g, ldp)) {
                weight.setInt(e, WEIGHT_SEQUENCE_FLOW_IN_SUBPROCESS);
                minLength.setInt(e, MIN_LENGTH_SEQUENCE_FLOW);
              } else {
                weight.setInt(e, WEIGHT_SEQUENCE_FLOW);
                minLength.setInt(e, MIN_LENGTH_SEQUENCE_FLOW);
              }
            }
          }
        }
      }
    }
    //insert super root to guarantee that graph is connected
    Node superRoot = g.createNode();
    dummies.add(superRoot);
    for (INodeCursor nc = g.getNodeCursor(); nc.ok(); nc.next()) {
      Node n = nc.node();
      if (n != superRoot && n.inDegree() == 0) {
        int dummyEdgeWeight = (this.assignStartNodesToLeftOrTopmostPosition && this.isStartNode(n, g)) ? 100 : 0;
        Edge dummyEdge = g.createEdge(superRoot, n);
        weight.setInt(dummyEdge, dummyEdgeWeight);
        minLength.setInt(dummyEdge, 0);
      }
    }
    //assign layers
    INodeMap node2Layer = g.createNodeMap();
    RankAssignments.simplex(g, node2Layer, weight, minLength);

    {
      //undo graph transformation
      for (INodeCursor nc = dummies.nodes(); nc.ok(); nc.next()) {
        g.removeNode(nc.node());
      }
      hider.unhideAll();
      for (IEdgeCursor ec = reversedEdges.edges(); ec.ok(); ec.next()) {
        g.reverseEdge(ec.edge());
      }
      //build result data structure
      int layerCount = this.countLayers(g, node2Layer);
      for (int i = 0; i < layerCount; i++) {
        layers.insert(LayerType.NORMAL, i);
      }
      for (INodeCursor nc = g.getNodeCursor(); nc.ok(); nc.next()) {
        Node node = nc.node();
        int layer = node2Layer.getInt(node);
        layers.getLayer(layer).add(node);
      }
      //dispose
      g.disposeEdgeMap(weight);
      g.disposeEdgeMap(minLength);
      g.disposeNodeMap(node2Layer);
    }
  }

  /**
   * Returns if the edge is of type {@link EdgeDataType#REDIRECTED_GROUP_EDGE} and the node inside the group this edge was
   * redirected is an {@link BpmnElementTypes#isArtifact(NodeType) Artifact} node.
   */
  private boolean isRedirectedEdgeToInnerArtifact( Edge e, IEdgeData edgeData, LayoutGraph g ) {
    if (edgeData.getType() == EdgeDataType.REDIRECTED_GROUP_EDGE) {
      Edge originalEdge = edgeData.getAssociatedEdge();
      Node innerNode = originalEdge.source() == e.source() ? e.target() : e.source();
      NodeType innerNodeType = BpmnElementTypes.getType(innerNode, g);
      return BpmnElementTypes.isArtifact(innerNodeType);
    }
    return false;
  }

  /**
   * Creates a weak same layer constraint for the source and target node of an edge that is used by a later RankAssignment.
   * <p>
   * The constraint is modelled by a dummy node connected with two dummy edges to source and target. The weight and minimal
   * length of these dummy edges depends on the type of the original edge.
   * </p>
   */
  private void createWeakSameLayerConstraint( Edge e, LayoutGraph g, IEdgeMap weight, IEdgeMap minLength, NodeList dummies ) {
    EdgeType eType = this.getType(e, g);
    Node dummyNode = g.createNode();
    dummies.add(dummyNode);
    Edge dummyEdge1 = g.createEdge(e.source(), dummyNode);
    weight.setInt(dummyEdge1, BpmnLayerer.getWeight(eType));
    minLength.setInt(dummyEdge1, BpmnLayerer.getMinimalLength(eType));
    Edge dummyEdge2 = g.createEdge(e.target(), dummyNode);
    weight.setInt(dummyEdge2, BpmnLayerer.getWeight(eType));
    minLength.setInt(dummyEdge2, BpmnLayerer.getMinimalLength(eType));
  }

  private static int getWeight( EdgeType bpmnType ) {
    switch (bpmnType) {
      case ASSOCIATION:
        {
          return WEIGHT_ASSOCIATION;
        }

      case MESSAGE_FLOW:
        {
          return WEIGHT_MESSAGE_FLOW;
        }

      default:
        {
          return WEIGHT_SEQUENCE_FLOW;
        }
    }
  }

  private static int getMinimalLength( EdgeType bpmnType ) {
    switch (bpmnType) {
      case ASSOCIATION:
        {
          return MIN_LENGTH_ASSOCIATION;
        }

      case MESSAGE_FLOW:
        {
          return MIN_LENGTH_MESSAGE_FLOW;
        }

      default:
        {
          return MIN_LENGTH_SEQUENCE_FLOW;
        }
    }
  }

  private int countLayers( Graph g, INodeMap layer ) {
    int maxLayerIndex = 0;
    for (INodeCursor nc = g.getNodeCursor(); nc.ok(); nc.next()) {
      int currentLayer = layer.getInt(nc.node());
      maxLayerIndex = Math.max(maxLayerIndex, currentLayer);
    }
    return maxLayerIndex + 1;
  }

  /**
   * Returns if the source and target node of the specified edge are children of the same SubProcess group node.
   */
  private boolean isContainedInSubProcess( Edge e, LayoutGraph g, ILayoutDataProvider ldp ) {
    Node sourceParent = ldp.getNodeData(e.source()).getGroupNode();
    Node targetParent = ldp.getNodeData(e.target()).getGroupNode();
    if (sourceParent != null && targetParent != null && sourceParent == targetParent) {
      NodeType sourceParentType = BpmnElementTypes.getType(sourceParent, g);
      return BpmnElementTypes.isInvalidNodeType(sourceParentType) || BpmnElementTypes.isSubProcess(sourceParentType);
    }
    return false;
  }
}
