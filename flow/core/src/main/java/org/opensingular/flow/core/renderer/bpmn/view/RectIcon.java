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

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

class RectIcon extends AbstractIcon {
  private double cornerRadius;

  final double getCornerRadius() {
    return this.cornerRadius;
  }

  final void setCornerRadius( double value ) {
    this.cornerRadius = value;
  }

  private Paint paint;

  final Paint getPaint() {
    return this.paint;
  }

  final void setPaint(Paint value) {
    this.paint = value;
  }

  private Pen pen;

  final Pen getPen() {
    return this.pen;
  }

  final void setPen( Pen value ) {
    this.pen = value;
  }

  @Override
  public IVisual createVisual( IRenderContext context ) {
    RectD layout = getBounds().toRectD();

    final RoundRectangle2D.Double roundRect = new RoundRectangle2D.Double(
        layout.getX(),
        layout.getY(),
        layout.getWidth(),
        layout.getHeight(),
        getCornerRadius() * 2,
        getCornerRadius() * 2);

    return new ShapeVisual(roundRect, getPen(), getPaint());
  }

  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    if(!(oldVisual instanceof ShapeVisual)) {
      return createVisual(context);
    }
    ShapeVisual rectangle = (ShapeVisual) oldVisual;
    Shape shape = rectangle.getShape();
    if(!(shape instanceof RoundRectangle2D.Double) || rectangle.getPen() != getPen() || rectangle.getFill() != getPaint()) {
      return createVisual(context);
    }
    RoundRectangle2D rr = (RoundRectangle2D) shape;
    if(rr.getArcHeight() != getCornerRadius() * 2 || rr.getArcWidth() != getCornerRadius() * 2 ) {
      return createVisual(context);
    }
    updateRectangle(rr);
    return oldVisual;
  }

  private void updateRectangle(RoundRectangle2D rectangle) {
    RectD bounds = getBounds().toRectD();
    rectangle.setFrame(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
  }
}
