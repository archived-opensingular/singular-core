package br.net.mirante.singular.dao.form;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MDicionarioResolver;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.showcase.CaseBase;
import br.net.mirante.singular.showcase.ShowCaseTable;
import br.net.mirante.singular.showcase.ShowCaseTable.ShowCaseGroup;
import br.net.mirante.singular.showcase.ShowCaseTable.ShowCaseItem;
import br.net.mirante.singular.view.page.form.examples.ExamplePackage;
import br.net.mirante.singular.view.page.form.examples.MPacoteCurriculo;

public class TemplateRepository extends MDicionarioResolver {

    private static final ShowCaseTable showCaseTable = new ShowCaseTable();
    private static final TemplateRepository templates = new TemplateRepository();

    private final Map<String, TemplateEntry> entries = new LinkedHashMap<>();

    static {
        templates.add(MPacoteCurriculo.class, MPacoteCurriculo.TIPO_CURRICULO);
        templates.add(ExamplePackage.class, ExamplePackage.Types.ORDER.name);

        for (ShowCaseGroup group : showCaseTable.getGroups()) {
            for (ShowCaseItem item : group.getItens()) {
                String itemName = group.getGroupName() + " - " + item.getComponentName();
                for (CaseBase c : item.getCases()) {
                    if (c.getSubCaseName() == null) {
                        templates.add(itemName, c.getCaseType());
                    } else {
                        templates.add(itemName + " - " + c.getSubCaseName(), c.getCaseType());
                    }
                }
            }
        }
    }

    public static TemplateRepository get() {
        return templates;
    }

    private void add(Class<? extends MPacote> packageClass, String typeName) {
        MDicionario d = MDicionario.create();
        d.carregarPacote(packageClass);
        add(d.getTipo(typeName));
    }

    public void add(MTipo<?> type) {
        add(type.getNomeSimples(), type);
    }

    public void add(String displayName, MTipo<?> type) {
        entries.put(type.getNome(), new TemplateEntry(displayName, type));
    }

    @Override
    public Optional<MDicionario> loadDicionaryForType(String typeName) {
        return Optional.ofNullable(entries.get(typeName)).map(e -> e.getType().getDicionario());
    }

    public Collection<TemplateEntry> getEntries() {
        return entries.values();
    }

    public static class TemplateEntry {

        private final String displayName;
        private final MTipo<?> type;

        public TemplateEntry(String displayName, MTipo<?> type) {
            this.displayName = displayName;
            this.type = type;
        }

        public String getDisplayName() {
            return displayName;
        }

        public MTipo<?> getType() {
            return type;
        }

    }
}
