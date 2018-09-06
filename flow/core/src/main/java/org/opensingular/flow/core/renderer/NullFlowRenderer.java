package org.opensingular.flow.core.renderer;

import java.io.IOException;
import java.io.OutputStream;

import org.opensingular.flow.core.FlowDefinition;

import javax.annotation.Nonnull;

public enum NullFlowRenderer implements IFlowRenderer {
        INSTANCE {
            @Nonnull
            @Override
            public byte[] generatePng(@Nonnull RendererRequest request) {
                return NULL_IMAGE_BYTES;
            }

            @Override
            public void generatePng(@Nonnull RendererRequest request, @Nonnull OutputStream out) throws IOException {
                out.write(NULL_IMAGE_BYTES);
            }
        };

    private static final byte[] NULL_IMAGE_BYTES = {
        -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, //
        0, 1, 0, 0, 0, 1, 1, 3, 0, 0, 0, 37, -37, 86, -54, 0, 0, 0, 1, 115, //
        82, 71, 66, 1, -39, -55, 44, 127, 0, 0, 0, 3, 80, 76, 84, 69, 0, 0, //
        0, -89, 122, 61, -38, 0, 0, 0, 1, 116, 82, 78, 83, 0, 64, -26, -40, //
        102, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -60, 0, 0, 14, -60, 1, //
        -107, 43, 14, 27, 0, 0, 0, 10, 73, 68, 65, 84, 120, -100, 99, 96, 0, //
        0, 0, 2, 0, 1, 72, -81, -92, 113, 0, 0, 0, 0, 73, 69, 78, 68
    };
}
