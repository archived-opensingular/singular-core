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

public class SMultiSelectionBySelectView extends AbstractSViewList {

    private String  dataPlaceholder        = "Selecione";
    private String  noResultsText          = "Nenhum resultado encontrado!";
    private boolean disableSearch          = false;
    private boolean hideResultsOnSelect    = false;
    private boolean showSpinner            = false;
    private int     disableSearchThreshold = 0;

    public String getDataPlaceholder() {
        return dataPlaceholder;
    }

    public SMultiSelectionBySelectView setDataPlaceholder(String dataPlaceholder) {
        this.dataPlaceholder = dataPlaceholder;
        return this;
    }

    public String getNoResultsText() {
        return noResultsText;
    }

    public SMultiSelectionBySelectView setNoResultsText(String noResultsText) {
        this.noResultsText = noResultsText;
        return this;
    }

    public boolean isDisableSearch() {
        return disableSearch;
    }

    public SMultiSelectionBySelectView setDisableSearch(boolean disableSearch) {
        this.disableSearch = disableSearch;
        return this;
    }

    public boolean isHideResultsOnSelect() {
        return hideResultsOnSelect;
    }

    public SMultiSelectionBySelectView setHideResultsOnSelect(boolean hideResultsOnSelect) {
        this.hideResultsOnSelect = hideResultsOnSelect;
        return this;
    }

    public int getDisableSearchThreshold() {
        return disableSearchThreshold;
    }

    public SMultiSelectionBySelectView setDisableSearchThreshold(int disableSearchThreshold) {
        this.disableSearchThreshold = disableSearchThreshold;
        return this;
    }

    public boolean isShowSpinner() {
        return showSpinner;
    }

    public SMultiSelectionBySelectView setShowSpinner(boolean showSpinner) {
        this.showSpinner = showSpinner;
        return this;
    }
}
