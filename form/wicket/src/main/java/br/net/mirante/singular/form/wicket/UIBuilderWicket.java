/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.commons.lambda.ISupplier;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeAttachmentList;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.context.UIBuilder;
import br.net.mirante.singular.form.context.UIComponentMapper;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.type.core.STypeDate;
import br.net.mirante.singular.form.type.core.STypeDateTime;
import br.net.mirante.singular.form.type.core.STypeDecimal;
import br.net.mirante.singular.form.type.core.STypeHTML;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeMonetary;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.core.STypeTime;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.type.country.brazil.STypeTelefoneNacional;
import br.net.mirante.singular.form.type.util.STypeLatitudeLongitude;
import br.net.mirante.singular.form.type.util.STypeYearMonth;
import br.net.mirante.singular.form.view.SMultiSelectionByCheckboxView;
import br.net.mirante.singular.form.view.SMultiSelectionByPicklistView;
import br.net.mirante.singular.form.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.view.SView;
import br.net.mirante.singular.form.view.SViewAttachmentList;
import br.net.mirante.singular.form.view.SViewAutoComplete;
import br.net.mirante.singular.form.view.SViewBooleanByRadio;
import br.net.mirante.singular.form.view.SViewBreadcrumb;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.view.SViewDateTime;
import br.net.mirante.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.view.SViewListByTable;
import br.net.mirante.singular.form.view.SViewReadOnly;
import br.net.mirante.singular.form.view.SViewSearchModal;
import br.net.mirante.singular.form.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.view.SViewSelectionBySelect;
import br.net.mirante.singular.form.view.SViewTab;
import br.net.mirante.singular.form.view.SViewTextArea;
import br.net.mirante.singular.form.view.ViewMapperRegistry;
import br.net.mirante.singular.form.view.ViewResolver;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.BooleanMapper;
import br.net.mirante.singular.form.wicket.mapper.DateMapper;
import br.net.mirante.singular.form.wicket.mapper.DateTimeMapper;
import br.net.mirante.singular.form.wicket.mapper.DecimalMapper;
import br.net.mirante.singular.form.wicket.mapper.IntegerMapper;
import br.net.mirante.singular.form.wicket.mapper.LatitudeLongitudeMapper;
import br.net.mirante.singular.form.wicket.mapper.ListBreadcrumbMapper;
import br.net.mirante.singular.form.wicket.mapper.MoneyMapper;
import br.net.mirante.singular.form.wicket.mapper.PanelListaMapper;
import br.net.mirante.singular.form.wicket.mapper.ReadOnlyControlsFieldComponentMapper;
import br.net.mirante.singular.form.wicket.mapper.RichTextMapper;
import br.net.mirante.singular.form.wicket.mapper.StringMapper;
import br.net.mirante.singular.form.wicket.mapper.TabMapper;
import br.net.mirante.singular.form.wicket.mapper.TableListMapper;
import br.net.mirante.singular.form.wicket.mapper.TelefoneNacionalMapper;
import br.net.mirante.singular.form.wicket.mapper.TextAreaMapper;
import br.net.mirante.singular.form.wicket.mapper.TimeMapper;
import br.net.mirante.singular.form.wicket.mapper.YearMonthMapper;
import br.net.mirante.singular.form.wicket.mapper.attachment.list.AttachmentListMapper;
import br.net.mirante.singular.form.wicket.mapper.attachment.single.AttachmentMapper;
import br.net.mirante.singular.form.wicket.mapper.composite.BlocksCompositeMapper;
import br.net.mirante.singular.form.wicket.mapper.composite.DefaultCompositeMapper;
import br.net.mirante.singular.form.wicket.mapper.masterdetail.ListMasterDetailMapper;
import br.net.mirante.singular.form.wicket.mapper.search.SearchModalMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.AutocompleteMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.BooleanRadioMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.BooleanSelectMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.MultipleCheckMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.MultipleSelectBSMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.PicklistMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.RadioMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectMapper;
import br.net.mirante.singular.form.wicket.panel.BreadPanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;

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
            child.setAnnotationMode(ctx.getAnnotationMode());
        }

        final IWicketComponentMapper mapper = resolveMapper(ctx.getCurrentInstance());

        if (ctx.getAnnotationMode().enabled()) {
            ctx.init(this, viewMode);
            new AnnotationBuilder(this).build(ctx, viewMode, mapper);
        } else {
            mapper.buildView(child.init(this, viewMode));
        }

    }

    private IWicketComponentMapper resolveMapper(SInstance instancia) {
        final ISupplier<? extends UIComponentMapper> customMapperFactory = instancia.getType().getCustomMapperFactory();
        final UIComponentMapper customMapper = (customMapperFactory != null) ? customMapperFactory.get() : null;

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
                .register(STypeInteger.class,                                           IntegerMapper::new)
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
                .register(STypeAttachmentList.class, SViewAttachmentList.class,         AttachmentListMapper::new);
        //@formatter:on
    }

    static class AnnotationBuilder {
        //private static final Logger LOGGER = Logger.getLogger(AnnotationBuilder.class.getName());

        private UIBuilderWicket    parent;
        private WicketBuildContext mainCtx;
        private BSRow              mainGrid;

        AnnotationBuilder(UIBuilderWicket parent) {
            this.parent = parent;
        }

        public void build(WicketBuildContext ctx, ViewMode viewMode, IWicketComponentMapper mapper) {
            final BSContainer<?> parentCol = ctx.getContainer();
            mainGrid = parentCol.newGrid().newRow();
            mainGrid.setOutputMarkupId(true);

            mainGrid.setCssClass("sannotation-form-row");
            mainCtx = createMainColumn(ctx, mainGrid);
            executeMainMapper(viewMode, mapper, mainCtx);
        }

        private void executeMainMapper(ViewMode viewMode, IWicketComponentMapper mapper, WicketBuildContext mainCtx) {
            mapper.buildView(mainCtx.init(parent, viewMode));
        }

        private WicketBuildContext createMainColumn(WicketBuildContext ctx, BSRow superRow) {
            BSCol supercol = superRow.newCol(0).setCssClass("sannotation-form-col");
            final BSGrid formGrid = supercol.newGrid();
            return ctx.createChild(formGrid, false, ctx.getModel());
        }
    }
}
