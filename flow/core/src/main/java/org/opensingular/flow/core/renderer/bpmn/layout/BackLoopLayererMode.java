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
 * Working mode specifier of the stage.
 * @see BackLoopLayererStage#getLayererMode()
 */
public enum BackLoopLayererMode {
  /**
   * Working mode specifier of the stage. The stage only temporarily reverses edges but lets the core layerer decide the
   * final layering.
   * @see BackLoopLayererStage#getLayererMode()
   */
  REVERSING_EDGES(0),

  /**
   * Working mode specifier of the stage. The stage actively changes the layer assignment of back looping nodes.
   * @see BackLoopLayererStage#getLayererMode()
   */
  REASSIGNING_LAYERS(1);

  private final int value;

  BackLoopLayererMode(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final BackLoopLayererMode fromOrdinal( int ordinal ) {
    for (BackLoopLayererMode current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
