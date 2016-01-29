package br.net.mirante.singular.form;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.bamclient.portlet.FilterConfig;
import br.net.mirante.singular.bamclient.portlet.filter.AggregationPeriod;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;
import br.net.mirante.singular.form.mform.options.MFixedOptionsSimpleProvider;
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
        final PacoteBuilder builder = SDictionary.create().criarNovoPacote("FilterPackage");
        final STypeComposite<? extends SIComposite> filtro = builder.createTipoComposto("filter");
        appendFilters(filtro);
        return filtro;
    }

    private void appendFilters(STypeComposite root) {
        filterConfigs.forEach(fc -> {
            STypeSimple field = null;
            switch (fc.getFieldType()) {
                case BOOLEAN:
                    field = root.addCampoBoolean(fc.getIdentifier());
                    break;
                case INTEGER:
                    field = root.addCampoInteger(fc.getIdentifier());
                    break;
                case TEXT:
                    field = root.addCampoString(fc.getIdentifier());
                    break;
                case TEXTAREA:
                    field = root.addCampoString(fc.getIdentifier()).withTextAreaView();
                    break;
                case DATE:
                    field = root.addCampoData(fc.getIdentifier());
                    break;
                case SELECTION:
                    field = root.addCampoString(fc.getIdentifier());
                    final MFixedOptionsSimpleProvider selectionProvider = field.withSelection();
                    if (!StringUtils.isEmpty(fc.getRestEndpoint())) {
                        final String connectionURL = getGroupConnectionURL();
                        if (!StringUtils.isEmpty(connectionURL)) {
                            final String fullConnectionPoint = connectionURL + fc.getRestEndpoint();
                            switch (fc.getRestReturnType()) {
                                case VALUE:
                                    fillValueOptions(selectionProvider, fullConnectionPoint);
                                    break;
                                case KEY_VALUE:
                                    fillKeyValueOptions(selectionProvider, fullConnectionPoint);
                                    break;
                            }
                        }
                    } else if (fc.getOptions() != null && fc.getOptions().length > 0) {
                        Arrays.asList(fc.getOptions()).forEach(selectionProvider::add);
                    }
                    break;
                case AGGREGATION_PERIOD:
                    field = root.addCampoString(fc.getIdentifier());
                    final MFixedOptionsSimpleProvider aggregationProvider = field.withSelection();
                    Arrays.asList(AggregationPeriod.values()).forEach(ap -> aggregationProvider.add(ap, ap.getDescription()));
                    break;
            }
            if (field != null) {
                field.asAtrBasic().label(fc.getLabel());
                field.asAtrBootstrap().colPreference(fc.getSize());
                field.asAtrCore().obrigatorio(fc.getRequired());
            }
        });
    }

    public void fillValueOptions(MFixedOptionsSimpleProvider provider, String endpoint) {
        final RestTemplate restTemplate = new RestTemplate();
        final List<String> list = restTemplate.getForObject(endpoint, List.class);
        if (list != null) {
            list.forEach(provider::add);
        }
    }

    public void fillKeyValueOptions(MFixedOptionsSimpleProvider provider, String endpoint) {
        final RestTemplate restTemplate = new RestTemplate();
        final Map<String, String> map = restTemplate.getForObject(endpoint, Map.class);
        if (map != null) {
            map.forEach(provider::add);
        }
    }

    private String getGroupConnectionURL() {
        return registry.lookupService(FlowMetadataFacade.class).retrieveGroupByProcess(processAbbreviation).getConnectionURL();
    }

}