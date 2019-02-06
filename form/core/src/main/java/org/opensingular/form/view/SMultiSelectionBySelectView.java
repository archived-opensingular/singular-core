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

package org.opensingular.form.view;

/**
 * View for the Multi Select.
 * Para maiores informações sobre o componente:
 * <a href='https://developer.snapappointments.com/bootstrap-select/examples/#live-search' ></a>
 */
public class SMultiSelectionBySelectView extends AbstractSViewList {

    //This will include the Live filter.
    boolean withLiveFilter = false;

    /**
     * Method for show the filter for multi select.
     * Default: False, don't show the filter.
     *
     * @param withLiveFilter True for show the live filter. False for not.
     * @return <code>this</code>
     */
    public SMultiSelectionBySelectView withLiveFilter(boolean withLiveFilter) {
        this.withLiveFilter = withLiveFilter;
        return this;
    }

    /**
     * @return True for show the filter, false for not.
     */
    public boolean isWithLiveFilter() {
        return withLiveFilter;
    }
}
