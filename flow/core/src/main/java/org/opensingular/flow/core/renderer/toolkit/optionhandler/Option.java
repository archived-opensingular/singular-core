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

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.List;

/**
 * A data holder for a single configuration option.
 * <p>
 * The {@link ConfigConverter} scanns a configuration object in its {@link ConfigConverter#convert(Object)} method and
 * creates {@link Option} items for the public fields and properties of this object.
 * </p>
 */
public class Option {
  private String name;

  /**
   * The internally used name of this option.
   * @return The Name.
   * @see #setName(String)
   */
  public final String getName() {
    return this.name;
  }

  /**
   * The internally used name of this option.
   * @param value The Name to set.
   * @see #getName()
   */
  public final void setName( String value ) {
    this.name = value;
  }

  private String label;

  /**
   * The displayed label of this option.
   * @return The Label.
   * @see #setLabel(String)
   */
  public final String getLabel() {
    return this.label;
  }

  /**
   * The displayed label of this option.
   * @param value The Label to set.
   * @see #getLabel()
   */
  public final void setLabel( String value ) {
    this.label = value;
  }

  private Object defaultValue;

  /**
   * The default value this option has.
   * @return The DefaultValue.
   * @see #setDefaultValue(Object)
   */
  public final Object getDefaultValue() {
    return this.defaultValue;
  }

  /**
   * The default value this option has.
   * @param value The DefaultValue to set.
   * @see #getDefaultValue()
   */
  public final void setDefaultValue( Object value ) {
    this.defaultValue = value;
  }

  /**
   * The current value of the field or property in the scanned configuration object.
   * <p>
   * Note that this property is read from and written to the configuration object this option was created for.
   * </p>
   * @return The Value.
   * @see #setValue(Object)
   */
  public final Object getValue() {
    if (getGetter() != null) {
      return getGetter().get();
    }
    return getDefaultValue();
  }

  /**
   * The current value of the field or property in the scanned configuration object.
   * <p>
   * Note that this property is read from and written to the configuration object this option was created for.
   * </p>
   * @param value The Value to set.
   * @see #getValue()
   */
  public final void setValue( Object value ) {
    if (getSetter() != null) {
      getSetter().accept(value);
    }
  }

  private Type valueType;

  /**
   * The {@link Type} of this option.
   * @return The ValueType.
   * @see #setValueType(Type)
   */
  public final Type getValueType() {
    return this.valueType;
  }

  /**
   * The {@link Type} of this option.
   * @param value The ValueType to set.
   * @see #getValueType()
   */
  public final void setValueType( Type value ) {
    this.valueType = value;
  }

  private ComponentTypes componentType = ComponentTypes.OPTION_GROUP;

  /**
   * The type of the ui component that shall be used to represent this option.
   * @return The ComponentType.
   * @see #setComponentType(ComponentTypes)
   */
  public final ComponentTypes getComponentType() {
    return this.componentType;
  }

  /**
   * The type of the ui component that shall be used to represent this option.
   * @param value The ComponentType to set.
   * @see #getComponentType()
   */
  public final void setComponentType( ComponentTypes value ) {
    this.componentType = value;
  }

  private List<EnumValue> enumValues;

  /**
   * A list of the available enum values for this option.
   * @return The EnumValues.
   * @see #setEnumValues(List)
   */
  public final List<EnumValue> getEnumValues() {
    return this.enumValues;
  }

  /**
   * A list of the available enum values for this option.
   * @param value The EnumValues to set.
   * @see #getEnumValues()
   */
  public final void setEnumValues( List<EnumValue> value ) {
    this.enumValues = value;
  }

  private MinMax minMax;

  /**
   * The {@link MinMax} containing the minimum and maximum value for this option.
   * @return The MinMax.
   * @see #setMinMax(MinMax)
   */
  public final MinMax getMinMax() {
    return this.minMax;
  }

  /**
   * The {@link MinMax} containing the minimum and maximum value for this option.
   * @param value The MinMax to set.
   * @see #getMinMax()
   */
  public final void setMinMax( MinMax value ) {
    this.minMax = value;
  }

  private Supplier<Boolean> checkDisabled;

  /**
   * A utility method that returns whether this option should currently be disabled.
   * @return The CheckDisabled.
   * @see #setCheckDisabled(Supplier)
   */
  public final Supplier<Boolean> getCheckDisabled() {
    return this.checkDisabled;
  }

  /**
   * A utility method that returns whether this option should currently be disabled.
   * @param value The CheckDisabled to set.
   * @see #getCheckDisabled()
   */
  public final void setCheckDisabled( Supplier<Boolean> value ) {
    this.checkDisabled = value;
  }

  //region internal properties

  private Supplier<Object> getter;

  final Supplier<Object> getGetter() {
    return this.getter;
  }

  final void setGetter( Supplier<Object> value ) {
    this.getter = value;
  }

  private Consumer<Object> setter;

  final Consumer<Object> getSetter() {
    return this.setter;
  }

  final void setSetter( Consumer<Object> value ) {
    this.setter = value;
  }

  //endregion

  /**
   * Resets the {@link #getValue() Value} to the {@link #getDefaultValue() DefaultValue}.
   */
  public void reset() {
    setValue(getDefaultValue());
  }

  //region Add new code here
  //endregion END: new code
}
