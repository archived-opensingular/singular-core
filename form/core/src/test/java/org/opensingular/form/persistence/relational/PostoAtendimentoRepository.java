package org.opensingular.form.persistence.relational;

import org.opensingular.form.SIComposite;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormPersistenceInRelationalDB;
import org.opensingular.form.persistence.RelationalDatabase;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class PostoAtendimentoRepository extends FormPersistenceInRelationalDB<RelationalMultilevelSQLTest.STypePostoAtendimento, SIComposite> {

    @Inject
    public PostoAtendimentoRepository(RelationalDatabase db) {
        super(db, SDocumentFactory.empty(), RelationalMultilevelSQLTest.STypePostoAtendimento.class);
    }

    @Nonnull
    @Override
    public List<SIComposite> loadAll() {
        return super.loadAll();
    }
}
