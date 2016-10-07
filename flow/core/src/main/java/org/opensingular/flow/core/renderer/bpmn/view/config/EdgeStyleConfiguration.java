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
package org.opensingular.flow.core.renderer.bpmn.view.config;

import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.view.GraphComponent;

/**
 * Abstract base class for configurations that can be displayed in an option editor.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
public abstract class EdgeStyleConfiguration<TStyle extends IEdgeStyle> {
  /**
   * Applies this configuration to the given {@code item} in a {@link GraphComponent}.
   * <p>
   * This is the main method of this class.
   * </p>
   * @param graphControl The {@code GraphControl} to apply the configuration on.
   * @param item The item to change.
   */
  public void apply( GraphComponent graphControl, IEdge item ) {
    graphControl.getGraph().setStyle(item, getStyleTemplate());
    graphControl.getGraphModelManager().updateDescriptor(item);
  }

  private TStyle styleTemplate;

  public final TStyle getStyleTemplate() {
    if (styleTemplate == null) {
      styleTemplate = createDefault();
    }
    return styleTemplate;
  }

  public void initializeFromExistingStyle( TStyle item ) {
    styleTemplate = (TStyle)item.clone();
  }

  protected abstract TStyle createDefault();
}
