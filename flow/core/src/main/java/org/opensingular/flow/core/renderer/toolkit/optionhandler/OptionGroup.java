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

import java.util.ArrayList;
import java.util.List;

/**
 * An option group containing a list of {@link #getChildOptions() ChildOptions}.
 */
public class OptionGroup extends Option {
  private List<Option> childOptions;

  /**
   * The child options contained in this group.
   * @return The ChildOptions.
   */
  public final List<Option> getChildOptions() {
    return this.childOptions;
  }

  /**
   * The child options contained in this group.
   * @param value The ChildOptions to set.
   * @see #getChildOptions()
   */
  private final void setChildOptions( List<Option> value ) {
    this.childOptions = value;
  }

  /**
   * Creates a new OptionGroup and initializes {@link #getChildOptions() ChildOptions}.
   */
  public OptionGroup() {
    setChildOptions(new ArrayList<Option>());
  }

  //region Add new code here
  //endregion END: new code
}
