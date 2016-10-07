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

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.view.IVisualCreator;

/**
 * An extension of {@link IVisualCreator} that allows to set bounds for the visualization.
 * <p>
 * To use this interface for the flyweight pattern, {@link #setBounds(IRectangle)} should be called before creating or
 * updating the visuals.
 * </p>
 */
interface IIcon extends IVisualCreator, ILookup {
  /**
   * Sets the bounds the visual shall consider.
   */
  void setBounds(IRectangle bounds);

}
