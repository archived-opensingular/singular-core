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

/**
 * A participant of a Choreography that can be added to a {@link ChoreographyNodeStyle}.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class Participant {
  //region Properties

  final int getModCount() {
    return modCount;
  }

  private boolean multiInstance = false;

  private int modCount;

  /**
   * Gets if the participant contains multiple instances.
   * @return The MultiInstance.
   * @see #setMultiInstance(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isMultiInstance() {
    return multiInstance;
  }

  /**
   * Sets if the participant contains multiple instances.
   * @param value The MultiInstance to set.
   * @see #isMultiInstance()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setMultiInstance( boolean value ) {
    if (multiInstance != value) {
      modCount++;
      multiInstance = value;
    }
  }

  //endregion

  final double getSize() {
    return isMultiInstance() ? 32 : 20;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final Participant clone() {
    Participant newInstance = new Participant();
      newInstance.setMultiInstance(isMultiInstance());
    return newInstance;
  }
}
