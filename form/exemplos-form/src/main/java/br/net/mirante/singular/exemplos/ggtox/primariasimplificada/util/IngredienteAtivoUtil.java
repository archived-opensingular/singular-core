package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.util;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.STypeAtivoAmostra;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.STypeIngredienteAtivo;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SInstances;
import br.net.mirante.singular.form.STypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utilitário para computar informações referentes aos ingredientes ativos e aos ingredientes ativos
 * referenciados na seção de amostras do estudo de resíduos.
 *
 * Encontra os tipos e as instancias de maneira global sem precisar de referencias.
 */
public class IngredienteAtivoUtil {


    public static IngedienteAtivoTypes collectData(SInstance instance) {
        SIComposite rootInstannce = SInstances.getRootInstance(instance);

        final List<SIComposite> ativosAmostra = new ArrayList<>();
        final List<SIComposite> ativos = new ArrayList<>();
        final Map<Class<? extends STypeIngredienteAtivo>, STypeIngredienteAtivo> mapInstanceClass = new HashMap<>();

        STypes.visitAll(rootInstannce.getType(), true, type -> {
            if (type instanceof STypeIngredienteAtivo && ativos.isEmpty()) {
                ativos.addAll((List<SIComposite>) rootInstannce.listDescendants(type));
                mapInstanceClass.put(STypeIngredienteAtivo.class, (STypeIngredienteAtivo) type);
            }
            if (type instanceof STypeAtivoAmostra && ativosAmostra.isEmpty()) {
                ativosAmostra.addAll((List<SIComposite>) rootInstannce.listDescendants(type));
                mapInstanceClass.put(STypeAtivoAmostra.class, (STypeAtivoAmostra) type);
            }
        });

        STypeIngredienteAtivo ingredienteAtivoType = mapInstanceClass.get(STypeIngredienteAtivo.class);
        STypeAtivoAmostra ativoAmostraType = (STypeAtivoAmostra) mapInstanceClass.get(STypeAtivoAmostra.class);

        Map<String, SIComposite> ativosMap = ativos
                .parallelStream()
                .collect(
                        Collectors.toMap(a -> (String) a.getField(ingredienteAtivoType.idAtivo).getValue(), a -> a));

        return new IngedienteAtivoTypes(ingredienteAtivoType, ativoAmostraType, ativos, ativosAmostra, ativosMap);
    }


    public static class IngedienteAtivoTypes {
        private STypeIngredienteAtivo ingredienteAtivoType;
        private STypeAtivoAmostra ativoAmostraType;
        private List<SIComposite> ingredientesAtivos;
        private List<SIComposite> ativosAmostras;
        private Map<String, SIComposite> ingredientesAtivosMap;

        public IngedienteAtivoTypes(STypeIngredienteAtivo ingredienteAtivoType, STypeAtivoAmostra ativoAmostraType, List<SIComposite> ingredientesAtivos, List<SIComposite> ativosAmostras, Map<String, SIComposite> ativosMap) {
            this.ingredienteAtivoType = ingredienteAtivoType;
            this.ativoAmostraType = ativoAmostraType;
            this.ingredientesAtivos = ingredientesAtivos;
            this.ativosAmostras = ativosAmostras;
            this.ingredientesAtivosMap = ativosMap;
        }

        public STypeIngredienteAtivo getIngredienteAtivoType() {
            return ingredienteAtivoType;
        }

        public STypeAtivoAmostra getAtivoAmostraType() {
            return ativoAmostraType;
        }

        public List<SIComposite> getIngredientesAtivos() {
            return ingredientesAtivos;
        }

        public List<SIComposite> getAtivosAmostras() {
            return ativosAmostras;
        }

        public Map<String, SIComposite> getIngredientesAtivosMap() {
            return ingredientesAtivosMap;
        }
    }
}
