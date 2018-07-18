/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.view;

/**
 * This view will include a button for check user location.
 */
public class SViewCurrentLocation extends SView {

    private boolean disableUserLocationSelection;

    /**
     * Disable inputs and markers, so that user cannot select its position
     *
     * @return
     */
    public SViewCurrentLocation disableUserLocationSelection() {
        this.disableUserLocationSelection = true;
        return this;
    }

    public SViewCurrentLocation disableUserLocationSelection(boolean value) {
        this.disableUserLocationSelection = value;
        return this;
    }

    public boolean isDisableUserLocationSelection() {
        return disableUserLocationSelection;
    }
}
