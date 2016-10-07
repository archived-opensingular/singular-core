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

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing an Annotation according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class AnnotationNodeStyle extends BpmnNodeStyle {
  //region Properties

  private boolean left;

  /**
   * Gets if the bracket of the open rectangle shall be on the left side.
   * @return The Left.
   * @see #setLeft(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isLeft() {
    return left;
  }

  /**
   * Sets if the bracket of the open rectangle shall be on the left side.
   * @param value The Left to set.
   * @see #isLeft()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setLeft( boolean value ) {
    if (value != left) {
      incrementModCount();
      left = value;
      setIcon(IconFactory.createAnnotation(value));
    }
  }

  //endregion

  /**
   * Creates a new instance.
   */
  public AnnotationNodeStyle() {
    setLeft(true);
    setMinimumSize(new SizeD(30, 10));
  }
}
