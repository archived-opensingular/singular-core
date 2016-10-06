/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket;

import org.opensingular.singular.commons.lambda.ISupplier;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.STypeAttachmentList;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.STypeSimple;
import org.opensingular.singular.form.SingularFormException;
import org.opensingular.singular.form.context.UIBuilder;
import org.opensingular.singular.form.context.UIComponentMapper;
import org.opensingular.singular.form.type.core.STypeBoolean;
import org.opensingular.singular.form.type.core.STypeDate;
import org.opensingular.singular.form.type.core.STypeDateTime;
import org.opensingular.singular.form.type.core.STypeDecimal;
import org.opensingular.singular.form.type.core.STypeHTML;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeLong;
import org.opensingular.singular.form.type.core.STypeMonetary;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.type.core.STypeTime;
import org.opensingular.singular.form.type.core.attachment.STypeAttachment;
import org.opensingular.singular.form.type.country.brazil.STypeTelefoneNacional;
import org.opensingular.singular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.singular.form.type.util.STypeYearMonth;
import org.opensingular.singular.form.wicket.enums.ViewMode;
import org.opensingular.singular.form.wicket.mapper.*;
import org.opensingular.singular.form.wicket.mapper.attachment.list.AttachmentListMapper;
import org.opensingular.singular.form.wicket.mapper.attachment.single.AttachmentMapper;
import org.opensingular.singular.form.wicket.mapper.composite.BlocksCompositeMapper;
import org.opensingular.singular.form.wicket.mapper.composite.DefaultCompositeMapper;
import org.opensingular.singular.form.wicket.mapper.masterdetail.ListMasterDetailMapper;
import org.opensingular.singular.form.wicket.mapper.richtext.PortletRichTextMapper;
import org.opensingular.singular.form.wicket.mapper.search.SearchModalMapper;
import org.opensingular.singular.form.wicket.mapper.selection.*;
import org.opensingular.singular.form.wicket.panel.BreadPanel;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSRow;
import org.opensingular.singular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.singular.form.view.SMultiSelectionByPicklistView;
import org.opensingular.singular.form.view.SMultiSelectionBySelectView;
import org.opensingular.singular.form.view.SView;
import org.opensingular.singular.form.view.SViewAttachmentList;
import org.opensingular.singular.form.view.SViewAutoComplete;
import org.opensingular.singular.form.view.SViewBooleanByRadio;
import org.opensingular.singular.form.view.SViewBreadcrumb;
import org.opensingular.singular.form.view.SViewByBlock;
import org.opensingular.singular.form.view.SViewByPortletRichText;
import org.opensingular.singular.form.view.SViewDateTime;
import org.opensingular.singular.form.view.SViewListByForm;
import org.opensingular.singular.form.view.SViewListByMasterDetail;
import org.opensingular.singular.form.view.SViewListByTable;
import org.opensingular.singular.form.view.SViewReadOnly;
import org.opensingular.singular.form.view.SViewSearchModal;
import org.opensingular.singular.form.view.SViewSelectionByRadio;
import org.opensingular.singular.form.view.SViewSelectionBySelect;
import org.opensingular.singular.form.view.SViewTab;
import org.opensingular.singular.form.view.SViewTextArea;
import org.opensingular.singular.form.view.ViewMapperRegistry;
import org.opensingular.singular.form.view.ViewResolver;

public class UIBuilderWicket implements UIBuilder<IWicketComponentMapper> {

    private final ViewMapperRegistry<IWicketComponentMapper> registry = newViewMapperRegistry();

    ViewMapperRegistry<IWicketComponentMapper> getViewMapperRegistry() {
        return registry;
    }

    public void build(WicketBuildContext ctx, ViewMode viewMode) {
        WicketBuildContext child = ctx;
        if (ctx.getParent() == null || ctx.isShowBreadcrumb()) {
            ctx.init(this, viewMode);
            BreadPanel panel = new BreadPanel("panel", ctx.getBreadCrumbs()) {
                @Override
                public boolean isVisible() {
                    return !breads.isEmpty();
                }
            };

            BSRow row = ctx.getContainer().newGrid().newRow();
            row.newCol().appendTag("div", panel);
            child = ctx.createChild(row.newCol(), true, ctx.getModel());
        }

        final IWicketComponentMapper mapper = resolveMapper(ctx.getCurrentInstance());
        mapper.buildView(child.init(this, viewMode));
    }

    private IWicketComponentMapper resolveMapper(SInstance instancia) {
        final ISupplier<? extends UIComponentMapper> customMapperFactory = instancia.getType().getCustomMapperFactory();
        final UIComponentMapper                      customMapper        = (customMapperFactory != null) ? customMapperFactory.get() : null;

        if (customMapper != null) {
            if (customMapper instanceof IWicketComponentMapper) {
                return (IWicketComponentMapper) customMapper;
            } else {
                throw new SingularFormException("Para utilizar custom mapper com Wicket, é necessário " + customMapper.getClass().getName()
                        + " implementar IWicketComponentMapper", instancia);
            }
        } else {
            final SView view = ViewResolver.resolve(instancia);
            return getViewMapperRegistry().getMapper(instancia, view).orElseThrow(
                    () -> new SingularFormException("Não há mappeamento de componente Wicket para o tipo", instancia, "view=" + view));
        }
    }

    protected ViewMapperRegistry<IWicketComponentMapper> newViewMapperRegistry() {
        //@formatter:off
        return new ViewMapperRegistry<IWicketComponentMapper>()
                .register(STypeSimple.class,     SViewSelectionByRadio.class,           RadioMapper::new)
                .register(STypeSimple.class,     SViewSelectionBySelect.class,          SelectMapper::new)
                .register(STypeSimple.class,     SViewReadOnly.class,                   ReadOnlyControlsFieldComponentMapper::new)
                .register(STypeBoolean.class,     SViewSelectionBySelect.class,         BooleanSelectMapper::new)
                .register(STypeBoolean.class,                                           BooleanMapper::new)
                .register(STypeBoolean.class,    SViewBooleanByRadio.class,             BooleanRadioMapper::new)
                .register(STypeInteger.class,                                           () -> new NumberMapper<>(Integer.class))
                .register(STypeLong.class,                                              () -> new NumberMapper<>(Long.class))
                .register(STypeString.class,                                            StringMapper::new)
                .register(STypeString.class,     SViewSearchModal.class,                SearchModalMapper::new)
                .register(STypeString.class,     SViewTextArea.class,                   TextAreaMapper::new)
                .register(STypeString.class,     SViewAutoComplete.class,               AutocompleteMapper::new)
                .register(STypeDate.class,                                              DateMapper::new)
                .register(STypeYearMonth.class,                                         YearMonthMapper::new)
                .register(STypeDecimal.class,                                           DecimalMapper::new)
                .register(STypeMonetary.class,                                          MoneyMapper::new)
                .register(STypeAttachment.class,                                        AttachmentMapper::new)
                .register(STypeLatitudeLongitude.class,                                 LatitudeLongitudeMapper::new)
                .register(STypeComposite.class,                                         DefaultCompositeMapper::new)
                .register(STypeComposite.class,   SViewTab.class,                       TabMapper::new)
                .register(STypeComposite.class,   SViewByBlock.class,                   BlocksCompositeMapper::new)
                .register(STypeComposite.class,   SViewSelectionByRadio.class,          RadioMapper::new)
                .register(STypeComposite.class,   SViewSelectionBySelect.class,         SelectMapper::new)
                .register(STypeComposite.class,   SViewSearchModal.class,               SearchModalMapper::new)
                .register(STypeComposite.class,   SViewAutoComplete.class,              AutocompleteMapper::new)
                .register(STypeComposite.class,   SViewReadOnly.class,                  ReadOnlyControlsFieldComponentMapper::new)
                .register(STypeList.class,        SMultiSelectionBySelectView.class,    MultipleSelectBSMapper::new)
                .register(STypeList.class,        SMultiSelectionByCheckboxView.class,  MultipleCheckMapper::new)
                .register(STypeList.class,        SMultiSelectionByPicklistView.class,  PicklistMapper::new)
                .register(STypeList.class,                                              TableListMapper::new)
                .register(STypeList.class,        SViewListByTable.class,               TableListMapper::new)
                .register(STypeList.class,        SViewListByForm.class,                PanelListaMapper::new)
                .register(STypeList.class,        SViewListByMasterDetail.class,        ListMasterDetailMapper::new)
                .register(STypeList.class,        SViewBreadcrumb.class,                ListBreadcrumbMapper::new)
                .register(STypeDateTime.class,                                          DateTimeMapper::new)
                .register(STypeDateTime.class,    SViewDateTime.class,                  DateTimeMapper::new)
                .register(STypeTime.class,                                              TimeMapper::new)
                .register(STypeTelefoneNacional.class,                                  TelefoneNacionalMapper::new)
                .register(STypeHTML.class,                                              RichTextMapper::new)
                .register(STypeAttachmentList.class, SViewAttachmentList.class, AttachmentListMapper::new)
                .register(STypeHTML.class, SViewByPortletRichText.class, PortletRichTextMapper::new);
        //@formatter:on
    }
}
