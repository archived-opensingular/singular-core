/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.flow.core.renderer;

import org.opensingular.flow.core.FlowDefinition;
import org.opensingular.flow.core.SFlowUtil;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.lib.commons.extension.SingularExtensionUtil;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Helper class for {@link IFlowRenderer} related tasks.
 *
 * @author Daniel C. Bordin
 */
public final class RendererUtil {

    private RendererUtil() {}

    /**
     * Finds the appropriate {@link IFlowRenderer} implementation by using the extension point configuration (see {@link
     * SingularExtensionUtil} or throws a exception if the implementation isn't available.
     */
    @Nonnull
    public static IFlowRenderer findRenderer() {
        return SingularExtensionUtil.get().findExtensionOrException(FlowRendererProviderExtension.class).getRenderer();
    }

    /**
     * Finds the appropriate {@link IFlowRenderer} implementation used for displaying diagrams for the end user.
     * <p>Uses the extension point configuration (see {@link SingularExtensionUtil}.</p>
     */
    @Nonnull
    public static Optional<IFlowRenderer> findRendererForUserDisplay() {
        return SingularExtensionUtil.get().findExtension(FlowRendererProviderExtension.class,
                FlowRendererProviderExtension.FOR_USER_DISPLAY).map(p -> p.getRenderer());
    }

    /**
     * Generates a diagram of the flow and shows the result on the developer console.
     * <p>This method isn't supposed to be used in production environments, but rather for debugging or inspection
     * during developing.</p>
     */
    public static void showDiagramOnDesktopForUser(@Nonnull Class<? extends FlowDefinition<?>> definitionClass) {
        showDiagramOnDesktopForUser(definitionClass, SingularTestUtil.DEFAULT_WAIT_TIME_MILLI_AFTER_SHOW_ON_DESKTOP);
    }

    /**
     * Generates a diagram of the flow and shows the result on the developer console.
     * <p>This method isn't supposed to be used in production environments, but rather for debugging or inspection
     * during developing.</p>
     */
    public static void showDiagramOnDesktopForUser(@Nonnull Class<? extends FlowDefinition<?>> definitionClass,
            int waitTimeMilliAfterCall) {
        showDiagramOnDesktopForUser(SFlowUtil.instanceForDebug(definitionClass), waitTimeMilliAfterCall);
    }

    /**
     * Generates a diagram of the flow and shows the result on the developer console.
     * <p>This method isn't supposed to be used in production environments, but rather for debugging or inspection
     * during developing.</p>
     */
    public static void showDiagramOnDesktopForUser(@Nonnull FlowDefinition<?> definition) {
        showDiagramOnDesktopForUser(definition, SingularTestUtil.DEFAULT_WAIT_TIME_MILLI_AFTER_SHOW_ON_DESKTOP);
    }

    /**
     * Generates a diagram of the flow and shows the result on the developer console.
     * <p>This method isn't supposed to be used in production environments, but rather for debugging or inspection
     * during developing.</p>
     */
    public static void showDiagramOnDesktopForUser(@Nonnull FlowDefinition<?> definition,
            int waitTimeMilliAfterCall) {
        SingularTestUtil.showFileOnDesktopForUserAndWaitOpening(RendererUtil.class, "png", out -> {
            RendererUtil.findRenderer().generatePng(definition, out);
        });
    }

    /**
     * Generates a diagram of the flow and opens it in a swing frame for inspection.
     * <p>This method isn't supposed to be used in production environments, but rather for debugging or inspection
     * during developing.</p>
     */
    public static void showDiagramOnSwingFrame(@Nonnull Class<? extends FlowDefinition<?>> definitionClass) {
        FlowDefinition<?> definition = SFlowUtil.instanceForDebug(definitionClass);
        showDiagramOnSwingFrame(definition);
    }

    /**
     * Generates a diagram of the flow and opens it in a swing frame for inspection.
     * <p>This method isn't supposed to be used in production environments, but rather for debugging or inspection
     * during developing.</p>
     */
    public static void showDiagramOnSwingFrame(@Nonnull FlowDefinition<?> definition) {
        new ImageViewer(definition.getName(), findRenderer().generatePng(definition));
    }

    private static class ImageViewer extends JFrame {

        public ImageViewer(String title, byte[] image) throws HeadlessException {
            super(title);
            getRootPane().setContentPane(getImageComponent(image));
            pack();
            setLocationRelativeTo(null);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private static JComponent getImageComponent(byte[] image) {
            JPanel panel = new JPanel();
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel();
            label.setIcon(icon);
            panel.add(label);
            return panel;
        }
    }
}
