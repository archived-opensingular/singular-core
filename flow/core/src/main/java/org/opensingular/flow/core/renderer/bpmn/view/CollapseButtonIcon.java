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
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.ICommand;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;

class CollapseButtonIcon extends AbstractIcon implements IClickListener {
  private static final IIcon COLLAPSED_ICON;

  private static final IIcon EXPANDED_ICON;

  private INode node;

  public CollapseButtonIcon( INode node ) {
    this.node = node;
  }

  @Override
  public IVisual createVisual( IRenderContext context ) {
    COLLAPSED_ICON.setBounds(new RectD(PointD.ORIGIN, getBounds().toSizeD()));
    EXPANDED_ICON.setBounds(new RectD(PointD.ORIGIN, getBounds().toSizeD()));
    boolean expanded = true;
    CanvasComponent canvas = context != null ? context.getCanvasComponent() : null;

    if (canvas != null) {
      IGraph graph = canvas.lookup(IGraph.class);
      if (graph != null) {
        IFoldingView foldingView = graph.lookup(IFoldingView.class);
        if (foldingView != null && foldingView.getGraph().contains(node)) {
          expanded = foldingView.isExpanded(node);
        }
      }
    }
    if(expanded) {
      return EXPANDED_ICON.createVisual(context);
    }
    else {
      return COLLAPSED_ICON.createVisual(context);
    }
  }

  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    COLLAPSED_ICON.setBounds(new RectD(PointD.ORIGIN, getBounds().toSizeD()));
    EXPANDED_ICON.setBounds(new RectD(PointD.ORIGIN, getBounds().toSizeD()));
    boolean expanded = true;
    CanvasComponent canvas = context != null ? context.getCanvasComponent() : null;

    if (canvas != null) {
      IGraph graph = canvas.lookup(IGraph.class);
      if (graph != null) {
        IFoldingView foldingView = graph.lookup(IFoldingView.class);
        if (foldingView != null && foldingView.getGraph().contains(node)) {
          expanded = foldingView.isExpanded(node);
        }
      }
    }
    if(expanded) {
      return EXPANDED_ICON.updateVisual(context, oldVisual);
    }
    else {
      return COLLAPSED_ICON.updateVisual(context, oldVisual);
    }
  }

  static {
    COLLAPSED_ICON = IconFactory.createStaticSubState(SubState.COLLAPSED);
    EXPANDED_ICON = IconFactory.createStaticSubState(SubState.EXPANDED);
  }

  @Override
  public <T> T lookup(Class<T> type) {
    if(type == IClickListener.class) {
      return (T)this;
    }
    return super.lookup(type);
  }

  @Override
  public IHitTestable getHitTestable() {
    return (context, p) -> getBounds().toRectD().contains(p, context.getHitTestRadius());
  }

  @Override
  public void onClicked(IInputModeContext context, PointD location) {
    ICommand.TOGGLE_EXPANSION_STATE.execute(node, context.getCanvasComponent());
  }
}
