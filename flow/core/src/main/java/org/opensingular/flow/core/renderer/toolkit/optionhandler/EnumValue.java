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
 * A data holder that represents a single enum value with a display name used for the ui component.
 */
public class EnumValue {
  private String name;

  /**
   * The display name of the enum value.
   * @return The Name.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * The display name of the enum value.
   * @param value The Name to set.
   * @see #getName()
   */
  private final void setName( String value ) {
    this.name = value;
  }

  private Object value;

  /**
   * The enum value.
   * @return The Value.
   */
  public final Object getValue() {
    return this.value;
  }

  /**
   * The enum value.
   * @param value The Value to set.
   * @see #getValue()
   */
  private final void setValue( Object value ) {
    this.value = value;
  }

  /**
   * Creates a new EnumValue instance.
   * @param name The display name of the enum value.
   * @param value The enum value.
   */
  public EnumValue( String name, Object value ) {
    setName(name);
    setValue(value);
  }

  //region Add new code here
  //endregion END: new code
}
