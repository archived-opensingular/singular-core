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

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.INodeInsetsProvider;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * A simple node style for group nodes used by some of the demos.
 */
public class DemoGroupNodeStyle extends AbstractNodeStyle {

  private static final int BORDER_THICKNESS = 4;
  private static final int HEADER_THICKNESS = 22;
  private static final int INSET = 4;

  private static final Color BORDER_COLOR = Color.decode("#68B0E3");
  private static final Color FOLDER_FRONT_COLOR = Color.decode("#68B0E3");
  private static final Color FOLDER_BACK_COLOR = Color.decode("#3C679B");
  private static final Pen OUTER_BORDER_PEN = new Pen(Colors.WHITE, 1);

  private static final InsetsD INSETS = new InsetsD(HEADER_THICKNESS + INSET, BORDER_THICKNESS + INSET, BORDER_THICKNESS + INSET, BORDER_THICKNESS + INSET);

  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    IRectangle layout = node.getLayout();
    DemoGroupNodeStyleVisual group = new DemoGroupNodeStyleVisual(layout.toSizeD());
    group.setTransform(AffineTransform.getTranslateInstance(layout.getX(), layout.getY()));

    Rectangle2D outerRect = new Rectangle2D.Double(0, 0, layout.getWidth(), layout.getHeight());
    ShapeVisual backgroundRectVisual = new ShapeVisual(outerRect);
    backgroundRectVisual.setPen(OUTER_BORDER_PEN);
    backgroundRectVisual.setFill(BORDER_COLOR);
    group.add(backgroundRectVisual);

    double innerWidth = layout.getWidth() - 2 * BORDER_THICKNESS;
    double innerHeight = layout.getHeight() -  HEADER_THICKNESS - BORDER_THICKNESS;
    Rectangle2D innerRect = new Rectangle2D.Double(BORDER_THICKNESS, HEADER_THICKNESS, innerWidth, innerHeight);
    ShapeVisual rectVisual = new ShapeVisual(innerRect);
    rectVisual.setFill(Colors.WHITE);
    group.add(rectVisual);

    return group;
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (!(oldVisual instanceof DemoGroupNodeStyleVisual)) {
      return createVisual(context, node);
    }

    DemoGroupNodeStyleVisual group = (DemoGroupNodeStyleVisual) oldVisual;
    IRectangle layout = node.getLayout();
    if (SizeD.notEquals(layout.toSizeD(), group.getSize())) {
      return createVisual(context, node);
    }

    group.setTransform(AffineTransform.getTranslateInstance(layout.getX(), layout.getY()));
    return group;
  }

  @Override
  protected Object lookup(INode node, Class type) {
    if (type == INodeInsetsProvider.class) {
      return (INodeInsetsProvider) (node2) -> INSETS;
    }
    return super.lookup(node, type);
  }

  private static class DemoGroupNodeStyleVisual extends VisualGroup {
    SizeD size;

    DemoGroupNodeStyleVisual(SizeD size) {
      this.size = size;
    }

    public SizeD getSize() {
      return size;
    }

    public void setSize(SizeD size) {
      this.size = size;
    }
  }
}
