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
