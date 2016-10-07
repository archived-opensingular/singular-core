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

package org.opensingular.server.commons.service.dto;

import java.io.Serializable;
import java.util.List;

public class MenuGroup implements Serializable {

    private String id;
    private String label;
    private List<ItemBox> itemBoxes;
    private List<ProcessDTO> processes;
    private List<FormDTO> forms;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<ItemBox> getItemBoxes() {
        return itemBoxes;
    }

    public void setItemBoxes(List<ItemBox> itemBoxes) {
        this.itemBoxes = itemBoxes;
    }

    public ItemBox getItemPorLabel(String itemName) {
        if (itemBoxes != null) {
            for (ItemBox itemBox : itemBoxes) {
                if (itemBox.getName().equalsIgnoreCase(itemName)) {
                    return itemBox;
                }
            }
        }

        return null;
    }

    public List<FormDTO> getForms() {
        return forms;
    }

    public void setForms(List<FormDTO> forms) {
        this.forms = forms;
    }
}
