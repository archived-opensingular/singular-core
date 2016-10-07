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
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import java.util.ArrayList;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing a Data Object according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class DataObjectNodeStyle extends BpmnNodeStyle {
  //region Static icons

  private static final IIcon DATA_ICON;

  private static final IIcon COLLECTION_ICON;

  //endregion

  //region Properties

  private boolean collection;

  /**
   * Gets whether this is a Collection Data Object.
   * @return The Collection.
   * @see #setCollection(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isCollection() {
    return collection;
  }

  /**
   * Sets whether this is a Collection Data Object.
   * @param value The Collection to set.
   * @see #isCollection()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setCollection( boolean value ) {
    if (collection != value) {
      incrementModCount();
      collection = value;
    }
  }

  private DataObjectType type;

  /**
   * Gets the data object type for this style.
   * @return The Type.
   * @see #setType(DataObjectType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = DataObjectType.class, stringValue = "NONE")
  public final DataObjectType getType() {
    return type;
  }

  /**
   * Sets the data object type for this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = DataObjectType.class, stringValue = "NONE")
  public final void setType( DataObjectType value ) {
    if (type != value) {
      incrementModCount();
      type = value;
      typeIcon = IconFactory.createDataObjectType(value);
      if (typeIcon != null) {
        typeIcon = IconFactory.createPlacedIcon(typeIcon, BpmnConstants.Placements.DATA_OBJECT_TYPE, BpmnConstants.Sizes.DATA_OBJECT_TYPE);
      }
    }
  }

  //endregion

  private IIcon typeIcon;

  /**
   * Creates a new instance.
   */
  public DataObjectNodeStyle() {
    setMinimumSize(new SizeD(25, 30));
    setType(DataObjectType.NONE);
  }

  @Override
  void updateIcon(INode node) {
    ArrayList<IIcon> icons = new ArrayList<>();
    icons.add(DATA_ICON);

    if (isCollection()) {
      icons.add(COLLECTION_ICON);
    }
    if (typeIcon != null) {
      icons.add(typeIcon);
    }
    if (icons.size() > 1) {
      setIcon(IconFactory.createCombinedIcon(icons));
    } else {
      setIcon(DATA_ICON);
    }
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    IRectangle layout = node.getLayout().toRectD();
    double cornerSize = Math.min(layout.getWidth(), layout.getHeight()) * 0.4;

    GeneralPath path = new GeneralPath();
    path.moveTo(0, 0);
    path.lineTo(layout.getWidth() - cornerSize, 0);
    path.lineTo(layout.getWidth(), cornerSize);
    path.lineTo(layout.getWidth(), layout.getHeight());
    path.lineTo(0, layout.getHeight());
    path.close();

    Matrix2D transform = new Matrix2D();
    transform.translate(layout.getTopLeft());
    path.transform(transform);
    return path;
  }

  static {
    DATA_ICON = IconFactory.createDataObject();
    COLLECTION_ICON = IconFactory.createPlacedIcon(IconFactory.createLoopCharacteristic(LoopCharacteristic.PARALLEL), BpmnConstants.Placements.DATA_OBJECT_MARKER, BpmnConstants.Sizes.MARKER);
  }

}
