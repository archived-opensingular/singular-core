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
package org.opensingular.singular.flow.core.renderer.bpmn.view;

import com.yworks.yfiles.annotations.Obfuscation;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyleRenderer;
import com.yworks.yfiles.graph.styles.NodeStyleLabelStyleAdapter;
import com.yworks.yfiles.graph.styles.SimpleLabelStyle;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisualCreator;

/**
 * An {@link ILabelStyle} implementation representing a Message according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class MessageLabelStyle implements ILabelStyle {
  //region Initialize static fields

  private static final ILabelStyle ADAPTER;

  private static final ILabelStyleRenderer RENDERER;

  //endregion

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final MessageLabelStyle clone() {
    return new MessageLabelStyle();
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelStyleRenderer getRenderer() {
    return RENDERER;
  }

  /**
   * An {@link ILabelStyleRenderer} implementation used by {@link MessageLabelStyle}.
   */
  static class MessageLabelStyleRenderer implements ILabelStyleRenderer {
    public final IVisualCreator getVisualCreator( ILabel item, ILabelStyle style ) {
      return ADAPTER.getRenderer().getVisualCreator(item, ADAPTER);
    }

    public final IBoundsProvider getBoundsProvider( ILabel item, ILabelStyle style ) {
      return ADAPTER.getRenderer().getBoundsProvider(item, ADAPTER);
    }

    public final IVisibilityTestable getVisibilityTestable( ILabel item, ILabelStyle style ) {
      return ADAPTER.getRenderer().getVisibilityTestable(item, ADAPTER);
    }

    public final IHitTestable getHitTestable( ILabel item, ILabelStyle style ) {
      return ADAPTER.getRenderer().getHitTestable(item, ADAPTER);
    }

    public final IMarqueeTestable getMarqueeTestable( ILabel item, ILabelStyle style ) {
      return ADAPTER.getRenderer().getMarqueeTestable(item, ADAPTER);
    }

    public final ILookup getContext( ILabel item, ILabelStyle style ) {
      return ADAPTER.getRenderer().getContext(item, ADAPTER);
    }

    public final SizeD getPreferredSize( ILabel label, ILabelStyle style ) {
      return ADAPTER.getRenderer().getPreferredSize(label, ADAPTER);
    }

  }

  static {
    IIcon messageIcon = IconFactory.createMessage(BpmnConstants.Pens.MESSAGE, BpmnConstants.Paints.MESSAGE);
    BpmnNodeStyle bpmnNodeStyle = new BpmnNodeStyle();
    bpmnNodeStyle.setIcon(messageIcon);
    bpmnNodeStyle.setMinimumSize(BpmnConstants.Sizes.MESSAGE);

    SimpleLabelStyle labelStyle = new SimpleLabelStyle();
    ADAPTER = new NodeStyleLabelStyleAdapter(bpmnNodeStyle, labelStyle);
    RENDERER = new MessageLabelStyleRenderer();
  }

}
