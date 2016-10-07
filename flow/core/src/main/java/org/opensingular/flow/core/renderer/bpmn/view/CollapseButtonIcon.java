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
