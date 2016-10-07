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
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.geom.AffineTransform;
import java.util.List;

class CombinedIcon extends AbstractIcon {
  private final List<IIcon> icons;

  public CombinedIcon( List<IIcon> icons ) {
    this.icons = icons;
  }

  @Override
  public IVisual createVisual( IRenderContext context ) {
    RectD bounds = getBounds().toRectD();

    if (bounds == null) {
      return null;
    }

    MyVisual container = new MyVisual(bounds);

    RectD iconBounds = new RectD(PointD.ORIGIN, bounds.toSizeD());
    for (IIcon icon : icons) {
      icon.setBounds(iconBounds);
      container.add(icon.createVisual(context));
    }

    container.setTransform(AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY()));
    return container;
  }

  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    MyVisual container = (oldVisual instanceof MyVisual) ? (MyVisual)oldVisual : null;
    if (container == null || container.getChildren().size() != icons.size()) {
      return createVisual(context);
    }

    RectD bounds = getBounds().toRectD();

    if (SizeD.notEquals(container.getBounds().getSize(), bounds.toSizeD())) {
      // size changed -> we have to update the icons
      RectD iconBounds = new RectD(PointD.ORIGIN, bounds.toSizeD());
      int index = 0;
      for (IIcon pathIcon : icons) {
        pathIcon.setBounds(iconBounds);
        IVisual oldPathVisual = container.getChildren().get(index);
        IVisual newPathVisual = pathIcon.updateVisual(context, oldPathVisual);
        if (!oldPathVisual.equals(newPathVisual)) {
          newPathVisual = newPathVisual != null ? newPathVisual : new VisualGroup();
          container.getChildren().remove(oldPathVisual);
          container.getChildren().add(index, newPathVisual);
        }
        index++;
      }
    } else if (PointD.equals(container.getBounds().getTopLeft(), bounds.getTopLeft())) {
      // bounds didn't change at all
      return container;
    }
    container.getTransform().setToTranslation(bounds.getX(), bounds.getY());
    container.setBounds(bounds);
    return container;
  }

  private static class MyVisual extends VisualGroup {
    private RectD bounds;

    public MyVisual(RectD bounds) {
      this.bounds = bounds;
    }

    public RectD getBounds() {
      return bounds;
    }

    public void setBounds(RectD bounds) {
      this.bounds = bounds;
    }
  }

  @Override
  public <T> T lookup(Class<T> type) {
    if (type == IClickListener.class) {
      return (T) new MyActionButtonProvider();
    }
    return super.lookup(type);
  }

  private class MyActionButtonProvider implements IClickListener, IHitTestable {
    public IHitTestable getHitTestable() {
      return this;
    }

    public void onClicked(IInputModeContext ctx, PointD p) {
      RectD bounds = getBounds().toRectD();
      PointD topLeft = bounds.getTopLeft();
      for (IIcon icon : icons) {
        IClickListener abp = icon.lookup(IClickListener.class);
        if (abp != null) {
          RectD iconBounds = new RectD(PointD.ORIGIN, bounds.toSizeD());
          icon.setBounds(iconBounds);
          PointD d = PointD.subtract(p, topLeft);
          if (abp.getHitTestable().isHit(ctx, d)) {
            abp.onClicked(ctx, d);
            return;
          }
        }
      }
    }

    public boolean isHit(IInputModeContext ctx, PointD p) {
      RectD bounds = getBounds().toRectD();
      PointD topLeft = bounds.getTopLeft();
      for (IIcon icon : icons) {
        IClickListener abp = icon.lookup(IClickListener.class);
        if (abp != null) {
          RectD iconBounds = new RectD(PointD.ORIGIN, bounds.toSizeD());
          icon.setBounds(iconBounds);
          PointD d = PointD.subtract(p, topLeft);
          if (abp.getHitTestable().isHit(ctx, d)) {
            return true;
          }
        }
      }
      return false;
    }
  }
}
