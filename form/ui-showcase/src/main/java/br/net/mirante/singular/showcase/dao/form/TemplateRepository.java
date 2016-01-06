package br.net.mirante.singular.showcase.dao.form;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MDicionarioResolver;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ShowCaseTable;
import br.net.mirante.singular.showcase.component.ShowCaseTable.ShowCaseGroup;
import br.net.mirante.singular.showcase.component.ShowCaseTable.ShowCaseItem;
import br.net.mirante.singular.showcase.view.page.form.examples.ExamplePackage;
import br.net.mirante.singular.showcase.view.page.form.examples.MPacoteCurriculo;
import br.net.mirante.singular.showcase.view.page.form.examples.MPacotePeticaoGGTOX;

public class TemplateRepository extends MDicionarioResolver {

    private final Map<String, TemplateEntry> entries = new LinkedHashMap<>();

    static {
        TemplateRepository.setDefault(TemplateRepository.get());
    }

    public static TemplateRepository get() {
        return novoTemplate();
    }

    private static TemplateRepository novoTemplate() {
        TemplateRepository novo = new TemplateRepository();
        novo.add(MPacoteCurriculo.class, MPacoteCurriculo.TIPO_CURRICULO);
        novo.add(ExamplePackage.class, ExamplePackage.Types.ORDER.name);
        novo.add(MPacotePeticaoGGTOX.class, MPacotePeticaoGGTOX.NOME_COMPLETO);

        for (ShowCaseGroup group : new ShowCaseTable().getGroups()) {
            for (ShowCaseItem item : group.getItens()) {
                String itemName = group.getGroupName() + " - " + item.getComponentName();
                for (CaseBase c : item.getCases()) {
                    if (c.getSubCaseName() == null) {
                        novo.add(itemName, c.getCaseType());
                    } else {
                        novo.add(itemName + " - " + c.getSubCaseName(), c.getCaseType());
                    }
                }
            }
        }
//        MDicionarioResolver.setDefault(novo);
        return novo;
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
    
    public TemplateEntry findEntryByType(String type) {
        for(TemplateEntry t : entries.values()){
            if(t.getType().getNome().equals(type)){
                return t;
            }
        }
        return null;
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
