/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.form;

import br.net.mirante.singular.bamclient.portlet.FilterConfig;
import br.net.mirante.singular.bamclient.portlet.filter.FieldType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;

import java.util.Arrays;
import java.util.Optional;

public enum FilterFieldType {

    BOOLEAN(FieldType.BOOLEAN) {
        @Override
        protected STypeSimple addFieldImpl(String groupConnectionURL, FilterConfig fc,
                                           STypeComposite root) {
            return root.addFieldBoolean(fc.getIdentifier());
        }
    },

    INTEGER(FieldType.INTEGER) {
        @Override
        protected STypeSimple addFieldImpl(String groupConnectionURL, FilterConfig fc,
                                           STypeComposite root) {
            return root.addFieldInteger(fc.getIdentifier());
        }
    },

    TEXT(FieldType.TEXT) {
        @Override
        protected STypeSimple addFieldImpl(String groupConnectionURL, FilterConfig fc,
                                           STypeComposite root) {
            return root.addFieldString(fc.getIdentifier());
        }
    },

    TEXTAREA(FieldType.TEXTAREA) {
        @Override
        protected STypeSimple addFieldImpl(String groupConnectionURL, FilterConfig fc,
                                           STypeComposite root) {
            return root.addFieldString(fc.getIdentifier()).withTextAreaView();
        }
    },

    SELECTION(FieldType.SELECTION) {
        @Override
        protected STypeSimple addFieldImpl(String groupConnectionURL, FilterConfig fc,
                                           STypeComposite root) {
//            final STypeSimple simpleType = root.addFieldString(fc.getIdentifier());
//            if (!isEmpty(fc.getRestEndpoint()) && !isEmpty(groupConnectionURL)) {
//                final String connectionURL = groupConnectionURL + fc.getRestEndpoint();
//                switch (fc.getRestReturnType()) {
//                    case VALUE:
//                        fillValueOptions(selectionProvider, connectionURL);
//                        break;
//                    case KEY_VALUE:
//                        fillKeyValueOptions(selectionProvider, connectionURL);
//                        break;
//                }
//            } else if (fc.getOptions() != null && fc.getOptions().length > 0) {
//                Arrays.asList(fc.getOptions()).forEach(selectionProvider::add);
//            }
//            return simpleType;
            return null;
        }

//        private void fillValueOptions(SFixedOptionsSimpleProvider provider, String endpoint) {
//            final RestTemplate restTemplate = new RestTemplate();
//            final List<String> list = restTemplate.getForObject(endpoint, List.class);
//            if (list != null) {
//                list.forEach(provider::add);
//            }
//        }
//
//        private void fillKeyValueOptions(SFixedOptionsSimpleProvider provider, String endpoint) {
//            final RestTemplate restTemplate = new RestTemplate();
//            final Map<String, String> map = restTemplate.getForObject(endpoint, Map.class);
//            if (map != null) {
//                map.forEach(provider::add);
//            }
//        }

    },

    DATE(FieldType.DATE) {
        @Override
        protected STypeSimple addFieldImpl(String groupConnectionURL, FilterConfig fc,
                                           STypeComposite root) {
            return root.addFieldDate(fc.getIdentifier());
        }
    },

    AGGREGATION_PERIOD(FieldType.AGGREGATION_PERIOD) {
        @Override
        protected STypeSimple addFieldImpl(String groupConnectionURL, FilterConfig fc,
                                           STypeComposite root) {
            final STypeSimple typeSimple = root.addFieldString(fc.getIdentifier());
//            final SFixedOptionsSimpleProvider provider = typeSimple.withSelection();
//            Arrays.asList(AggregationPeriod.values()).forEach(ap -> provider.add(ap, ap.getDescription()));
            return typeSimple;
        }
    };

    private FieldType fieldType;

    FilterFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public void addField(String groupConnectionURL, FilterConfig fc, STypeComposite root) {
        final STypeSimple sType = addFieldImpl(groupConnectionURL, fc, root);
        if (sType != null) {
            sType.asAtrBasic().label(fc.getLabel());
            sType.asAtrBootstrap().colPreference(fc.getSize());
            sType.asAtrBasic().required(fc.getRequired());
        }
    }

    protected abstract STypeSimple addFieldImpl(String groupConnectionURL, FilterConfig fc,
                                                STypeComposite root);

    public static Optional<FilterFieldType> valueOfFieldType(FieldType fieldType) {
        return Arrays.asList(values()).stream().filter(f -> f.fieldType == fieldType).findFirst();
    }

}