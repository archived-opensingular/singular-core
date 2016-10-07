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
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing a Data Store according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class DataStoreNodeStyle extends BpmnNodeStyle {
  /**
   * Creates a new instance.
   */
  public DataStoreNodeStyle() {
    setIcon(IconFactory.createDataStore());
    setMinimumSize(new SizeD(30, 20));
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    final double halfEllipseHeight = 0.125;
    GeneralPath path = new GeneralPath();

    path.moveTo(0, halfEllipseHeight);
    path.lineTo(0, 1 - halfEllipseHeight);
    path.cubicTo(0, 1, 1, 1, 1, 1 - halfEllipseHeight);
    path.lineTo(1, halfEllipseHeight);
    path.cubicTo(1, 0, 0, 0, 0, halfEllipseHeight);
    path.close();

    Matrix2D transform = new Matrix2D();
    IRectangle layout = node.getLayout().toRectD();
    transform.translate(layout.getTopLeft());
    transform.scale(layout.getWidth(), layout.getHeight());
    path.transform(transform);
    return path;
  }
}
