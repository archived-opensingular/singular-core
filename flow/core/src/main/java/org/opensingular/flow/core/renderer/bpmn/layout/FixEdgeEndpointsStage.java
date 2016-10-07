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
import com.yworks.yfiles.algorithms.IEdgeCursor;
import com.yworks.yfiles.algorithms.LineSegment;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.algorithms.YPointPath;
import com.yworks.yfiles.layout.AbstractLayoutStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutGraph;

class FixEdgeEndpointsStage extends AbstractLayoutStage {
  /**
   * Creates a new instance that uses the specified core layout algorithm.
   */
  public FixEdgeEndpointsStage( ILayoutAlgorithm core ) {
    super(core);
  }

  /**
   * Creates a new instance that uses no core layout algorithm.
   */
  public FixEdgeEndpointsStage() {
  }

  @Override
  public void applyLayout( LayoutGraph graph ) {
    if (getCoreLayout() != null) {
      getCoreLayout().applyLayout(graph);
    }
    for (IEdgeCursor ec = graph.getEdgeCursor(); ec.ok(); ec.next()) {
      Edge e = ec.edge();
      YPointPath path = graph.getPath(e);
      //adjust source point
      this.adjustPortLocation(graph, e, path, true);
      this.adjustPortLocation(graph, e, path, false);
    }
  }

  /**
   * Adjusts the edge end points at non-activity nodes so they don't end outside the shape of the node they are attached to.
   */
  private void adjustPortLocation( LayoutGraph graph, Edge e, YPointPath path, boolean atSource ) {
    Node node = atSource ? e.source() : e.target();
    // only adjust ports of activity nodes as they have an irregular shape
    if (!BpmnElementTypes.isActivityNode(BpmnElementTypes.getType(node, graph))) {
      YPoint pointRel = atSource ? graph.getSourcePointRel(e) : graph.getTargetPointRel(e);
      // get offset from the node center to the end of the shape at the node side the edge connects to
      LineSegment segment = path.getLineSegment(atSource ? 0 : path.length() - 2);
      double offset = Math.min(graph.getWidth(node), graph.getHeight(node)) / 2;
      double offsetX = segment.getDeltaX() > 0 ^ atSource ? -offset : offset;
      double offsetY = segment.getDeltaY() > 0 ^ atSource ? -offset : offset;
      // if the edge end point is at the center of this side, we use the calculated offset to put the end point on
      // the node bounds, otherwise we prolong the last segment to the center line of the node so it doesn't end
      // outside the node's shape
      YPoint newPortLocation = FixEdgeEndpointsStage.isHorizontal(segment.getDeltaY()) ? new YPoint(pointRel.getY() != 0 ? 0 : offsetX, pointRel.getY()) : new YPoint(pointRel.getX(), pointRel.getX() != 0 ? 0 : offsetY);
      if (atSource) {
        graph.setSourcePointRel(e, newPortLocation);
      } else {
        graph.setTargetPointRel(e, newPortLocation);
      }
    }
  }

  private static final double EPS = 0.01;

  private static boolean isHorizontal( double deltaY ) {
    return Math.abs(deltaY) < EPS;
  }
}
