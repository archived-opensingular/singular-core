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
package org.opensingular.flow.core.renderer.bpmn.editor;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.export.PixelImageExporter;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.WeakHashMap;

/**
 * Paints {@link INode} instances in a
 * {@link JList}.
 */
class PaletteNodeRenderer implements ListCellRenderer<INode> {
  // renders the list cell
  private DefaultListCellRenderer renderer;
  // holds an icon for each node
  private WeakHashMap<INode, NodeIcon> node2icon;

  PaletteNodeRenderer() {
    renderer = new DefaultListCellRenderer();
    node2icon = new WeakHashMap<>();
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends INode> list, INode node, int index, boolean isSelected, boolean cellHasFocus) {
    // we use a label as component that renders the list cell and sets the icon that paints the given node
    JLabel label = (JLabel) renderer.getListCellRendererComponent(list, node, index, isSelected, cellHasFocus);
    label.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setIcon(getIcon(node));
    label.setText(null);
    return label;
  }

  /**
   * Returns an {@link Icon} painting the given node.
   */
  private Icon getIcon(INode node) {
    NodeIcon icon = node2icon.get(node);
    if (icon == null) {
      icon = new NodeIcon(node);
      node2icon.put(node, icon);
    }
    return icon;
  }


  /**
   * An {@link Icon} that paints an {@link INode}.
   */
  private static class NodeIcon implements Icon {
    private static final int MAX_WIDTH = 300;
    private static final int MAX_HEIGHT = 70;
    final BufferedImage image;

    NodeIcon(INode node) {
      // create a GraphComponent instance and add a copy of the given node with its labels
      GraphComponent graphComponent = new GraphComponent();
      RectD newLayout = new RectD(PointD.ORIGIN, node.getLayout().toSizeD());
      INode newNode = graphComponent.getGraph().createNode(newLayout, node.getStyle(), node.getTag());
      node.getLabels().forEach(label ->
          graphComponent.getGraph().addLabel(newNode, label.getText(), label.getLayoutParameter(), label.getStyle(), label.getPreferredSize(), label.getTag()));
      // create an image of the node with its labels
      graphComponent.updateContentRect();
      PixelImageExporter pixelImageExporter = new PixelImageExporter(graphComponent.getContentRect().getEnlarged(2));
      pixelImageExporter.setTransparencyEnabled(true);
      double scale1 = Math.min(1, pixelImageExporter.getConfiguration().calculateScaleForWidth(MAX_WIDTH));
      double scale2 = Math.min(1, pixelImageExporter.getConfiguration().calculateScaleForHeight(MAX_HEIGHT));
      pixelImageExporter.getConfiguration().setScale(Math.min(scale1, scale2));
      image =  pixelImageExporter.exportToBitmap(graphComponent);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.drawImage(image, x, y, null);
    }

    @Override
    public int getIconWidth() {
      return image.getWidth();
    }

    @Override
    public int getIconHeight() {
      return image.getHeight();
    }
  }
}
