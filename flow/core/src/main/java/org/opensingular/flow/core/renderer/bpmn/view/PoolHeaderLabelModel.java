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
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.ITable;
import com.yworks.yfiles.graph.labelmodels.ConstantLabelCandidateDescriptorProvider;
import com.yworks.yfiles.graph.labelmodels.DefaultLabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.ILabelCandidateDescriptorProvider;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterProvider;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.SingletonSerialization;
import com.yworks.yfiles.utils.IEnumerable;

import java.util.ArrayList;

/**
 * A label model for nodes using a {@link PoolNodeStyle} that position labels inside the
 * {@link ITable#getInsets() table insets}.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class PoolHeaderLabelModel implements ILabelModel, ILabelModelParameterProvider {
  //region Initialize static fields

  /**
   * The {@link PoolHeaderLabelModel} singleton.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final PoolHeaderLabelModel INSTANCE = new PoolHeaderLabelModel();

  /**
   * A parameter instance using the north insets of the pool node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @GraphML(name = "North")
  public static final ILabelModelParameter NORTH = new PoolHeaderParameter((byte) 0);

  /**
   * A parameter instance using the east insets of the pool node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @GraphML(name = "East")
  public static final ILabelModelParameter EAST = new PoolHeaderParameter((byte) 1);

  /**
   * A parameter instance using the south insets of the pool node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @GraphML(name = "South")
  public static final ILabelModelParameter SOUTH = new PoolHeaderParameter((byte) 2);

  /**
   * A parameter instance using the west insets of the pool node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @GraphML(name = "West")
  public static final ILabelModelParameter WEST = new PoolHeaderParameter((byte) 3);

  static  {
    ArrayList<ILabelModelParameter>  params = new ArrayList<>(4);
    params.add(NORTH);
    params.add(EAST);
    params.add(SOUTH);
    params.add(WEST);
    PARAMETERS = IEnumerable.create(params);
  }
  private static final IEnumerable<ILabelModelParameter> PARAMETERS;

  //endregion

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public <TLookup> TLookup lookup( Class<TLookup> type ) {
    if (type == ILabelModelParameterProvider.class) {
      return (TLookup)this;
    }
    if (type == ILabelModelParameterFinder.class) {
      return (TLookup)DefaultLabelModelParameterFinder.INSTANCE;
    }
    if (type == ILabelCandidateDescriptorProvider.class) {
      return (TLookup)ConstantLabelCandidateDescriptorProvider.INTERNAL_DESCRIPTOR_PROVIDER;
    }
    return null;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IOrientedRectangle getGeometry( ILabel label, ILabelModelParameter parameter ) {
    PoolHeaderParameter php = (parameter instanceof PoolHeaderParameter) ? (PoolHeaderParameter)parameter : null;
    INode owner = (INode)label.getOwner();
    if (php == null || owner == null) {
      return null;
    }

    ITable table = owner.lookup(ITable.class);
    InsetsD insets = table != null && InsetsD.notEquals(table.getInsets(), InsetsD.EMPTY) ? table.getInsets() : new InsetsD(0);

    OrientedRectangle orientedRectangle = new OrientedRectangle();
    orientedRectangle.resize(label.getPreferredSize());
    IRectangle layout = owner.getLayout().toRectD();
    switch (php.getSide()) {
      case 0: // North
        orientedRectangle.setUpVector(0, -1);
        orientedRectangle.setCenter(new PointD(layout.getX() + layout.getWidth() / 2, layout.getY() + insets.top / 2));
        break;
      case 1: // East
        orientedRectangle.setUpVector(1, 0);
        orientedRectangle.setCenter(new PointD(layout.getMaxX() - insets.right / 2, layout.getY() + layout.getHeight() / 2));
        break;
      case 2: // South
        orientedRectangle.setUpVector(0, -1);
        orientedRectangle.setCenter(new PointD(layout.getX() + layout.getWidth() / 2, layout.getMaxY() - insets.bottom / 2));
        break;
      case 3: // West
      default:
        orientedRectangle.setUpVector(-1, 0);
        orientedRectangle.setCenter(new PointD(layout.getX() + insets.left / 2, layout.getY() + layout.getHeight() / 2));
        break;
    }

    return orientedRectangle;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelModelParameter createDefaultParameter() {
    return WEST;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILookup getContext( ILabel label, ILabelModelParameter parameter ) {
    return ILookup.EMPTY;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IEnumerable<ILabelModelParameter> getParameters( ILabel label, ILabelModel model ) {
    return PARAMETERS;
  }

  @SingletonSerialization({PoolHeaderLabelModel.class})
  static class PoolHeaderParameter implements ILabelModelParameter {
    private final byte side;

    public final byte getSide() {
      return side;
    }

    public PoolHeaderParameter( byte side ) {
      this.side = side;
    }

    public final PoolHeaderParameter clone() {
      return this;
    }

    public final ILabelModel getModel() {
      return INSTANCE;
    }

    public final boolean supports( ILabel label ) {
      return label.getOwner().lookup(ITable.class) != null;
    }
  }
}
