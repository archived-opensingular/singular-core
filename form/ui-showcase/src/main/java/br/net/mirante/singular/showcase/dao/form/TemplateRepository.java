package br.net.mirante.singular.showcase.dao.form;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SDictionaryLoader;
import br.net.mirante.singular.form.mform.SDictionaryRefByLoader;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ShowCaseTable;
import br.net.mirante.singular.showcase.component.ShowCaseTable.ShowCaseGroup;
import br.net.mirante.singular.showcase.component.ShowCaseTable.ShowCaseItem;
import br.net.mirante.singular.showcase.view.page.form.examples.ExamplePackage;
import br.net.mirante.singular.showcase.view.page.form.examples.SPackageCurriculo;
import br.net.mirante.singular.showcase.view.page.form.examples.SPackagePeticaoGGTOX;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.SPackagePeticaoCanabidiol;

public class TemplateRepository extends SDictionaryLoader<String> {

    private final Map<String, TemplateEntry> entries = new LinkedHashMap<>();

    public static TemplateRepository create() {
        return novoTemplate();
    }

    private static TemplateRepository novoTemplate() {
        TemplateRepository novo = new TemplateRepository();
        novo.add(SPackageCurriculo.class, SPackageCurriculo.TIPO_CURRICULO);
        novo.add(ExamplePackage.class, ExamplePackage.Types.ORDER.name);
        novo.add(SPackagePeticaoGGTOX.class, SPackagePeticaoGGTOX.NOME_COMPLETO);
        novo.add(SPackagePeticaoCanabidiol.class, SPackagePeticaoCanabidiol.NOME_COMPLETO);

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
        return novo;
    }

    private void add(Class<? extends SPackage> packageClass, String typeName) {
        SDictionary d = SDictionary.create();
        d.carregarPacote(packageClass);
        add(d.getTipo(typeName));
    }

    public void add(SType<?> type) {
        add(type.getNomeSimples(), type);
    }

    public void add(String displayName, SType<?> type) {
        ShowcaseDicionaryRef ref = new ShowcaseDicionaryRef(type.getNome());
        type.getDicionario().setSerializableDictionarySelfReference(ref);

        entries.put(type.getNome(), new TemplateEntry(displayName, type));
    }

    @Override
    public Optional<SDictionary> loadDictionaryImpl(String typeName) {
        return Optional.ofNullable(entries.get(typeName)).map(e -> e.getDictionary());
    }

    public Collection<TemplateEntry> getEntries() {
        return entries.values();
    }

    private TemplateEntry get(String name) {
        return entries.get(name);
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
        private final SType<?> type;

        public TemplateEntry(String displayName, SType<?> type) {
            this.displayName = displayName;
            this.type = type;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SType<?> getType() {
            return type;
        }

        public SDictionary getDictionary() {
            return type.getDicionario();
        }
    }

    final static class ShowcaseDicionaryRef extends SDictionaryRefByLoader<String> {

        ShowcaseDicionaryRef(String typeName) {
            super(typeName);
        }

        @Override
        public SDictionaryLoader<String> getDictionaryLoader() {
            return TemplateRepository.create();
        }
    }
}
