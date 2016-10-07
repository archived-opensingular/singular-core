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
package org.opensingular.flow.core.renderer.toolkit;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

/**
 * A simple node style for non-group nodes used by some of the demos.
 */
public class DemoNodeStyle extends AbstractNodeStyle {

  /**
   * The radius of the round corners in world coordinates.
   */
  public static final double CORNER_RADIUS = 2;

  /**
   * The background color of the node.
   */
  public static final Color BACKGROUND = Colors.DARK_ORANGE;

  /**
   * The border pen of the node.
   */
  public static final Pen PEN = new Pen(Colors.WHITE, 1);


  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    return new DemoVisualGroup(node.getLayout().toRectD(), PEN, BACKGROUND);
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (oldVisual instanceof DemoVisualGroup) {
      // reuse and update old visual
      DemoVisualGroup demoVisualGroup = (DemoVisualGroup) oldVisual;
      demoVisualGroup.update(node.getLayout().toRectD());
      return oldVisual;
    } else {
      // create a new visual
      return createVisual(context, node);
    }
  }

  /**
   * Creates a round rect path with the specified width and height and a fix {@link #CORNER_RADIUS}.
   */
  private static GeneralPath createPath(double width, double height) {
    GeneralPath shape = new GeneralPath();
    double x1 = width > CORNER_RADIUS * 2 ? CORNER_RADIUS : width /2;
    double x2 = width - x1;

    double y1 = height > CORNER_RADIUS * 2 ? CORNER_RADIUS : height /2;
    double y2 = height - y1;

    shape.moveTo(x1, 0);
    shape.lineTo(x2, 0);
    shape.quadTo(width, 0, width, y1);
    shape.lineTo(width, y2);
    shape.quadTo(width, height, x2, height);
    shape.lineTo(x1, height);
    shape.quadTo(0, height, 0, y2);
    shape.lineTo(0, y1);
    shape.quadTo(0, 0, x1, 0);
    return shape;
  }

  /**
   * This VisualGroup caches the node layout to speed-up updates of the position and/or size of the node hasn't changed.
   */
  private static class DemoVisualGroup extends VisualGroup {

    private static final double EPS = 0.001;

    private RectD layout;

    DemoVisualGroup(RectD layout, Pen pen, Paint fill) {
      GeneralPath path = createPath(layout.getWidth(), layout.getHeight());
      this.add(new ShapeVisual(path, pen, fill));
      this.setTransform(AffineTransform.getTranslateInstance(layout.getX(), layout.getY()));
      this.layout = layout;
    }

    public void update(RectD newLayout) {
      if (!RectD.equals(layout, newLayout)) {
        if (layout.getWidth() != newLayout.getWidth() || layout.getHeight() != newLayout.getHeight()) {
          // recreate path
          ShapeVisual shapeVisual = (ShapeVisual) getChildren().get(0);
          shapeVisual.setShape(createPath(newLayout.getWidth(), newLayout.getHeight()));
        }
        if (layout.getX() != newLayout.getX() || layout.getY() != newLayout.getY()) {
          // update transform
          this.setTransform(AffineTransform.getTranslateInstance(newLayout.getX(), newLayout.getY()));
        }
        this.layout = newLayout;
      }
    }
  }
}
