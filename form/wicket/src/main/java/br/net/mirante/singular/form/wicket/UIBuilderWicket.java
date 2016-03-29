/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

import org.apache.wicket.Component;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeAttachmentList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionByCheckboxView;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionByPicklistView;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.form.mform.basic.view.SViewAttachmentList;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewBooleanByRadio;
import br.net.mirante.singular.form.mform.basic.view.SViewBreadcrumb;
import br.net.mirante.singular.form.mform.basic.view.SViewDateTime;
import br.net.mirante.singular.form.mform.basic.view.SViewListByForm;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySelect;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.basic.view.SViewTextArea;
import br.net.mirante.singular.form.mform.basic.view.ViewMapperRegistry;
import br.net.mirante.singular.form.mform.basic.view.ViewResolver;
import br.net.mirante.singular.form.mform.context.UIBuilder;
import br.net.mirante.singular.form.mform.context.UIComponentMapper;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeDateTime;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeLatitudeLongitude;
import br.net.mirante.singular.form.mform.core.STypeMonetary;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.brasil.STypeTelefoneNacional;
import br.net.mirante.singular.form.mform.util.comuns.STypeYearMonth;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.BooleanMapper;
import br.net.mirante.singular.form.wicket.mapper.DateMapper;
import br.net.mirante.singular.form.wicket.mapper.DateTimeMapper;
import br.net.mirante.singular.form.wicket.mapper.DecimalMapper;
import br.net.mirante.singular.form.wicket.mapper.DefaultCompostoMapper;
import br.net.mirante.singular.form.wicket.mapper.IntegerMapper;
import br.net.mirante.singular.form.wicket.mapper.LatitudeLongitudeMapper;
import br.net.mirante.singular.form.wicket.mapper.ListBreadcrumbMapper;
import br.net.mirante.singular.form.wicket.mapper.ListMasterDetailMapper;
import br.net.mirante.singular.form.wicket.mapper.MonetarioMapper;
import br.net.mirante.singular.form.wicket.mapper.PanelListaMapper;
import br.net.mirante.singular.form.wicket.mapper.StringMapper;
import br.net.mirante.singular.form.wicket.mapper.TabMapper;
import br.net.mirante.singular.form.wicket.mapper.TableListMapper;
import br.net.mirante.singular.form.wicket.mapper.TelefoneNacionalMapper;
import br.net.mirante.singular.form.wicket.mapper.TextAreaMapper;
import br.net.mirante.singular.form.wicket.mapper.YearMonthMapper;
import br.net.mirante.singular.form.wicket.mapper.annotation.AnnotationComponent;
import br.net.mirante.singular.form.wicket.mapper.attachment.AttachmentListMapper;
import br.net.mirante.singular.form.wicket.mapper.attachment.AttachmentMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.AutocompleteMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.BooleanRadioMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.MultipleCheckMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.MultipleSelectBSMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.PicklistMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.RadioMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectMapper;
import br.net.mirante.singular.form.wicket.mapper.selection.SelectModalBuscaMapper;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
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

        if (ctx.getParent() == null || ctx.isShowBreadcrumb()) {
            ctx.init(this, viewMode);
            //TODO mostrar apenas para quando tiver breadcrumbmapper na hierarquia
            BreadPanel panel = new BreadPanel("panel", ctx.getBreadCrumbs());
            BSRow row = ctx.getContainer().newGrid().newRow();
            row.newCol().appendTag("div", panel);
            ctx = ctx.createChild(row.newCol(), true, ctx.getModel());
        }

        final IWicketComponentMapper mapper = resolveMapper(ctx.getCurrentInstance());

        if (ctx.isRootContext() && ctx.annotation().enabled()) {
            ctx.init(this, viewMode);
            new AnnotationBuilder(this).build(ctx, viewMode, mapper);
        } else {
            mapper.buildView(ctx.init(this, viewMode));
        }

    }

    private IWicketComponentMapper resolveMapper(SInstance instancia) {

        final UIComponentMapper customMapper = instancia.getType().getCustomMapper();
        final SView view = ViewResolver.resolve(instancia);

        if (customMapper != null) {
            if (customMapper instanceof IWicketComponentMapper) {
                return (IWicketComponentMapper) customMapper;
            } else {
                throw new SingularFormException("Para utilizar custom mapper com Wicket, é necessario " + customMapper.getClass().getName()
                        + " implementar IWicketComponentMapper", instancia);
            }
        } else {
            return getViewMapperRegistry().getMapper(instancia, view).orElseThrow(
                    () -> new SingularFormException("Não há mappeamento de componente Wicket para o tipo", instancia, "view=" + view));
        }
    }

    protected ViewMapperRegistry<IWicketComponentMapper> newViewMapperRegistry() {
        //@formatter:off
        return new ViewMapperRegistry<IWicketComponentMapper>()
                .register(STypeSimple.class,    SViewSelectionByRadio.class,            RadioMapper::new)
                .register(STypeSimple.class,    SViewSelectionBySelect.class,           SelectMapper::new)
                .register(STypeBoolean.class,                                           BooleanMapper::new)
                .register(STypeBoolean.class,    SViewBooleanByRadio.class,             BooleanRadioMapper::new)
                .register(STypeInteger.class,                                           IntegerMapper::new)
                .register(STypeString.class,                                            StringMapper::new)
                .register(STypeString.class,     SViewSelectionBySearchModal.class,     SelectModalBuscaMapper::new)
                .register(STypeString.class,     SViewTextArea.class,                   TextAreaMapper::new)
                .register(STypeString.class,     SViewAutoComplete.class,               AutocompleteMapper::new)
                .register(STypeDate.class,                                              DateMapper::new)
                .register(STypeYearMonth.class,                                         YearMonthMapper::new)
                .register(STypeDecimal.class,                                           DecimalMapper::new)
                .register(STypeMonetary.class,                                          MoneyMapper::new)
                .register(STypeAttachment.class,                                        AttachmentMapper::new)
                .register(STypeLatitudeLongitude.class,                                 LatitudeLongitudeMapper::new)
                .register(STypeComposite.class,                                         DefaultCompostoMapper::new)
                .register(STypeComposite.class,   SViewTab.class,                       TabMapper::new)
                .register(STypeComposite.class,   SViewSelectionByRadio.class,          RadioMapper::new)
                .register(STypeComposite.class,   SViewSelectionBySelect.class,         SelectMapper::new)
                .register(STypeComposite.class,   SViewSelectionBySearchModal.class,    SelectModalBuscaMapper::new)
                .register(STypeComposite.class,   SViewAutoComplete.class,              AutocompleteMapper::new)
                .register(STypeList.class,        SMultiSelectionBySelectView.class,    MultipleSelectBSMapper::new)
                .register(STypeList.class,        SMultiSelectionByCheckboxView.class,  MultipleCheckMapper::new)
                .register(STypeList.class,        SMultiSelectionByPicklistView.class,  PicklistMapper::new)
                .register(STypeList.class,                                              TableListMapper::new)
                .register(STypeList.class,        SViewListByTable.class,               TableListMapper::new)
                .register(STypeList.class,        SViewListByForm.class,                PanelListaMapper::new)
                .register(STypeList.class,        SViewListByMasterDetail.class,        ListMasterDetailMapper::new)
                .register(STypeList.class,      SViewBreadcrumb.class,                  ListBreadcrumbMapper::new)
                .register(STypeDateTime.class,                                          DateTimeMapper::new)
                .register(STypeDateTime.class,    SViewDateTime.class,                  DateTimeMapper::new)
                .register(STypeTelefoneNacional.class,                                  TelefoneNacionalMapper::new)
                .register(STypeAttachmentList.class, SViewAttachmentList.class,         AttachmentListMapper::new);
        //@formatter:on
    }
}

class AnnotationBuilder {
    private static final Logger LOGGER = Logger.getLogger(AnnotationBuilder.class.getName());

    private UIBuilderWicket parent;
    private WicketBuildContext mainCtx;
    private BSRow mainGrid;

    AnnotationBuilder(UIBuilderWicket parent){
        this.parent = parent;
    }

    public void build(WicketBuildContext ctx, ViewMode viewMode, IWicketComponentMapper mapper) {
        final BSContainer<?> parentCol = ctx.getContainer();
        mainGrid = parentCol.newGrid().newRow();
        mainGrid.setOutputMarkupId(true);

        mainGrid.setCssClass("sannotation-form-row");
        mainCtx = createMainColumn(ctx, mainGrid);
        executeMainMapper(viewMode, mapper, mainCtx);

        BSGrid annotationColumn = createAnnotationColumn(mainGrid);
        mainCtx.setAnnotationContainer(annotationColumn);
        addAnnotationsFor(ctx, annotationColumn, (SInstance) ctx.getCurrentInstance());
    }

    private void executeMainMapper(ViewMode viewMode, IWicketComponentMapper mapper, WicketBuildContext mainCtx) {
        mapper.buildView(mainCtx.init(parent, viewMode));
    }

    private WicketBuildContext createMainColumn(WicketBuildContext ctx, BSRow superRow) {
        BSCol supercol = superRow.newCol(0).setCssClass("sannotation-form-col");
        final BSGrid formGrid = supercol.newGrid();
        return new WicketBuildContext(ctx, formGrid, ctx.getExternalContainer(),
                false, ctx.getModel());
    }

    private BSGrid createAnnotationColumn(BSRow superRow) {
        return superRow.newCol(0).setCssClass("sannotation-master-col").newGrid();
    }

    private void addAnnotationsFor(WicketBuildContext ctx, BSGrid ngrid, SInstance instance) {
        if (instance.asAtrAnnotation().isAnnotated()) {
            addAnnotationComponent(ngrid, instance, ctx);
        }
        if(instance instanceof SIComposite){
            addAnnotationsFor(ctx, ngrid, ((SIComposite) instance).getAllFields());
        }
    }

    private void addAnnotationComponent(BSGrid ngrid, SInstance instance,  WicketBuildContext ctx) {
        Optional<Component> target = ctx.getAnnotationTargetFor(instance);
        ngrid.newRow().appendTag("div", true, "",
            (id) -> {
                AnnotationComponent component = new AnnotationComponent(id, modelFor(instance), ctx);
                if(target.isPresent()){
                    component.setReferencedComponent(target.get());
                }
                component.setMainGrid(mainGrid);
                ctx.add(component);
                return component;
            });
        ;
    }

    private MInstanceRootModel<SInstance> modelFor(SInstance instance) {
        MInstanceRootModel<SInstance> model = new MInstanceRootModel<>();
        model.setObject(instance);
        return model;
    }

    private void addAnnotationsFor(WicketBuildContext ctx, BSGrid ngrid, Collection<SInstance> children) {
        for(SInstance field: children){
            addAnnotationsFor(ctx, ngrid, field);
        }
    }

}
