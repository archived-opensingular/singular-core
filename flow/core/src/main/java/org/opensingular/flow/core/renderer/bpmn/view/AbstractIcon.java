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
import com.yworks.yfiles.geometry.MutableRectangle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;

abstract class AbstractIcon implements IIcon {
  private IRectangle bounds;

  public final IRectangle getBounds() {
    return bounds;
  }

  protected AbstractIcon() {
    bounds = new MutableRectangle(0, 0, 0, 0);
  }

  public void setBounds( IRectangle bounds ) {
    this.bounds = bounds;
  }

  public abstract IVisual createVisual( IRenderContext context );

  public abstract IVisual updateVisual( IRenderContext context, IVisual oldVisual );

  @Override
  public <T> T lookup(Class<T> type) {
    return null;
  }
}
