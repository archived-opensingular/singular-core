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

package org.opensingular.form.wicket;

import java.util.Deque;
import java.util.LinkedList;

import org.opensingular.form.SInstance;
import org.opensingular.form.STypeAttachmentList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.context.UIBuilder;
import org.opensingular.form.context.UIComponentMapper;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeHTML;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.country.brazil.STypeCNPJ;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.type.country.brazil.STypeTelefoneNacional;
import org.opensingular.form.type.util.STypeLatitudeLongitude;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.form.view.SMultiSelectionByPicklistView;
import org.opensingular.form.view.SMultiSelectionBySelectView;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewAttachmentList;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.view.SViewBooleanByRadio;
import org.opensingular.form.view.SViewBreadcrumb;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewByPortletRichText;
import org.opensingular.form.view.SViewDateTime;
import org.opensingular.form.view.SViewListByForm;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.form.view.SViewReadOnly;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.form.view.SViewSelectionBySelect;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.view.SViewTextArea;
import org.opensingular.form.view.ViewMapperRegistry;
import org.opensingular.form.view.ViewResolver;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.BooleanMapper;
import org.opensingular.form.wicket.mapper.DateMapper;
import org.opensingular.form.wicket.mapper.DateTimeMapper;
import org.opensingular.form.wicket.mapper.DecimalMapper;
import org.opensingular.form.wicket.mapper.LatitudeLongitudeMapper;
import org.opensingular.form.wicket.mapper.ListBreadcrumbMapper;
import org.opensingular.form.wicket.mapper.MoneyMapper;
import org.opensingular.form.wicket.mapper.NumberMapper;
import org.opensingular.form.wicket.mapper.PanelListMapper;
import org.opensingular.form.wicket.mapper.ReadOnlyControlsFieldComponentMapper;
import org.opensingular.form.wicket.mapper.RichTextMapper;
import org.opensingular.form.wicket.mapper.StringMapper;
import org.opensingular.form.wicket.mapper.TabMapper;
import org.opensingular.form.wicket.mapper.TableListMapper;
import org.opensingular.form.wicket.mapper.TelefoneNacionalMapper;
import org.opensingular.form.wicket.mapper.TextAreaMapper;
import org.opensingular.form.wicket.mapper.TimeMapper;
import org.opensingular.form.wicket.mapper.YearMonthMapper;
import org.opensingular.form.wicket.mapper.attachment.list.AttachmentListMapper;
import org.opensingular.form.wicket.mapper.attachment.single.AttachmentMapper;
import org.opensingular.form.wicket.mapper.composite.BlocksCompositeMapper;
import org.opensingular.form.wicket.mapper.composite.DefaultCompositeMapper;
import org.opensingular.form.wicket.mapper.country.brazil.CNPJMapper;
import org.opensingular.form.wicket.mapper.country.brazil.CPFMapper;
import org.opensingular.form.wicket.mapper.masterdetail.ListMasterDetailMapper;
import org.opensingular.form.wicket.mapper.richtext.PortletRichTextMapper;
import org.opensingular.form.wicket.mapper.search.SearchModalMapper;
import org.opensingular.form.wicket.mapper.selection.AutocompleteMapper;
import org.opensingular.form.wicket.mapper.selection.BooleanRadioMapper;
import org.opensingular.form.wicket.mapper.selection.BooleanSelectMapper;
import org.opensingular.form.wicket.mapper.selection.MultipleCheckMapper;
import org.opensingular.form.wicket.mapper.selection.MultipleSelectBSMapper;
import org.opensingular.form.wicket.mapper.selection.PicklistMapper;
import org.opensingular.form.wicket.mapper.selection.RadioMapper;
import org.opensingular.form.wicket.mapper.selection.SelectMapper;
import org.opensingular.form.wicket.panel.BreadPanel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSRow;

public class UIBuilderWicket implements UIBuilder<IWicketComponentMapper> {

    private final ViewMapperRegistry<IWicketComponentMapper> registry = newViewMapperRegistry();

    public UIBuilderWicket() {}

    ViewMapperRegistry<IWicketComponentMapper> getViewMapperRegistry() {
        return registry;
    }

    public void build(WicketBuildContext ctx, ViewMode viewMode) {
        final Deque<IWicketBuildListener> listeners = new LinkedList<>(ctx.getListeners());

        ctx.init(this, viewMode);

        // onBuildContextInitialized
        listeners.stream().forEach(it -> it.onBuildContextInitialized(ctx, viewMode));

        final IWicketComponentMapper mapper = resolveMapper(ctx.getCurrentInstance());

        if (mapper instanceof IWicketBuildListener)
            listeners.addFirst((IWicketBuildListener) mapper);

        // onMapperResolved
        listeners.stream().forEach(it -> it.onMapperResolved(ctx, mapper, viewMode));

        // decorateContext
        WicketBuildContext childCtx = ctx;
        for (IWicketBuildListener listener : listeners) {
            WicketBuildContext newContext = listener.decorateContext(ctx, mapper, viewMode);
            if (newContext != null && newContext != childCtx) {
                childCtx = newContext;
                childCtx.init(this, viewMode);
            }
        }

        if (ctx.getParent() == null || ctx.isShowBreadcrumb()) {
            BreadPanel panel = new BreadPanel("panel", ctx.getBreadCrumbs()) {
                @Override
                public boolean isVisible() {
                    return !this.isEmpty();
                }
            };

            BSRow row = ctx.getContainer().newGrid().newRow();
            row.newCol().appendTag("div", panel);
            childCtx = ctx.createChild(row.newCol(), ctx.getModel());
            childCtx.init(this, viewMode);
        }

        // onBeforeBuild
        listeners.stream().forEach(it -> it.onBeforeBuild(ctx, mapper, viewMode));

        mapper.buildView(childCtx);

        // onAfterBuild
        listeners.stream().forEach(it -> it.onAfterBuild(ctx, mapper, viewMode));
    }

    private IWicketComponentMapper resolveMapper(SInstance instancia) {

        final UIComponentMapper customMapper = instancia.getType().getComponentMapper();

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
                .register(STypeList.class,        SViewListByForm.class,                PanelListMapper::new)
                .register(STypeList.class,        SViewListByMasterDetail.class,        ListMasterDetailMapper::new)
                .register(STypeList.class,        SViewBreadcrumb.class,                ListBreadcrumbMapper::new)
                .register(STypeDateTime.class,                                          DateTimeMapper::new)
                .register(STypeDateTime.class,    SViewDateTime.class,                  DateTimeMapper::new)
                .register(STypeTime.class,                                              TimeMapper::new)
                .register(STypeTelefoneNacional.class,                                  TelefoneNacionalMapper::new)
                .register(STypeHTML.class,                                              RichTextMapper::new)
                .register(STypeAttachmentList.class, SViewAttachmentList.class,         AttachmentListMapper::new)
                .register(STypeCNPJ.class,                                              CNPJMapper::new)
                .register(STypeCPF.class,                                               CPFMapper::new)
                .register(STypeHTML.class,            SViewByPortletRichText.class,     PortletRichTextMapper::new);
        //@formatter:on
    }
}
