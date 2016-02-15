package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.core.SPackageCore;

/**
 * This type encloses a MTipoLista of MTipoAnnotation and can be used in order to persist
 * a set of anotations without much hassle.
 *
 * @author Fabricio Buzeto
 */
@MInfoTipo(nome = STypeAnnotationList.NAME, pacote = SPackageCore.class)
public class STypeAnnotationList extends STypeLista {

    public static final String NAME = "AnnotationList";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        setTipoElementos(tb.getTipo().getDictionary().getType(STypeAnnotation.class));
    }
}
