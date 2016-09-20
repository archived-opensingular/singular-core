package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;


import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeHTML;
import br.net.mirante.singular.form.view.SViewByPortletRichText;

@SInfoType(name = "STypeNotificacaoExigencia", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypeNotificacaoExigencia extends STypePersistentComposite {

    public static final String NOTIFICACAO = "notificacao";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        final STypeHTML notificacao = addField(NOTIFICACAO, STypeHTML.class);
        notificacao.setView(SViewByPortletRichText::new);
        notificacao.asAtr().label("Notificação de Exigência").required();
    }

}