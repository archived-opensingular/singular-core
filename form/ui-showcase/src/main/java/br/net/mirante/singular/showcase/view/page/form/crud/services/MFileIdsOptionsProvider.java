package br.net.mirante.singular.showcase.view.page.form.crud.services;

import javax.inject.Inject;

import java.util.List;

import org.springframework.stereotype.Component;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.showcase.dao.form.ExampleFile;
import br.net.mirante.singular.showcase.dao.form.FileDao;

@SuppressWarnings("serial")
@Component("filesChoiceProvider")
public class MFileIdsOptionsProvider implements MOptionsProvider {
    @Inject
    private FileDao filePersistence;

    /**
     * Returns a MILista of the type of the field it will be used.
     *
     * @param optionsInstance : Current instance of the selection.
     */
    @Override
    // @destacar:bloco
    public MILista<? extends MInstancia> listOptions(MInstancia optionsInstance) {
        if (optionsInstance instanceof MILista) {
            MILista<?> list = ((MILista) optionsInstance).getTipoElementos().novaLista();
            files().forEach(f -> list.addValor(f.getId()));
            return list;
        }
        return null;
    }
    // @destacar:fim

    private List<ExampleFile> files() {
        return filePersistence.list();
    }
}