package br.net.mirante.singular.exemplos.notificacaosimplificada.common;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.Substancia;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.exemplos.util.TripleConverter;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewListByTable;
import br.net.mirante.singular.form.view.SViewReadOnly;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.function.Function;

public class STypeSubstanciaPopulator {

    private final STypeComposite<?>                     root;
    private final SType<?>                              dependentType;
    private final STypeSimple                           idConfiguracaoLinhaProducao;
    private final Function<SInstance, List<Substancia>> substanciasSupplier;

    public STypeSubstanciaPopulator(
            STypeComposite<?> root,
            SType<?> dependentType,
            STypeSimple idConfiguracaoLinhaProducao,
            Function<SInstance, List<Substancia>> substanciasSupplier) {
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

        final STypeComposite<?> concentracaoSubstancia = substancias.getElementsType();
        final STypeComposite<?> substancia             = concentracaoSubstancia.addFieldComposite("substancia");

        final STypeSimple idSubstancia                          = substancia.addFieldInteger("id");
        final STypeSimple idConfiguracaoLinhaProducaoSubstancia = substancia.addFieldInteger("configuracaoLinhaProducao");
        final STypeSimple substanciaDescricao                   = substancia.addFieldString("descricao");

        {
            substancias
                    .withView(() -> new SViewListByTable().disableNew().disableDelete())
                    .withUpdateListener(list -> {
                        for (Substancia s : substanciasSupplier.apply(list)) {
                            final SIComposite cs = list.addNew();
                            final SIComposite si = (SIComposite) cs.getField(substancia.getNameSimple());
                            si.setValue(idSubstancia, s.getId());
                            si.setValue(idConfiguracaoLinhaProducaoSubstancia, Value.of(list, idConfiguracaoLinhaProducao));
                            si.setValue(substanciaDescricao, s.getDescricao());
                        }
                    })
                    .asAtr()
                    .label("Substância")
                    .dependsOn(dependentType)
                    .visible(i -> Value.notNull(i, idConfiguracaoLinhaProducao));
        }

        final String substanciaSimpleName = substanciaDescricao.getNameSimple();
        {
            substancia
                    .withView(SViewReadOnly::new)
                    .asAtr()
                    .displayString("${descricao}")
                    .label("Nome")
                    .asAtrBootstrap()
                    .colPreference(6);
        }

        final STypeComposite<SIComposite> concentracao             = concentracaoSubstancia.addFieldComposite("concentracao");
        final SType<?>          idConcentracacao         = concentracao.addFieldInteger("id");
        final STypeSimple       idSubstanciaConcentracao = concentracao.addFieldInteger("idSubstancia");
        final STypeSimple       descConcentracao         = concentracao.addFieldString("descricao");
        {
            concentracao
                    .asAtr()
                    .required()
                    .label("Concentração")
                    .asAtrBootstrap()
                    .colPreference(6);

            concentracao.selectionOf(Triple.class)
                    .id(t -> String.valueOf(t.getLeft()))
                    .display("${right}")
                    .converter(new TripleConverter(idConcentracacao, idSubstanciaConcentracao, descConcentracao))
                    .simpleProvider((ins) -> {
                        Integer id = (Integer) Value.of(ins, idSubstancia);
                        return dominioService(ins).concentracoes(id);
                    });
        }

        return substancias;
    }
}
