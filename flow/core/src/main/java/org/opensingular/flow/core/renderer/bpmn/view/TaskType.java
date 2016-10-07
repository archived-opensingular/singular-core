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
 * Specifies the type of a task according to BPMN.
 * @see ActivityNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum TaskType {
  /**
   * Specifies the type of a task to be an Abstract Task according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "Abstract")
  ABSTRACT(0),

  /**
   * Specifies the type of a task to be a Send Task according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "Send")
  SEND(1),

  /**
   * Specifies the type of a task to be a Receive Task according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "Receive")
  RECEIVE(2),

  /**
   * Specifies the type of a task to be a User Task according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "User")
  USER(3),

  /**
   * Specifies the type of a task to be a Manual Task according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "Manual")
  MANUAL(4),

  /**
   * Specifies the type of a task to be a Business Rule Task according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "BusinessRule")
  BUSINESS_RULE(5),

  /**
   * Specifies the type of a task to be a Service Task according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "Service")
  SERVICE(6),

  /**
   * Specifies the type of a task to be a Script Task according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "Script")
  SCRIPT(7),

  /**
   * Specifies the type of a task to be an Event-Triggered Sub-Task according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "EventTriggered")
  EVENT_TRIGGERED(8);

  private final int value;

  TaskType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final TaskType fromOrdinal( int ordinal ) {
    for (TaskType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
