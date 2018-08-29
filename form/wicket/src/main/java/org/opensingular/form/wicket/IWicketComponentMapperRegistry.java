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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeAttachmentList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.aspect.AspectRef;
import org.opensingular.form.aspect.QualifierStrategyByClassQualifier;
import org.opensingular.form.aspect.SingleAspectRegistry;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeHTML;
import org.opensingular.form.type.core.STypeHiddenString;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypePassword;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.country.brazil.STypeCNPJ;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.type.country.brazil.STypeTelefoneNacional;
import org.opensingular.form.type.util.STypeLatitudeLongitudeGMaps;
import org.opensingular.form.type.util.STypeLatitudeLongitudeMultipleMarkable;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.form.view.*;
import org.opensingular.form.view.date.SViewDate;
import org.opensingular.form.view.date.SViewDateTime;
import org.opensingular.form.view.date.SViewTime;
import org.opensingular.form.view.richtext.SViewByRichText;
import org.opensingular.form.view.richtext.SViewByRichTextNewTab;
import org.opensingular.form.wicket.mapper.BooleanMapper;
import org.opensingular.form.wicket.mapper.DateMapper;
import org.opensingular.form.wicket.mapper.DateTimeMapper;
import org.opensingular.form.wicket.mapper.DecimalMapper;
import org.opensingular.form.wicket.mapper.InputHiddenMapper;
import org.opensingular.form.wicket.mapper.ListBreadcrumbMapper;
import org.opensingular.form.wicket.mapper.MoneyMapper;
import org.opensingular.form.wicket.mapper.NumberMapper;
import org.opensingular.form.wicket.mapper.PanelListMapper;
import org.opensingular.form.wicket.mapper.PasswordMapper;
import org.opensingular.form.wicket.mapper.ReadOnlyControlsFieldComponentMapper;
import org.opensingular.form.wicket.mapper.RichTextMapper;
import org.opensingular.form.wicket.mapper.StringMapper;
import org.opensingular.form.wicket.mapper.TabMapper;
import org.opensingular.form.wicket.mapper.TableListMapper;
import org.opensingular.form.wicket.mapper.TelefoneNacionalMapper;
import org.opensingular.form.wicket.mapper.TextAreaMapper;
import org.opensingular.form.wicket.mapper.TimeMapper;
import org.opensingular.form.wicket.mapper.YearMonthMapper;
import org.opensingular.form.wicket.mapper.attachment.image.AttachmentImageMapper;
import org.opensingular.form.wicket.mapper.attachment.list.AttachmentListMapper;
import org.opensingular.form.wicket.mapper.attachment.single.AttachmentMapper;
import org.opensingular.form.wicket.mapper.composite.BlocksCompositeMapper;
import org.opensingular.form.wicket.mapper.composite.DefaultCompositeMapper;
import org.opensingular.form.wicket.mapper.country.brazil.CNPJMapper;
import org.opensingular.form.wicket.mapper.country.brazil.CPFMapper;
import org.opensingular.form.wicket.mapper.maps.LatitudeLongitudeListMapper;
import org.opensingular.form.wicket.mapper.maps.LatitudeLongitudeMapper;
import org.opensingular.form.wicket.mapper.masterdetail.ListMasterDetailMapper;
import org.opensingular.form.wicket.mapper.richtext.PortletRichTextMapper;
import org.opensingular.form.wicket.mapper.search.SearchModalMapper;
import org.opensingular.form.wicket.mapper.selection.AutocompleteMapper;
import org.opensingular.form.wicket.mapper.selection.BooleanRadioMapper;
import org.opensingular.form.wicket.mapper.selection.BooleanSelectMapper;
import org.opensingular.form.wicket.mapper.selection.BooleanSwitchMapper;
import org.opensingular.form.wicket.mapper.selection.MultipleCheckMapper;
import org.opensingular.form.wicket.mapper.selection.MultipleSelectBSMapper;
import org.opensingular.form.wicket.mapper.selection.PicklistMapper;
import org.opensingular.form.wicket.mapper.selection.RadioMapper;
import org.opensingular.form.wicket.mapper.selection.SelectMapper;

/**
 * @author Daniel C. Bordin on 25/08/2017.
 */
public class IWicketComponentMapperRegistry
        extends SingleAspectRegistry<IWicketComponentMapper, Class<? extends SView>> {

    public IWicketComponentMapperRegistry(@Nonnull AspectRef<IWicketComponentMapper> aspectRef) {
        super(aspectRef, new QualifierStrategyByView());
        registerDefaultMapping();
    }

    public static class QualifierStrategyByView extends QualifierStrategyByClassQualifier<Class<? extends SView>> {
        @Nullable
        @Override
        protected Class<? extends SView> extractQualifier(@Nonnull SInstance instance) {
            SView view = ViewResolver.resolve(instance);
            return view == null ? null : view.getClass();
        }

        @Nullable
        @Override
        protected Class<? extends SView> extractQualifier(@Nonnull SType<?> type) {
            SView view = ViewResolver.resolveView(type);
            return view == null ? null : view.getClass();
        }
    }

    private void registerDefaultMapping() {
        add(STypeSimple.class,     SViewSelectionByRadio.class,           RadioMapper::new);
        add(STypeSimple.class,     SViewSelectionBySelect.class,          SelectMapper::new);
        add(STypeSimple.class,     SViewReadOnly.class,                   ReadOnlyControlsFieldComponentMapper::new);
        add(STypeBoolean.class,    SViewSelectionBySelect.class,          BooleanSelectMapper::new);
        add(STypeBoolean.class,                                           BooleanMapper::new);
        add(STypeBoolean.class,    SViewCheckBox.class,                   BooleanMapper::new);
        add(STypeBoolean.class,    SViewBooleanSwitch.class,              BooleanSwitchMapper::new);
        add(STypeBoolean.class,    SViewBooleanByRadio.class,             BooleanRadioMapper::new);
        add(STypeInteger.class,                                           () -> new NumberMapper<>(Integer.class));
        add(STypeLong.class,                                              () -> new NumberMapper<>(Long.class));
        add(STypeString.class,                                            StringMapper::new);
        add(STypeString.class,     SViewSearchModal.class,                SearchModalMapper::new);
        add(STypeString.class,     SViewTextArea.class,                   TextAreaMapper::new);
        add(STypeString.class,     SViewAutoComplete.class,               AutocompleteMapper::new);
        add(STypeDate.class,                                              DateMapper::new);
        add(STypeDate.class,       SViewDate.class,                       DateMapper::new);
        add(STypeYearMonth.class,                                         YearMonthMapper::new);
        add(STypeDecimal.class,                                           DecimalMapper::new);
        add(STypeMonetary.class,                                          MoneyMapper::new);
        add(STypeAttachment.class,                                        AttachmentMapper::new);
        add(STypeAttachment.class, SViewAttachment.class,                 AttachmentMapper::new);
        add(STypeAttachment.class, SViewAttachmentImage.class,            AttachmentImageMapper::new);
        add(STypeLatitudeLongitudeGMaps.class,                                 LatitudeLongitudeMapper::new);
        add(STypeLatitudeLongitudeGMaps.class, SViewCurrentLocation.class,     LatitudeLongitudeMapper::new);
        add(STypeLatitudeLongitudeMultipleMarkable.class,                                 LatitudeLongitudeListMapper::new);
        add(STypeComposite.class,                                         DefaultCompositeMapper::new);
        add(STypeComposite.class,   SViewTab.class,                       TabMapper::new);
        add(STypeComposite.class,   SViewByBlock.class,                   BlocksCompositeMapper::new);
        add(STypeComposite.class,   SViewSelectionByRadio.class,          RadioMapper::new);
        add(STypeComposite.class,   SViewSelectionBySelect.class,         SelectMapper::new);
        add(STypeComposite.class,   SViewSearchModal.class,               SearchModalMapper::new);
        add(STypeComposite.class,   SViewTree.class,                      SearchModalMapper::new);
        add(STypeComposite.class,   SViewAutoComplete.class,              AutocompleteMapper::new);
        add(STypeComposite.class,   SViewReadOnly.class,                  ReadOnlyControlsFieldComponentMapper::new);
        add(STypeList.class,        SMultiSelectionBySelectView.class,    MultipleSelectBSMapper::new);
        add(STypeList.class,        SMultiSelectionByCheckboxView.class,  MultipleCheckMapper::new);
        add(STypeList.class,        SMultiSelectionByPicklistView.class,  PicklistMapper::new);
        add(STypeList.class,                                              TableListMapper::new);
        add(STypeList.class,        SViewListByTable.class,               TableListMapper::new);
        add(STypeList.class,        SViewListByForm.class,                PanelListMapper::new);
        add(STypeList.class,        SViewListByMasterDetail.class,        ListMasterDetailMapper::new);
        add(STypeList.class,        SViewBreadcrumb.class,                ListBreadcrumbMapper::new);
        add(STypeDateTime.class,                                          DateTimeMapper::new);
        add(STypeDateTime.class,    SViewDateTime.class,                  DateTimeMapper::new);
        add(STypeTime.class,                                              TimeMapper::new);
        add(STypeTime.class,        SViewTime.class,                      TimeMapper::new);
        add(STypeTelefoneNacional.class,                                  TelefoneNacionalMapper::new);
        add(STypeHTML.class,                                              PortletRichTextMapper::new);
        add(STypeHTML.class,        SViewByRichTextNewTab.class,          PortletRichTextMapper::new);
        add(STypeHTML.class,        SViewByRichText.class,                RichTextMapper::new);
        add(STypeAttachmentList.class, SViewAttachmentList.class,         AttachmentListMapper::new);
        add(STypeCNPJ.class,                                              CNPJMapper::new);
        add(STypeCPF.class,                                               CPFMapper::new);
        add(STypePassword.class,        SViewPassword.class,              PasswordMapper::new);
        add(STypePassword.class,                                          PasswordMapper::new);
        add(STypeHiddenString.class,                                      InputHiddenMapper::new);
        //@formatter:on
    }
}
