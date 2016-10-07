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

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.EdgeList;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.IEdgeCursor;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.INodeCursor;
import com.yworks.yfiles.algorithms.Maps;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YDimension;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.layout.INodeLayout;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.PortCandidate;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.PortDirections;
import com.yworks.yfiles.layout.PortSide;
import com.yworks.yfiles.layout.router.MonotonicPathRestriction;
import com.yworks.yfiles.layout.router.polyline.EdgeLayoutDescriptor;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.router.polyline.PathSearchConfiguration;
import com.yworks.yfiles.layout.router.Scope;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * An EdgeRouter specialized to route edges of a business process diagram.
 * <p>
 * Before running the layout, monotonic restrictions and port constraints are set for the edges depending on the {@link BpmnElementTypes#getType(Node, com.yworks.yfiles.algorithms.Graph) node type}
 * and {@link BpmnElementTypes#getType(Edge, com.yworks.yfiles.algorithms.Graph)} edge type }.
 * </p>
 */
class BpmnEdgeRouter extends EdgeRouter {
  private static final double COSTS_STRONG_CANDIDATE_NOT_PREFERRED = 3.5;

  private static final double COSTS_WEAK_CANDIDATE_PREFERRED = 4;

  private static final double COSTS_WEAK_CANDIDATE_NOT_PREFERRED = 5;

  private static final double PRIORITY_SEQUENCE_FLOW = -100;

  private static final double PRIORITY_STRONG_CONSTRAINT = -50;

  private static final double PRIORITY_MAIN_DIRECTION = -30;

  private static final double PRIORITY_BACK_DIRECTION = -15;

  private static final double PRIORITY_SIDE_DIRECTION = -10;

  private static final double PRIORITY_ALIGNED = -25;

  private static final double PRIORITY_ALIGNED_IN_CROSS_DIRECTION = 5;

  private static final double PRIORITY_ANGLE = -5;

  private LayoutOrientation layoutOrientation = LayoutOrientation.LEFT_TO_RIGHT;

  /**
   * The layout orientation of the business process diagram.
   * <p>
   * Specifies the layout orientation of the business process diagram.
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
   * The layout orientation of the business process diagram.
   * <p>
   * Specifies the layout orientation of the business process diagram.
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
   * Creates a new instance and initializes the settings.
   */
  public BpmnEdgeRouter() {
    getDefaultEdgeLayoutDescriptor().setMinimumEdgeToEdgeDistance(10);
    getDefaultEdgeLayoutDescriptor().setMinimumFirstSegmentLength(10);
    getDefaultEdgeLayoutDescriptor().getPenaltySettings().setEdgeLengthPenalty(100);
    getDefaultEdgeLayoutDescriptor().getPenaltySettings().setEdgeCrossingPenalty(0.3);
    setMinimumNodeToEdgeDistance(10);
    setNodeLabelConsiderationEnabled(true);
    setIgnoringInnerNodeLabelsEnabled(true);
    setScope(Scope.ROUTE_AFFECTED_EDGES);
    setAffectedEdgesDpKey(BpmnLayout.AFFECTED_NODES_DP_KEY);
    setAffectedEdgesDpKey(BpmnLayout.AFFECTED_EDGES_DP_KEY);
  }

  private static final int IN_FLOW = 0;

  private static final int RIGHT_OF_FLOW = 1;

  private static final int AGAINST_FLOW = 2;

  private static final int LEFT_OF_FLOW = 3;

  private static final int NO_FLOW = 4;

  private static final byte[][] FLOW2_PORT_CONSTRAINT = new byte[][]{new byte[]{(byte)PortSide.SOUTH.value(), (byte)PortSide.WEST.value(), (byte)PortSide.NORTH.value(), (byte)PortSide.EAST.value()}, new byte[]{(byte)PortSide.EAST.value(), (byte)PortSide.SOUTH.value(), (byte)PortSide.WEST.value(), (byte)PortSide.NORTH.value()}};

  private int getPortSide( Edge e, LayoutGraph graph, boolean atSource ) {
    boolean horizontal = this.isHorizontalOrientation();
    int side;
    YPoint[] path = graph.getPath(e).toArray();
    YPoint p1 = atSource ? path[0] : path[path.length - 1];
    YPoint p2 = atSource ? path[1] : path[path.length - 2];
    boolean segmentIsMoreVertical = Math.abs(p1.getX() - p2.getX()) < Math.abs(p1.getY() - p2.getY());
    if (segmentIsMoreVertical) {
      if (p1.getY() < p2.getY()) {
        side = horizontal ? RIGHT_OF_FLOW : IN_FLOW;
      } else {
        side = horizontal ? LEFT_OF_FLOW : AGAINST_FLOW;
      }
    } else {
      if (p1.getX() < p2.getX()) {
        side = horizontal ? IN_FLOW : LEFT_OF_FLOW;
      } else {
        side = horizontal ? AGAINST_FLOW : RIGHT_OF_FLOW;
      }
    }
    return side;
  }

  /**
   * Creates strong and weak port candidates for all routed edges.
   */
  private void createPortCandidates( Node n, LayoutGraph graph, IEdgeMap edge2SPC, IEdgeMap edge2TPC ) {
    // we only want one strong candidate at each side
    boolean[] strongCandidateAtSide = new boolean[4];
    // collect edges to assign port candidates for and mark sides already used for a fixed port candidate
    EdgeList edgesToAssign = new EdgeList();
    for (int i = 0; i < 2; i++) {
      boolean inEdges = i == 0;
      IEdgeCursor ec = inEdges ? n.getInEdgeCursor() : n.getOutEdgeCursor();
      for (; ec.ok(); ec.next()) {
        Edge edge = ec.edge();
        if (this.isAffected(edge, graph)) {
          // get existing port constraint
          PortConstraint pc = inEdges ? PortConstraint.getTPC(graph, edge) : PortConstraint.getSPC(graph, edge);
          if (pc != null && !pc.isAtAnySide()) {
            // edge already has a side constraint so we don't change it
            // and block this side for strong constraints
            int side = this.getFlowDirection((byte)pc.getSide().value());
            strongCandidateAtSide[side] = true;
          } else {
            edgesToAssign.add(edge);
          }
        } else {
          // fixed edge
          int side = this.getPortSide(edge, graph, !inEdges);
          strongCandidateAtSide[side] = true;
        }
      }
    }
    if (edgesToAssign.isEmpty()) {
      return;
    }
    // create possible EdgePortCandidates
    ArrayList<EdgePortCandidate> candidateList = this.createEdgePortCandidates(n, graph, edgesToAssign, strongCandidateAtSide);
    // distribute strong port candidates to free node sides and all weak port candidates
    this.distributePortCandidates(edge2SPC, edge2TPC, strongCandidateAtSide, candidateList);
  }

  /**
   * For each edge to assign we create potential weak and strong port constraints and calculate priorities to use those.
   * <p>
   * The list of {@code EdgePortCandidate}s is sorted by these priorities and returned.
   * </p>
   */
  private ArrayList<EdgePortCandidate> createEdgePortCandidates( Node n, LayoutGraph graph, EdgeList edgesToAssign, boolean[] strongCandidateAtSide ) {
    // we store if multiple edges at the node prefer the same side and don't assign a strong constraint there in this case
    int[] edgesPreferringSide = new int[4];
    ArrayList<EdgePortCandidate> candidateList = new ArrayList<>(edgesToAssign.size() * 2);
    for (IEdgeCursor ec = edgesToAssign.edges(); ec.ok(); ec.next()) {
      Edge edge = ec.edge();
      for (int i = 0; i < 2; i++) {
        boolean atSource = i == 0;
        if ((atSource && !n.equals(edge.source())) || (!atSource && !n.equals(edge.target()))) {
          continue;
        }
        Node opposite = edge.opposite(n);
        YDimension nodeSize = graph.getSize(n);
        YPoint oppositePort = null;
        // if the edge has a fixed port candidate at the opposite side, we take the strong port location, otherwise the node center
        PortConstraint pcAtOpposite = atSource ? PortConstraint.getTPC(graph, edge) : PortConstraint.getSPC(graph, edge);
        if (pcAtOpposite != null && pcAtOpposite.isStrong()) {
          oppositePort = atSource ? graph.getTargetPointRel(edge) : graph.getSourcePointRel(edge);
        } else {
          oppositePort = YPoint.ORIGIN;
        }
        int sideDirection = this.toSideDirection(n, opposite, graph, oppositePort);
        int layerDirection = this.toLayerDirection(n, opposite, graph, oppositePort);
        int edgeDirection = atSource ? IN_FLOW : AGAINST_FLOW;
        double basePriority = BpmnEdgeRouter.isSequenceFlowOrInvalid(edge, graph) ? PRIORITY_SEQUENCE_FLOW : 0;
        // first the possible candidates IN_FLOW and AGAINST_FLOW are created
        if (layerDirection == NO_FLOW) {
          // same layer --> no strong pc in/against flow
          PortCandidate candidate = this.createCandidate(edgeDirection, false, COSTS_WEAK_CANDIDATE_NOT_PREFERRED, nodeSize);
          candidateList.add(new EdgePortCandidate(candidate, basePriority + PRIORITY_ALIGNED_IN_CROSS_DIRECTION, edge, atSource));
          PortCandidate oppositeCandidate = this.createCandidate(this.getOppositeDirection(edgeDirection), false, COSTS_WEAK_CANDIDATE_NOT_PREFERRED, nodeSize);
          candidateList.add(new EdgePortCandidate(oppositeCandidate, basePriority + PRIORITY_ALIGNED_IN_CROSS_DIRECTION, edge, atSource));
        } else {
          double priority = basePriority;
          priority += layerDirection == edgeDirection ? PRIORITY_MAIN_DIRECTION : PRIORITY_BACK_DIRECTION;
          priority += sideDirection == NO_FLOW ? PRIORITY_ALIGNED : 0;
          // angles are only considered for strong constraints
          double lowAnglePriority = (1 - this.getAngleRatio(n, opposite, true, graph)) * PRIORITY_ANGLE;
          double strongPriority = priority + PRIORITY_STRONG_CONSTRAINT + lowAnglePriority;
          PortCandidate strongCandidate = this.createCandidate(layerDirection, true, 0, nodeSize);
          candidateList.add(new EdgePortCandidate(strongCandidate, strongPriority, edge, atSource));
          PortCandidate weakCandidate = this.createCandidate(layerDirection, false, COSTS_WEAK_CANDIDATE_PREFERRED, nodeSize);
          candidateList.add(new EdgePortCandidate(weakCandidate, priority, edge, atSource));
          PortCandidate oppositeWeakCandidate = this.createCandidate(this.getOppositeDirection(layerDirection), false, COSTS_WEAK_CANDIDATE_NOT_PREFERRED, nodeSize);
          candidateList.add(new EdgePortCandidate(oppositeWeakCandidate, priority, edge, atSource));
          if (sideDirection == NO_FLOW) {
            // directly in/against flow
            edgesPreferringSide[layerDirection]++;
          }
        }
        // second the possible candidates LEFT_OF_FLOW and RIGHT_OF_FLOW are added
        if (sideDirection == NO_FLOW) {
          // aligned nodes --> no strong pc left/right of flow
          PortCandidate weakCandidateLeft = this.createCandidate(LEFT_OF_FLOW, false, COSTS_WEAK_CANDIDATE_NOT_PREFERRED, nodeSize);
          candidateList.add(new EdgePortCandidate(weakCandidateLeft, basePriority + PRIORITY_ALIGNED_IN_CROSS_DIRECTION, edge, atSource));
          PortCandidate weakCandidateRight = this.createCandidate(RIGHT_OF_FLOW, false, COSTS_WEAK_CANDIDATE_NOT_PREFERRED, nodeSize);
          candidateList.add(new EdgePortCandidate(weakCandidateRight, basePriority + PRIORITY_ALIGNED_IN_CROSS_DIRECTION, edge, atSource));
        } else {
          double priority = basePriority + (layerDirection == NO_FLOW ? PRIORITY_MAIN_DIRECTION : PRIORITY_SIDE_DIRECTION);
          // angles are only considered for strong constraints
          double lowAnglePriority = (1 - this.getAngleRatio(n, opposite, false, graph)) * PRIORITY_ANGLE;
          double strongPriority = priority + PRIORITY_STRONG_CONSTRAINT + lowAnglePriority;
          PortCandidate strongCandidate = this.createCandidate(sideDirection, true, 0, nodeSize);
          candidateList.add(new EdgePortCandidate(strongCandidate, strongPriority, edge, atSource));
          PortCandidate weakCandidate = this.createCandidate(sideDirection, false, COSTS_WEAK_CANDIDATE_PREFERRED, nodeSize);
          candidateList.add(new EdgePortCandidate(weakCandidate, priority, edge, atSource));
          PortCandidate oppositeWeakCandidate = this.createCandidate(this.getOppositeDirection(sideDirection), false, COSTS_WEAK_CANDIDATE_NOT_PREFERRED, nodeSize);
          candidateList.add(new EdgePortCandidate(oppositeWeakCandidate, priority, edge, atSource));
          if (layerDirection == NO_FLOW) {
            // same layer edge
            edgesPreferringSide[sideDirection]++;
          }
        }
      }
    }

    {
      // if several edges prefer the same side, we don't assign a strong candidate but only weak ones at this side
      for (int i = 0; i < 4; i++) {
        strongCandidateAtSide[i] = strongCandidateAtSide[i] || edgesPreferringSide[i] > 1;
      }
      // sort constraints by their priority
      Collections.sort(candidateList, new EdgePriorityComparator());
      return candidateList;
    }
  }

  /**
   * Calculates the acute angle between the line connecting the node centers and a line in respectively orthogonal to the
   * layout orientation and returns its ratio of 90 degree.
   */
  private double getAngleRatio( Node node, Node opposite, boolean inLayoutOrientation, LayoutGraph graph ) {
    double dx = Math.abs(graph.getCenterX(node) - graph.getCenterX(opposite));
    double dy = Math.abs(graph.getCenterY(node) - graph.getCenterY(opposite));
    double angle = Math.atan2(dy, dx);
    double angleRatio = 2 * angle / Math.PI;
    return inLayoutOrientation ^ this.isHorizontalOrientation() ? 1 - angleRatio : angleRatio;
  }

  private void distributePortCandidates( IEdgeMap edge2SPC, IEdgeMap edge2TPC, boolean[] strongCandidateAtSide, ArrayList<EdgePortCandidate> allCandidates ) {
    // remember strong candidates not used in this first round
    ArrayList<EdgePortCandidate> unusedStrongCandidates = new ArrayList<>();
    // As a first step only one strong candidate per edge and all weak candidates are assigned
    for (int i = 0; i < allCandidates.size(); i++) {
      EdgePortCandidate epc = allCandidates.get(i);
      // get the previously created candidate list, if there exists one, yet
      IEdgeMap pcMap = epc.atSource ? edge2SPC : edge2TPC;
      Collection<PortCandidate> candidateList = (Collection<PortCandidate>)pcMap.get(epc.edge);
      if (epc.candidate.isFixed()) {
        if (candidateList != null) {
          // there is already a candidate list. As all strong edge port candidates are processed before any weak ones,
          // a strong port candidate was already set for this edge and we don't need more strong candidates at this side
          // for now
          unusedStrongCandidates.add(epc);
        } else {
          // get direction of this candidate relative to the flow...
          int flowDirection = this.getFlowDirection(epc.candidate.getDirection().value());
          // ... and check if this side is already blocked by another candidate
          if (strongCandidateAtSide[flowDirection]) {
          } else {
            // side isn't free
            // -> continue;
            // side is empty -> set this candidate
            candidateList = new ArrayList<>();
            pcMap.set(epc.edge, candidateList);
            candidateList.add(epc.candidate);
            strongCandidateAtSide[flowDirection] = true;
          }
        }
      } else {
        // weak candidate: add to list
        if (candidateList == null) {
          candidateList = new ArrayList<>();
          pcMap.set(epc.edge, candidateList);
        }
        candidateList.add(epc.candidate);
      }
    }

    {
      // distribute additional strong candidates to free sides
      for (int i = 0; i < unusedStrongCandidates.size(); i++) {
        EdgePortCandidate epc = unusedStrongCandidates.get(i);
        Collection<PortCandidate> candidateList = (Collection<PortCandidate>)(epc.atSource ? edge2SPC : edge2TPC).get(epc.edge);
        int flowDirection = this.getFlowDirection(epc.candidate.getDirection().value());
        if (!strongCandidateAtSide[flowDirection]) {
          // side is empty -> set this candidate but with increased costs so the first distributed fixed candidate is preferred
          PortCandidate c = epc.candidate;
          PortCandidate newCandidate = PortCandidate.createCandidate(c.getXOffset(), c.getYOffset(), c.getDirection(), COSTS_STRONG_CANDIDATE_NOT_PREFERRED);
          candidateList.add(newCandidate);
          strongCandidateAtSide[flowDirection] = true;
        }
      }
    }
  }

  private PortCandidate createCandidate( int direction, boolean strong, double costs, YDimension nodeSize ) {
    byte side = FLOW2_PORT_CONSTRAINT[this.layoutOrientation.value()][direction];
    if (strong) {
      double xOffset = 0;
      double yOffset = 0;
      switch (PortSide.fromOrdinal(side)) {
        case EAST:
          {
            xOffset = nodeSize.getWidth() / 2;
            break;
          }

        case WEST:
          {
            xOffset = -nodeSize.getWidth() / 2;
            break;
          }

        case NORTH:
          {
            yOffset = -nodeSize.getHeight() / 2;
            break;
          }

        case SOUTH:
          {
            yOffset = nodeSize.getHeight() / 2;
            break;
          }
      }
      return PortCandidate.createCandidate(xOffset, yOffset, PortDirections.fromOrdinal((int)(side)), costs);
    } else {
      return PortCandidate.createCandidate(PortDirections.fromOrdinal((int)(side)), costs);
    }
  }

  private int getOppositeDirection( int direction ) {
    if (direction == NO_FLOW) {
      return direction;
    }
    return (direction + 2) % 4;
  }

  private int getFlowDirection( int constraintSide ) {
    boolean horizontal = this.isHorizontalOrientation();
    switch (PortSide.fromOrdinal(constraintSide)) {
      case NORTH:
        {
          return horizontal ? LEFT_OF_FLOW : AGAINST_FLOW;
        }

      case SOUTH:
        {
          return horizontal ? RIGHT_OF_FLOW : IN_FLOW;
        }

      case WEST:
        {
          return horizontal ? AGAINST_FLOW : RIGHT_OF_FLOW;
        }

      case EAST:
        {
          return horizontal ? IN_FLOW : LEFT_OF_FLOW;
        }

      default:
        {
          return -1;
        }
    }
  }

  private static final double MIN_DIST = 10;

  private int toLayerDirection( Node node, Node other, LayoutGraph graph, YPoint oppositePort ) {
    INodeLayout nl = graph.getLayout(node);
    if (this.isHorizontalOrientation()) {
      double n2Port = graph.getCenterX(other) + oppositePort.getX();
      if (nl.getX() + nl.getWidth() + MIN_DIST < n2Port) {
        return IN_FLOW;
      } else {
        if (n2Port + MIN_DIST < nl.getX()) {
          return AGAINST_FLOW;
        }
      }
    } else {
      double n2Port = graph.getCenterY(other) + oppositePort.getY();
      if (nl.getY() + nl.getHeight() + MIN_DIST < n2Port) {
        return IN_FLOW;
      } else {
        if (n2Port + MIN_DIST < nl.getY()) {
          return AGAINST_FLOW;
        }
      }
    }
    return NO_FLOW;
  }

  private int toSideDirection( Node node, Node other, LayoutGraph graph, YPoint oppositePort ) {
    INodeLayout nl = graph.getLayout(node);
    if (!this.isHorizontalOrientation()) {
      double n2Port = graph.getCenterX(other) + oppositePort.getX();
      if (nl.getX() + nl.getWidth() + MIN_DIST < n2Port) {
        return LEFT_OF_FLOW;
      } else {
        if (n2Port + MIN_DIST < nl.getX()) {
          return RIGHT_OF_FLOW;
        }
      }
    } else {
      double n2Port = graph.getCenterY(other) + oppositePort.getY();
      if (nl.getY() + nl.getHeight() + MIN_DIST < n2Port) {
        return RIGHT_OF_FLOW;
      } else {
        if (n2Port + MIN_DIST < nl.getY()) {
          return LEFT_OF_FLOW;
        }
      }
    }
    return NO_FLOW;
  }

  @Override
  protected Comparator<Object> createDefaultEdgeOrderComparator( LayoutGraph graph, PathSearchConfiguration configuration ) {
    return new DefaultEdgeOrderComparator(graph);
  }

  static class DefaultEdgeOrderComparator implements Comparator<Object> {
    private LayoutGraph graph;

    DefaultEdgeOrderComparator( LayoutGraph graph ) {
      this.graph = graph;
    }

    public int compare( Object o1, Object o2 ) {
      Edge e1 = (Edge)o1;
      Edge e2 = (Edge)o2;
      boolean e1IsSequenceFlow = BpmnEdgeRouter.isSequenceFlowOrInvalid(e1, this.graph);
      boolean e2IsSequenceFlow = BpmnEdgeRouter.isSequenceFlowOrInvalid(e2, this.graph);
      if (e1IsSequenceFlow && !e2IsSequenceFlow) {
        return -1;
      } else {
        if (!e1IsSequenceFlow && e2IsSequenceFlow) {
          return 1;
        } else {
          boolean isStraightLineE1 = this.graph.getPointList(e1).size() == 0;
          boolean isStraightLineE2 = this.graph.getPointList(e2).size() == 0;
          if (isStraightLineE1 && !isStraightLineE2) {
            return -1;
          } else {
            if (!isStraightLineE1 && isStraightLineE2) {
              return 1;
            } else {
              return 0;
            }
          }
        }
      }
    }
  }

  static final boolean isSequenceFlowOrInvalid( Edge e, LayoutGraph g ) {
    int type = BpmnElementTypes.getType(e, g).value();
    return BpmnElementTypes.isSequenceFlow(EdgeType.fromOrdinal((type))) || BpmnElementTypes.isInvalidEdgeType(EdgeType.fromOrdinal((type)));
  }

  private void setMonotonicPathRestriction( Edge e, EdgeLayoutDescriptor descriptor, LayoutGraph graph ) {
    EdgeType type = BpmnElementTypes.getType(e, graph);
    switch (type) {
      case ASSOCIATION:
      case MESSAGE_FLOW:
      case CONVERSATION_LINK:
        {
          descriptor.setMonotonicPathRestriction(MonotonicPathRestriction.NONE);
          break;
        }

      default:
        {
          // EDGE_TYPE_SEQUENCE_FLOW or invalid
          if (this.isHorizontalOrientation()) {
            descriptor.setMonotonicPathRestriction(MonotonicPathRestriction.HORIZONTAL);
          } else {
            descriptor.setMonotonicPathRestriction(MonotonicPathRestriction.VERTICAL);
          }
          break;
        }
    }
  }

  private boolean isHorizontalOrientation() {
    return this.layoutOrientation == LayoutOrientation.LEFT_TO_RIGHT;
  }

  @Override
  public void applyLayout( LayoutGraph graph ) {
    if (getScope() == Scope.ROUTE_ALL_EDGES || graph.getDataProvider(getAffectedEdgesDpKey()) != null) {
      //backup data providers
      IDataProvider oldEdgeSelection = graph.getDataProvider(getAffectedEdgesDpKey());
      IDataProvider oldSourcePortCandidates = graph.getDataProvider(PortCandidate.SOURCE_PORT_CANDIDATE_COLLECTION_DPKEY);
      IDataProvider oldTargetPortCandidates = graph.getDataProvider(PortCandidate.TARGET_PORT_CANDIDATE_COLLECTION_DPKEY);
      IEdgeMap edge2SPC = Maps.createHashedEdgeMap();
      graph.addDataProvider(PortCandidate.SOURCE_PORT_CANDIDATE_COLLECTION_DPKEY, edge2SPC);
      IEdgeMap edge2TPC = Maps.createHashedEdgeMap();
      graph.addDataProvider(PortCandidate.TARGET_PORT_CANDIDATE_COLLECTION_DPKEY, edge2TPC);
      IEdgeMap descriptorMap = Maps.createHashedEdgeMap();
      for (IEdgeCursor ec = graph.getEdgeCursor(); ec.ok(); ec.next()) {
        Edge e = ec.edge();
        EdgeLayoutDescriptor edgeLayoutDescriptor = getDefaultEdgeLayoutDescriptor().createCopy();
        descriptorMap.set(e, edgeLayoutDescriptor);
        this.setMonotonicPathRestriction(e, edgeLayoutDescriptor, graph);
      }
      graph.addDataProvider(EDGE_LAYOUT_DESCRIPTOR_DPKEY, descriptorMap);
      for (INodeCursor nc = graph.getNodeCursor(); nc.ok(); nc.next()) {
        this.createPortCandidates(nc.node(), graph, edge2SPC, edge2TPC);
      }
      super.applyLayout(graph);
      //restore original settings
      graph.removeDataProvider(EDGE_LAYOUT_DESCRIPTOR_DPKEY);
      if (oldSourcePortCandidates != null) {
        graph.addDataProvider(PortCandidate.SOURCE_PORT_CANDIDATE_COLLECTION_DPKEY, oldSourcePortCandidates);
      } else {
        graph.removeDataProvider(PortCandidate.SOURCE_PORT_CANDIDATE_COLLECTION_DPKEY);
      }
      if (oldTargetPortCandidates != null) {
        graph.addDataProvider(PortCandidate.TARGET_PORT_CANDIDATE_COLLECTION_DPKEY, oldTargetPortCandidates);
      } else {
        graph.removeDataProvider(PortCandidate.TARGET_PORT_CANDIDATE_COLLECTION_DPKEY);
      }
      if (oldEdgeSelection != null) {
        graph.addDataProvider(getAffectedEdgesDpKey(), oldEdgeSelection);
      } else {
        graph.removeDataProvider(getAffectedEdgesDpKey());
      }
    }
  }

  /**
   * Holds a PortCandidate for the source or target side of an edge and its priority.
   */
  static class EdgePortCandidate {
    public double priority;

    public Edge edge;

    public boolean atSource;

    public PortCandidate candidate;

    EdgePortCandidate( PortCandidate candidate, double priority, Edge edge, boolean atSource ) {
      this.candidate = candidate;
      this.priority = priority;
      this.edge = edge;
      this.atSource = atSource;
    }
  }

  static class EdgePriorityComparator implements Comparator<EdgePortCandidate> {
    public int compare( EdgePortCandidate epc1, EdgePortCandidate epc2 ) {
      double dp = epc1.priority - epc2.priority;
      return dp < 0 ? -1 : dp > 0 ? 1 : 0;
    }
  }

}
