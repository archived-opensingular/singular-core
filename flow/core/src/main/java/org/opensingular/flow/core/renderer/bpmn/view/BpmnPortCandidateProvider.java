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

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.AdjacencyTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.CreateEdgeInputMode;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;

import java.util.ArrayList;

/**
 * Provides some existing ports as well as ports on the north, south, east and west center of the visual bounds of a BPMN
 * node.
 * <p>
 * An existing port is provided if it either uses an {@link EventPortStyle} or is used by at least one edge.
 * </p>
 */
public class BpmnPortCandidateProvider extends AbstractPortCandidateProvider {
  private final INode node;

  public BpmnPortCandidateProvider( INode node ) {
    this.node = node;
  }

  @Override
  protected Iterable<IPortCandidate> getPortCandidates( IInputModeContext context ) {
    ArrayList<IPortCandidate> portCandidates = new ArrayList<>();

    // provide existing ports as candidates only if they use EventPortStyle and have no edges attached to them.
    for (IPort port : node.getPorts()) {
      if (port.getStyle() instanceof EventPortStyle && context.lookup(IGraph.class).edgesAt(port,
          AdjacencyTypes.ALL).size() == 0) {
        portCandidates.add(new DefaultPortCandidate(port));
      }
    }

    INodeStyle nodeStyle = node.getStyle();
    if (nodeStyle instanceof ActivityNodeStyle
        || nodeStyle instanceof ChoreographyNodeStyle
        || nodeStyle instanceof DataObjectNodeStyle
        || nodeStyle instanceof AnnotationNodeStyle
        || nodeStyle instanceof GroupNodeStyle
        || nodeStyle instanceof DataStoreNodeStyle) {
      portCandidates.add(new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_TOP_ANCHORED));
      portCandidates.add(new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED));
      portCandidates.add(new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_LEFT_ANCHORED));
      portCandidates.add(new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_RIGHT_ANCHORED));
    } else if (nodeStyle instanceof EventNodeStyle
        || nodeStyle instanceof GatewayNodeStyle) {
      double dmax = Math.min(node.getLayout().getWidth() / 2, node.getLayout().getHeight() / 2);
      FreeNodePortLocationModel model = FreeNodePortLocationModel.INSTANCE;
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(0, -dmax))));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(dmax, 0))));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(0, dmax))));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(-dmax, 0))));
    } else if (nodeStyle instanceof ConversationNodeStyle) {
      double dx = 0.5 * Math.min(node.getLayout().getWidth(), node.getLayout().getHeight() / BpmnConstants.Sizes.CONVERSATION_WIDTH_HEIGHT_RATIO);
      double dy = dx * BpmnConstants.Sizes.CONVERSATION_WIDTH_HEIGHT_RATIO;
      FreeNodePortLocationModel model = FreeNodePortLocationModel.INSTANCE;
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(0, -dy))));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(dx, 0))));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(0, dy))));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(-dx, 0))));
    }
    CreateEdgeInputMode ceim = context.getParentInputMode() instanceof CreateEdgeInputMode ? (CreateEdgeInputMode)context.getParentInputMode() : null;
    CanvasComponent canvasControl = context.getCanvasComponent();
    if (ceim == null || canvasControl == null || IEventRecognizer.SHIFT_PRESSED.isRecognized(canvasControl, canvasControl.getLastMouse2DEvent())) {
      // add a dynamic candidate
      portCandidates.add(new DefaultPortCandidate(node, new FreeNodePortLocationModel()));
    }
    return portCandidates;

  }

}
