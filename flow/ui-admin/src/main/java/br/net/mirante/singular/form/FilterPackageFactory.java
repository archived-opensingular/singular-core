package br.net.mirante.singular.form;

import java.util.List;

import br.net.mirante.singular.bamclient.portlet.FilterConfig;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;
import br.net.mirante.singular.service.FlowMetadataFacade;

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
        final PackageBuilder builder = SDictionary.create().createNewPackage("FilterPackage");
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