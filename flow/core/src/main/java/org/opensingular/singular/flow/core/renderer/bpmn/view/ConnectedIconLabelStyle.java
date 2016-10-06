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

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.SimpleEdge;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.SimplePort;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.SimpleLabelStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;

/**
 * An {@link ILabelStyle} implementation combining an text label, an icon and a connecting line between the icon and the
 * label owner.
 */
class ConnectedIconLabelStyle extends AbstractLabelStyle {
  //region Properties

  private ILabelModelParameter textPlacement;

  public final ILabelModelParameter getTextPlacement() {
    return this.textPlacement;
  }

  public final void setTextPlacement( ILabelModelParameter value ) {
    this.textPlacement = value;
  }

  private IPortLocationModelParameter labelConnectorLocation;

  public final IPortLocationModelParameter getLabelConnectorLocation() {
    return this.labelConnectorLocation;
  }

  public final void setLabelConnectorLocation( IPortLocationModelParameter value ) {
    this.labelConnectorLocation = value;
  }

  private IPortLocationModelParameter nodeConnectorLocation;

  public final IPortLocationModelParameter getNodeConnectorLocation() {
    return this.nodeConnectorLocation;
  }

  public final void setNodeConnectorLocation( IPortLocationModelParameter value ) {
    this.nodeConnectorLocation = value;
  }

  private SizeD iconSize = new SizeD();

  public final SizeD getIconSize() {
    return this.iconSize;
  }

  public final void setIconSize( SizeD value ) {
    this.iconSize = value;
  }

  private INodeStyle iconStyle;

  public final INodeStyle getIconStyle() {
    return this.iconStyle;
  }

  public final void setIconStyle( INodeStyle value ) {
    this.iconStyle = value;
  }

  private ILabelStyle textStyle;

  public final ILabelStyle getTextStyle() {
    return this.textStyle;
  }

  public final void setTextStyle( ILabelStyle value ) {
    this.textStyle = value;
  }

  private IEdgeStyle connectorStyle;

  public final IEdgeStyle getConnectorStyle() {
    return this.connectorStyle;
  }

  public final void setConnectorStyle( IEdgeStyle value ) {
    this.connectorStyle = value;
  }

  //endregion

  //region Initialize static fields

  private static final SimpleNode LABEL_AS_NODE;

  private static final SimpleLabel DUMMY_TEXT_LABEL;

  private static final SimpleEdge DUMMY_EDGE;

  private static final SimpleNode DUMMY_FOR_LABEL_OWNER;

  //endregion

  @Override
  protected VisualGroup createVisual( IRenderContext context, ILabel label ) {

    configure(label);
    VisualGroup container = new VisualGroup();

    IVisual iconVisual = null;
    if (getIconStyle() != null) {
      iconVisual = getIconStyle().getRenderer().getVisualCreator(LABEL_AS_NODE, LABEL_AS_NODE.getStyle()).createVisual(context);
    }
    container.add(iconVisual != null ? iconVisual : new VisualGroup());

    IVisual textVisual = null;
    if (getTextStyle() != null && getTextPlacement() != null) {
      textVisual = getTextStyle().getRenderer().getVisualCreator(DUMMY_TEXT_LABEL, DUMMY_TEXT_LABEL.getStyle()).createVisual(context);
    }
    container.add(textVisual != null ? textVisual : new VisualGroup());

    IVisual connectorVisual = null;
    if (getConnectorStyle() != null) {
      connectorVisual = DUMMY_EDGE.getStyle().getRenderer().getVisualCreator(DUMMY_EDGE, DUMMY_EDGE.getStyle()).createVisual(context);
    }
    container.add(connectorVisual != null ? connectorVisual : new VisualGroup());

    return container;
  }

  @Override
  protected SizeD getPreferredSize( ILabel label ) {
    if (SizeD.notEquals(getIconSize(), SizeD.ZERO)) {
      return getIconSize();
    } else {
      return label.getPreferredSize();
    }
  }

  @Override
  protected IVisual updateVisual( IRenderContext context, IVisual oldVisual, ILabel label ) {
    if(!(oldVisual instanceof VisualGroup)) {
      return createVisual(context, label);
    }
    VisualGroup container = (VisualGroup) oldVisual;
    configure(label);

    IVisual oldIconVisual = container.getChildren().get(0);
    IVisual newIconVisual = null;
    if (getIconStyle() != null) {
      newIconVisual = getIconStyle().getRenderer().getVisualCreator(LABEL_AS_NODE, LABEL_AS_NODE.getStyle()).updateVisual(context, oldIconVisual);
    }
    if (oldIconVisual != newIconVisual) {
      container.getChildren().set(0, newIconVisual != null ? newIconVisual : new VisualGroup());
    }

    IVisual oldTextVisual = container.getChildren().get(1);
    IVisual newTextVisual = null;
    if (getTextStyle() != null && getTextPlacement() != null) {
      newTextVisual = getTextStyle().getRenderer().getVisualCreator(DUMMY_TEXT_LABEL, DUMMY_TEXT_LABEL.getStyle()).updateVisual(context, oldTextVisual);
    }
    if (oldTextVisual != newTextVisual) {
      container.getChildren().set(1, newTextVisual != null ? newTextVisual : new VisualGroup());
    }

    IVisual oldConnectorVisual = container.getChildren().get(2);
    IVisual newConnectorVisual = null;
    if (getConnectorStyle() != null) {
      newConnectorVisual = DUMMY_EDGE.getStyle().getRenderer().getVisualCreator(DUMMY_EDGE, DUMMY_EDGE.getStyle()).updateVisual(context, oldConnectorVisual);
    }
    if (oldConnectorVisual != newConnectorVisual) {
      container.getChildren().set(2, newConnectorVisual != null ? newConnectorVisual : new VisualGroup());
    }

    return container;
  }

  protected final void configure( ILabel item ) {
    LABEL_AS_NODE.setStyle(getIconStyle());
    RectD bounds = item.getLayout().getBounds();
    LABEL_AS_NODE.setLayout(bounds);
    ILabelOwner tmp;
    INode nodeOwner = ((tmp = item.getOwner()) instanceof INode) ? (INode)tmp : null;
    if (nodeOwner != null) {
      DUMMY_FOR_LABEL_OWNER.setStyle(nodeOwner.getStyle());
      DUMMY_FOR_LABEL_OWNER.setLayout(nodeOwner.getLayout());
    }

    DUMMY_TEXT_LABEL.setStyle(getTextStyle());
    DUMMY_TEXT_LABEL.setLayoutParameter(getTextPlacement());
    DUMMY_TEXT_LABEL.setText(item.getText());
    DUMMY_TEXT_LABEL.setPreferredSize(DUMMY_TEXT_LABEL.getStyle().getRenderer().getPreferredSize(DUMMY_TEXT_LABEL, DUMMY_TEXT_LABEL.getStyle()));
    TextBounds = getTextPlacement().getModel().getGeometry(DUMMY_TEXT_LABEL, getTextPlacement());

    BoundingBox = RectD.add(bounds, TextBounds.getBounds());

    // Set source port to the port of the node using a dummy node that is located at the origin.
    ((SimplePort)DUMMY_EDGE.getSourcePort()).setLocationParameter(getLabelConnectorLocation());
    ((SimplePort)DUMMY_EDGE.getTargetPort()).setLocationParameter(getNodeConnectorLocation());
  }

  private IOrientedRectangle TextBounds;

  private RectD BoundingBox = new RectD();

  @Override
  protected boolean isHit( IInputModeContext context, PointD p, ILabel label ) {
    configure(label);
    return label.getLayout().contains(p, context.getHitTestRadius())
        || TextBounds.contains(p, context.getHitTestRadius())
        || DUMMY_EDGE.getStyle().getRenderer().getHitTestable(DUMMY_EDGE, DUMMY_EDGE.getStyle()).isHit(context, p);
  }

  @Override
  protected boolean isInBox( IInputModeContext context, RectD box, ILabel label ) {
    configure(label);
    return box.intersects(BoundingBox.getEnlarged(context.getHitTestRadius()));
  }

  @Override
  protected RectD getBounds( ICanvasContext context, ILabel label ) {
    return RectD.add(BoundingBox, DUMMY_EDGE.getStyle().getRenderer().getBoundsProvider(DUMMY_EDGE, DUMMY_EDGE.getStyle()).getBounds(context));
  }

  @Override
  protected boolean isVisible( ICanvasContext context, RectD clip, ILabel label ) {
    ILabelOwner owner = label.getOwner();
    if(owner instanceof INode) {
    // We're computing a (very generous) bounding box here because relying on GetBounds does not work.
    // The visibility test does not call Configure, which means we don't have the dummy edge set up yet.
      return clip.intersects(RectD.add(BoundingBox, ((INode)owner).getLayout().toRectD()));
    }
    else {
    return clip.intersects(BoundingBox);
  }
  }

  static {
    LABEL_AS_NODE = new SimpleNode();
    SimpleLabel sl = new SimpleLabel(LABEL_AS_NODE, "", FreeLabelModel.INSTANCE.createDefaultParameter());
    sl.setStyle(new SimpleLabelStyle());
    DUMMY_TEXT_LABEL = sl;

    DUMMY_FOR_LABEL_OWNER = new SimpleNode();
    SimpleEdge se = new SimpleEdge(new SimplePort(LABEL_AS_NODE, FreeNodePortLocationModel.NODE_CENTER_ANCHORED),
        new SimplePort(DUMMY_FOR_LABEL_OWNER, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));
    BpmnEdgeStyle bes = new BpmnEdgeStyle();
    bes.setType(EdgeType.ASSOCIATION);
    se.setStyle(bes);
    DUMMY_EDGE = se;
  }

}
