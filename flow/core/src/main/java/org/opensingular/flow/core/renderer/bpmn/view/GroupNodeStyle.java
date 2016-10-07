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

import com.yworks.yfiles.annotations.DefaultValue;
import com.yworks.yfiles.annotations.Obfuscation;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyleRenderer;
import com.yworks.yfiles.graph.styles.IShapeGeometry;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyleRenderer;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisualCreator;

/**
 * An {@link INodeStyle} implementation representing an Group Node according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class GroupNodeStyle implements INodeStyle {
  //region Initialize static fields

  private static final INodeStyle SHAPE_NODE_STYLE;

  private static final INodeStyleRenderer RENDERER;

  //endregion

  private InsetsD insets = new InsetsD(15);

  /**
   * Gets the insets for the node.
   * <p>
   * These insets are returned via an {@link INodeInsetsProvider} if such an instance is queried through the
   * {@link INodeStyleRenderer#getContext(INode, INodeStyle) context lookup}.
   * </p>
   * @return The Insets.
   * @see INodeInsetsProvider
   * @see #setInsets(InsetsD)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "15", classValue = InsetsD.class)
  public final InsetsD getInsets() {
    return insets;
  }

  /**
   * Sets the insets for the node.
   * <p>
   * These insets are returned via an {@link INodeInsetsProvider} if such an instance is queried through the
   * {@link INodeStyleRenderer#getContext(INode, INodeStyle) context lookup}.
   * </p>
   * @param value The Insets to set.
   * @see INodeInsetsProvider
   * @see #getInsets()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "15", classValue = InsetsD.class)
  public final void setInsets( InsetsD value ) {
    insets = value;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final GroupNodeStyle clone() {
    return new GroupNodeStyle();
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final INodeStyleRenderer getRenderer() {
    return RENDERER;
  }

  /**
   * An {@link INodeStyleRenderer} implementation used by {@link GroupNodeStyle}.
   */
  static class GroupNodeStyleRenderer implements INodeStyleRenderer, ILookup {
    private INode lastNode;

    private GroupNodeStyle lastStyle;

    public final IVisualCreator getVisualCreator( INode item, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getVisualCreator(item, SHAPE_NODE_STYLE);
    }

    public final IBoundsProvider getBoundsProvider( INode item, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getBoundsProvider(item, SHAPE_NODE_STYLE);
    }

    public final IVisibilityTestable getVisibilityTestable( INode item, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getVisibilityTestable(item, SHAPE_NODE_STYLE);
    }

    public final IHitTestable getHitTestable( INode item, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getHitTestable(item, SHAPE_NODE_STYLE);
    }

    public final IMarqueeTestable getMarqueeTestable( INode item, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getMarqueeTestable(item, SHAPE_NODE_STYLE);
    }

    public final ILookup getContext( INode item, INodeStyle style ) {
      lastNode = item;
      lastStyle = (style instanceof GroupNodeStyle) ? (GroupNodeStyle)style : null;
      return this;
    }

    public final IShapeGeometry getShapeGeometry( INode node, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getShapeGeometry(node, SHAPE_NODE_STYLE);
    }

    @Obfuscation(stripAfterObfuscation = false, exclude = true)
    public final <TLookup> TLookup lookup( Class<TLookup> type ) {
      if (type == INodeInsetsProvider.class && lastStyle != null) {
        return (TLookup)new GroupInsetsProvider(lastStyle);
      }
      ILookup lookup = SHAPE_NODE_STYLE.getRenderer().getContext(lastNode, SHAPE_NODE_STYLE);
      return lookup != null ? lookup.lookup(type) : null;
    }

    /**
     * Uses the style insets extended by the size of the participant bands.
     */
    private static class GroupInsetsProvider implements INodeInsetsProvider {
      private final GroupNodeStyle style;

      GroupInsetsProvider( GroupNodeStyle style ) {
        this.style = style;
      }

      public final InsetsD getInsets( INode node ) {
        return style.getInsets();
      }
    }

  }

  static {
    ShapeNodeStyle newInstance = new ShapeNodeStyle();
      newInstance.setShape(ShapeNodeShape.ROUND_RECTANGLE);
      newInstance.setPaint(BpmnConstants.Paints.GROUP_NODE);
      newInstance.setPen(BpmnConstants.Pens.GROUP_NODE);
    SHAPE_NODE_STYLE = newInstance;
    ((ShapeNodeStyleRenderer)SHAPE_NODE_STYLE.getRenderer()).setRoundRectArcRadius(BpmnConstants.GROUP_NODE_CORNER_RADIUS);
    RENDERER = new GroupNodeStyleRenderer();
  }

}
