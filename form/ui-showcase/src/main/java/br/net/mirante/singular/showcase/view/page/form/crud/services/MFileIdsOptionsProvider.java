package br.net.mirante.singular.showcase.view.page.form.crud.services;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import br.net.mirante.singular.showcase.dao.form.ExampleFile;
import br.net.mirante.singular.showcase.dao.form.FileDao;
import br.net.mirante.singular.showcase.dao.form.TemplateRepository;
import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;

@SuppressWarnings("serial")
@Component("filesChoiceProvider")
public class MFileIdsOptionsProvider implements MOptionsProvider {
    @Inject private FileDao filePersistence;

    @Override
    public String toDebug() {
        return null;
    }

    /**
     * Returns a MILista of the type of the field it will be used.
     * @param optionsInstance : Current instance of the selection.
     */
    @Override
    public MILista<? extends MInstancia> listOptions(MInstancia optionsInstance) {
        MILista<?> list = newMIStringList(optionsInstance);
        files().forEach((f) -> list.addValor(f.getId()));
        return list;
    }

    private MILista<?> newMIStringList(MInstancia optionsInstance) {
        MTipoString tipoString = dictionary(optionsInstance).getTipo(MTipoString.class);
        return tipoString.novaLista();
    }

    private MDicionario dictionary(MInstancia optionsInstance) {
        MTipo<?> type = optionsInstance.getMTipo();
        return type.getDicionario();
    }

    private List<ExampleFile> files() {
        return filePersistence.list();
    }
}