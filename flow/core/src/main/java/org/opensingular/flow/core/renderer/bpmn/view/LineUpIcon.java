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

import com.yworks.yfiles.geometry.IRectangle;
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

class LineUpIcon extends AbstractIcon {
  private final List<IIcon> icons;

  private final SizeD innerIconSize;

  private final double gap;

  private final SizeD combinedSize;

  public LineUpIcon( List<IIcon> icons, SizeD innerIconSize, double gap ) {
    this.icons = icons;
    this.innerIconSize = innerIconSize;
    this.gap = gap;

    double combinedWidth = icons.size() * innerIconSize.width + (icons.size() - 1) * gap;
    combinedSize = new SizeD(combinedWidth, innerIconSize.height);
  }

  @Override
  public IVisual createVisual( IRenderContext context ) {
    RectD bounds = getBounds().toRectD();
    if (bounds == null) {
      return null;
    }

    VisualGroup container = new VisualGroup();

    double offset = 0;
    for (IIcon pathIcon : icons) {
      pathIcon.setBounds(new RectD(offset, 0, innerIconSize.width, innerIconSize.height));
      container.add(pathIcon.createVisual(context));
      offset += innerIconSize.width + gap;
    }
    container.setTransform(AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY()));
    return container;
  }

  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    VisualGroup container = (oldVisual instanceof VisualGroup) ? (VisualGroup)oldVisual : null;
    if (container == null || container.getChildren().size() != icons.size()) {
      return createVisual(context);
    }
    RectD bounds = getBounds().toRectD();
    container.getTransform().setToTranslation(bounds.getX(), bounds.getY());
    return container;
  }

  @Override
  public void setBounds( IRectangle bounds ) {
    super.setBounds(RectD.fromCenter(bounds.getCenter(), combinedSize));
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
      PointD topLeft = getBounds().getTopLeft();
      double offset = 0;
      for (IIcon icon : icons) {
        IClickListener abp = icon.lookup(IClickListener.class);
        if (abp != null) {
          icon.setBounds(new RectD(offset, 0, innerIconSize.getWidth(), innerIconSize.getHeight()));
          PointD d = PointD.subtract(p, topLeft);
          if (abp.getHitTestable().isHit(ctx, d)) {
            abp.onClicked(ctx, d);
            return;
          }
          offset += innerIconSize.getWidth() + gap;
        }
      }
    }

    public boolean isHit(IInputModeContext ctx, PointD p) {
      PointD topLeft = getBounds().getTopLeft();
      double offset = 0;
      for(IIcon icon: icons) {
        IClickListener abp = icon.lookup(IClickListener.class);
        icon.setBounds(new RectD(offset, 0, innerIconSize.getWidth(), innerIconSize.getHeight()));
        if (abp != null) {
          PointD d = PointD.subtract(p, topLeft);
          if (abp.getHitTestable().isHit(ctx, d)) {
            return true;
          }
        }
        offset += innerIconSize.getWidth() + gap;
      }
      return false;
    }
  }
}
