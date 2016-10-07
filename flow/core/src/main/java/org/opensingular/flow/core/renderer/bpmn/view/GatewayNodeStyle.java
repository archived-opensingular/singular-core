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
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.util.Arrays;
/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing a Gateway according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class GatewayNodeStyle extends BpmnNodeStyle {
  //region Initialize static fields

  private static final IIcon GATEWAY_ICON;

  //endregion

  //region Properties

  private GatewayType type;

  /**
   * Gets the gateway type for this style.
   * @return The Type.
   * @see #setType(GatewayType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GatewayType.class, stringValue = "EXCLUSIVE_WITHOUT_MARKER")
  public final GatewayType getType() {
    return type;
  }

  /**
   * Sets the gateway type for this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GatewayType.class, stringValue = "EXCLUSIVE_WITHOUT_MARKER")
  public final void setType( GatewayType value ) {
    if (type != value) {
      incrementModCount();
      type = value;
      typeIcon = IconFactory.createGatewayType(type);
      if (typeIcon != null) {
        typeIcon = IconFactory.createPlacedIcon(typeIcon, BpmnConstants.Placements.GATEWAY_TYPE, SizeD.EMPTY);
      }
    }
  }

  //endregion

  private IIcon typeIcon;

  /**
   * Creates a new instance.
   */
  public GatewayNodeStyle() {
    setMinimumSize(new SizeD(20, 20));
    setType(GatewayType.EXCLUSIVE_WITHOUT_MARKER);
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  void updateIcon(INode node) {
    setIcon(typeIcon != null ? IconFactory.createCombinedIcon(Arrays.asList(GATEWAY_ICON, typeIcon)) : GATEWAY_ICON);
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    double size = Math.min(node.getLayout().getWidth(), node.getLayout().getHeight());
    RectD bounds = new RectD(node.getLayout().getX() + node.getLayout().getWidth() / 2 - size / 2, node.getLayout().getY() + node.getLayout().getHeight() / 2 - size / 2, size, size);

    GeneralPath path = new GeneralPath();
    path.moveTo(bounds.x, bounds.getCenterY()); // <
    path.lineTo(bounds.getCenterX(), bounds.y); // ^
    path.lineTo(bounds.getMaxX(), bounds.getCenterY()); // >
    path.lineTo(bounds.getCenterX(), bounds.getMaxY()); // v
    path.close();
    return path;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected boolean isHit( IInputModeContext context, PointD p, INode node ) {
    RectD layout = node.getLayout().toRectD();
    if (!layout.getEnlarged(context.getHitTestRadius()).contains(p)) {
      return false;
    }
    double size = Math.min(layout.getWidth(), layout.getHeight());

    PointD distVector = PointD.subtract(layout.getCenter(), p);
    double dist = Math.abs(distVector.x) + Math.abs(distVector.y);
    return dist < size / 2 + context.getHitTestRadius();
  }

  static {
    GATEWAY_ICON = IconFactory.createPlacedIcon(IconFactory.createGateway(), BpmnConstants.Placements.GATEWAY, SizeD.EMPTY);
  }

}
