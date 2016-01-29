package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.util.transformer.MListaBuilder;

/**
 * Interface funcional para prover lambda de montagem de MIlista de MTipoComposto
 * a partir de um ListBuilder
 */
@FunctionalInterface
public interface MOptionsCompositeProvider extends MOptionsProvider {


    /**
     * Returns the list of options for this selection.
     *
     * @param optionsInstance : Current isntance used to select the options.
     * @return list of options from the expected {@link SInstance2} type.
     */
    @Override
    public default SList<? extends SInstance2> listOptions(SInstance2 optionsInstance) {
        SType<?> tipo;
        if (optionsInstance instanceof SList){
            tipo = ((SList) optionsInstance).getTipoElementos();
        } else {
            tipo = optionsInstance.getMTipo();
        }
        MListaBuilder<STypeComposto> lb = new MListaBuilder<>((STypeComposto)tipo);
        listOptions(optionsInstance, lb);
        return lb.getList();
    }

    /**
     * Método para montar uma MLista a partir do MListaBuilder
     *
     * @param instancia
     * @param lb
     * @return
     */
    public void listOptions(SInstance2 instancia, MListaBuilder<STypeComposto> lb);


}
