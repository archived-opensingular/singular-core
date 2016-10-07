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

import com.yworks.yfiles.annotations.Obfuscation;
import com.yworks.yfiles.graphml.GraphML;

/**
 * Specifies if an Activity is an expanded or collapsed Sub-Process according to BPMN.
 * @see ActivityNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum SubState {
  /**
   * Specifies that an Activity is either no Sub-Process according to BPMN or should use no Sub-Process marker.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "None")
  NONE(0),

  /**
   * Specifies that an Activity is an expanded Sub-Process according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "Expanded")
  EXPANDED(1),

  /**
   * Specifies that an Activity is a collapsed Sub-Process according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "Collapsed")
  COLLAPSED(2),

  /**
   * Specifies that the folding state of an {@link com.yworks.yfiles.graph.INode} determines if an Activity is an expanded or
   * collapsed Sub-Process according to BPMN.
   * @see ActivityNodeStyle
   * @see com.yworks.yfiles.graph.IFoldingView#isExpanded(com.yworks.yfiles.graph.INode)
   */
  @GraphML(name = "Dynamic")
  DYNAMIC(3);

  private final int value;

  SubState(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final SubState fromOrdinal( int ordinal ) {
    for (SubState current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
