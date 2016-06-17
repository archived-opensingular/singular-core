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

import com.yworks.yfiles.algorithms.DataProviderAdapter;
import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.EdgeList;
import com.yworks.yfiles.algorithms.Graph;
import com.yworks.yfiles.algorithms.GraphConnectivity;
import com.yworks.yfiles.algorithms.GraphPartitionManager;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.IEdgeCursor;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.INodeCursor;
import com.yworks.yfiles.algorithms.INodeMap;
import com.yworks.yfiles.algorithms.LayoutGraphHider;
import com.yworks.yfiles.algorithms.ListCell;
import com.yworks.yfiles.algorithms.Maps;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.NodeList;
import com.yworks.yfiles.algorithms.Paths;
import com.yworks.yfiles.algorithms.RankAssignments;
import com.yworks.yfiles.layout.hierarchic.IItemFactory;
import com.yworks.yfiles.layout.hierarchic.ILayers;
import com.yworks.yfiles.layout.hierarchic.ILayoutDataProvider;
import com.yworks.yfiles.layout.hierarchic.INodeData;
import com.yworks.yfiles.layout.hierarchic.IPortConstraintOptimizer;
import com.yworks.yfiles.layout.hierarchic.NodeDataType;
import com.yworks.yfiles.layout.hierarchic.SwimlaneDescriptor;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.PartitionGrid;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Calculates nodes that can be aligned in layout orientation and provides this information for the node placer.
 */
class NodeAlignmentPortOptimizer implements IPortConstraintOptimizer {
  private static final byte LANE_ALIGNMENT_LEFT = 0;

  private static final byte LANE_ALIGNMENT_RIGHT = 1;

  private static final int PRIORITY_LOW = 1;

  private static final int PRIORITY_BASIC = 3;

  private static final int PRIORITY_HIGH = 100;

  private IPortConstraintOptimizer coreOptimizer;

  private boolean horizontalOrientation;

  private LayoutGraph graph;

  /**
   * Creates a new instance.
   *
   * @param coreOptimizer An optimizer to delegate the port constraint optimization after the node alignment calculation
   * @param horizontalOrientation {@code true}, if the layout orientation is horizontal, {@code false} if it is vertical.
   */
  public NodeAlignmentPortOptimizer( IPortConstraintOptimizer coreOptimizer, boolean horizontalOrientation ) {
    this.coreOptimizer = coreOptimizer;
    this.horizontalOrientation = horizontalOrientation;
  }

  public void optimizeAfterLayering( LayoutGraph graph, ILayers layers, ILayoutDataProvider ldp, IItemFactory itemFactory ) {
    if (this.coreOptimizer != null) {
      this.coreOptimizer.optimizeAfterLayering(graph, layers, ldp, itemFactory);
    }
  }

  public void optimizeAfterSequencing( LayoutGraph graph, ILayers layers, ILayoutDataProvider ldp, IItemFactory itemFactory ) {
    this.graph = graph;
    // determine priorities which edges to align
    IEdgeMap edge2Priority = Maps.createHashedEdgeMap();
    this.determineEdgePriorities(graph, edge2Priority, ldp);
    // determine basic node alignment
    INodeMap node2LaneAlignment = Maps.createHashedNodeMap();
    for (INodeCursor nc = graph.getNodeCursor(); nc.ok(); nc.next()) {
      Node n = nc.node();
      byte alignment = this.getLaneAlignment(n, ldp);
      node2LaneAlignment.setInt(n, alignment);
    }
    //...for each column we align the corresponding nodes
    INodeMap node2AlignWith = Maps.createHashedNodeMap();
    graph.addDataProvider("y.layout.hierarchic.incremental.SimlexNodePlacer.NODE_TO_ALIGN_WITH", node2AlignWith);
    PartitionGrid grid = PartitionGrid.getPartitionGrid(graph);
    if (grid != null) {
      IDataProvider node2Component = new SwimLaneIdDataProviderAdapter(ldp);
      GraphPartitionManager columnPartitionManager = new GraphPartitionManager(graph, node2Component);
      columnPartitionManager.hideAll();
      for (int i = 0; i < grid.getColumns().size(); i++) {
        columnPartitionManager.displayPartition(i);
        if (graph.nodeCount() > 1) {
          this.alignNodes(graph, edge2Priority, node2AlignWith, node2LaneAlignment, ldp);
        }
      }
      columnPartitionManager.unhideAll();
    } else {
      // if we have no grid, all nodes are in the same 'column'
      this.alignNodes(graph, edge2Priority, node2AlignWith, node2LaneAlignment, ldp);
    }
    if (this.coreOptimizer != null) {
      this.coreOptimizer.optimizeAfterSequencing(graph, layers, ldp, itemFactory);
    }
  }

  /**
   * Determines the priorities of all edges to align their source and target nodes.
   */
  private void determineEdgePriorities( LayoutGraph graph, IEdgeMap edge2Priority, ILayoutDataProvider ldp ) {
    IEdgeMap edge2IsRelevant = Maps.createHashedEdgeMap();
    IEdgeMap edge2Length = Maps.createHashedEdgeMap();
    for (IEdgeCursor ec = graph.getEdgeCursor(); ec.ok(); ec.next()) {
      Edge e = ec.edge();
      edge2Priority.setInt(e, PRIORITY_BASIC);
      boolean isRelevant = this.canBeAligned(e.source(), e.target(), ldp);
      edge2IsRelevant.setBool(e, isRelevant);
    }
    // calculate the edge length, i.e. the importance of the edge (as we are looking for longest paths later)
    this.calculateEdgeLength(graph, edge2Length, ldp, edge2IsRelevant);
    //hide edges whose source and target node cannot be aligned
    LayoutGraphHider hider = new LayoutGraphHider(graph);

    {
      for (IEdgeCursor ec = graph.getEdgeCursor(); ec.ok(); ec.next()) {
        Edge e = ec.edge();
        if (!edge2IsRelevant.getBool(e)) {
          hider.hide(e);
        }
      }
      //for each connected component we iteratively find a longest path that is used as critical path and set a high
      // priority for edges on a critical path
      INodeMap node2CompId = Maps.createHashedNodeMap();
      int compCount = GraphConnectivity.connectedComponents(graph, node2CompId);
      GraphPartitionManager gpm = new GraphPartitionManager(graph, node2CompId);
      gpm.hideAll();
      for (int i = 0; i < compCount; i++) {
        gpm.displayPartition(i);
        LayoutGraphHider localHider = new LayoutGraphHider(graph);
        EdgeList path = Paths.findLongestPath(graph, edge2Length);
        while (!path.isEmpty()) {
          for (IEdgeCursor ec = path.edges(); ec.ok(); ec.next()) {
            edge2Priority.setInt(ec.edge(), PRIORITY_HIGH);
          }
          localHider.hide(Paths.constructNodePath(path));
          path = Paths.findLongestPath(graph, edge2Length);
        }
        localHider.unhideAll();
      }
      gpm.unhideAll();
      hider.unhideAll();
    }
  }

  /**
   * Determine the alignment of a node in its swim lane depending on the number of edges to nodes in other swim lanes to the
   * left or right.
   */
  private byte getLaneAlignment( Node n, ILayoutDataProvider ldp ) {
    int toLeftCount = 0;
    int toRightCount = 0;
    EdgeList nEdges = new EdgeList(n.getEdgeCursor());
    nEdges.splice(this.getSameLayerEdges(n, ldp));
    for (IEdgeCursor ec = nEdges.edges(); ec.ok(); ec.next()) {
      Edge e = ec.edge();
      if (NodeAlignmentPortOptimizer.toLeftPartition(n, e.opposite(n), ldp)) {
        toLeftCount++;
      } else {
        if (NodeAlignmentPortOptimizer.toRightPartition(n, e.opposite(n), ldp)) {
          toRightCount++;
        }
      }
    }
    if (toLeftCount > toRightCount) {
      return LANE_ALIGNMENT_LEFT;
    } else {
      if (toLeftCount < toRightCount) {
        return LANE_ALIGNMENT_RIGHT;
      } else {
        if (this.horizontalOrientation) {
          return LANE_ALIGNMENT_RIGHT;
        } else {
          return LANE_ALIGNMENT_LEFT;
        }
      }
    }
  }

  /**
   * Returns if the two nodes can be aligned with respect to the layout direction.
   * <p>
   * Node alignment is prohibited if the nodes are in different swim lanes or have different parent nodes.
   * </p>
   */
  private boolean canBeAligned( Node n1, Node n2, ILayoutDataProvider ldp ) {
    int laneId1 = NodeAlignmentPortOptimizer.getSwimLaneId(n1, ldp);
    int laneId2 = NodeAlignmentPortOptimizer.getSwimLaneId(n2, ldp);
    if (laneId1 != -1 && laneId1 != laneId2) {
      return false;
    }
    Node n1Group = ldp.getNodeData(n1).getGroupNode();
    Node n2Group = ldp.getNodeData(n2).getGroupNode();
    if (n1Group != null && n1Group != n2Group) {
      return false;
    }
    return true;
  }

  /**
   * Calculates artificial edge length values such that more important edge get bigger values.
   */
  private void calculateEdgeLength( LayoutGraph graph, IEdgeMap edge2Length, ILayoutDataProvider layoutData, IDataProvider edge2IsRelevant ) {
    int ZeroLength = 0;
    int BasicDummyEdgeLength = 1;
    int BasicEdgeLength = 3;
    int PenaltyLength = BasicEdgeLength + graph.nodeCount();
    int HighPenaltyLength = PenaltyLength * graph.nodeCount();
    //assign basic length
    for (IEdgeCursor ec = graph.getEdgeCursor(); ec.ok(); ec.next()) {
      Edge e = ec.edge();
      int length = NodeAlignmentPortOptimizer.isRealEdge(e, layoutData) ? BasicEdgeLength : BasicDummyEdgeLength;
      edge2Length.setInt(e, length);
    }
    NodeList specialNodes = new NodeList();
    for (INodeCursor nc = graph.getNodeCursor(); nc.ok(); nc.next()) {
      Node n = nc.node();
      if (n.degree() < 3) {
      } else {
        //nothing to do
        if (n.outDegree() == 2 && n.inDegree() == 2) {
          specialNodes.add(n);
        } else {
          //assign high length to inner edges (the two non-inner edges should be attached to the side ports)
          Edge lastEdge = null;
          IEdgeCursor ec = n.outDegree() > n.inDegree() ? n.getOutEdgeCursor() : n.getInEdgeCursor();
          for (; ec.ok(); ec.next()) {
            Edge e = ec.edge();
            if (lastEdge != null) {
              edge2Length.setInt(e, edge2Length.getInt(e) + PenaltyLength);
            }
            lastEdge = e;
          }
          edge2Length.setInt(lastEdge, edge2Length.getInt(lastEdge) - PenaltyLength);
        }
      }
    }

    {
      for (INodeCursor nc = specialNodes.nodes(); nc.ok(); nc.next()) {
        Node n = nc.node();
        //either firstIn, lastOut or lastIn, firstOut should be on a longest path
        Edge firstIn = n.firstInEdge();
        Edge lastOut = n.lastOutEdge();
        Edge lastIn = n.lastInEdge();
        Edge firstOut = n.firstOutEdge();
        if (!edge2IsRelevant.getBool(firstIn) || !edge2IsRelevant.getBool(lastOut)) {
          edge2Length.setInt(firstIn, ZeroLength);
          edge2Length.setInt(lastOut, ZeroLength);
        }
        if (!edge2IsRelevant.getBool(firstOut) || !edge2IsRelevant.getBool(lastIn)) {
          edge2Length.setInt(firstOut, ZeroLength);
          edge2Length.setInt(lastIn, ZeroLength);
        }
        if (edge2Length.getInt(firstIn) + edge2Length.getInt(lastOut) > edge2Length.getInt(lastIn) + edge2Length.getInt(firstOut)) {
          edge2Length.setInt(firstIn, edge2Length.getInt(firstIn) + HighPenaltyLength);
          edge2Length.setInt(lastOut, edge2Length.getInt(lastOut) + HighPenaltyLength);
        } else {
          edge2Length.setInt(lastIn, edge2Length.getInt(lastIn) + HighPenaltyLength);
          edge2Length.setInt(firstOut, edge2Length.getInt(firstOut) + HighPenaltyLength);
        }
      }
    }
  }

  /**
   * Determines nodes in the specified subgraph that shall be aligned.
   * <p>
   * If a node A shall be aligned with node B on a smaller layer, B is set as value of A in the map node2AlignWith.
   * </p>
   */
  private void alignNodes( LayoutGraph subgraph, IDataProvider edge2Priority, INodeMap node2AlignWith, IDataProvider node2LaneAlignment, ILayoutDataProvider ldp ) {
    IEdgeMap edge2MinLength = Maps.createHashedEdgeMap();
    IEdgeMap edge2Weight = Maps.createHashedEdgeMap();
    INodeMap node2NetworkRep = Maps.createHashedNodeMap();
    Graph network = new Graph();
    // create network nodes:
    // - one for each non-group-bounds node,
    // - one for all group begin nodes of the same group
    // - one for all group end nodes of the same group
    this.addNetworkNodes(subgraph, network, node2NetworkRep, ldp);
    // create alignment network edges (two edges from source/target to a dummy node) for each edge that is:
    // - no same-layer edge
    // - no self-loop
    // - not between two group nodes
    this.addNetworkEdges(subgraph, network, node2NetworkRep, edge2Priority, edge2MinLength, edge2Weight, ldp);
    // create a network edge for each edges that is:
    // - a same-layer edge
    // - no self-loop
    // - not between two group nodes
    // the direction of the network edge is from the lesser to higher node position
    this.addNetworkEdgesForSameLayerEdges(subgraph, network, node2NetworkRep, edge2MinLength, edge2Weight, ldp);
    // create a network edge between each two succeeding same layer nodes
    this.addNetworkEdgesForSameLayerNodes(subgraph, network, node2NetworkRep, edge2MinLength, edge2Weight, ldp);
    // connect all nodes to a global source and a global sink node
    this.addGlobalNetworkFlow(subgraph, network, node2NetworkRep, node2LaneAlignment, edge2MinLength, edge2Weight);
    // apply simplex to each connected component of the network
    INodeMap networkNode2AlignmentLayer = Maps.createHashedNodeMap();
    RankAssignments.simplex(network, networkNode2AlignmentLayer, edge2Weight, edge2MinLength);
    //transfer results to original nodes
    INodeMap node2AlignmentLayer = Maps.createHashedNodeMap();
    for (INodeCursor nc = subgraph.getNodeCursor(); nc.ok(); nc.next()) {
      Node n = nc.node();
      Node nRep = (Node)node2NetworkRep.get(n);
      node2AlignmentLayer.setDouble(n, networkNode2AlignmentLayer.getInt(nRep));
    }
    //we do not want to align bend nodes with common nodes except if the (chain of) dummy nodes can be aligned with the corresponding common node
    INodeMap seenBendMap = Maps.createHashedNodeMap();

    {
      for (INodeCursor nc = subgraph.getNodeCursor(); nc.ok(); nc.next()) {
        Node n = nc.node();
        if (NodeAlignmentPortOptimizer.isBendNode(n, ldp) && !seenBendMap.getBool(n)) {
          this.adjustBendAlignmentLayer(n, node2AlignmentLayer, seenBendMap, ldp);
        }
      }
      //add alignment constraints
      Node[] nodes = subgraph.getNodeArray();
      Arrays.sort(nodes, new AlignedNodePositionComparator(ldp, node2AlignmentLayer));
      Node last = null;
      for (int i = 0; i < nodes.length; i++) {
        Node n = nodes[i];
        if (!NodeAlignmentPortOptimizer.isGroupNodeBorder(n, ldp) && !NodeAlignmentPortOptimizer.isGroupNodeProxy(n, ldp)) {
          if (last != null && node2AlignmentLayer.getDouble(last) == node2AlignmentLayer.getDouble(n)) {
            //node n should be aligned with last
            node2AlignWith.set(n, last);
          }
          last = n;
        }
      }
    }
  }

  /**
   * Create alignment network edges (two edges from source/target to a dummy node) for each edge that is:.
   * 
   * <ul>
   * <li>no same-layer edge</li>
   * <li>no self-loop</li>
   * <li>not between two group nodes</li>
   * </ul>
   */
  private void addNetworkEdges( LayoutGraph subgraph, Graph network, INodeMap node2NetworkRep, IDataProvider edge2Priority, IEdgeMap edge2MinLength, IEdgeMap edge2Weight, ILayoutDataProvider ldp ) {
    // subgraph only contains edges between nodes on different layers
    for (IEdgeCursor ec = subgraph.getEdgeCursor(); ec.ok(); ec.next()) {
      Edge e = ec.edge();
      // ignore self loops and edges between group nodes sides
      if (!e.isSelfLoop() && (!NodeAlignmentPortOptimizer.isGroupNodeBorder(e.source(), ldp) || !NodeAlignmentPortOptimizer.isGroupNodeBorder(e.target(), ldp))) {
        Node absNode = network.createNode();
        int priority = edge2Priority.getInt(e);
        this.addNetworkEdge((Node)node2NetworkRep.get(e.source()), absNode, network, priority, edge2Weight, 0, edge2MinLength);
        this.addNetworkEdge((Node)node2NetworkRep.get(e.target()), absNode, network, priority, edge2Weight, 0, edge2MinLength);
      }
    }
  }

  /**
   * Create a network edge for each edges that is:
   * <ul>
   * <li>a same-layer edge</li>
   * <li>no self-loop</li>
   * <li>not between two group nodes</li>
   * </ul>
   * The direction of the network edge is from the lesser to higher node position.
   */
  private void addNetworkEdgesForSameLayerEdges( LayoutGraph subgraph, Graph network, INodeMap node2NetworkRep, IEdgeMap edge2MinLength, IEdgeMap edge2Weight, ILayoutDataProvider ldp ) {
    for (IEdgeCursor ec = NodeAlignmentPortOptimizer.getSameLayerEdges(subgraph, ldp).edges(); ec.ok(); ec.next()) {
      Edge e = ec.edge();
      // ignore self loops and edges between group nodes sides
      if (!e.isSelfLoop() && (!NodeAlignmentPortOptimizer.isGroupNodeBorder(e.source(), ldp) || !NodeAlignmentPortOptimizer.isGroupNodeBorder(e.target(), ldp))) {
        boolean sourceToTarget = ldp.getNodeData(e.source()).getPosition() < ldp.getNodeData(e.target()).getPosition();
        Node source = (Node)node2NetworkRep.get(sourceToTarget ? e.source() : e.target());
        Node target = (Node)node2NetworkRep.get(sourceToTarget ? e.target() : e.source());
        this.addNetworkEdge(source, target, network, PRIORITY_BASIC, edge2Weight, 1, edge2MinLength);
      }
    }
  }

  /**
   * Create a network edge between each two succeeding same layer nodes.
   */
  private void addNetworkEdgesForSameLayerNodes( LayoutGraph subgraph, Graph network, INodeMap node2NetworkRep, IEdgeMap edge2MinLength, IEdgeMap edge2Weight, ILayoutDataProvider ldp ) {
    Node[] nodes = subgraph.getNodeArray();
    // sort nodes by ascending layer index and position
    Arrays.sort(nodes, new NodePositionComparator(ldp));
    Node last = null;
    for (int i = 0; i < nodes.length; i++) {
      Node n = nodes[i];
      if (last != null && NodeAlignmentPortOptimizer.areInSameLayer(last, n, ldp)) {
        Node nRep = (Node)node2NetworkRep.get(n);
        Node lastRep = (Node)node2NetworkRep.get(last);
        if (!network.containsEdge(lastRep, nRep)) {
          //guarantees that last is placed to the left of n
          int length = NodeAlignmentPortOptimizer.calculateMinLength(last, n, ldp);
          this.addNetworkEdge(lastRep, nRep, network, 0, edge2Weight, length, edge2MinLength);
        }
      }
      last = n;
    }
  }

  /**
   * Create network nodes:.
   * 
   * <ul>
   * <li>one for each non-group-bounds node</li>
   * <li>one for all group begin nodes of the same group</li>
   * <li>one for all group end nodes of the same group</li>
   * </ul>
   */
  private void addNetworkNodes( LayoutGraph subgraph, Graph network, INodeMap node2NetworkRep, ILayoutDataProvider ldp ) {
    INodeMap groupNode2BeginRep = Maps.createHashedNodeMap();
    INodeMap groupNode2EndRep = Maps.createHashedNodeMap();
    for (INodeCursor nc = subgraph.getNodeCursor(); nc.ok(); nc.next()) {
      Node n = nc.node();
      INodeData data = ldp.getNodeData(n);
      Node nRep;
      if (data != null && data.getType() == NodeDataType.GROUP_BEGIN) {
        //all group begin dummies of the same group node are mapped to the same network node
        nRep = (Node)groupNode2BeginRep.get(data.getGroupNode());
        if (nRep == null) {
          nRep = network.createNode();
          groupNode2BeginRep.set(data.getGroupNode(), nRep);
        }
      } else {
        if (data != null && data.getType() == NodeDataType.GROUP_END) {
          //all group end dummies of the same group node are mapped to the same network node
          nRep = (Node)groupNode2EndRep.get(data.getGroupNode());
          if (nRep == null) {
            nRep = network.createNode();
            groupNode2EndRep.set(data.getGroupNode(), nRep);
          }
        } else {
          nRep = network.createNode();
        }
      }
      node2NetworkRep.set(n, nRep);
    }
  }

  /**
   * Creates a network edge between the specified nodes and sets the specified length and priority values.
   */
  private void addNetworkEdge( Node from, Node to, Graph network, int priority, IEdgeMap edge2Weight, int length, IEdgeMap edge2MinLength ) {
    Edge sConnector = network.createEdge(from, to);
    edge2MinLength.setInt(sConnector, length);
    edge2Weight.setInt(sConnector, priority);
  }

  /**
   * Connect all nodes to a global source and a global sink node.
   * <p>
   * Depending on the lane alignment of a node, the connection between the global source and the node or the one between the
   * node and the global sink gets more weight assigned.
   * </p>
   */
  private void addGlobalNetworkFlow( LayoutGraph subgraph, Graph network, INodeMap node2NetworkRep, IDataProvider node2LaneAlignment, IEdgeMap edge2MinLength, IEdgeMap edge2Weight ) {
    //connect nodes to global source/sink
    Node globalSource = network.createNode();
    Node globalSink = network.createNode();
    for (INodeCursor nc = subgraph.getNodeCursor(); nc.ok(); nc.next()) {
      Node n = nc.node();
      Node nRep = (Node)node2NetworkRep.get(n);
      int nLaneAlignment = node2LaneAlignment.getInt(n);
      if (!network.containsEdge(nRep, globalSink)) {
        int priority = (nLaneAlignment == LANE_ALIGNMENT_RIGHT) ? PRIORITY_LOW : 0;
        this.addNetworkEdge(nRep, globalSink, network, priority, edge2Weight, 0, edge2MinLength);
      }
      if (!network.containsEdge(globalSource, nRep)) {
        int priority = (nLaneAlignment == LANE_ALIGNMENT_LEFT) ? PRIORITY_LOW : 0;
        this.addNetworkEdge(globalSource, nRep, network, priority, edge2Weight, 0, edge2MinLength);
      }
    }
  }

  /**
   * Returns all edges between nodes in the specified graph that are assigned to the same layer.
   */
  private static EdgeList getSameLayerEdges( LayoutGraph graph, ILayoutDataProvider ldp ) {
    EdgeList sameLayerEdges = new EdgeList();
    IEdgeMap edge2Seen = Maps.createHashedEdgeMap();
    for (INodeCursor nc = graph.getNodeCursor(); nc.ok(); nc.next()) {
      Node n = nc.node();
      INodeData nData = ldp.getNodeData(n);
      for (ListCell cell = nData.getFirstSameLayerEdgeCell(); cell != null; cell = cell.succ()) {
        Edge sameLayerEdge = (Edge)cell.getInfo();
        Node opposite = sameLayerEdge.opposite(n);
        if (!edge2Seen.getBool(sameLayerEdge) && graph.contains(opposite)) {
          sameLayerEdges.add(sameLayerEdge);
          edge2Seen.setBool(sameLayerEdge, true);
        }
      }
    }
    return sameLayerEdges;
  }

  /**
   * Determines the minimal network length between two nodes.
   * <p>
   * The minimal network length is 0 for two nodes A and B, if:
   * <ul>
   * <li>exactly one of them is a group node border node of group G and the other one is a child node of G</li>
   * <li>both nodes are group node border nodes of the same group node</li>
   * <li>both nodes are group node border nodes of different group nodes G1 and G2 and G1 is either a child node or
   * the parent node of G2</li>
   * </ul>
   * For all other two nodes, the minimal network length is 1.
   * </p>
   */
  private static int calculateMinLength( Node n1, Node n2, ILayoutDataProvider ldp ) {
    boolean isN1GroupNodeBorder = NodeAlignmentPortOptimizer.isGroupNodeBorder(n1, ldp);
    boolean isN2GroupNodeBorder = NodeAlignmentPortOptimizer.isGroupNodeBorder(n2, ldp);
    if (isN1GroupNodeBorder && isN2GroupNodeBorder) {
      Node n1GroupNode = ldp.getNodeData(n1).getGroupNode();
      Node n2GroupNode = ldp.getNodeData(n2).getGroupNode();
      Node n1GroupNodeParent = (n1GroupNode == null) ? null : ldp.getNodeData(n1GroupNode).getGroupNode();
      Node n2GroupNodeParent = (n2GroupNode == null) ? null : ldp.getNodeData(n2GroupNode).getGroupNode();
      if (n1GroupNode == n2GroupNode || n1GroupNodeParent == n2GroupNode || n2GroupNodeParent == n1GroupNode) {
        return 0;
      } else {
        return 1;
      }
    } else {
      if (isN1GroupNodeBorder || isN2GroupNodeBorder) {
        if (ldp.getNodeData(n1).getGroupNode() == ldp.getNodeData(n2).getGroupNode()) {
          return 0;
        } else {
          return 1;
        }
      } else {
        return 1;
      }
    }
  }

  private void adjustBendAlignmentLayer( Node bendNode, INodeMap node2AlignmentLayer, INodeMap seenBendMap, ILayoutDataProvider ldp ) {
    double dummyAlignmentLayer = node2AlignmentLayer.getDouble(bendNode);
    NodeList seenBendNodes = new NodeList(bendNode);
    boolean alignsWithCommonNode = false;
    // check if all bend nodes towards the source node of the original edge have the same alignment layer assigned
    Edge inEdge = bendNode.firstInEdge();
    while (inEdge != null && NodeAlignmentPortOptimizer.isBendNode(inEdge.source(), ldp) && dummyAlignmentLayer == node2AlignmentLayer.getDouble(inEdge.source())) {
      seenBendNodes.add(inEdge.source());
      inEdge = inEdge.source().firstInEdge();
    }
    if (inEdge != null && !NodeAlignmentPortOptimizer.isBendNode(inEdge.source(), ldp)) {
      alignsWithCommonNode = (dummyAlignmentLayer == node2AlignmentLayer.getDouble(inEdge.source()));
    }
    // check if all bend nodes towards the target node of the original edge have the same alignment layer assigned
    Edge outEdge = bendNode.firstOutEdge();
    while (outEdge != null && NodeAlignmentPortOptimizer.isBendNode(outEdge.target(), ldp) && dummyAlignmentLayer == node2AlignmentLayer.getDouble(outEdge.target())) {
      seenBendNodes.add(outEdge.target());
      outEdge = outEdge.target().firstOutEdge();
    }
    if (!alignsWithCommonNode && outEdge != null && !NodeAlignmentPortOptimizer.isBendNode(outEdge.target(), ldp)) {
      alignsWithCommonNode = (dummyAlignmentLayer == node2AlignmentLayer.getDouble(outEdge.target()));
    }
    // if the bends are not aligned with the source and target node, assign a separate layer to them
    if (!alignsWithCommonNode) {
      for (INodeCursor nc = seenBendNodes.nodes(); nc.ok(); nc.next()) {
        seenBendMap.setBool(nc.node(), true);
        node2AlignmentLayer.setDouble(nc.node(), dummyAlignmentLayer - 0.5);
      }
    }
  }

  /**
   * Disposes the data provider used to provide the alignment information to the node placer.
   */
  public void dispose() {
    if (this.graph != null && this.graph.getDataProvider("y.layout.hierarchic.incremental.SimlexNodePlacer.NODE_TO_ALIGN_WITH") != null) {
      this.graph.removeDataProvider("y.layout.hierarchic.incremental.SimlexNodePlacer.NODE_TO_ALIGN_WITH");
    }
  }

  /**
   * Returns all edges between the specified node and all other nodes on the same layer.
   */
  private EdgeList getSameLayerEdges( Node n, ILayoutDataProvider ldp ) {
    EdgeList result = new EdgeList();
    for (ListCell cell = ldp.getNodeData(n).getFirstSameLayerEdgeCell(); cell != null; cell = cell.succ()) {
      result.add(cell.getInfo());
    }
    return result;
  }

  private static boolean toLeftPartition( Node source, Node target, ILayoutDataProvider layoutData ) {
    int sourceId = NodeAlignmentPortOptimizer.getSwimLaneId(source, layoutData);
    int targetId = NodeAlignmentPortOptimizer.getSwimLaneId(target, layoutData);
    return targetId != -1 && sourceId > targetId;
  }

  private static boolean toRightPartition( Node source, Node target, ILayoutDataProvider layoutData ) {
    int sourceId = NodeAlignmentPortOptimizer.getSwimLaneId(source, layoutData);
    int targetId = NodeAlignmentPortOptimizer.getSwimLaneId(target, layoutData);
    return sourceId != -1 && sourceId < targetId;
  }

  static final int getSwimLaneId( Node n, ILayoutDataProvider ldp ) {
    SwimlaneDescriptor laneDesc = ldp.getNodeData(n).getSwimLaneDescriptor();
    return laneDesc != null ? laneDesc.getComputedLaneIndex() : -1;
  }

  private static boolean isRealEdge( Edge e, ILayoutDataProvider layoutData ) {
    return layoutData.getNodeData(e.source()).getType() == NodeDataType.NORMAL && layoutData.getNodeData(e.target()).getType() == NodeDataType.NORMAL;
  }

  private static boolean isBendNode( Node n, ILayoutDataProvider ldp ) {
    INodeData data = ldp.getNodeData(n);
    return data != null && (data.getType() == NodeDataType.BEND);
  }

  private static boolean isGroupNodeBorder( Node n, ILayoutDataProvider ldp ) {
    INodeData data = ldp.getNodeData(n);
    return data != null && (data.getType() == NodeDataType.GROUP_BEGIN || data.getType() == NodeDataType.GROUP_END);
  }

  private static boolean isGroupNodeProxy( Node n, ILayoutDataProvider ldp ) {
    INodeData data = ldp.getNodeData(n);
    return data != null && (data.getType() == NodeDataType.PROXY_FOR_EDGE_AT_GROUP);
  }

  private static boolean areInSameLayer( Node n1, Node n2, ILayoutDataProvider ldp ) {
    return ldp.getNodeData(n1).getLayer() == ldp.getNodeData(n2).getLayer();
  }

  /**
   * Comparator that can be used to sort nodes by ascending alignment layer and node layer.
   */
  static class AlignedNodePositionComparator implements Comparator<Node> {
    private final ILayoutDataProvider ldp;

    private final IDataProvider node2AlignmentLayer;

    AlignedNodePositionComparator( ILayoutDataProvider ldp, IDataProvider node2AlignmentLayer ) {
      this.ldp = ldp;
      this.node2AlignmentLayer = node2AlignmentLayer;
    }

    public int compare( Node o1, Node o2 ) {
      double alignment1 = this.node2AlignmentLayer.getDouble(o1);
      double alignment2 = this.node2AlignmentLayer.getDouble(o2);
      if (alignment1 < alignment2) {
        return -1;
      } else {
        if (alignment1 > alignment2) {
          return 1;
        } else {
          int layer1 = this.ldp.getNodeData(o1).getLayer();
          int layer2 = this.ldp.getNodeData(o2).getLayer();
          return layer1 < layer2 ? -1 : layer1 > layer2 ? 1 : 0;
        }
      }
    }
  }

  /**
   * Comparator that can be used to sort nodes by ascending layer index and position.
   */
  static class NodePositionComparator implements Comparator<Node> {
    private final ILayoutDataProvider ldp;

    NodePositionComparator( ILayoutDataProvider ldp ) {
      this.ldp = ldp;
    }

    public int compare( Node o1, Node o2 ) {
      INodeData nd1 = this.ldp.getNodeData(o1);
      INodeData nd2 = this.ldp.getNodeData(o2);
      int layer1 = nd1.getLayer();
      int layer2 = nd2.getLayer();
      if (layer1 < layer2) {
        return -1;
      } else {
        if (layer1 > layer2) {
          return 1;
        } else {
          int position1 = nd1.getPosition();
          int position2 = nd2.getPosition();
          return position1 < position2 ? -1 : position1 > position2 ? 1 : 0;
        }
      }
    }
  }

  static class SwimLaneIdDataProviderAdapter extends DataProviderAdapter {
    private ILayoutDataProvider ldp;

    SwimLaneIdDataProviderAdapter( ILayoutDataProvider ldp ) {
      this.ldp = ldp;
    }

    @Override
    public Object get( Object dataHolder ) {
      int swimLaneID = NodeAlignmentPortOptimizer.getSwimLaneId((Node)dataHolder, this.ldp);
      if (swimLaneID < 0) {
        return dataHolder;
      } else {
        return swimLaneID;
      }
    }
  }
}
