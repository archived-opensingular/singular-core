package br.net.mirante.singular.form.builder.selection;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.internal.freemarker.FormFreemarkerUtil;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.util.transformer.Value.Content;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static br.net.mirante.singular.form.util.transformer.Value.hydrate;
import static java.lang.String.valueOf;
import static java.util.Optional.*;


public class SSelectionDisplayBuilder extends AbstractBuilder {

    public SSelectionDisplayBuilder(SType type) {
        super(type);
    }

    public SProviderBuilder selfDisplay() {
        return display(type);
    }

    public SProviderBuilder display(final SType display) {
        type.asAtrProvider().asAtrProvider().displayFunction(new IFunction<Content, String>() {
            @Override
            public String apply(Content content) {
                final SType elementsType;
                if (type instanceof STypeList) {
                    elementsType = ((STypeList) type).getElementsType();
                } else {
                    elementsType = type;
                }
                final SInstance ins = elementsType.newInstance();
                Value.hydrate(ins, content);
                if (ins instanceof SIComposite) {
                    return valueOf(ofNullable(((SIComposite) ins).getValue(display)).orElse(StringUtils.EMPTY));
                }
                return valueOf(ofNullable(ins.getValue()).orElse(StringUtils.EMPTY));
            }
        });
        addConverter();
        return new SProviderBuilder(super.type);
    }

    public SProviderBuilder display(String freemakerTemplate) {
        type.asAtrProvider().asAtrProvider().displayFunction(new IFunction<Content, String>() {
            @Override
            public String apply(Content content) {
                final SType elementsType;
                if (type instanceof STypeList) {
                    elementsType = ((STypeList) type).getElementsType();
                } else {
                    elementsType = type;
                }
                final SInstance dummy = elementsType.newInstance();
                Value.hydrate(dummy, content);
                hydrate(dummy, content);
                return FormFreemarkerUtil.merge(dummy, freemakerTemplate);
            }
        });
        addConverter();
        return new SProviderBuilder(super.type);
    }

    private void addConverter() {
        type.asAtrProvider().asAtrProvider().converter(new SInstanceConverter<Content, SIComposite>() {
            @Override
            public void fillInstance(SIComposite ins, Content obj) {
                hydrate(ins, obj);
            }

            @Override
            public Content toObject(SIComposite ins) {
                return Value.dehydrate(ins);
            }
        });
    }

}