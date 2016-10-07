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
