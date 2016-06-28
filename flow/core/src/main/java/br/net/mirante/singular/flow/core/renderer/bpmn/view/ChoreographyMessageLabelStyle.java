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
package br.net.mirante.singular.flow.core.renderer.bpmn.view;

import com.yworks.yfiles.annotations.DefaultValue;
import com.yworks.yfiles.annotations.Obfuscation;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.common.VoidLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyleRenderer;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.SimpleLabelStyle;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.VisualGroup;

/**
 * A label style for message labels of nodes using a {@link ChoreographyNodeStyle}.
 * <p>
 * To place labels with this style, {@link ChoreographyLabelModel#NORTH_MESSAGE} or {@link ChoreographyLabelModel#SOUTH_MESSAGE}
 * are recommended.
 * </p>
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class ChoreographyMessageLabelStyle implements ILabelStyle {
  //region Initialize static fields

  private static final ChoreographyMessageLabelStyleRenderer RENDERER = new ChoreographyMessageLabelStyleRenderer();

  private static final BpmnEdgeStyle CONNECTOR_STYLE;

  private static final SimpleLabelStyle TEXT_STYLE;

  private static final ILabelModelParameter DEFAULT_TEXT_PLACEMENT;

  private static final BpmnNodeStyle INITIATING_MESSAGE_STYLE;

  private static final BpmnNodeStyle RESPONSE_MESSAGE_STYLE;

  private ConnectedIconLabelStyle delegateStyle;

  static final ILabelModelParameter getDefaultTextPlacement() {
    return DEFAULT_TEXT_PLACEMENT;
  }

  static final BpmnNodeStyle getInitiatingMessageStyle() {
    return INITIATING_MESSAGE_STYLE;
  }

  static final BpmnNodeStyle getResponseMessageStyle() {
    return RESPONSE_MESSAGE_STYLE;
  }

  //endregion

  //region Properties

  /**
   * Gets where the text is placed relative to the message icon.
   * <p>
   * The label model parameter has to support {@link INode}s.
   * </p>
   * @return The TextPlacement.
   * @see #setTextPlacement(ILabelModelParameter)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Demo.yFiles.Graph.Bpmn.Styles.ChoreographyMessageLabelStyle.DefaultTextPlacement", classValue = BpmnDefaultValueSerializerHolder.class)
  public final ILabelModelParameter getTextPlacement() {
    return delegateStyle != null ? delegateStyle.getTextPlacement() : null;
  }

  /**
   * Sets where the text is placed relative to the message icon.
   * <p>
   * The label model parameter has to support {@link INode}s.
   * </p>
   * @param value The TextPlacement to set.
   * @see #getTextPlacement()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Demo.yFiles.Graph.Bpmn.Styles.ChoreographyMessageLabelStyle.DefaultTextPlacement", classValue = BpmnDefaultValueSerializerHolder.class)
  public final void setTextPlacement( ILabelModelParameter value ) {
    if (delegateStyle != null) {
      delegateStyle.setTextPlacement(value);
    }
  }

  final ConnectedIconLabelStyle getDelegateStyle() {
    return delegateStyle;
  }

  //endregion

  /**
   * Creates a new instance.
   */
  public ChoreographyMessageLabelStyle() {
    ConnectedIconLabelStyle delegateStyle = new ConnectedIconLabelStyle();
    delegateStyle.setIconSize(BpmnConstants.Sizes.MESSAGE);
    delegateStyle.setIconStyle(getInitiatingMessageStyle());
    delegateStyle.setTextStyle(TEXT_STYLE);
    delegateStyle.setConnectorStyle(CONNECTOR_STYLE);
    delegateStyle.setLabelConnectorLocation(FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED);
    delegateStyle.setNodeConnectorLocation(FreeNodePortLocationModel.NODE_TOP_ANCHORED);
    this.delegateStyle = delegateStyle;
    setTextPlacement(getDefaultTextPlacement());
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ChoreographyMessageLabelStyle clone() {
    return new ChoreographyMessageLabelStyle();
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelStyleRenderer getRenderer() {
    return RENDERER;
  }

  //region Renderer Class

  /**
   * An {@link ILabelStyleRenderer} implementation used by {@link ChoreographyMessageLabelStyle}.
   */
  static class ChoreographyMessageLabelStyleRenderer implements ILabelStyleRenderer, IVisualCreator {
    private ILabel item;

    private ILabelStyle style;

    private boolean north;

    private boolean responseMessage;

    private ILabelStyle getCurrentStyle( ILabel item, ILabelStyle style ) {

      if (!(style instanceof ChoreographyMessageLabelStyle)) {
        return VoidLabelStyle.INSTANCE;
      }
      ChoreographyMessageLabelStyle labelStyle = (ChoreographyMessageLabelStyle)style;

      north = true;
      responseMessage = false;
      ILabelOwner owner = item.getOwner();
      INode node = (owner instanceof INode) ? (INode)owner : null;
      if (node != null) {
        north = item.getLayout().getCenter().y < node.getLayout().getCenter().y;
        INodeStyle s = node.getStyle();
        ChoreographyNodeStyle nodeStyle = (s instanceof ChoreographyNodeStyle) ? (ChoreographyNodeStyle)s : null;
        if (nodeStyle != null) {
          responseMessage = nodeStyle.isInitiatingAtTop() ^ north;
        }
      }

      ConnectedIconLabelStyle delegateStyle = labelStyle.getDelegateStyle();
      delegateStyle.setIconStyle(responseMessage ? getResponseMessageStyle() : getInitiatingMessageStyle());
      delegateStyle.setLabelConnectorLocation(north ? FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED : FreeNodePortLocationModel.NODE_TOP_ANCHORED);
      delegateStyle.setNodeConnectorLocation(north ? FreeNodePortLocationModel.NODE_TOP_ANCHORED : FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED);
      return delegateStyle;
    }

    public final IVisualCreator getVisualCreator( ILabel item, ILabelStyle style ) {
      this.item = item;
      this.style = style;
      return this;
    }

    public final IBoundsProvider getBoundsProvider( ILabel item, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      return delegateStyle.getRenderer().getBoundsProvider(item, delegateStyle);
    }

    public final IVisibilityTestable getVisibilityTestable( ILabel item, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      return delegateStyle.getRenderer().getVisibilityTestable(item, delegateStyle);
    }

    public final IHitTestable getHitTestable( ILabel item, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      return delegateStyle.getRenderer().getHitTestable(item, delegateStyle);
    }

    public final IMarqueeTestable getMarqueeTestable( ILabel item, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      return delegateStyle.getRenderer().getMarqueeTestable(item, delegateStyle);
    }

    public final ILookup getContext( ILabel item, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      return delegateStyle.getRenderer().getContext(item, delegateStyle);
    }

    public final SizeD getPreferredSize( ILabel label, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(label, style);
      return delegateStyle.getRenderer().getPreferredSize(label, delegateStyle);
    }

    public final IVisual createVisual( IRenderContext context ) {
      MyVisual container = new MyVisual(getTextPlacement(), responseMessage, north);
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      container.add(delegateStyle.getRenderer().getVisualCreator(item, delegateStyle).createVisual(context));
      return container;
    }

    public final IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
      MyVisual container = (oldVisual instanceof MyVisual) ? (MyVisual)oldVisual : null;
      ILabelStyle delegateStyle = getCurrentStyle(item, style);

      if(container == null) {
        return createVisual(context);
      }
      ILabelModelParameter textPlacement = container.getTextPlacement();
      if (!(textPlacement == getTextPlacement() && container.isNorth() == north && container.isResponseMessage() == responseMessage) || container.getChildren().size() != 1) {
        return createVisual(context);
      }
      IVisual oldDelegateVisual = container.getChildren().get(0);
      IVisual newDelegateVisual = delegateStyle.getRenderer().getVisualCreator(item, delegateStyle).updateVisual(context, oldDelegateVisual);
      if (oldDelegateVisual != newDelegateVisual) {
        container.getChildren().set(0, newDelegateVisual);
      }
      container.setNorth(north);
      container.setTextPlacement(textPlacement);
      container.setResponseMessage(responseMessage);
      return container;
    }

    private ILabelModelParameter getTextPlacement() {
      return ((ChoreographyMessageLabelStyle)style).getTextPlacement();
    }

    static class MyVisual extends VisualGroup {

      public MyVisual(ILabelModelParameter textPlacement, boolean north, boolean responseMessage) {
        this.textPlacement = textPlacement;
        this.north = north;
        this.responseMessage = responseMessage;
      }

      private ILabelModelParameter textPlacement;

      private final ILabelModelParameter getTextPlacement() {
        return this.textPlacement;
      }

      public final void setTextPlacement( ILabelModelParameter value ) {
        this.textPlacement = value;
      }

      private boolean north;

      private final boolean isNorth() {
        return this.north;
      }

      public final void setNorth( boolean value ) {
        this.north = value;
      }

      private boolean responseMessage;

      private final boolean isResponseMessage() {
        return this.responseMessage;
      }

      public final void setResponseMessage( boolean value ) {
        this.responseMessage = value;
      }

      @Override
      public boolean equals( Object obj ) {
        MyVisual other = (obj instanceof MyVisual) ? (MyVisual)obj : null;
        if (other == null) {
          return false;
        }
        return getTextPlacement() == other.getTextPlacement() && isNorth() == other.isNorth() && isResponseMessage() == other.isResponseMessage();
      }
    }
  }

  //endregion
  static {
    ExteriorLabelModel newInstance = new ExteriorLabelModel();
      newInstance.setInsets(new InsetsD(5));
    DEFAULT_TEXT_PLACEMENT = newInstance.createParameter(ExteriorLabelModel.Position.WEST);
    BpmnNodeStyle newInstance2 = new BpmnNodeStyle();
      newInstance2.setIcon(IconFactory.createMessage(BpmnConstants.Pens.MESSAGE, BpmnConstants.Paints.CHOREOGRAPHY_INITIALIZING_PARTICIPANT));
      newInstance2.setMinimumSize(BpmnConstants.Sizes.MESSAGE);
    INITIATING_MESSAGE_STYLE = newInstance2;
    BpmnNodeStyle newInstance3 = new BpmnNodeStyle();
      newInstance3.setIcon(IconFactory.createMessage(BpmnConstants.Pens.MESSAGE, BpmnConstants.Paints.CHOREOGRAPHY_RECEIVING_PARTICIPANT));
      newInstance3.setMinimumSize(BpmnConstants.Sizes.MESSAGE);
    RESPONSE_MESSAGE_STYLE = newInstance3;
    BpmnEdgeStyle newInstance4 = new BpmnEdgeStyle();
      newInstance4.setType(EdgeType.ASSOCIATION);
    CONNECTOR_STYLE = newInstance4;
    TEXT_STYLE = new SimpleLabelStyle();
  }

}
