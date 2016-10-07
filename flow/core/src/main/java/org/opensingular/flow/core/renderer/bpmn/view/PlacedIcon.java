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
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.input.IClickListener;

class PlacedIcon implements IIcon {
  private final SimpleNode dummyNode;

  private final SimpleLabel dummyLabel;

  private final ILabelModelParameter placementParameter;

  private final IIcon innerIcon;

  public PlacedIcon( IIcon innerIcon, ILabelModelParameter placementParameter, SizeD minimumSize ) {
    this.innerIcon = innerIcon;
    this.placementParameter = placementParameter;
    dummyNode = new SimpleNode();
    dummyLabel = new SimpleLabel(dummyNode, "", placementParameter);
    dummyLabel.setPreferredSize(minimumSize);
  }

  public final IVisual createVisual( IRenderContext context ) {
    return innerIcon.createVisual(context);
  }

  public final IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    return innerIcon.updateVisual(context, oldVisual);
  }

  public void setBounds( IRectangle bounds ) {
    dummyNode.setLayout(bounds);
    innerIcon.setBounds(placementParameter.getModel().getGeometry(dummyLabel, placementParameter).getBounds());
  }

  public <T> T lookup(Class<T> type) {
    if (type == IClickListener.class) {
      return (T) innerIcon.lookup(IClickListener.class);
    }
    return null;
  }
}
