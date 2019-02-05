package org.opensingular.lib.wicket.util.multiselect;

import java.io.Serializable;

public class ChosenOptions implements Serializable {

    private String  dataPlaceholder        = "Selecione";
    private String  noResultsText          = "Nenhum resultado encontrado!";
    private String  width                  = "100%";
    private boolean disableSearch          = false;
    private boolean showSpinner            = false;
    private boolean hideResultsOnSelect    = true;
    private int     disableSearchThreshold = 0;

    public String getDataPlaceholder() {
        return dataPlaceholder;
    }

    public ChosenOptions setDataPlaceholder(String dataPlaceholder) {
        this.dataPlaceholder = dataPlaceholder;
        return this;
    }

    public String getNoResultsText() {
        return noResultsText;
    }

    public ChosenOptions setNoResultsText(String noResultsText) {
        this.noResultsText = noResultsText;
        return this;
    }

    public boolean isDisableSearch() {
        return disableSearch;
    }

    public ChosenOptions setDisableSearch(boolean disableSearch) {
        this.disableSearch = disableSearch;
        return this;
    }

    public boolean isHideResultsOnSelect() {
        return hideResultsOnSelect;
    }

    public ChosenOptions setHideResultsOnSelect(boolean hideResultsOnSelect) {
        this.hideResultsOnSelect = hideResultsOnSelect;
        return this;
    }

    public int getDisableSearchThreshold() {
        return disableSearchThreshold;
    }

    public ChosenOptions setDisableSearchThreshold(int disableSearchThreshold) {
        this.disableSearchThreshold = disableSearchThreshold;
        return this;
    }

    public String getWidth() {
        return width;
    }

    public ChosenOptions setWidth(String width) {
        this.width = width;
        return this;
    }

    public boolean isShowSpinner() {
        return showSpinner;
    }

    public ChosenOptions setShowSpinner(boolean showSpinner) {
        this.showSpinner = showSpinner;
        return this;
    }
}
