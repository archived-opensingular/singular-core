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
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;

import java.util.Arrays;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing a Conversation according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class ConversationNodeStyle extends BpmnNodeStyle {
  //region Properties

  private ConversationType type;

  /**
   * Gets the conversation type for this style.
   * @return The Type.
   * @see #setType(ConversationType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ConversationType.class, stringValue = "CONVERSATION")
  public final ConversationType getType() {
    return type;
  }

  /**
   * Sets the conversation type for this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ConversationType.class, stringValue = "CONVERSATION")
  public final void setType( ConversationType value ) {
    if (type != value || getIcon() == null) {
      setModCount(getModCount() + 1);
      type = value;

      IIcon typeIcon = IconFactory.createConversation(type);
      IIcon markerIcon = IconFactory.createConversationMarker(type);

      if (markerIcon != null) {
        markerIcon = IconFactory.createPlacedIcon(markerIcon, BpmnConstants.Placements.CONVERSATION_MARKER, BpmnConstants.Sizes.MARKER);
        typeIcon = IconFactory.createCombinedIcon(Arrays.asList(typeIcon, markerIcon));
      }

      setIcon(IconFactory.createPlacedIcon(typeIcon, BpmnConstants.Placements.CONVERSATION, BpmnConstants.Sizes.CONVERSATION));
    }
  }

  //endregion

  /**
   * Creates a new instance.
   */
  public ConversationNodeStyle() {
    setType(ConversationType.CONVERSATION);
    setMinimumSize(BpmnConstants.Sizes.CONVERSATION);
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    IRectangle layout = node.getLayout().toRectD();
    double width = Math.min(layout.getWidth(), layout.getHeight() / BpmnConstants.Sizes.CONVERSATION_WIDTH_HEIGHT_RATIO);
    double height = width * BpmnConstants.Sizes.CONVERSATION_WIDTH_HEIGHT_RATIO;
    RectD bounds = new RectD(layout.getCenter().x - width / 2, layout.getCenter().y - height / 2, width, height);

    GeneralPath path = new GeneralPath();
    path.moveTo(0, 0.5);
    path.lineTo(0.25, 0);
    path.lineTo(0.75, 0);
    path.lineTo(1, 0.5);
    path.lineTo(0.75, 1);
    path.lineTo(0.25, 1);
    path.close();

    Matrix2D transform = new Matrix2D();
    transform.translate(bounds.getTopLeft());
    transform.scale(bounds.width, bounds.height);
    path.transform(transform);
    return path;
  }

}
