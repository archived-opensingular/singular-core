package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.validators;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.util.IngredienteAtivoUtil;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IInstanceValidator;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AtivoAmostraValidator implements IInstanceValidator<SIComposite> {


    @Override
    public void validate(IInstanceValidatable<SIComposite> validatable) {
        IngredienteAtivoUtil.IngedienteAtivoTypes data = IngredienteAtivoUtil.collectData(validatable.getInstance());

        Set<String> idsAmostras = data.getAtivosAmostras()
                .parallelStream()
                .map(si -> si.getField(data.getAtivoAmostraType().idAtivo).getValue())
                .collect(Collectors.toSet());

        for (Map.Entry<String, SIComposite> ativoEntry : data.getIngredientesAtivosMap().entrySet()) {
            if (!idsAmostras.contains(ativoEntry.getKey())) {
                validatable.error(ValidationErrorLevel.WARNING,
                        String.format("O ingrediente ativo %s não está presente no estudo de resíduos",
                                ativoEntry
                                        .getValue()
                                        .getField(data.getIngredienteAtivoType().nomeComumPortugues).getValue()));
            }
        }

    }


}
