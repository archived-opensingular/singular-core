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
package org.opensingular.flow.core.renderer.bpmn.view;

import org.opensingular.flow.core.renderer.bpmn.layout.*;
import com.yworks.yfiles.annotations.Obfuscation;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.IMapperRegistry;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.IPortOwner;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.layout.NodeHalo;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.PortConstraintKeys;
import com.yworks.yfiles.layout.PortSide;
import org.opensingular.flow.core.renderer.bpmn.layout.EdgeType;

/**
 * Convenience class that prepares BPMN layout information provided by the styles for assignment of layout information
 * calculated by {@link BpmnLayout}.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class BpmnLayoutConfigurator {
  private IMapper<IEdge, PortConstraint> oldSpcDp;

  private IMapper<IEdge, PortConstraint> oldTpcDp;

  public void prepareAll( IGraph graph ) {
    Mapper<INode, NodeType> nodeTypes = new Mapper<>();
    for (INode node : graph.getNodes()) {
      nodeTypes.setValue(node, getNodeType(node.getStyle()));
    }
    IMapperRegistry mapperRegistry = graph.getMapperRegistry();

    mapperRegistry.addMapper(INode.class, NodeType.class, BpmnLayout.BPMN_NODE_TYPE_DP_KEY, nodeTypes);

    oldSpcDp = mapperRegistry.getMapper(IEdge.class, PortConstraint.class, PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY);
    oldTpcDp = mapperRegistry.getMapper(IEdge.class, PortConstraint.class, PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY);
    Mapper<IEdge, org.opensingular.flow.core.renderer.bpmn.layout.EdgeType> edgeTypes = new Mapper<>();
    Mapper<IEdge, PortConstraint> edgeSpc = new Mapper<>();
    Mapper<IEdge, PortConstraint> edgeTpc = new Mapper<>();

    for (IEdge edge : graph.getEdges()) {
      edgeTypes.setValue(edge, getEdgeType(edge.getStyle()));

      PortConstraint spc = oldSpcDp != null ? oldSpcDp.getValue(edge) : null;
      if (edge.getSourcePort().getStyle() instanceof EventPortStyle && spc == null) {
        spc = PortConstraint.create(getSide(edge, true));
      }
      edgeSpc.setValue(edge, spc);
      PortConstraint tpc = oldTpcDp != null ? oldTpcDp.getValue(edge) : null;
      if (edge.getTargetPort().getStyle() instanceof EventPortStyle && tpc == null) {
        tpc = PortConstraint.create(getSide(edge, false));
      }
      edgeTpc.setValue(edge, tpc);
    }

    Mapper<INode, NodeHalo> nodeHalos = new Mapper<>();
    for (INode node : graph.getNodes()) {
      double top = 0.0;
      double left = 0.0;
      double bottom = 0.0;
      double right = 0.0;

      for (IPort port : node.getPorts()) {
        if (port.getStyle() instanceof EventPortStyle) {
          SizeD renderSize = ((EventPortStyle)port.getStyle()).getRenderSize();
          PointD location = port.getLocation();
          top = Math.max(top, node.getLayout().getY() - location.y - renderSize.height / 2);
          left = Math.max(left, node.getLayout().getX() - location.x - renderSize.width / 2);
          bottom = Math.max(bottom, location.y + renderSize.height / 2 - node.getLayout().getMaxY());
          right = Math.max(right, location.x + renderSize.width / 2 - node.getLayout().getMaxX());
        }
      }
      nodeHalos.setValue(node, NodeHalo.create(top, left, bottom, right));
    }

    mapperRegistry.addMapper(IEdge.class, EdgeType.class, BpmnLayout.BPMN_EDGE_TYPE_DP_KEY, edgeTypes);
    if (oldSpcDp != null) {
      mapperRegistry.removeMapper(PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY);
    }
    mapperRegistry.addMapper(IEdge.class, PortConstraint.class,
        PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY,
        edgeSpc);
    if (oldTpcDp != null) {
      mapperRegistry.removeMapper(PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY);
    }
    mapperRegistry.addMapper(IEdge.class, PortConstraint.class,
        PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY,
        edgeTpc);
    mapperRegistry.addMapper(INode.class, NodeHalo.class, NodeHalo.NODE_HALO_DPKEY, nodeHalos);
  }

  private static NodeType getNodeType( INodeStyle style ) {
    EventNodeStyle eventNodeStyle = (style instanceof EventNodeStyle) ? (EventNodeStyle)style : null;
    if (eventNodeStyle != null) {
      switch (eventNodeStyle.getCharacteristic()) {
        case START:
        case SUB_PROCESS_INTERRUPTING:
        case SUB_PROCESS_NON_INTERRUPTING:
          return NodeType.START_EVENT;
        case THROWING:
        case END:
          return NodeType.END_EVENT;
        default:
          return NodeType.EVENT;
      }
    } else if (style instanceof GatewayNodeStyle) {
      return NodeType.GATEWAY;
    } else if (style instanceof AnnotationNodeStyle) {
      return NodeType.ANNOTATION;
    } else if (style instanceof DataObjectNodeStyle || style instanceof DataStoreNodeStyle || style instanceof GroupNodeStyle) {
      return NodeType.ARTIFACT;
    } else if (style instanceof PoolNodeStyle) {
      return NodeType.POOL;
    } else if (style instanceof ChoreographyNodeStyle) {
      return NodeType.CHOREOGRAPHY;
    } else if (style instanceof ConversationNodeStyle) {
      return NodeType.CONVERSATION;
    }

    ActivityNodeStyle activityNodeStyle = (style instanceof ActivityNodeStyle) ? (ActivityNodeStyle)style : null;
    if (activityNodeStyle != null && (activityNodeStyle.getActivityType() == ActivityType.SUB_PROCESS || activityNodeStyle.getActivityType() == ActivityType.EVENT_SUB_PROCESS)) {
      return NodeType.SUB_PROCESS;
    }

    return NodeType.TASK;
  }

  private static EdgeType getEdgeType(IEdgeStyle style ) {
    BpmnEdgeStyle bpmnEdgeStyle = (style instanceof BpmnEdgeStyle) ? (BpmnEdgeStyle)style : null;
    if (bpmnEdgeStyle != null) {
      switch (bpmnEdgeStyle.getType()) {
        case MESSAGE_FLOW:
          return EdgeType.MESSAGE_FLOW;
        case ASSOCIATION:
        case DIRECTED_ASSOCIATION:
        case BIDIRECTED_ASSOCIATION:
          return EdgeType.ASSOCIATION;
        case CONVERSATION:
          return EdgeType.CONVERSATION_LINK;
        case SEQUENCE_FLOW:
        case DEFAULT_FLOW:
        case CONDITIONAL_FLOW:
          return EdgeType.SEQUENCE_FLOW;
      }
    }
    return EdgeType.SEQUENCE_FLOW;
  }

  private static PortSide getSide(IEdge edge, boolean atSource) {
    IPort port = atSource ? edge.getSourcePort() : edge.getTargetPort();
    IPortOwner owner = port.getOwner();
    if (!(owner instanceof INode)) {
      return PortSide.ANY;
    }
    INode node = (INode) owner;
    PointD relPortLocation = PointD.subtract(port.getLocation(), node.getLayout().getCenter());

    // calculate relative port position scaled by the node size
    double sdx = relPortLocation.x / (node.getLayout().getWidth() / 2);
    double sdy = relPortLocation.y / (node.getLayout().getHeight() / 2);

    if (Math.abs(sdx) > Math.abs(sdy)) {
      // east or west
      if (sdx < 0) {
        return PortSide.WEST;
      } else {
        return PortSide.EAST;
      }
    } else if (Math.abs(sdx) < Math.abs(sdy)) {
      if (sdy < 0) {
        return PortSide.NORTH;
      } else {
        return PortSide.SOUTH;
      }
    }

    // port is somewhere at the diagonals of the node bounds
    // so we can't decide the port side based on the port location
    // better use the attached segment to decide on the port side
    return getSideFromSegment(edge, atSource);
  }

  private static PortSide getSideFromSegment( IEdge edge, boolean atSource ) {
    IPort port = atSource ? edge.getSourcePort() : edge.getTargetPort();
    IPort opposite = atSource ? edge.getTargetPort() : edge.getSourcePort();
    IPoint from = port.getLocation();

    boolean hasBends = edge.getBends().size() > 0;

    IPoint to = null;
    if(atSource) {
      to = (hasBends ? edge.getBends().first().getLocation() : opposite.getLocation());
    }
    else {
      to = (hasBends ? edge.getBends().last().getLocation() : opposite.getLocation());
    }

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

  public void restoreAll( IGraph graph ) {
    IMapperRegistry mapperRegistry = graph.getMapperRegistry();
    mapperRegistry.removeMapper(BpmnLayout.BPMN_NODE_TYPE_DP_KEY);
    mapperRegistry.removeMapper(BpmnLayout.BPMN_EDGE_TYPE_DP_KEY);
    mapperRegistry.removeMapper(PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY);
    if (oldSpcDp != null) {
      mapperRegistry.addMapper(IEdge.class, PortConstraint.class,
          PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY,
          oldSpcDp);
      oldSpcDp = null;
    }
    mapperRegistry.removeMapper(PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY);
    if (oldTpcDp != null) {
      mapperRegistry.addMapper(IEdge.class, PortConstraint.class,
          PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY,
          oldTpcDp);
      oldTpcDp = null;
    }
    mapperRegistry.removeMapper(NodeHalo.NODE_HALO_DPKEY);
  }
}
