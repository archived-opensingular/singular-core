/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.view;

public class SViewListByMasterDetail extends AbstractSViewListWithCustomColuns<SViewListByMasterDetail> {

    private boolean editEnabled = true;
    private String newActionLabel = "Adicionar";

    private String editActionLabel = "Atualizar";
    private String modalSize = "NORMAL";

    public SViewListByMasterDetail disableEdit() {
        this.editEnabled = false;
        return this;
    }

    public boolean isEditEnabled() {
        return editEnabled;
    }
    
    public SViewListByMasterDetail withNewActionLabel(String actionLabel) {
        this.newActionLabel = actionLabel;
        return this;
    }
    
    public String getNewActionLabel() {
        return newActionLabel;
    }

    public SViewListByMasterDetail withEditActionLabel(String actionLabel) {
        this.editActionLabel = actionLabel;
        return this;
    }
    
    public String getEditActionLabel() {
        return editActionLabel;
    }

    public String getModalSize() {
        return modalSize;
    }

    public SViewListByMasterDetail largeSize(){
        modalSize = "LARGE";
        return this;
    }

    public SViewListByMasterDetail autoSize(){
        modalSize = "FIT";
        return this;
    }

    public SViewListByMasterDetail smallSize(){
        modalSize = "SMALL";
        return this;
    }

    public SViewListByMasterDetail mediumSize(){
        modalSize = "NORMAL";
        return this;
    }

    public SViewListByMasterDetail fullSize(){
        modalSize = "FULL";
        return this;
    }
}
