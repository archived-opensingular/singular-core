package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.provider.Provider;
import br.net.mirante.singular.form.provider.ProviderContext;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IInstanceValidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeListaAtivosEstudo extends STypeList<STypeIngredienteAtivo, SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .setElementsType(STypeIngredienteAtivo.class);

        this
                .selectionOf(AtivoSelect.class)
                .id(AtivoSelect::getId)
                .display(AtivoSelect::getDescricao)
                .converter(new SInstanceConverter<AtivoSelect, SIComposite>() {

                    @Override
                    public void fillInstance(SIComposite ins, AtivoSelect obj) {
                        SIList<SIComposite> ativos = findListAtivosPeticao(ins);
                        for (SIComposite si : ativos) {
                            if (si.getId().equals(obj.getId())) {
                                Value.hydrate(ins, Value.dehydrate(si));
                            }
                        }
                    }

                    @Override
                    public AtivoSelect toObject(SIComposite ins) {
                        return AtivoSelect.fromInstance(ins);
                    }
                });

        this
                .asAtrProvider()
                .provider(new AtivosProvider());

//        this
//                .addInstanceValidator(validatable -> {
//                    SIList<SIComposite> list = validatable.getInstance();
//
//                    for (Iterator<SIComposite> it = list.iterator(); it.hasNext(); ){
//                        SIComposite ativoEstudo = it.next();
//                        boolean match = false;
//                        Value.Content ae = Value.dehydrate(ativoEstudo);
//                        for (SIComposite ativo :  findListAtivosPeticao(validatable.getInstance())){
//                            Value.Content a = Value.dehydrate(ativo);
//                            if (a.equals(ae)){
//                                match = true;
//                            }
//                        }
//                        if (!match){
//                            validatable.error("Ativo referenciado no estudo não foi cadastado na seção de ativos: "+ ativoEstudo.getField(STypeIngredienteAtivo.FIELD_NAME_NOME_COMUM_PTBR).getValue());
//                            break;
//                        }
//                    }
//                });

    }

    private static final SIList<SIComposite> findListAtivosPeticao(SInstance instance) {
        SIComposite ativo = findAtivoPeticao(instance);
        SIList<SIComposite> ativos = (SIList<SIComposite>) ativo.getField(STypeIngredienteAtivoPeticaoPrimariaSimplificada.FIELD_NAME_LIST_ATIVOS);
        return ativos;
    }

    private static SIComposite findAtivoPeticao(SInstance instance){
        SInstance root = null;
        root = instance;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return findNearestAncestorTypeClass(root, STypeIngredienteAtivoPeticaoPrimariaSimplificada.class);
    }

    private static <T extends SInstance> T findNearestAncestorTypeClass(SInstance rootInstance, Class<? extends SType> clazz) {
        if (clazz.isAssignableFrom(rootInstance.getType().getClass())) {
            return (T) rootInstance;
        }
        if (rootInstance instanceof SIComposite) {
            for (SInstance instance : ((SIComposite) rootInstance).getChildren()) {
                T result = findNearestAncestorTypeClass(instance, clazz);
                if (result != null) {
                    return result;
                }
            }
        } else if (rootInstance instanceof SIList) {
            for (Object o : ((SIList) rootInstance)) {
                if (o instanceof SInstance) {
                    T result = findNearestAncestorTypeClass((SInstance) o, clazz);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    public static class AtivoSelect implements Serializable {
        private Integer id;
        private String descricao;

        public AtivoSelect(Integer id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }

        public static AtivoSelect fromInstance(SIComposite ins) {
            return new AtivoSelect(ins.getId(), (String) ins.getField(STypeIngredienteAtivo.FIELD_NAME_NOME_COMUM_PTBR).getValue());
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    class AtivosProvider implements Provider<AtivoSelect, SIComposite> {

        @Override
        public List<AtivoSelect> load(ProviderContext<SIComposite> context) {

            SIList<SIComposite> ativos = findListAtivosPeticao(context.getInstance());

            List<AtivoSelect> list = new ArrayList<>();
            for (SIComposite ins : ativos) {
                list.add(AtivoSelect.fromInstance(ins));
            }

            return list;
        }


    }
}
