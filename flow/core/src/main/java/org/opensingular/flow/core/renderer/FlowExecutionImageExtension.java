package org.opensingular.flow.core.renderer;

import java.io.Serializable;

import org.opensingular.flow.core.FlowInstance;
import org.opensingular.lib.commons.extension.SingularExtension;

public interface FlowExecutionImageExtension extends SingularExtension, Serializable {

    public byte[] generateHistoryImage(FlowInstance flowInstance);

}
