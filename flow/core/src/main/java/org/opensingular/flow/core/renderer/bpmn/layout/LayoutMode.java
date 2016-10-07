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
package org.opensingular.flow.core.renderer.bpmn.layout;


/**
 * Specifies the layout mode.
 * @see BpmnLayout#getLayoutMode()
 */
public enum LayoutMode {
  /**
   * Specifies the layout mode. In this mode only edges are routed while the node positions are fixed.
   * @see BpmnLayout#getLayoutMode()
   */
  ROUTE_EDGES(0),

  /**
   * Specifies the layout mode. In this mode all elements are laid out.
   * @see BpmnLayout#getLayoutMode()
   */
  FULL_LAYOUT(1);

  private final int value;

  LayoutMode(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final LayoutMode fromOrdinal( int ordinal ) {
    for (LayoutMode current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
