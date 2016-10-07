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
 * Specifies the type of an Event according to BPMN.
 * @see EventNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum EventType {
  /**
   * Specifies that an Event is a Plain Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Plain")
  PLAIN(0),

  /**
   * Specifies that an Event is a Message Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Message")
  MESSAGE(1),

  /**
   * Specifies that an Event is a Timer Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Timer")
  TIMER(2),

  /**
   * Specifies that an Event is an Escalation Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Escalation")
  ESCALATION(3),

  /**
   * Specifies that an Event is a Conditional Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Conditional")
  CONDITIONAL(4),

  /**
   * Specifies that an Event is a Link Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Link")
  LINK(5),

  /**
   * Specifies that an Event is an Error Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Error")
  ERROR(6),

  /**
   * Specifies that an Event is a Cancel Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Cancel")
  CANCEL(7),

  /**
   * Specifies that an Event is a Compensation Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Compensation")
  COMPENSATION(8),

  /**
   * Specifies that an Event is a Signal Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Signal")
  SIGNAL(9),

  /**
   * Specifies that an Event is a Multiple Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Multiple")
  MULTIPLE(10),

  /**
   * Specifies that an Event is a Parallel Multiple Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "ParallelMultiple")
  PARALLEL_MULTIPLE(11),

  /**
   * Specifies that an Event is a Terminate Event according to BPMN.
   * @see EventNodeStyle
   */
  @GraphML(name = "Terminate")
  TERMINATE(12);

  private final int value;

  EventType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final EventType fromOrdinal( int ordinal ) {
    for (EventType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
