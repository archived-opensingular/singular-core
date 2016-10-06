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

import com.yworks.yfiles.annotations.DefaultValue;
import com.yworks.yfiles.annotations.Obfuscation;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.Table;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.StretchStripeLabelModel;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.SimpleLabelStyle;
import com.yworks.yfiles.graph.styles.TableNodeStyle;
import com.yworks.yfiles.graph.styles.TableRenderingOrder;
import com.yworks.yfiles.graphml.ContentProperty;
import com.yworks.yfiles.graphml.SingletonSerialization;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VerticalAlignment;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.EditLabelHelper;
import com.yworks.yfiles.view.input.IEditLabelHelper;
import com.yworks.yfiles.view.input.IInputModeContext;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing a Pool according to the BPMN.
 * <p>
 * The main visualization is delegated to {@link #getTableNodeStyle() TableNodeStyle}.
 * </p>
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
@ContentProperty("TableNodeStyle")
@SingletonSerialization
public class PoolNodeStyle extends AbstractNodeStyle {
  //region Initialize static fields

  private static final IIcon MULTIPLE_INSTANCE_ICON;

  private static TableNodeStyle createDefaultTableNodeStyle( boolean vertical ) {
    // create a new table
    Table table = new Table();
    TableNodeStyle tns = new TableNodeStyle();
    AlternatingLeafStripeStyle alternatingLeafStripeStyle = new AlternatingLeafStripeStyle();

    StripeDescriptor evenStripeDescriptor = new StripeDescriptor();
    evenStripeDescriptor.setBackgroundPaint(BpmnConstants.Paints.POOL_NODE_EVEN_LEAF_BACKGROUND);
    evenStripeDescriptor.setInsetPaint(BpmnConstants.Paints.POOL_NODE_EVEN_LEAF_INSET);
    alternatingLeafStripeStyle.setEvenLeafDescriptor(evenStripeDescriptor);

    StripeDescriptor oddStripeDescriptor = new StripeDescriptor();
    oddStripeDescriptor.setBackgroundPaint(BpmnConstants.Paints.POOL_NODE_ODD_LEAF_BACKGROUND);
    oddStripeDescriptor.setInsetPaint(BpmnConstants.Paints.POOL_NODE_ODD_LEAF_INSET);
    alternatingLeafStripeStyle.setOddLeafDescriptor(oddStripeDescriptor);

    StripeDescriptor parentStripeDescriptor = new StripeDescriptor();
    parentStripeDescriptor.setBackgroundPaint(BpmnConstants.Paints.POOL_NODE_PARENT_BACKGROUND);
    parentStripeDescriptor.setInsetPaint(BpmnConstants.Paints.POOL_NODE_PARENT_INSET);
    alternatingLeafStripeStyle.setParentDescriptor(parentStripeDescriptor);

    // we'd like to use a special stripe style
    if (vertical) {
      table.setInsets(InsetsD.fromLTRB(0, 20, 0, 0));

      // set the column defaults
      table.getColumnDefaults().setInsets(InsetsD.fromLTRB(0, 20, 0, 0));
      SimpleLabelStyle columnLabelStyle = new SimpleLabelStyle();
      columnLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
      table.getColumnDefaults().getLabels().setStyle(columnLabelStyle);
      table.getColumnDefaults().getLabels().setLayoutParameter(StretchStripeLabelModel.NORTH);
      table.getColumnDefaults().setStyle(alternatingLeafStripeStyle);
      table.getColumnDefaults().setMinimumSize(50);
      tns.setTableRenderingOrder(TableRenderingOrder.COLUMNS_FIRST);
    } else {
      table.setInsets(InsetsD.fromLTRB(20, 0, 0, 0));

      // set the row defaults
      table.getRowDefaults().setInsets(InsetsD.fromLTRB(20, 0, 0, 0));
      SimpleLabelStyle rowLabelStyle = new SimpleLabelStyle();
      rowLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
      table.getRowDefaults().getLabels().setStyle(rowLabelStyle);
      table.getRowDefaults().getLabels().setLayoutParameter(StretchStripeLabelModel.WEST);
      table.getRowDefaults().setStyle(alternatingLeafStripeStyle);
      table.getRowDefaults().setMinimumSize(50);
      tns.setTableRenderingOrder(TableRenderingOrder.ROWS_FIRST);
    }
    ShapeNodeStyle backgroundStyle = new ShapeNodeStyle();
    backgroundStyle.setPaint(BpmnConstants.Paints.POOL_NODE_BACKGROUND);
    tns.setBackgroundStyle(backgroundStyle);
    tns.setTable(table);
    return tns;
  }

  //endregion

  //region Properties

  private boolean multipleInstance;

  /**
   * Gets if this pool represents a multiple instance participant.
   * @return The MultipleInstance.
   * @see #setMultipleInstance(boolean)
   */
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final boolean isMultipleInstance() {
    return this.multipleInstance;
  }

  /**
   * Sets if this pool represents a multiple instance participant.
   * @param value The MultipleInstance to set.
   * @see #isMultipleInstance()
   */
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final void setMultipleInstance( boolean value ) {
    this.multipleInstance = value;
  }

  private boolean vertical;

  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  private boolean isVertical() {
    return this.vertical;
  }

  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  private void setVertical( boolean value ) {
    this.vertical = value;
  }

  private TableNodeStyle tableNodeStyle;

  /**
   * Gets the {@link #getTableNodeStyle() TableNodeStyle} the visualization is delegated to.
   * @return The TableNodeStyle.
   * @see #setTableNodeStyle(TableNodeStyle)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final TableNodeStyle getTableNodeStyle() {
    return tableNodeStyle != null ? tableNodeStyle : (tableNodeStyle = createDefaultTableNodeStyle(isVertical()));
  }

  /**
   * Sets the {@link #getTableNodeStyle() TableNodeStyle} the visualization is delegated to.
   * @param value The TableNodeStyle to set.
   * @see #getTableNodeStyle()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final void setTableNodeStyle( TableNodeStyle value ) {
    tableNodeStyle = value;
  }

  //endregion

  /**
   * Creates a new instance for a horizontal pool.
   */
  public PoolNodeStyle() {
    this(false);
  }

  /**
   * Creates a new instance.
   * @param vertical Whether the style represents a vertical pool.
   */
  public PoolNodeStyle( boolean vertical ) {
    setVertical(vertical);
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public PoolNodeStyle clone() {
    PoolNodeStyle newInstance = (PoolNodeStyle) super.clone();
    newInstance.setTableNodeStyle(getTableNodeStyle().clone());
    return newInstance;
  }

  @Override
  protected VisualGroup createVisual( IRenderContext context, INode node ) {
    VisualGroup container = new VisualGroup();
    container.add(getTableNodeStyle().getRenderer().getVisualCreator(node, getTableNodeStyle()).createVisual(context));
    if (isMultipleInstance()) {
      MULTIPLE_INSTANCE_ICON.setBounds(node.getLayout());
      container.add(MULTIPLE_INSTANCE_ICON.createVisual(context));
    }
    context.registerForChildrenIfNecessary(container, this::disposeChildren);
    return container;
  }

  private final IVisual disposeChildren( IRenderContext ctx, IVisual removedVisual, boolean dispose ) {
    if(!(removedVisual instanceof VisualGroup)) {
      throw new IllegalArgumentException("removedVisual");
    }
    VisualGroup vg = (VisualGroup) removedVisual;
    vg.getChildren().forEach(ctx::childVisualRemoved);
    return null;
  }

  @Override
  protected VisualGroup updateVisual( IRenderContext context, IVisual oldVisual, INode node ) {
    if(!(oldVisual instanceof VisualGroup)) {
      context.childVisualRemoved(oldVisual);
      return createVisual(context, node);
    }
    VisualGroup container = (VisualGroup) oldVisual;
    if (container.getChildren().size() == 0) {
      context.childVisualRemoved(oldVisual);
      return createVisual(context, node);
    }

    IVisual oldTableVisual = container.getChildren().get(0);
    IVisual newTableVisual = getTableNodeStyle().getRenderer().getVisualCreator(node, getTableNodeStyle()).updateVisual(context, oldTableVisual);
    if (oldTableVisual != newTableVisual) {
      container.getChildren().remove(oldTableVisual);
      context.childVisualRemoved(oldTableVisual);
      container.getChildren().add(0, newTableVisual);
    }

    IVisual oldMultipleVisual = container.getChildren().size() > 1 ? container.getChildren().get(1) : null;
    if (isMultipleInstance()) {
      MULTIPLE_INSTANCE_ICON.setBounds(node.getLayout());
      IVisual newMultipleVisual = MULTIPLE_INSTANCE_ICON.updateVisual(context, oldMultipleVisual);
      if (oldMultipleVisual != newMultipleVisual) {
        if (oldMultipleVisual != null) {
          container.getChildren().remove(oldMultipleVisual);
          context.childVisualRemoved(oldMultipleVisual);
        }
        container.add(newMultipleVisual);
      }
    } else if (oldMultipleVisual != null) {
      // there has been a multipleInstance icon before
      container.getChildren().remove(oldMultipleVisual);
      context.childVisualRemoved(oldMultipleVisual);
    }
    context.registerForChildrenIfNecessary(container, this::disposeChildren);
    return container;
  }

  @Override
  protected Object lookup( INode item, Class type ) {
    if (type == IEditLabelHelper.class) {
      return new PoolNodeEditLabelHelper();
    }
    return getTableNodeStyle().getRenderer().getContext(item, getTableNodeStyle()).lookup(type);
  }

  private class PoolNodeEditLabelHelper extends EditLabelHelper {
    @Override
    protected ILabelModelParameter getLabelParameter( IInputModeContext context, ILabelOwner owner ) {
      if (getTableNodeStyle().getTableRenderingOrder() == TableRenderingOrder.COLUMNS_FIRST) {
        return PoolHeaderLabelModel.NORTH;
      } else {
        return PoolHeaderLabelModel.WEST;
      }
    }
  }

  static {
    MULTIPLE_INSTANCE_ICON = new PlacedIcon(IconFactory.createLoopCharacteristic(LoopCharacteristic.PARALLEL), BpmnConstants.Placements.POOL_NODE_MARKER, BpmnConstants.Sizes.MARKER);
  }
}
