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
 * Specifies the Loop Characteristic of an Activity or Choreography according to BPMN.
 * @see ActivityNodeStyle
 * @see ChoreographyNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum LoopCharacteristic {
  /**
   * Specifies that an Activity or Choreography in not looping according to BPMN.
   * @see ActivityNodeStyle
   * @see ChoreographyNodeStyle
   */
  @GraphML(name = "None")
  NONE(0),

  /**
   * Specifies that an Activity or Choreography has a Standard Loop Characteristic according to BPMN.
   * @see ActivityNodeStyle
   * @see ChoreographyNodeStyle
   */
  @GraphML(name = "Loop")
  LOOP(1),

  /**
   * Specifies that an Activity or Choreography has a parallel Multi-Instance Loop Characteristic according to BPMN.
   * @see ActivityNodeStyle
   * @see ChoreographyNodeStyle
   */
  @GraphML(name = "Parallel")
  PARALLEL(2),

  /**
   * Specifies that an Activity or Choreography has a sequential Multi-Instance Loop Characteristic according to BPMN.
   * @see ActivityNodeStyle
   * @see ChoreographyNodeStyle
   */
  @GraphML(name = "Sequential")
  SEQUENTIAL(3);

  private final int value;

  LoopCharacteristic(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final LoopCharacteristic fromOrdinal( int ordinal ) {
    for (LoopCharacteristic current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
