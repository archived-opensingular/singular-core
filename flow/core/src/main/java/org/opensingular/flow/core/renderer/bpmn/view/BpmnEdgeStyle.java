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
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IArrowOwner;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.IEdgeStyleRenderer;
import com.yworks.yfiles.graph.styles.IPathGeometry;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyleRenderer;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;

/**
 * An {@link IEdgeStyle} implementation representing a connection according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class BpmnEdgeStyle implements IEdgeStyle, IArrowOwner {
  //region Initialize static fields

  private static final IconArrow DEFAULT_TARGET_ARROW;

  private static final IconArrow DEFAULT_SOURCE_ARROW;

  private static final IconArrow ASSOCIATION_ARROW;

  private static final IconArrow CONDITIONAL_SOURCE_ARROW;

  private static final IconArrow MESSAGE_TARGET_ARROW;

  private static final IconArrow MESSAGE_SOURCE_ARROW;
  //endregion

  //region Properties

  private PolylineEdgeStyle delegateStyle;

  private EdgeType type = EdgeType.SEQUENCE_FLOW;

  private double smoothingLength = 20;

  private IEdgeStyleRenderer renderer;

  private IArrow sourceArrow;

  private IArrow targetArrow;

  private Pen pen;

  private Pen doubleLineCenterPen;

  /**
   * Gets the edge type of this style.
   * @return The Type.
   * @see #setType(EdgeType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeType.class, stringValue = "SEQUENCE_FLOW")
  public final EdgeType getType() {
    return type;
  }

  /**
   * Sets the edge type of this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeType.class, stringValue = "SEQUENCE_FLOW")
  public final void setType( EdgeType value ) {
    type = value;
    switch (value) {
      case CONDITIONAL_FLOW:
        pen = BpmnConstants.Pens.BPMN_EDGE_STYLE;
        sourceArrow = CONDITIONAL_SOURCE_ARROW;
        targetArrow = DEFAULT_TARGET_ARROW;
        break;
      case ASSOCIATION:
        pen = BpmnConstants.Pens.ASSOCIATION_EDGE_STYLE;
        sourceArrow = IArrow.NONE;
        targetArrow = IArrow.NONE;
        break;
      case DIRECTED_ASSOCIATION:
        pen = BpmnConstants.Pens.ASSOCIATION_EDGE_STYLE;
        sourceArrow = IArrow.NONE;
        targetArrow = ASSOCIATION_ARROW;
        break;
      case BIDIRECTED_ASSOCIATION:
        pen = BpmnConstants.Pens.ASSOCIATION_EDGE_STYLE;
        sourceArrow = ASSOCIATION_ARROW;
        targetArrow = ASSOCIATION_ARROW;
        break;
      case MESSAGE_FLOW:
        pen = BpmnConstants.Pens.MESSAGE_EDGE_STYLE;
        sourceArrow = MESSAGE_SOURCE_ARROW;
        targetArrow = MESSAGE_TARGET_ARROW;
        break;
      case DEFAULT_FLOW:
        pen = BpmnConstants.Pens.BPMN_EDGE_STYLE;
        sourceArrow = DEFAULT_SOURCE_ARROW;
        targetArrow = DEFAULT_TARGET_ARROW;
        break;
      case CONVERSATION:
        pen = BpmnConstants.Pens.CONVERSATION_DOUBLE_LINE;
        sourceArrow = IArrow.NONE;
        targetArrow = IArrow.NONE;
        break;
      case SEQUENCE_FLOW:
      default:
        pen = BpmnConstants.Pens.BPMN_EDGE_STYLE;
        sourceArrow = IArrow.NONE;
        targetArrow = DEFAULT_TARGET_ARROW;
        break;
    }
    updateDelegate();
  }

  //endregion

  /**
   * Creates a new instance using {@link EdgeType#SEQUENCE_FLOW}.
   */
  public BpmnEdgeStyle() {
    renderer = new BpmnEdgeStyleRenderer();
    doubleLineCenterPen = BpmnConstants.Pens.CONVERSATION_CENTER_LINE;
    delegateStyle = new PolylineEdgeStyle();
    setType(EdgeType.SEQUENCE_FLOW);
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final BpmnEdgeStyle clone() {
    BpmnEdgeStyle newInstance = new BpmnEdgeStyle();
      newInstance.setType(getType());
    return newInstance;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IEdgeStyleRenderer getRenderer() {
    return renderer;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IArrow getSourceArrow() {
    return sourceArrow;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IArrow getTargetArrow() {
    return targetArrow;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final Pen getPen() {
    return pen;
  }

  /**
   * Gets the {@link #getPen() Pen} for the center line of a {@link EdgeType#CONVERSATION}.
   * @return The DoubleLineCenterPen.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  final Pen getDoubleLineCenterPen() {
    return doubleLineCenterPen;
  }

  /**
   * Gets the smoothing length used for creating smooth bends.
   * <p>
   * A value of {@code 0.0d} will disable smoothing.
   * </p>
   * @return The SmoothingLength.
   * @see #setSmoothingLength(double)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(doubleValue = 20.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final double getSmoothingLength() {
    return smoothingLength;
  }

  /**
   * Sets the smoothing length used for creating smooth bends.
   * <p>
   * A value of {@code 0.0d} will disable smoothing.
   * </p>
   * @param value The SmoothingLength to set.
   * @see #getSmoothingLength()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(doubleValue = 20.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final void setSmoothingLength( double value ) {
    smoothingLength = value;
    updateDelegate();
  }

  private void updateDelegate() {
    if (delegateStyle != null) {
      delegateStyle.setPen(getPen());
      delegateStyle.setSourceArrow(getSourceArrow());
      delegateStyle.setTargetArrow(getTargetArrow());
      delegateStyle.setSmoothingLength(getSmoothingLength());
    }
  }

  //region Renderer Class

  /**
   * Renderer class used for the {@link BpmnEdgeStyle}.
   */
  private static class BpmnEdgeStyleRenderer implements IEdgeStyleRenderer, IVisualCreator {
    private static final PolylineEdgeStyleRenderer delegateRenderer = new PolylineEdgeStyleRenderer();

    private BpmnEdgeStyle style;

    private IEdge edge;

    public final IBoundsProvider getBoundsProvider( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getBoundsProvider(edge, this.style.delegateStyle);
    }

    public final IPathGeometry getPathGeometry( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getPathGeometry(edge, this.style.delegateStyle);
    }

    public final IVisualCreator getVisualCreator( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      this.edge = edge;
      delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle);
      return this;
    }

    public final IVisibilityTestable getVisibilityTestable( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getVisibilityTestable(edge, this.style.delegateStyle);
    }

    public final IHitTestable getHitTestable( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getHitTestable(edge, this.style.delegateStyle);
    }

    public final IMarqueeTestable getMarqueeTestable( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getMarqueeTestable(edge, this.style.delegateStyle);
    }

    public final ILookup getContext( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getContext(edge, this.style.delegateStyle);
    }

    public IVisual createVisual( IRenderContext context ) {
      if (style.getType() != EdgeType.CONVERSATION) {
        return delegateRenderer.createVisual(context);
      } else {
        VisualGroup container = new VisualGroup();
        container.add(delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).createVisual(context));
        style.delegateStyle.setPen(style.getDoubleLineCenterPen());
        container.add(delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).createVisual(context));
        style.delegateStyle.setPen(style.getPen());
        return container;
      }
    }

    public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
      if (style.getType() != EdgeType.CONVERSATION) {
        return delegateRenderer.updateVisual(context, oldVisual);
      } else {
        VisualGroup container = (VisualGroup)oldVisual;
        IVisual firstPath = container.getChildren().get(0);
        IVisual newFirstPath = delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).updateVisual(context, firstPath);
        if (firstPath != newFirstPath) {
          container.getChildren().remove(firstPath);
          container.getChildren().add(0, newFirstPath);
        }

        style.delegateStyle.setPen(style.getDoubleLineCenterPen());
        IVisual secondPath = container.getChildren().get(1);
        IVisual newSecondPath = delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).updateVisual(context, secondPath);
        if (secondPath != newSecondPath) {
          container.getChildren().remove(secondPath);
          container.getChildren().add(1, newSecondPath);
        }
        style.delegateStyle.setPen(style.getPen());
        return container;
      }
    }
  }

  //endregion
  static {
    DEFAULT_TARGET_ARROW = new IconArrow(IconFactory.createArrowIcon(ArrowType.DEFAULT_TARGET));
    DEFAULT_TARGET_ARROW.setBounds(new SizeD(8, 6));
    DEFAULT_TARGET_ARROW.setCropLength(0);
    DEFAULT_TARGET_ARROW.setLength(8);

    DEFAULT_SOURCE_ARROW = new IconArrow(IconFactory.createArrowIcon(ArrowType.DEFAULT_SOURCE));
    DEFAULT_SOURCE_ARROW.setBounds(new SizeD(8, 6));
    DEFAULT_SOURCE_ARROW.setCropLength(0);
    DEFAULT_SOURCE_ARROW.setLength(0);

    ASSOCIATION_ARROW = new IconArrow(IconFactory.createArrowIcon(ArrowType.ASSOCIATION));
    ASSOCIATION_ARROW.setBounds(new SizeD(8, 6));
    ASSOCIATION_ARROW.setCropLength(0);
    ASSOCIATION_ARROW.setLength(0);

    CONDITIONAL_SOURCE_ARROW = new IconArrow(IconFactory.createArrowIcon(ArrowType.CONDITIONAL_SOURCE));
    CONDITIONAL_SOURCE_ARROW.setBounds(new SizeD(16, 8));
    CONDITIONAL_SOURCE_ARROW.setCropLength(0);
    CONDITIONAL_SOURCE_ARROW.setLength(16);

    MESSAGE_TARGET_ARROW = new IconArrow(IconFactory.createArrowIcon(ArrowType.MESSAGE_TARGET));
    MESSAGE_TARGET_ARROW.setBounds(new SizeD(8, 6));
    MESSAGE_TARGET_ARROW.setCropLength(0);
    MESSAGE_TARGET_ARROW.setLength(8);


    MESSAGE_SOURCE_ARROW = new IconArrow(IconFactory.createArrowIcon(ArrowType.MESSAGE_SOURCE));
    MESSAGE_SOURCE_ARROW.setBounds(new SizeD(6, 6));
    MESSAGE_SOURCE_ARROW.setCropLength(0);
    MESSAGE_SOURCE_ARROW.setLength(6);


  }

}
