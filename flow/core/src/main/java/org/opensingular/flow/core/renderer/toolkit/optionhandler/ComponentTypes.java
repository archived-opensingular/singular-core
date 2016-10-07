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
package org.opensingular.flow.core.renderer.toolkit.optionhandler;


/**
 * The ui components available in the generated UI.
 */
public enum ComponentTypes {
  OPTION_GROUP(0),

  SLIDER(1),

  COMBOBOX(2),

  RADIO_BUTTON(3),

  CHECKBOX(4),

  SPINNER(5),

  HTML_BLOCK(6),

  TEXT(7);

  private final int value;

  private ComponentTypes( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final ComponentTypes fromOrdinal( int ordinal ) {
    for (ComponentTypes current : values()) {
      if (ordinal == current.value) return current;
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

  //region Add new code here
  //endregion END: new code
}
