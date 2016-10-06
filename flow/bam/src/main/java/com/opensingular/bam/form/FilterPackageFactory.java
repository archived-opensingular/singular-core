/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.form;

import com.opensingular.bam.service.FlowMetadataFacade;
import com.opensingular.bam.client.portlet.FilterConfig;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.document.ServiceRegistry;

import java.util.List;

public class FilterPackageFactory {

    public static final String ROOT = "filter";

    private final List<FilterConfig> filterConfigs;
    private final ServiceRegistry registry;
    private final String processAbbreviation;

    public FilterPackageFactory(List<FilterConfig> filterConfigs, ServiceRegistry registry, String processAbbreviation) {
        this.filterConfigs = filterConfigs;
        this.registry = registry;
        this.processAbbreviation = processAbbreviation;
    }

    public SType<?> createFilterPackage() {
        final PackageBuilder                        builder = SDictionary.create().createNewPackage("FilterPackage");
        final STypeComposite<? extends SIComposite> filtro = builder.createCompositeType("filter");
        appendFilters(filtro);
        return filtro;
    }

    private void appendFilters(STypeComposite root) {
        final String groupConnectionURL;

        if (processAbbreviation != null) {
            final FlowMetadataFacade facade = registry.lookupService(FlowMetadataFacade.class);
            groupConnectionURL = facade.retrieveGroupByProcess(processAbbreviation).getConnectionURL();
        } else {
            groupConnectionURL = null;
        }

        filterConfigs.forEach(fc -> {
            FilterFieldType.valueOfFieldType(fc.getFieldType()).ifPresent(type -> {
                type.addField(groupConnectionURL, fc, root);
            });
        });
    }

}