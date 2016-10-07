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
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.IEdgeCursor;
import com.yworks.yfiles.algorithms.INodeCursor;
import com.yworks.yfiles.algorithms.ListCell;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YRectangle;
import com.yworks.yfiles.layout.hierarchic.IEdgeData;
import com.yworks.yfiles.layout.hierarchic.IItemFactory;
import com.yworks.yfiles.layout.hierarchic.ILayer;
import com.yworks.yfiles.layout.hierarchic.ILayers;
import com.yworks.yfiles.layout.hierarchic.ILayoutDataProvider;
import com.yworks.yfiles.layout.hierarchic.INodeData;
import com.yworks.yfiles.layout.hierarchic.IPortConstraintOptimizer;
import com.yworks.yfiles.layout.hierarchic.NodeDataType;
import com.yworks.yfiles.layout.hierarchic.SwimlaneDescriptor;
import com.yworks.yfiles.layout.INodeLayout;
import com.yworks.yfiles.layout.LabelLayoutData;
import com.yworks.yfiles.layout.LabelLayoutKeys;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.PortConstraint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A port optimizer wrapper that places exterior node labels after the sequencing phase.
 * <p>
 * Although implementing the {@link IPortConstraintOptimizer IPortConstraintOptimizer} interface, it doesn't modify any
 * port constraints but only uses the callbacks to place the labels after the sequencing phase and before the nodes are
 * placed.
 * </p>
 * <p>
 * Only node labels that have been translated by a {@link com.yworks.yfiles.layout.LabelLayoutTranslator} and that exceed
 * their owners bounds are placed.
 * </p>
 */
class NodeLabelingPortOptimizer implements IPortConstraintOptimizer {
  /**
   * Symbolic position specifier for label placements: 7 0 1 6 2 5 4 3.
   */
  private static final int NORTH = 0;

  private static final int NORTH_EAST = 1;

  private static final int EAST = 2;

  private static final int SOUTH_EAST = 3;

  private static final int SOUTH = 4;

  private static final int SOUTH_WEST = 5;

  private static final int WEST = 6;

  private static final int NORTH_WEST = 7;

  private static final byte EDGE_ORIENTATION_NONE = 0;

  private static final byte EDGE_ORIENTATION_ALIGNED = 1;

  private static final byte EDGE_ORIENTATION_EAST = 2;

  private static final byte EDGE_ORIENTATION_WEST = 3;

  private static final int COSTS_CORNER = 2;

  private static final int COSTS_EDGE_AT_SIDE = 10;

  private IPortConstraintOptimizer coreOptimizer;

  /**
   * Creates a new instance using the specified core port optimizer.
   *
   * @param coreOptimizer The optimizer to delegate to.
   */
  public NodeLabelingPortOptimizer( IPortConstraintOptimizer coreOptimizer ) {
    this.coreOptimizer = coreOptimizer;
  }

  public void optimizeAfterLayering( LayoutGraph graph, ILayers layers, ILayoutDataProvider ldp, IItemFactory itemFactory ) {
    if (this.coreOptimizer != null) {
      this.coreOptimizer.optimizeAfterLayering(graph, layers, ldp, itemFactory);
    }
  }

  public void optimizeAfterSequencing( LayoutGraph graph, ILayers layers, ILayoutDataProvider ldp, IItemFactory itemFactory ) {
    if (this.coreOptimizer != null) {
      this.coreOptimizer.optimizeAfterSequencing(graph, layers, ldp, itemFactory);
    }
    IDataProvider lldp = graph.getDataProvider(LabelLayoutKeys.NODE_LABEL_LAYOUT_DPKEY);
    if (lldp == null) {
      return;
    }
    // if available use the node alignment info to decide where an edge might be placed
    IDataProvider nodesToAlignWith = graph.getDataProvider("y.layout.hierarchic.incremental.SimlexNodePlacer.NODE_TO_ALIGN_WITH");
    // we store which position at the current (or top) layer is aligned with which position on the top/bottom (or current) layer
    int[] top2currentLayer = null;
    int[] current2topLayer = null;
    int[] current2bottomLayer = null;
    // alignment information is from higher to lower layers to we iterate the layers from bottom to top
    for (int layerIndex = layers.size() - 1; layerIndex >= 0; layerIndex--) {
      ILayer layer = layers.getLayer(layerIndex);
      // update alignment infos
      if (nodesToAlignWith != null) {
        current2bottomLayer = top2currentLayer;
        if (layerIndex > 0) {
          // the new arrays are initialized with -1 to indicate that there is no alignment found, yet
          ILayer topLayer = layers.getLayer(layerIndex - 1);
          current2topLayer = new int[layer.getList().size()];

          fillArray(current2topLayer, -1);
          top2currentLayer = new int[topLayer.getList().size()];
          fillArray(top2currentLayer, -1);
          for (INodeCursor nc = layer.getList().nodes(); nc.ok(); nc.next()) {
            Node node = nc.node();
            Node alignedNode = (Node)nodesToAlignWith.get(node);
            if (alignedNode != null) {
              INodeData alignedNodeData = ldp.getNodeData(alignedNode);
              if (alignedNodeData.getLayer() == layerIndex - 1) {
                // we consider only alignments between neighboured layers
                int nodePosition = ldp.getNodeData(node).getPosition();
                int alignedNodePosition = alignedNodeData.getPosition();
                current2topLayer[nodePosition] = alignedNodePosition;
                top2currentLayer[alignedNodePosition] = nodePosition;
              }
            }
          }
        } else {
          current2topLayer = null;
          top2currentLayer = null;
        }
      }

      {
        // place exterior node labels
        for (INodeCursor nc = layer.getList().nodes(); nc.ok(); nc.next()) {
          Node node = nc.node();
          INodeData nodeData = ldp.getNodeData(node);
          if (nodeData == null || nodeData.getType() != NodeDataType.NORMAL) {
            continue;
          }
          LabelLayoutData[] lld = (LabelLayoutData[])lldp.get(node);
          if (lld != null && lld.length > 0) {
            INodeLayout nl = graph.getNodeLayout(node);
            // only exterior labels are placed
            YRectangle relativeNodeBounds = new YRectangle(-nl.getWidth() / 2, -nl.getHeight() / 2, nl.getWidth(), nl.getHeight());
            ArrayList<LabelLayoutData> exteriorLabels = new ArrayList<>();
            for (int i = 0; i < lld.length; i++) {
              LabelLayoutData labelLayout = lld[i];
              YRectangle relativeLabelBounds = labelLayout.getBounds().getBoundingBox();
              if (!relativeNodeBounds.contains(relativeLabelBounds)) {
                exteriorLabels.add(labelLayout);
              }
            }
            // as we only consider 8 outer regions to place labels, nodes with more labels are skipped.
            if (0 < exteriorLabels.size() && exteriorLabels.size() <= 8) {
              // edges that might be routed at these sides increase the costs to place a label there
              double[] labelCostsAtSide = this.calculateLabelPositionCosts(node, current2topLayer, current2bottomLayer, ldp);
              // place the relevant labels at the node sides having the lowest label costs
              this.distributeLabels(nl, exteriorLabels, labelCostsAtSide);
            }
          }
        }
      }
    }
  }

  private static void fillArray( int[] a, int val ) {
    for (int i = 0; i < a.length; i++) {
      a[i] = val;
    }
  }

  /**
   * Calculates and returns the costs to place a label at each of the eight sectors around the specified node.
   * <p>
   * For all edges attached to the node a probable direction is calculated by a heuristic and the costs of the sectors close
   * to this direction are increased.
   * </p>
   */
  private double[] calculateLabelPositionCosts( Node node, int[] current2topLayer, int[] current2bottomLayer, ILayoutDataProvider ldp ) {
    double[] labelCostsAtSide = new double[8];
    // the corner sides are not preferred for the labels so we use initial costs for them
    labelCostsAtSide[NORTH_EAST] = COSTS_CORNER;
    labelCostsAtSide[NORTH_WEST] = COSTS_CORNER;
    labelCostsAtSide[SOUTH_EAST] = COSTS_CORNER;
    labelCostsAtSide[SOUTH_WEST] = COSTS_CORNER;
    INodeData nodeData = ldp.getNodeData(node);
    int position = nodeData.getPosition();
    // add label costs for same layer edges
    for (ListCell sleCell = nodeData.getFirstSameLayerEdgeCell(); sleCell != null; sleCell = sleCell.succ()) {
      Node opposite = ((Edge)sleCell.getInfo()).opposite(node);
      INodeData oppositeData = ldp.getNodeData(opposite);
      if (oppositeData != null) {
        int oppositePosition = oppositeData.getPosition();
        if (position < oppositePosition) {
          this.addEdgeCosts(EAST, 1, COSTS_EDGE_AT_SIDE, labelCostsAtSide);
        } else {
          this.addEdgeCosts(WEST, 1, COSTS_EDGE_AT_SIDE, labelCostsAtSide);
        }
      }
    }
    // add label costs for other edges
    for (IEdgeCursor ec = node.getEdgeCursor(); ec.ok(); ec.next()) {
      Edge edge = ec.edge();
      IEdgeData edgeData = ldp.getEdgeData(edge);
      Node opposite = edge.opposite(node);
      // determine possible edge orientation based on node alignment and swim lane information
      byte edgeOrientation = this.determineEdgeOrientation(node, opposite, current2topLayer, current2bottomLayer, ldp);
      // determine basic edge direction
      PortConstraint portConstraint = edge.source().equals(node) ? edgeData.getSourcePortConstraint() : edgeData.getTargetPortConstraint();
      int basicSide = nodeData.getLayer() < ldp.getNodeData(opposite).getLayer() ? SOUTH : NORTH;
      basicSide = portConstraint != null ? this.toLabelSide(portConstraint) : basicSide;
      // determine the main direction of the edge
      int mainSide = this.calculateMainEdgeSide(basicSide, edgeOrientation);
      // determine in what surrounding of the main edge side costs shall be applied to label positions
      int spreading = this.calculateSpreading(mainSide, edgeOrientation);
      this.addEdgeCosts(mainSide, spreading, COSTS_EDGE_AT_SIDE, labelCostsAtSide);
    }
    return labelCostsAtSide;
  }

  private byte determineEdgeOrientation( Node node, Node opposite, int[] current2topLayer, int[] current2bottomLayer, ILayoutDataProvider ldp ) {
    INodeData nodeData = ldp.getNodeData(node);
    INodeData oppositeData = ldp.getNodeData(opposite);
    if (oppositeData == null) {
      return EDGE_ORIENTATION_NONE;
    }
    SwimlaneDescriptor laneDesc = nodeData.getSwimLaneDescriptor();
    int laneIndex = laneDesc != null ? laneDesc.getComputedLaneIndex() : -1;
    SwimlaneDescriptor oppositeLaneDesc = oppositeData.getSwimLaneDescriptor();
    int oppositeLaneIndex = oppositeLaneDesc != null ? oppositeLaneDesc.getComputedLaneIndex() : -1;
    int[] node2other = null;
    if (oppositeData.getLayer() == nodeData.getLayer() - 1) {
      // edge from/to top layer
      node2other = current2topLayer;
    } else {
      if (oppositeData.getLayer() == nodeData.getLayer() + 1) {
        node2other = current2bottomLayer;
      }
    }
    byte edgeOrientation = EDGE_ORIENTATION_NONE;
    if (laneIndex < oppositeLaneIndex) {
      edgeOrientation = EDGE_ORIENTATION_EAST;
    } else {
      if (laneIndex > oppositeLaneIndex) {
        edgeOrientation = EDGE_ORIENTATION_WEST;
      } else {
        if (node2other != null) {
          int position = nodeData.getPosition();
          int oppositePosition = oppositeData.getPosition();
          if (node2other[position] == oppositePosition) {
            // node is aligned with opposite
            edgeOrientation = EDGE_ORIENTATION_ALIGNED;
          } else {
            if (node2other[position] != -1) {
              // node is aligned, but not with opposite
              edgeOrientation = node2other[position] > oppositePosition ? EDGE_ORIENTATION_WEST : EDGE_ORIENTATION_EAST;
            } else {
              // look for closest node to the west that is aligned
              for (int i = position - 1; i >= 0; i--) {
                if (node2other[i] != -1) {
                  edgeOrientation = node2other[i] >= oppositePosition ? EDGE_ORIENTATION_WEST : edgeOrientation;
                  break;
                }
              }

              {
                // look for closest node to the east that is aligned
                for (int i = position + 1; i < node2other.length; i++) {
                  if (node2other[i] != -1) {
                    edgeOrientation = node2other[i] <= oppositePosition ? EDGE_ORIENTATION_EAST : edgeOrientation;
                    break;
                  }
                }
              }
            }
          }
        }
      }
    }
    return edgeOrientation;
  }

  private void addEdgeCosts( int side, int spreading, int mainSideCosts, double[] labelCostsAtSide ) {
    double costs = mainSideCosts;
    labelCostsAtSide[side] += costs;
    for (int i = 1; i <= spreading; i++) {
      costs = costs / 2;
      labelCostsAtSide[(side + i) % 8] += costs;
      labelCostsAtSide[(side + 8 - i) % 8] += costs;
    }
  }

  /**
   * Adjust the basic side depending on the edge orientation.
   */
  private int calculateMainEdgeSide( int basicSide, byte edgeOrientation ) {
    switch (edgeOrientation) {
      case EDGE_ORIENTATION_EAST:
        {
          return basicSide == NORTH ? NORTH_EAST : basicSide == SOUTH ? SOUTH_EAST : basicSide;
        }

      case EDGE_ORIENTATION_WEST:
        {
          return basicSide == NORTH ? NORTH_WEST : basicSide == SOUTH ? SOUTH_WEST : basicSide;
        }

      default:
        {
          return basicSide;
        }
    }
  }

  private int calculateSpreading( int side, byte edgeOrientation ) {
    if (edgeOrientation == EDGE_ORIENTATION_ALIGNED) {
      // edges between aligned nodes should go straight -> no spreading
      return 0;
    } else {
      if (edgeOrientation == EDGE_ORIENTATION_EAST && side != WEST) {
        // if the edge has an east or west orientation not contradicting the side, we only need a small spreading
        return 1;
      } else {
        if (edgeOrientation == EDGE_ORIENTATION_WEST && side != EAST) {
          return 1;
        } else {
          // we don't know much about the edge direction, so we use a bigger spreading
          return 2;
        }
      }
    }
  }

  private int toLabelSide( PortConstraint constraint ) {
    switch (constraint.getSide()) {
      case NORTH:
        {
          return NORTH;
        }

      case EAST:
        {
          return EAST;
        }

      case SOUTH:
        {
          return SOUTH;
        }

      case WEST:
        {
          return WEST;
        }

      default:
        {
          return SOUTH;
        }
    }
  }

  /**
   * Places the relevant labels at the node sides having the lowest label costs.
   */
  private void distributeLabels( INodeLayout nodeLayout, ArrayList<LabelLayoutData> labels, double[] labelCostsAtSide ) {
    // sort the sides by increasing costs...
    ArrayList<LabelPlacementCost> placementCosts = new ArrayList<LabelPlacementCost>();
    for (int i = 0; i < 8; i++) {
      placementCosts.add(new LabelPlacementCost(i, labelCostsAtSide[i]));
    }
    Collections.sort(placementCosts, new LabelPlacementCostComparator());
    // ... and distribute the labels
    for (int i = 0; i < labels.size(); i++) {
      LabelLayoutData labelLayoutData = labels.get(i);
      LabelPlacementCost labelPlacementCost = placementCosts.get(i);
      int side = labelPlacementCost.side;
      this.putAtSide(labelLayoutData, nodeLayout, side);
    }
  }

  /**
   * Sets the LabelLayoutData location to be at the specified side of the NodeLayout.
   */
  private void putAtSide( LabelLayoutData lld, INodeLayout nodeLayout, int side ) {
    double x = 0;
    double y = 0;
    switch (side) {
      case WEST:
      case NORTH_WEST:
      case SOUTH_WEST:
        {
          x = -nodeLayout.getWidth() / 2 - lld.getWidth();
          break;
        }

      case EAST:
      case NORTH_EAST:
      case SOUTH_EAST:
        {
          x = nodeLayout.getWidth() / 2;
          break;
        }

      case NORTH:
      case SOUTH:
        {
          x = -lld.getWidth() / 2;
          break;
        }
    }
    switch (side) {
      case NORTH_WEST:
      case NORTH:
      case NORTH_EAST:
        {
          y = -nodeLayout.getHeight() / 2 - lld.getHeight();
          break;
        }

      case SOUTH_WEST:
      case SOUTH:
      case SOUTH_EAST:
        {
          y = nodeLayout.getHeight() / 2;
          break;
        }

      case WEST:
      case EAST:
        {
          y = -lld.getHeight() / 2;
          break;
        }
    }
    lld.setLocation(x, y);
  }

  /**
   * Struct representing the costs to place a label at a specified node side.
   */
  private static class LabelPlacementCost {
    public int side;

    public double costs;

    LabelPlacementCost( int side, double costs ) {
      this.side = side;
      this.costs = costs;
    }
  }

  private static class LabelPlacementCostComparator implements Comparator<LabelPlacementCost> {
    public final int compare( LabelPlacementCost lpc1, LabelPlacementCost lpc2 ) {
      return lpc1.costs < lpc2.costs ? -1 : lpc1.costs > lpc2.costs ? 1 : 0;
    }
  }
}
