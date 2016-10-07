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
 * @see BpmnLayout#getScope()
 */
public enum Scope {
  /**
   * Scope specifier. Consider all elements during the layout.
   * @see BpmnLayout#getScope()
   */
  ALL_ELEMENTS(0),

  /**
   * Scope specifier to consider only selected elements.
   * <p>
   * The selection state of an edge is determined by a boolean value returned by the data provider associated with the data
   * provider key {@link BpmnLayout#AFFECTED_EDGES_DP_KEY} .
   * <br />
   * The selection state of a node is determined by a boolean value returned by the data provider associated with the data
   * provider key {@link BpmnLayout#AFFECTED_NODES_DP_KEY} .
   * </p>
   * <p>
   * Note, if the layout mode is set to {@link LayoutMode#FULL_LAYOUT} non-selected elements may also be moved to
   * produce valid drawings. However the layout algorithm uses the initial position of such elements as sketch.
   * </p>
   * @see BpmnLayout#getScope()
   */
  SELECTED_ELEMENTS(2);

  private final int value;

  Scope(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final Scope fromOrdinal( int ordinal ) {
    for (Scope current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
