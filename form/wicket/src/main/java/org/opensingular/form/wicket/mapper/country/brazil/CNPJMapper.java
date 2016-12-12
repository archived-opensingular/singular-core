package org.opensingular.form.wicket.mapper.country.brazil;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.StringMapper;

public class CNPJMapper extends StringMapper {

    @Override
    public String getReadOnlyFormattedText(WicketBuildContext ctx, IModel<? extends SInstance> model) {
        return formatCnpj(super.getReadOnlyFormattedText(ctx, model));
    }

    private static String formatCnpj(String cnpj) {
        if (cnpj != null) {
            final String safeCnpj = cnpj.replaceAll("[^\\d]", "");
            if (safeCnpj.length() == 14) {
                return String.format("%s.%s.%s/%s-%s", safeCnpj.substring(0, 2), safeCnpj.substring(2, 5),
                        safeCnpj.substring(5, 8), safeCnpj.substring(8, 12), safeCnpj.substring(12, 14));
            } else {
                return cnpj;
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

}