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
 * Specifies the type of an activity according to BPMN.
 * @see ActivityNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum ActivityType {
  /**
   * Specifies the type of an activity to be a Task according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "Task")
  TASK(0),

  /**
   * Specifies the type of an activity to be a Sub-Process according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "SubProcess")
  SUB_PROCESS(1),

  /**
   * Specifies the type of an activity to be a Transaction Sub-Process according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "Transaction")
  TRANSACTION(2),

  /**
   * Specifies the type of an activity to be an Event Sub-Process according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "EventSubProcess")
  EVENT_SUB_PROCESS(3),

  /**
   * Specifies the type of an activity to be a Call Activity according to BPMN.
   * @see ActivityNodeStyle
   */
  @GraphML(name = "CallActivity")
  CALL_ACTIVITY(4);

  private final int value;

  ActivityType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final ActivityType fromOrdinal( int ordinal ) {
    for (ActivityType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
