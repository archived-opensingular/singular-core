/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.service.dto;

import java.io.Serializable;
import java.util.List;

public class MenuGroupDTO implements Serializable {

    private String label;
    private List<ItemBoxDTO> itemBoxes;
    private List<ProcessDTO> processes;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<ProcessDTO> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessDTO> processes) {
        this.processes = processes;
    }

    public List<ItemBoxDTO> getItemBoxes() {
        return itemBoxes;
    }

    public void setItemBoxes(List<ItemBoxDTO> itemBoxes) {
        this.itemBoxes = itemBoxes;
    }
}
