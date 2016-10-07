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
 * Specifies the characteristic of an event.
 * @see EventNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum EventCharacteristic {
  /**
   * Specifies that an Event is a Start Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Start")
  START(0),

  /**
   * Specifies that an Event is a Start Event for a Sub-Process according to BPMN that interrupts the containing Process.
   * @see EventNodeStyle
   */
  @GraphML(name = "SubProcessInterrupting")
  SUB_PROCESS_INTERRUPTING(1),

  /**
   * Specifies that an Event is a Start Event for a Sub-Process according to BPMN that doesn`t interrupt the containing
   * Process.
   * @see EventNodeStyle
   */
  @GraphML(name = "SubProcessNonInterrupting")
  SUB_PROCESS_NON_INTERRUPTING(2),

  /**
   * Specifies that an Event is an Intermediate Catching Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Catching")
  CATCHING(3),

  /**
   * Specifies that an Event is an Intermediate Event Attached to an Activity Boundary according to BPMN that interrupts the
   * Activity.
   * @see EventNodeStyle
   */
  @GraphML(name = "BoundaryInterrupting")
  BOUNDARY_INTERRUPTING(4),

  /**
   * Specifies that an Event is an Intermediate Event Attached to an Activity Boundary according to BPMN that doesn't
   * interrupt the Activity.
   * @see EventNodeStyle
   */
  @GraphML(name = "BoundaryNonInterrupting")
  BOUNDARY_NON_INTERRUPTING(5),

  /**
   * Specifies that an Event is an Intermediate Throwing Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Throwing")
  THROWING(6),

  /**
   * Specifies that an Event is an End Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "End")
  END(7);

  private final int value;

  EventCharacteristic(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final EventCharacteristic fromOrdinal( int ordinal ) {
    for (EventCharacteristic current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
