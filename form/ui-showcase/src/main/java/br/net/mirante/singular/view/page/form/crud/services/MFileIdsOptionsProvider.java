package br.net.mirante.singular.view.page.form.crud.services;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import br.net.mirante.singular.dao.form.ExampleFile;
import br.net.mirante.singular.dao.form.FileDao;
import br.net.mirante.singular.dao.form.TemplateRepository;
import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;

@Component("filesChoiceProvider")
public class MFileIdsOptionsProvider implements MOptionsProvider {
    @Inject private FileDao filePersistence;

    @Override
    public String toDebug() {
        return null;
    }

    @Override
    public MILista<? extends MInstancia> listOptions(MInstancia optionsInstance) {
        List<ExampleFile> files = filePersistence.list();
        TemplateRepository repo = TemplateRepository.get();
        MTipo<?> type = repo.getEntries().iterator().next().getType();
        Optional<MDicionario> dict = repo.loadDicionaryForType(type.getNome());
        MTipoString tipoString = dict.get().getTipo(MTipoString.class);
        MILista<?> list = tipoString.novaLista();
        files.forEach((f) -> list.addValor(f.getId()));
        return list;
    }
}