package br.net.mirante.singular.exemplos.notificacaosimplificada.common;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.Substancia;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.function.BiFunction;

public class STypeSubstanciaPopulator {

    private final STypeComposite<?>                               root;
    private final SType<?>                                        dependentType;
    private final STypeSimple                                     idConfiguracaoLinhaProducao;
    private final BiFunction<SInstance, String, List<Substancia>> substanciasSupplier;

    public STypeSubstanciaPopulator(
            STypeComposite<?> root,
            SType<?> dependentType,
            STypeSimple idConfiguracaoLinhaProducao,
            BiFunction<SInstance, String, List<Substancia>> substanciasSupplier) {
        this.root = root;
        this.dependentType = dependentType;
        this.idConfiguracaoLinhaProducao = idConfiguracaoLinhaProducao;
        this.substanciasSupplier = substanciasSupplier;
    }

    private DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public STypeList<STypeComposite<SIComposite>, SIComposite> populate() {

        final STypeList<STypeComposite<SIComposite>, SIComposite> substancias = root.addFieldListOfComposite("substancias", "concentracaoSubstancia");

        substancias
                .withMiniumSizeOf(1)
                .withView(SViewListByTable::new)
                .asAtrBasic()
                .label("Substâncias")
                .dependsOn(dependentType)
                .visible(i -> Value.notNull(i, idConfiguracaoLinhaProducao));

        final STypeComposite<?> concentracaoSubstancia                = substancias.getElementsType();
        final STypeComposite<?> substancia                            = concentracaoSubstancia.addFieldComposite("substancia");
        final STypeSimple       idSubstancia                          = substancia.addFieldInteger("id");
        final STypeSimple       idConfiguracaoLinhaProducaoSubstancia = substancia.addFieldInteger("configuracaoLinhaProducao");
        final STypeSimple       substanciaDescricao                   = substancia.addFieldString("descricao");

        substancia
                .asAtrBasic()
                .label("Substância")
                .required()
                .asAtrBootstrap()
                .colPreference(6);
        substancia
                .withView( () -> new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
        substancia
                .withSelectionFromProvider(substanciaDescricao, (SOptionsProvider) (i, f) -> {
                    final SIList<?> list = i.getType().newList();
                    for (Substancia s : substanciasSupplier.apply(i, f)) {
                        final SIComposite c = (SIComposite) list.addNew();
                        c.setValue(idSubstancia, s.getId());
                        c.setValue(idConfiguracaoLinhaProducaoSubstancia, Value.of(i, idConfiguracaoLinhaProducao));
                        c.setValue(substanciaDescricao, s.getDescricao());
                    }
                    return list;
                });

        final STypeComposite<?> concentracao             = concentracaoSubstancia.addFieldComposite("concentracao");
        final SType<?>          idConcentracacao         = concentracao.addFieldInteger("id");
        final STypeSimple       idSubstanciaConcentracao = concentracao.addFieldInteger("idSubstancia");
        final STypeSimple       descConcentracao         = concentracao.addFieldString("descricao");
        concentracao
                .asAtrBasic()
                .required()
                .label("Concentração")
                .dependsOn(substancia)
                .asAtrBootstrap()
                .colPreference(6);
        concentracao
                .withSelectView()
                .withSelectionFromProvider(substanciaDescricao, (optionsInstance, lb) -> {
                    Integer id = (Integer) Value.of(optionsInstance, idSubstancia);
                    for (Triple p : dominioService(optionsInstance).concentracoes(id)) {
                        lb
                                .add()
                                .set(idConcentracacao, p.getLeft())
                                .set(idSubstanciaConcentracao, p.getMiddle())
                                .set(descConcentracao, p.getRight());
                    }
                });
        return substancias;
    }
}
