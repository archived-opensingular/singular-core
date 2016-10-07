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

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;

import java.awt.Paint;
import java.awt.geom.AffineTransform;

class PathIcon extends AbstractIcon {
  private Paint paint;

  public final Paint getPaint() {
    return this.paint;
  }

  public final void setPaint(Paint value) {
    this.paint = value;
  }

  private Pen pen;

  public final Pen getPen() {
    return this.pen;
  }

  public final void setPen( Pen value ) {
    this.pen = value;
  }

  private GeneralPath path;

  final GeneralPath getPath() {
    return this.path;
  }

  final void setPath( GeneralPath value ) {
    this.path = value;
  }

  @Override
  public IVisual createVisual( IRenderContext context ) {
    RectD bounds = getBounds().toRectD();
    MyVisual container = new MyVisual(bounds.getSize());

    Matrix2D matrix2D = new Matrix2D();
    matrix2D.scale(Math.max(0, bounds.getWidth()), Math.max(0, bounds.getHeight()));

    container.add(new ShapeVisual(getPath().createPath(matrix2D), getPen(), getPaint()));
    container.setTransform(AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY()));
    return container;
  }

  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    RectD bounds = getBounds().toRectD();
    MyVisual container = (oldVisual instanceof MyVisual) ? (MyVisual)oldVisual : null;
    if (container == null || container.getChildren().size() != 1) {
      return createVisual(context);
    }
    IVisual visual = container.getChildren().get(0);
    ShapeVisual path = (visual instanceof ShapeVisual) ? (ShapeVisual)visual : null;
    if (path == null || SizeD.notEquals(container.getSize(), bounds.getSize())) {
      return createVisual(context);
    }

    Pen pen = getPen();
    if (path.getPen() != pen) {
      path.setPen(pen);
    }
    Paint paint = getPaint();
    if (path.getFill() != paint) {
      path.setFill(paint);
    }

    // arrange visual
    container.getTransform().setToTranslation(bounds.getX(), bounds.getY());
    container.setSize(bounds.getSize());
    return container;
  }

  private static class MyVisual extends VisualGroup {
    private SizeD size;

    public MyVisual(SizeD size) {
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
