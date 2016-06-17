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
import com.yworks.yfiles.algorithms.ICursor;
import com.yworks.yfiles.algorithms.IDataMap;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.IEdgeCursor;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.INodeCursor;
import com.yworks.yfiles.algorithms.INodeMap;
import com.yworks.yfiles.algorithms.Maps;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YDimension;
import com.yworks.yfiles.algorithms.YList;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.layout.ColumnDescriptor;
import com.yworks.yfiles.layout.hierarchic.AsIsLayerer;
import com.yworks.yfiles.layout.hierarchic.EdgeLayoutDescriptor;
import com.yworks.yfiles.layout.hierarchic.EdgeRoutingStyle;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutCore;
import com.yworks.yfiles.layout.hierarchic.IIncrementalHintsFactory;
import com.yworks.yfiles.layout.hierarchic.PortCandidateOptimizer;
import com.yworks.yfiles.layout.hierarchic.RoutingStyle;
import com.yworks.yfiles.layout.hierarchic.SimplexNodePlacer;
import com.yworks.yfiles.layout.IEdgeLabelLayout;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.INodeLabelLayout;
import com.yworks.yfiles.layout.INodeLayout;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.LabelLayoutKeys;
import com.yworks.yfiles.layout.LabelLayoutTranslator;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.PartitionGrid;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.PortConstraintKeys;
import com.yworks.yfiles.layout.PortSide;
import com.yworks.yfiles.layout.RowDescriptor;

/**
 * An automatic layout algorithm for BPMN diagrams.
 * <p>
 * The different type of elements have to be marked with the DataProvider keys {@link #BPMN_EDGE_TYPE_DP_KEY} and {@link #BPMN_NODE_TYPE_DP_KEY}
 * .
 * </p>
 * <p>
 * The algorithm supports both, full layout as well as routing of edges only (see {@link #getLayoutMode() LayoutMode} ).
 * </p>
 */
public class BpmnLayout implements ILayoutAlgorithm {
  /**
   * {@link IDataProvider} key used to mark nodes for layout when using using sphere of action {@link Scope#SELECTED_ELEMENTS}.
   *
   * @see #getScope()
   * @see #getScope()
   */
  public static final Object AFFECTED_NODES_DP_KEY = "com.yworks.yfiles.bpmn.layout.BpmnLayouter.AFFECTED_NODES_DPKEY";

  /**
   * {@link IDataProvider} key used to mark edges for layout when using using sphere of action {@link Scope#SELECTED_ELEMENTS}.
   *
   * @see #getScope()
   * @see #getScope()
   */
  public static final Object AFFECTED_EDGES_DP_KEY = "com.yworks.yfiles.bpmn.layout.BpmnLayouter.AFEECTED_EDGES_DPKEY";

  /**
   * {@link IDataProvider} key used to store the BPMN specific type for each node.
   *
   * <p>
   * Valid are all node type constants specified by class {@link BpmnElementTypes} .
   * </p>
   */
  public static final String BPMN_NODE_TYPE_DP_KEY = "com.yworks.yfiles.bpmn.layout.BpmnLayouter.BPMN_NODE_TYPE_DPKEY";

  /**
   * {@link IDataProvider} key used to store the BPMN specific type for each edge.
   *
   * <p>
   * Valid are all edge type constants specified by class {@link BpmnElementTypes} .
   * </p>
   */
  public static final String BPMN_EDGE_TYPE_DP_KEY = "com.yworks.yfiles.bpmn.layout.BpmnLayouter.BPMN_EDGE_TYPE_DPKEY";

  /**
   * {@link IDataProvider} key used to store if a node is a start node for a BPMN process or sub-process.
   */
  static final String IS_START_NODE_DP_KEY = "com.yworks.yfiles.bpmn.layout.BpmnLayouter.IS_START_NODE_DPKEY";

  /**
   * {@link IDataProvider} key used to store which labels shall be positioned by the labeling algorithm.
   */
  static final String LABELS_TO_CONSIDER_DP_KEY = "com.yworks.yfiles.bpmn.layout.BpmnLayouter.LABELS_TO_CONSIDER";

  private double minimumNodeDistance = 40;

  private double minimumEdgeLength = 20;

  private LayoutOrientation layoutOrientation = LayoutOrientation.LEFT_TO_RIGHT;

  private double poolDistance = 50.0;

  private double laneInsets = 10;

  private LayoutMode layoutMode = LayoutMode.FULL_LAYOUT;

  private Scope sphereOfAction = Scope.ALL_ELEMENTS;

  private boolean compactMessageFlowLayeringEnabled = false;

  /**
   * Returns the used layout mode.
   * <p>
   * Defaults to {@link LayoutMode#FULL_LAYOUT} .
   * </p>
   * @return The LayoutMode.
   * @see #setLayoutMode(LayoutMode)
   */
  public LayoutMode getLayoutMode() {
    return this.layoutMode;
  }

  /**
   * Sets the layout mode. Possible values are {@link LayoutMode#FULL_LAYOUT} and {@link LayoutMode#ROUTE_EDGES}.
   *
   * @param value The LayoutMode to set.
   * @throws IllegalArgumentException if the specified layout mode does not match any of the layout mode constants defined in this class.
   * @see #getLayoutMode()
   */
  public void setLayoutMode( LayoutMode value ) {
    switch (value) {
      case ROUTE_EDGES:
      case FULL_LAYOUT:
        {
          this.layoutMode = value;
          break;
        }

      default:
        {
          throw new IllegalArgumentException("Invalid layout mode: " + value);
        }
    }
  }

  /**
   * Returns the sphere of action.
   * <p>
   * Defaults to {@link Scope#ALL_ELEMENTS} .
   * </p>
   * <p>
   * Note, if the sphere of action is set to {@link Scope#SELECTED_ELEMENTS} and the layout mode to {@link LayoutMode#FULL_LAYOUT}
   * non-selected elements may also be moved. However the layout algorithm uses the initial position of such elements as
   * sketch.
   * </p>
   * @return The Scope.
   * @see #getLayoutMode()
   * @see #setScope(Scope)
   */
  public Scope getScope() {
    return this.sphereOfAction;
  }

  /**
   * Sets the sphere of action. Possible values are {@link Scope#ALL_ELEMENTS} and {@link Scope#SELECTED_ELEMENTS}.
   * <p>
   * Defaults to {@link Scope#ALL_ELEMENTS} .
   * </p>
   * <p>
   * Note, if the sphere of action is set to {@link Scope#SELECTED_ELEMENTS} and the layout mode to {@link LayoutMode#FULL_LAYOUT}
   * non-selected elements may also be moved. However the layout algorithm uses the initial position of such elements as
   * sketch.
   * </p>
   * @param value The Scope to set.
   * @throws IllegalArgumentException if the specified sphere of action does not match any of the sphere of action constants defined in this class.
   * @see #getLayoutMode()
   * @see #getScope()
   */
  public void setScope( Scope value ) {
    switch (value) {
      case ALL_ELEMENTS:
      case SELECTED_ELEMENTS:
        {
          this.sphereOfAction = value;
          break;
        }

      default:
        {
          throw new IllegalArgumentException("Invalid sphere of action: " + value);
        }
    }
  }

  /**
   * Returns the insets used for swim lanes.
   * <p>
   * Defaults to {@code 10.0}.
   * </p>
   * @return The LaneInsets.
   * @see #setLaneInsets(double)
   */
  public double getLaneInsets() {
    return this.laneInsets;
  }

  /**
   * Sets the insets for swim lanes, that is the distance between a graph element and the border of its enclosing swimlane.
   * <p>
   * Defaults to {@code 10.0}.
   * </p>
   * @param value The LaneInsets to set.
   * @see #getLaneInsets()
   */
  public void setLaneInsets( double value ) {
    this.laneInsets = value;
  }

  /**
   * The minimum distance between two node elements.
   * <p>
   * Specifies the minimum distance between two node elements.
   * </p>
   * <p>
   * Defaults to {@code 40.0}
   * </p>
   * @return The MinimumNodeDistance.
   * @see #setMinimumNodeDistance(double)
   */
  public double getMinimumNodeDistance() {
    return this.minimumNodeDistance;
  }

  /**
   * The minimum distance between two node elements.
   * <p>
   * Specifies the minimum distance between two node elements.
   * </p>
   * <p>
   * Defaults to {@code 40.0}
   * </p>
   * @param value The MinimumNodeDistance to set.
   * @see #getMinimumNodeDistance()
   */
  public void setMinimumNodeDistance( double value ) {
    this.minimumNodeDistance = value;
  }

  /**
   * The minimum length of edges.
   * <p>
   * Specifies the minimum length of edges.
   * </p>
   * <p>
   * Defaults to {@code 20.0}.
   * </p>
   * @return The MinimumEdgeLength.
   * @see #setMinimumEdgeLength(double)
   */
  public double getMinimumEdgeLength() {
    return this.minimumEdgeLength;
  }

  /**
   * The minimum length of edges.
   * <p>
   * Specifies the minimum length of edges.
   * </p>
   * <p>
   * Defaults to {@code 20.0}.
   * </p>
   * @param value The MinimumEdgeLength to set.
   * @see #getMinimumEdgeLength()
   */
  public void setMinimumEdgeLength( double value ) {
    this.minimumEdgeLength = value;
  }

  /**
   * The used minimum distance between two pool elements.
   * <p>
   * Specifies the used minimum distance between two pool elements.
   * </p>
   * <p>
   * Defaults to {@code 50.0}.
   * </p>
   * @return The PoolDistance.
   * @see #setPoolDistance(double)
   */
  public double getPoolDistance() {
    return this.poolDistance;
  }

  /**
   * The used minimum distance between two pool elements.
   * <p>
   * Specifies the used minimum distance between two pool elements.
   * </p>
   * <p>
   * Defaults to {@code 50.0}.
   * </p>
   * @param value The PoolDistance to set.
   * @see #getPoolDistance()
   */
  public void setPoolDistance( double value ) {
    this.poolDistance = value;
  }

  /**
   * The layout orientation.
   * <p>
   * Specifies the layout orientation.
   * </p>
   * <p>
   * Defaults to {@link LayoutOrientation#LEFT_TO_RIGHT} .
   * </p>
   * @return The LayoutOrientation.
   * @see #setLayoutOrientation(LayoutOrientation)
   */
  public LayoutOrientation getLayoutOrientation() {
    return this.layoutOrientation;
  }

  /**
   * The layout orientation.
   * <p>
   * Specifies the layout orientation.
   * </p>
   * <p>
   * Defaults to {@link LayoutOrientation#LEFT_TO_RIGHT} .
   * </p>
   * @param value The LayoutOrientation to set.
   * @throws IllegalArgumentException if the specified orientation does not match any of the layout orientation constants defined in this class.
   * @see #getLayoutOrientation()
   */
  public void setLayoutOrientation( LayoutOrientation value ) {
    switch (value) {
      case TOP_TO_BOTTOM:
      case LEFT_TO_RIGHT:
        {
          this.layoutOrientation = value;
          break;
        }

      default:
        {
          throw new IllegalArgumentException("Invalid layout orientation: " + value);
        }
    }
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
   * Lays out the specified graph.
   */
  public void applyLayout( LayoutGraph graph ) {
    if (graph.isEmpty()) {
      return;
    }
    if (this.layoutMode == LayoutMode.ROUTE_EDGES) {
      this.doEdgeRouting(graph);
    } else {
      // store old data providers
      IDataProvider oldIncrementalHintsMap = this.sphereOfAction == Scope.SELECTED_ELEMENTS ? graph.getDataProvider(HierarchicLayout.INCREMENTAL_HINTS_DPKEY) : null;
      IDataProvider oldEdgeLayoutDescriptors = graph.getDataProvider(HierarchicLayoutCore.EDGE_LAYOUT_DESCRIPTOR_DPKEY);
      // set the laneInsets to all partition grid columns and rows
      this.configurePartitionGrid(graph);
      HierarchicLayout ihl = this.getConfiguredIncrementalHierarchicLayout(graph);
      // mark event nodes with indegree 0 as start nodes to be considered during layering
      this.markStartNodes(graph);
      // mark non-sequence flow edges to consider during back-loop layering
      this.markNonSequenceFlowEdges(graph);
      // mark labels to consider during the layout
      this.markLabelsToConsider(graph);
      // set a minimum size for each edge to consider edge labels
      IEdgeMap edge2LayoutDescriptor = Maps.createHashedEdgeMap();
      for (IEdgeCursor ec = graph.getEdgeCursor(); ec.ok(); ec.next()) {
        Edge e = ec.edge();
        EdgeLayoutDescriptor descriptor = this.createEdgeLayoutDescriptor(e, graph, this.layoutOrientation == LayoutOrientation.LEFT_TO_RIGHT);
        edge2LayoutDescriptor.set(e, descriptor);
      }
      graph.addDataProvider(HierarchicLayoutCore.EDGE_LAYOUT_DESCRIPTOR_DPKEY, edge2LayoutDescriptor);
      // create edge selection data provider combining node and edge selection
      IDataProvider oldSelectedNodes = graph.getDataProvider(AFFECTED_NODES_DP_KEY);
      IDataProvider oldSelectedEdges = graph.getDataProvider(AFFECTED_EDGES_DP_KEY);
      IDataProvider combinedSelectedEdges = this.createCombinedEdgeSelectionDataProvider(oldSelectedNodes, oldSelectedEdges);
      IDataProvider selection = combinedSelectedEdges != null ? combinedSelectedEdges : oldSelectedEdges;
      IDataProvider oldSourcePortConstraintDp = graph.getDataProvider(PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY);
      IDataProvider oldTargetPortConstraintDp = graph.getDataProvider(PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY);
      // fix ports of unselected elements
      this.fixUnselectedEdges(graph, selection);
      // run core layout
      ihl.applyLayout(graph);
      // update the 'originalWidth/Height' and 'originalPosition' of grid rows and columns
      // with the corresponding computed values so they are considered by the edge router
      this.updateOriginalGridValues(PartitionGrid.getPartitionGrid(graph));
      // run additional edge routing
      if (combinedSelectedEdges != null) {
        graph.addDataProvider(AFFECTED_EDGES_DP_KEY, combinedSelectedEdges);
      }
      this.doEdgeRouting(graph);
      //restore original settings
      if (this.sphereOfAction == Scope.SELECTED_ELEMENTS && oldSelectedNodes != null) {
        graph.removeDataProvider(HierarchicLayout.INCREMENTAL_HINTS_DPKEY);
        if (oldIncrementalHintsMap != null) {
          graph.addDataProvider(HierarchicLayout.INCREMENTAL_HINTS_DPKEY, oldIncrementalHintsMap);
        }
        graph.removeDataProvider(AFFECTED_EDGES_DP_KEY);
        if (oldSelectedEdges != null) {
          graph.addDataProvider(AFFECTED_EDGES_DP_KEY, oldSelectedEdges);
        }
      }
      if (this.sphereOfAction == Scope.SELECTED_ELEMENTS && selection != null) {
        graph.removeDataProvider(PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY);
        if (oldSourcePortConstraintDp != null) {
          graph.addDataProvider(PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY, oldSourcePortConstraintDp);
        }
        graph.removeDataProvider(PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY);
        if (oldTargetPortConstraintDp != null) {
          graph.addDataProvider(PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY, oldTargetPortConstraintDp);
        }
      }
      graph.removeDataProvider(HierarchicLayoutCore.EDGE_LAYOUT_DESCRIPTOR_DPKEY);
      if (oldEdgeLayoutDescriptors != null) {
        graph.addDataProvider(HierarchicLayoutCore.EDGE_LAYOUT_DESCRIPTOR_DPKEY, oldEdgeLayoutDescriptors);
      }
      NodeAlignmentPortOptimizer portOptimizer = (NodeAlignmentPortOptimizer)ihl.getHierarchicLayoutCore().getPortConstraintOptimizer();
      portOptimizer.dispose();
      graph.removeDataProvider(IS_START_NODE_DP_KEY);
      graph.removeDataProvider(BackLoopLayererStage.EDGES_TO_IGNORE_DP_KEY);
      graph.removeDataProvider(LabelLayoutKeys.IGNORED_LABELS_DPKEY);
    }
    //do generic labeling for edges
    this.doLabelLayout(graph);
    //fix endpoints of edges
    new FixEdgeEndpointsStage().applyLayout(graph);
  }

  private void doEdgeRouting( LayoutGraph graph ) {
    BpmnEdgeRouter bpmnEdgeRouter = new BpmnEdgeRouter();
    bpmnEdgeRouter.setScope(this.sphereOfAction == Scope.ALL_ELEMENTS ? com.yworks.yfiles.layout.router.Scope.ROUTE_ALL_EDGES : com.yworks.yfiles.layout.router.Scope.ROUTE_AFFECTED_EDGES);
    bpmnEdgeRouter.setLayoutOrientation(this.layoutOrientation);
    bpmnEdgeRouter.applyLayout(graph);
  }

  private void configurePartitionGrid( LayoutGraph graph ) {
    PartitionGrid grid = PartitionGrid.getPartitionGrid(graph);
    if (grid != null) {
      for (ICursor cur = grid.getColumns().cursor(); cur.ok(); cur.next()) {
        ColumnDescriptor column = (ColumnDescriptor)cur.current();
        column.setLeftInset(column.getLeftInset() + this.laneInsets);
        column.setRightInset(column.getRightInset() + this.laneInsets);
      }

      {
        for (ICursor cur = grid.getRows().cursor(); cur.ok(); cur.next()) {
          RowDescriptor row = (RowDescriptor)cur.current();
          row.setTopInset(row.getTopInset() + this.laneInsets);
          row.setBottomInset(row.getBottomInset() + this.laneInsets);
        }
      }
    }
  }

  private HierarchicLayout getConfiguredIncrementalHierarchicLayout( LayoutGraph graph ) {
    HierarchicLayout ihl = new HierarchicLayout();
    ihl.setOrthogonalRoutingEnabled(true);
    ihl.setRecursiveGroupLayeringEnabled(false);
    ihl.setComponentLayoutEnabled(false);
    BpmnLayerer bpmnLayerer = new BpmnLayerer();
    bpmnLayerer.setCompactMessageFlowLayering(isCompactMessageFlowLayering());
    ihl.setFromScratchLayerer(new BackLoopLayererStage(bpmnLayerer));
    ihl.setBackLoopRoutingEnabled(false);
    ((SimplexNodePlacer)ihl.getNodePlacer()).setBaryCenterModeEnabled(true);
    ihl.setIntegratedEdgeLabelingEnabled(false);
    // the NodeLabelingPortOptimizer needs the node labels to be written back with relative locations.
    ihl.setNodeLabelConsiderationEnabled(true);
    ((LabelLayoutTranslator)ihl.getLabeling()).setWritingBackNodeLabelsEnabled(true);
    ((LabelLayoutTranslator)ihl.getLabeling()).setWritingBackRelativeNodeLabelLocationEnabled(true);
    ihl.setMinimumLayerDistance(this.minimumNodeDistance);
    ihl.setNodeToNodeDistance(this.minimumNodeDistance);
    ((SimplexNodePlacer)ihl.getNodePlacer()).setEdgeStraighteningEnabled(true);
    boolean isLeftToRight = this.layoutOrientation == LayoutOrientation.LEFT_TO_RIGHT;
    if (isLeftToRight) {
      ihl.setLayoutOrientation(com.yworks.yfiles.layout.LayoutOrientation.LEFT_TO_RIGHT);
    } else {
      ihl.setLayoutOrientation(com.yworks.yfiles.layout.LayoutOrientation.TOP_TO_BOTTOM);
    }
    NodeAlignmentPortOptimizer optimizer = new NodeAlignmentPortOptimizer(new NodeLabelingPortOptimizer(new PortCandidateOptimizer()), isLeftToRight);
    ihl.getHierarchicLayoutCore().setPortConstraintOptimizer(optimizer);
    if (this.sphereOfAction == Scope.SELECTED_ELEMENTS) {
      this.configureIncrementalMode(graph, ihl);
    }
    return ihl;
  }

  private void configureIncrementalMode( LayoutGraph graph, HierarchicLayout ihl ) {
    AsIsLayerer layerer = (AsIsLayerer)ihl.getFixedElementsLayerer();
    layerer.setMaximumNodeSize(5);
    ihl.setLayoutMode(com.yworks.yfiles.layout.hierarchic.LayoutMode.INCREMENTAL);
    IDataProvider selectedNodes = graph.getDataProvider(AFFECTED_NODES_DP_KEY);
    IDataProvider selectedEdges = graph.getDataProvider(AFFECTED_EDGES_DP_KEY);
    if (selectedNodes != null || selectedEdges != null) {
      IDataMap hintMap = Maps.createHashedDataMap();
      graph.addDataProvider(HierarchicLayout.INCREMENTAL_HINTS_DPKEY, hintMap);
      IIncrementalHintsFactory hintsFactory = ihl.createIncrementalHintsFactory();
      if (selectedNodes != null) {
        for (INodeCursor nc = graph.getNodeCursor(); nc.ok(); nc.next()) {
          Node n = nc.node();
          if (selectedNodes.getBool(n)) {
            hintMap.set(n, hintsFactory.createLayerIncrementallyHint(n));
          }
        }
      }
      if (selectedEdges != null) {
        for (IEdgeCursor ec = graph.getEdgeCursor(); ec.ok(); ec.next()) {
          Edge e = ec.edge();
          if (selectedEdges.getBool(e)) {
            hintMap.set(e, hintsFactory.createSequenceIncrementallyHint(e));
          }
        }
      }
    }
  }

  /**
   * Mark event nodes with indegree 0 as start nodes to be considered during layering.
   */
  private void markStartNodes( LayoutGraph graph ) {
    INodeMap node2IsStartNode = Maps.createHashedNodeMap();
    for (INodeCursor nodeCursor = graph.getNodeCursor(); nodeCursor.ok(); nodeCursor.next()) {
      Node node = nodeCursor.node();
      boolean isEventNode = BpmnElementTypes.isEventNode(BpmnElementTypes.getType(node, graph));
      boolean isStartNode = node.inDegree() == 0 && isEventNode;
      node2IsStartNode.setBool(node, isStartNode);
    }
    graph.addDataProvider(IS_START_NODE_DP_KEY, node2IsStartNode);
  }

  /**
   * Mark edges that are neither sequence flows nor conversation links so they are ignored during back-loop layering.
   */
  private void markNonSequenceFlowEdges( LayoutGraph graph ) {
    IEdgeMap edge2isNoSequenceFlow = Maps.createHashedEdgeMap();
    for (IEdgeCursor edgeCursor = graph.getEdgeCursor(); edgeCursor.ok(); edgeCursor.next()) {
      Edge edge = edgeCursor.edge();
      EdgeType type = BpmnElementTypes.getType(edge, graph);
      boolean isSequenceFlow = BpmnElementTypes.isSequenceFlow(type) || BpmnElementTypes.isConversationLink(type);
      edge2isNoSequenceFlow.setBool(edge, !isSequenceFlow);
    }
    graph.addDataProvider(BackLoopLayererStage.EDGES_TO_IGNORE_DP_KEY, edge2isNoSequenceFlow);
  }

  /**
   * Mark labels of pool and choreography nodes so they are ignored during the layout.
   */
  private void markLabelsToConsider( LayoutGraph graph ) {
    IDataMap labelsToIgnore = Maps.createHashedDataMap();
    for (INodeCursor nodeCursor = graph.getNodeCursor(); nodeCursor.ok(); nodeCursor.next()) {
      Node node = nodeCursor.node();
      NodeType type = BpmnElementTypes.getType(node, graph);
      boolean isPool = type == NodeType.POOL;
      boolean isChoreography = type == NodeType.CHOREOGRAPHY;
      INodeLabelLayout[] nll = graph.getLabelLayout(node);
      for (int i = 0; i < nll.length; i++) {
        labelsToIgnore.setBool(nll[i], isPool || isChoreography);
      }
    }
    for (IEdgeCursor edgeCursor = graph.getEdgeCursor(); edgeCursor.ok(); edgeCursor.next()) {
      Edge edge = edgeCursor.edge();
      IEdgeLabelLayout[] ell = graph.getLabelLayout(edge);
      for (int i = 0; i < ell.length; i++) {
        labelsToIgnore.setBool(ell[i], false);
      }
    }
    graph.addDataProvider(LabelLayoutKeys.IGNORED_LABELS_DPKEY, labelsToIgnore);
  }

  /**
   * Run label layout for edges.
   * <p>
   * Run label layout for edges.
   * </p>
   */
  private void doLabelLayout( LayoutGraph graph ) {
    IDataProvider node2IsSelected = graph.getDataProvider(AFFECTED_NODES_DP_KEY);
    IDataProvider edge2IsSelected = graph.getDataProvider(AFFECTED_EDGES_DP_KEY);
    IDataProvider labelsToConsider = new LabelToConsiderDataProviderAdapter(this, graph, edge2IsSelected, node2IsSelected);
    graph.addDataProvider(LABELS_TO_CONSIDER_DP_KEY, labelsToConsider);
    GenericLabeling labeling = new GenericLabeling();
    labeling.setMaximumDuration(0);
    labeling.setNodeLabelPlacementEnabled(false);
    labeling.setEdgeLabelPlacementEnabled(true);
    labeling.setAffectedLabelsDpKey(LABELS_TO_CONSIDER_DP_KEY);
    labeling.setProfitModel(new BpmnLabelProfitModel(graph));
    labeling.setCustomProfitModelRatio(0.25);
    labeling.applyLayout(graph);
    graph.removeDataProvider(LABELS_TO_CONSIDER_DP_KEY);
  }

  private static final double MIN_LABEL_TO_LABEL_DISTANCE = 5;

  /**
   * Creates an EdgeLayoutDescriptor for the specified edge and sets a minimumLength to keep space for its labels.
   * <p>
   * Creates an EdgeLayoutDescriptor for the specified edge and sets a minimumLength to keep space for its labels
   * </p>
   */
  private EdgeLayoutDescriptor createEdgeLayoutDescriptor( Edge e, LayoutGraph g, boolean horizontalOrientation ) {
    EdgeLayoutDescriptor descriptor = new EdgeLayoutDescriptor();
    descriptor.setRoutingStyle(new RoutingStyle(EdgeRoutingStyle.ORTHOGONAL));
    boolean isSequenceFlow = BpmnElementTypes.isSequenceFlow(BpmnElementTypes.getType(e, g));
    double minLength = 0;
    IEdgeLabelLayout[] ell = g.getEdgeLabelLayout(e);
    for (int i = 0; i < ell.length; i++) {
      IEdgeLabelLayout labelLayout = ell[i];
      YDimension labelSize = labelLayout.getBoundingBox();
      if (isSequenceFlow) {
        if (horizontalOrientation) {
          minLength += labelSize.getWidth();
        } else {
          minLength += labelSize.getHeight();
        }
      } else {
        if (horizontalOrientation) {
          minLength += labelSize.getHeight();
        } else {
          minLength += labelSize.getWidth();
        }
      }
      if (i > 0) {
        minLength += MIN_LABEL_TO_LABEL_DISTANCE;
      }
    }
    minLength = Math.max(minLength, this.minimumEdgeLength);
    descriptor.setMinimumLength(minLength);
    return descriptor;
  }

  private IDataProvider createCombinedEdgeSelectionDataProvider( IDataProvider selectedNodes, IDataProvider oldSelectedEdges ) {
    if (this.sphereOfAction == Scope.SELECTED_ELEMENTS && selectedNodes != null) {
      return new CombinedEdgeSelectionDataProviderAdapter(oldSelectedEdges, selectedNodes);
    }
    return null;
  }

  private void fixUnselectedEdges( LayoutGraph graph, IDataProvider selection ) {
    if (this.sphereOfAction == Scope.SELECTED_ELEMENTS && selection != null) {
      IDataProvider oldSourcePortConstraintDp = graph.getDataProvider(PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY);
      IDataProvider oldTargetPortConstraintDp = graph.getDataProvider(PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY);
      IEdgeMap sourcePortConstraints = Maps.createHashedEdgeMap();
      IEdgeMap targetPortConstraints = Maps.createHashedEdgeMap();
      for (IEdgeCursor edgeCursor = graph.getEdgeCursor(); edgeCursor.ok(); edgeCursor.next()) {
        Edge edge = edgeCursor.edge();
        if (!selection.getBool(edge)) {
          // edge is not selected -> use fixed port constraints
          sourcePortConstraints.set(edge, PortConstraint.create(BpmnLayout.getSide(edge, graph, true), true));
          targetPortConstraints.set(edge, PortConstraint.create(BpmnLayout.getSide(edge, graph, false), true));
        } else {
          sourcePortConstraints.set(edge, oldSourcePortConstraintDp != null ? oldSourcePortConstraintDp.get(edge) : null);
          targetPortConstraints.set(edge, oldTargetPortConstraintDp != null ? oldTargetPortConstraintDp.get(edge) : null);
        }
      }
      graph.addDataProvider(PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY, sourcePortConstraints);
      graph.addDataProvider(PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY, targetPortConstraints);
    }
  }

  private static PortSide getSide( Edge edge, LayoutGraph g, boolean atSource ) {
    PortSide portSide = BpmnLayout.getSideFromPortLocation(edge, g, atSource);
    if (portSide == PortSide.ANY) {
      // better use the attached segment to decide on the port side
      portSide = BpmnLayout.getSideFromSegment(edge, g, atSource);
    }
    return portSide;
  }

  private static PortSide getSideFromPortLocation( Edge edge, LayoutGraph g, boolean atSource ) {
    YPoint relPortLocation = atSource ? g.getSourcePointRel(edge) : g.getTargetPointRel(edge);
    INodeLayout nodeLayout = g.getNodeLayout(atSource ? edge.source() : edge.target());
    // calculate relative port position scaled by the node size
    double sdx = relPortLocation.getX() / (nodeLayout.getWidth() / 2);
    double sdy = relPortLocation.getY() / (nodeLayout.getHeight() / 2);
    if (Math.abs(sdx) > Math.abs(sdy)) {
      // east or west
      if (sdx < 0) {
        return PortSide.WEST;
      } else {
        return PortSide.EAST;
      }
    } else {
      if (Math.abs(sdx) < Math.abs(sdy)) {
        if (sdy < 0) {
          return PortSide.NORTH;
        } else {
          return PortSide.SOUTH;
        }
      }
    }
    // port is somewhere at the diagonals of the node bounds
    // so we can't decide the port side based on the port location
    return PortSide.ANY;
  }

  private static PortSide getSideFromSegment( Edge edge, LayoutGraph g, boolean atSource ) {
    YList pathList = g.getPathList(edge);
    YPoint from = (YPoint)(atSource ? pathList.get(0) : pathList.get(pathList.size() - 1));
    YPoint to = (YPoint)(atSource ? pathList.get(1) : pathList.get(pathList.size() - 2));
    double dx = to.getX() - from.getX();
    double dy = to.getY() - from.getY();
    if (Math.abs(dx) > Math.abs(dy)) {
      // east or west
      if (dx < 0) {
        return PortSide.WEST;
      } else {
        return PortSide.EAST;
      }
    } else {
      if (dy < 0) {
        return PortSide.NORTH;
      } else {
        return PortSide.SOUTH;
      }
    }
  }

  /**
   * Update the 'originalWidth/Height' and 'originalPosition' of grid rows and columns with the corresponding computed
   * values.
   */
  private void updateOriginalGridValues( PartitionGrid grid ) {
    if (grid != null) {
      for (ICursor cur = grid.getColumns().cursor(); cur.ok(); cur.next()) {
        ColumnDescriptor column = (ColumnDescriptor)cur.current();
        column.setOriginalWidth(column.getComputedWidth());
        column.setOriginalPosition(column.getComputedPosition());
      }

      {
        for (ICursor cur = grid.getRows().cursor(); cur.ok(); cur.next()) {
          RowDescriptor row = (RowDescriptor)cur.current();
          row.setOriginalHeight(row.getComputedHeight());
          row.setOriginalPosition(row.getComputedPosition());
        }
      }
    }
  }

  static class LabelToConsiderDataProviderAdapter extends DataProviderAdapter {
    private LayoutGraph graph;

    private IDataProvider edge2IsSelected;

    private IDataProvider node2IsSelected;

    LabelToConsiderDataProviderAdapter( BpmnLayout _enclosing, LayoutGraph graph, IDataProvider edge2IsSelected, IDataProvider node2IsSelected ) {
      this._enclosing = _enclosing;
      this.graph = graph;
      this.edge2IsSelected = edge2IsSelected;
      this.node2IsSelected = node2IsSelected;
    }

    @Override
    public boolean getBool( Object dataHolder ) {
      if (dataHolder instanceof INodeLabelLayout) {
        return false;
      } else {
        if (dataHolder instanceof IEdgeLabelLayout) {
          Edge e = this.graph.getOwner((IEdgeLabelLayout)dataHolder);
          EdgeType edgeType = BpmnElementTypes.getType(e, this.graph);
          boolean edgeIsSelected = this.edge2IsSelected != null && this.edge2IsSelected.getBool(e);
          boolean edgeNodeIsSelected = this.node2IsSelected != null && (this.node2IsSelected.getBool(e.source()) || this.node2IsSelected.getBool(e.target()));
          return BpmnElementTypes.isValidEdgeType(edgeType) && (this._enclosing.sphereOfAction == Scope.ALL_ELEMENTS || edgeIsSelected || edgeNodeIsSelected);
        }
      }
      return false;
    }

    private final BpmnLayout _enclosing;
  }

  static class CombinedEdgeSelectionDataProviderAdapter extends DataProviderAdapter {
    private IDataProvider oldSelectedEdges;

    private IDataProvider selectedNodes;

    CombinedEdgeSelectionDataProviderAdapter( IDataProvider oldSelectedEdges, IDataProvider selectedNodes ) {
      this.oldSelectedEdges = oldSelectedEdges;
      this.selectedNodes = selectedNodes;
    }

    @Override
    public boolean getBool( Object dataHolder ) {
      Edge edge = (Edge)dataHolder;
      // route edge if the edge itself or its source or target is selected
      return (this.oldSelectedEdges != null && this.oldSelectedEdges.getBool(edge)) || this.selectedNodes.getBool(edge.source()) || this.selectedNodes.getBool(edge.target());
    }
  }
}
