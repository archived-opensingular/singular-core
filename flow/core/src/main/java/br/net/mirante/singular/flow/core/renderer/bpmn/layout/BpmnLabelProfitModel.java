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
package br.net.mirante.singular.flow.core.renderer.bpmn.layout;

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.IDataMap;
import com.yworks.yfiles.algorithms.ILineSegmentCursor;
import com.yworks.yfiles.algorithms.INodeCursor;
import com.yworks.yfiles.algorithms.LineSegment;
import com.yworks.yfiles.algorithms.Maps;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.algorithms.YPointPath;
import com.yworks.yfiles.algorithms.YRectangle;
import com.yworks.yfiles.layout.DiscreteNodeLabelLayoutModel;
import com.yworks.yfiles.layout.IEdgeLabelLayout;
import com.yworks.yfiles.layout.INodeLabelLayout;
import com.yworks.yfiles.layout.INodeLabelLayoutModel;
import com.yworks.yfiles.layout.IProfitModel;
import com.yworks.yfiles.layout.LabelCandidate;
import com.yworks.yfiles.layout.LayoutGraph;

class BpmnLabelProfitModel implements IProfitModel {
  private IDataMap label2OriginalBox;

  private LayoutGraph graph;

  public BpmnLabelProfitModel( LayoutGraph graph ) {
    this.graph = graph;
    this.label2OriginalBox = Maps.createHashedDataMap();
    for (INodeCursor nc = graph.getNodeCursor(); nc.ok(); nc.next()) {
      Node n = nc.node();
      INodeLabelLayout[] nll = graph.getLabelLayout(n);
      for (int i = 0; i < nll.length; i++) {
        INodeLabelLayoutModel nlm = nll[i].getLabelModel();
        if (nlm instanceof DiscreteNodeLabelLayoutModel) {
          this.label2OriginalBox.set(nll[i], nll[i].getModelParameter());
        }
      }
    }
  }

  public double getProfit( LabelCandidate candidate ) {
    Object o = candidate.getOwner();
    if (o instanceof IEdgeLabelLayout) {
      return this.calcEdgeLabelProfit(this.graph, candidate);
    } else {
      double profit = 0;
      INodeLabelLayout nl = (INodeLabelLayout)o;
      INodeLabelLayoutModel nlm = nl.getLabelModel();
      if (nlm instanceof DiscreteNodeLabelLayoutModel) {
        Object param = candidate.getModelParameter();
        int pos = ((Integer) param).intValue();
        int originalPos = ((Integer) this.label2OriginalBox.get(nl)).intValue();
        if (pos == originalPos) {
          profit = 1;
        } else {
          switch (pos) {
            case 1:
            case 32:
            case 16:
            case 8:
              {
                profit = 0.95;
                break;
              }

            case 4:
            case 2:
            case 128:
            case 64:
              {
                profit = 0.9;
                break;
              }
          }
        }
      }
      return profit;
    }
  }

  private static final double MIN_PREFERRED_PLACEMENT_DISTANCE = 3;

  private static final double MAX_PREFERRED_PLACEMENT_DISTANCE = 40.0;

  private double calcEdgeLabelProfit( LayoutGraph g, LabelCandidate cand ) {
    Edge e = g.getOwner((IEdgeLabelLayout)cand.getOwner());
    if (BpmnElementTypes.isSequenceFlow(BpmnElementTypes.getType(e, g))) {
      double eLength = BpmnLabelProfitModel.calcEdgeLength(g, e);
      double maxPreferredPlacementDistance = Math.max(MAX_PREFERRED_PLACEMENT_DISTANCE, eLength * 0.2);
      double minDistToSource = BpmnLabelProfitModel.getDistance(cand.getBoundingBox(), g.getSourcePointAbs(e));
      if (minDistToSource > maxPreferredPlacementDistance) {
        return 0.0;
      } else {
        if (minDistToSource < MIN_PREFERRED_PLACEMENT_DISTANCE) {
          return 0.5;
        } else {
          return 1 - (minDistToSource / maxPreferredPlacementDistance);
        }
      }
    } else {
      return 0.0;
    }
  }

  private static double calcEdgeLength( LayoutGraph g, Edge e ) {
    YPointPath path = g.getPath(e);
    double length = 0;
    for (ILineSegmentCursor cur = path.lineSegments(); cur.ok(); cur.next()) {
      LineSegment segment = cur.lineSegment();
      length += segment.length();
    }
    return length;
  }

  static final double getDistance( YRectangle r, YPoint q ) {
    if (r.contains(q)) {
      return 0.0;
    } else {
      //determine corners of the rectangle
      YPoint upperLeft = r.getLocation();
      YPoint lowerLeft = new YPoint(upperLeft.getX(), upperLeft.getY() + r.getHeight());
      YPoint lowerRight = new YPoint(lowerLeft.getX() + r.getWidth(), lowerLeft.getY());
      YPoint upperRight = new YPoint(lowerRight.getX(), upperLeft.getY());
      //determine minDist to one of the four border segments
      double minDist = Double.MAX_VALUE;
      LineSegment rLeftSeg = new LineSegment(upperLeft, lowerLeft);
      minDist = Math.min(minDist, BpmnLabelProfitModel.getDistance(rLeftSeg, q));
      LineSegment rRightSeg = new LineSegment(upperRight, lowerRight);
      minDist = Math.min(minDist, BpmnLabelProfitModel.getDistance(rRightSeg, q));
      LineSegment rTopSeg = new LineSegment(upperLeft, upperRight);
      minDist = Math.min(minDist, BpmnLabelProfitModel.getDistance(rTopSeg, q));
      LineSegment rBottomSeg = new LineSegment(lowerLeft, lowerRight);
      minDist = Math.min(minDist, BpmnLabelProfitModel.getDistance(rBottomSeg, q));
      return minDist;
    }
  }

  static final double getDistance( LineSegment line, YPoint q ) {
    double x1 = line.getFirstEndPoint().getX();
    double x2 = line.getSecondEndPoint().getX();
    double y1 = line.getFirstEndPoint().getY();
    double y2 = line.getSecondEndPoint().getY();
    double pX = q.getX();
    double pY = q.getY();
    //adjust vectors relative to first endpoints of line
    x2 -= x1;
    y2 -= y1;
    pX -= x1;
    pY -= y1;
    //calculate distance
    double projSquaredDist = 0.0;
    double tmp = pX * x2 + pY * y2;
    if (tmp > 0.0) {
      pX = x2 - pX;
      pY = y2 - pY;
      tmp = pX * x2 + pY * y2;
      if (tmp <= 0.0) {
        projSquaredDist = 0.0;
      } else {
        projSquaredDist = tmp * tmp / (x2 * x2 + y2 * y2);
      }
    }
    double squaredDist = pX * pX + pY * pY - projSquaredDist;
    if (squaredDist < 0) {
      return 0;
    }
    return Math.sqrt(squaredDist);
  }
}
