package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
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
     * @return list of options from the expected {@link SInstance} type.
     */
    @Override
    public default SIList<? extends SInstance> listOptions(SInstance optionsInstance) {
        SType<?> tipo;
        if (optionsInstance instanceof SIList){
            tipo = ((SIList) optionsInstance).getTipoElementos();
        } else {
            tipo = optionsInstance.getType();
        }
        MListaBuilder<STypeComposite> lb = new MListaBuilder<>((STypeComposite)tipo);
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
    public void listOptions(SInstance instancia, MListaBuilder<STypeComposite> lb);


}
