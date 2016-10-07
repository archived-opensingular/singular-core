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
import com.yworks.yfiles.geometry.GeomUtilities;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.util.Arrays;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing an Event according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class EventNodeStyle extends BpmnNodeStyle {
  //region Properties

  private EventType type;

  /**
   * Gets the event type for this style.
   * @return The Type.
   * @see #setType(EventType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventType.class, stringValue = "PLAIN")
  public final EventType getType() {
    return type;
  }

  /**
   * Sets the event type for this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventType.class, stringValue = "PLAIN")
  public final void setType( EventType value ) {
    if (type != value) {
      incrementModCount();
      type = value;
      createTypeIcon();
    }
  }

  private EventCharacteristic characteristic;

  /**
   * Gets the event characteristic for this style.
   * @return The Characteristic.
   * @see #setCharacteristic(EventCharacteristic)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventCharacteristic.class, stringValue = "START")
  public final EventCharacteristic getCharacteristic() {
    return characteristic;
  }

  /**
   * Sets the event characteristic for this style.
   * @param value The Characteristic to set.
   * @see #getCharacteristic()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventCharacteristic.class, stringValue = "START")
  public final void setCharacteristic( EventCharacteristic value ) {
    if (characteristic != value || eventIcon == null) {
      incrementModCount();
      characteristic = value;
      createEventIcon();
    }
  }

  //endregion

  private IIcon eventIcon;

  private IIcon typeIcon;

  private boolean fillTypeIcon = false;

  /**
   * Creates a new instance.
   */
  public EventNodeStyle() {
    setMinimumSize(new SizeD(20, 20));
    setCharacteristic(EventCharacteristic.START);
    setType(EventType.PLAIN);
  }

  private void createTypeIcon() {
    typeIcon = IconFactory.createEventType(type, fillTypeIcon);
    if (typeIcon != null) {
      typeIcon = IconFactory.createPlacedIcon(typeIcon, BpmnConstants.Placements.EVENT_TYPE, SizeD.EMPTY);
    }
  }

  private void createEventIcon() {
    eventIcon = IconFactory.createEvent(getCharacteristic());
    eventIcon = IconFactory.createPlacedIcon(eventIcon, BpmnConstants.Placements.EVENT, getMinimumSize());
    boolean isFilled = getCharacteristic() == EventCharacteristic.THROWING || getCharacteristic() == EventCharacteristic.END;
    if (isFilled != fillTypeIcon) {
      fillTypeIcon = isFilled;
      createTypeIcon();
    }
  }

  @Override
  void updateIcon(INode node) {
    if (typeIcon != null) {
      setIcon(IconFactory.createCombinedIcon(Arrays.asList(eventIcon, typeIcon)));
    } else {
      setIcon(eventIcon);
    }
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    IRectangle layout = node.getLayout().toRectD();

    double size = Math.min(layout.getWidth(), layout.getHeight());
    RectD bounds = new RectD(layout.getCenter().x - size / 2, layout.getCenter().y - size / 2, size, size);

    GeneralPath path = new GeneralPath();
    path.appendEllipse(new RectD(bounds.getTopLeft(), bounds.toSizeD()), false);
    return path;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected boolean isHit( IInputModeContext context, PointD p, INode node ) {
    IRectangle layout = node.getLayout().toRectD();
    double size = Math.min(layout.getWidth(), layout.getHeight());
    RectD bounds = new RectD(layout.getCenter().x - size / 2, layout.getCenter().y - size / 2, size, size);
    return GeomUtilities.ellipseContains(bounds, p, context.getHitTestRadius());
  }
}
