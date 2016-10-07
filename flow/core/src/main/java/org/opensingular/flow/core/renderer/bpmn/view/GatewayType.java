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
 * Specifies the type of a Gateway according to BPMN.
 * @see GatewayNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum GatewayType {
  /**
   * Specifies that a Gateway has the type Exclusive according to BPMN but should not use a marker.
   * @see GatewayNodeStyle
   */
  @GraphML(name = "ExclusiveWithoutMarker")
  EXCLUSIVE_WITHOUT_MARKER(0),

  /**
   * Specifies that a Gateway has the type Exclusive according to BPMN and should use a marker.
   * @see GatewayNodeStyle
   */
  @GraphML(name = "ExclusiveWithMarker")
  EXCLUSIVE_WITH_MARKER(1),

  /**
   * Specifies that a Gateway has the type Inclusive according to BPMN.
   * @see GatewayNodeStyle
   */
  @GraphML(name = "Inclusive")
  INCLUSIVE(2),

  /**
   * Specifies that a Gateway has the type Parallel according to BPMN.
   * @see GatewayNodeStyle
   */
  @GraphML(name = "Parallel")
  PARALLEL(3),

  /**
   * Specifies that a Gateway has the type Complex according to BPMN.
   * @see GatewayNodeStyle
   */
  @GraphML(name = "Complex")
  COMPLEX(4),

  /**
   * Specifies that a Gateway has the type Event-Based according to BPMN.
   * @see GatewayNodeStyle
   */
  @GraphML(name = "EventBased")
  EVENT_BASED(5),

  /**
   * Specifies that a Gateway has the type Exclusive Event-Based according to BPMN.
   * @see GatewayNodeStyle
   */
  @GraphML(name = "ExclusiveEventBased")
  EXCLUSIVE_EVENT_BASED(6),

  /**
   * Specifies that a Gateway has the type Parallel Event-Based according to BPMN.
   * @see GatewayNodeStyle
   */
  @GraphML(name = "ParallelEventBased")
  PARALLEL_EVENT_BASED(7);

  private final int value;

  GatewayType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final GatewayType fromOrdinal( int ordinal ) {
    for (GatewayType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
