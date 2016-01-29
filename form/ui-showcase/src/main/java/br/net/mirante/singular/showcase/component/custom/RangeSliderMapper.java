package br.net.mirante.singular.showcase.component.custom;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSLabel;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

public class RangeSliderMapper implements IWicketComponentMapper {

    private final String valorInicialPath, valorFinalPath;

    public RangeSliderMapper(STypeInteger valorInicial, STypeInteger valorFinal) {
        this.valorInicialPath = valorInicial.getNomeSimples();
        this.valorFinalPath = valorFinal.getNomeSimples();
    }

    @Override
    public void buildView(WicketBuildContext ctx) {

        final BSControls formGroup = createFormGroup(ctx);
        final SIComposite rootInstance = ctx.getCurrenttInstance();

        final IModel<? extends SInstance> miInicial = resolveModel(rootInstance, valorInicialPath);
        final IModel<? extends SInstance> miFinal = resolveModel(rootInstance, valorFinalPath);

        final HiddenField valorInicial = new HiddenField<>("valorInicial", miInicial);
        final HiddenField valorFinal = new HiddenField<>("valorFinal", miFinal);

        final Boolean disable = ctx.getViewMode().isVisualization();

        final String initScript = String.format("RangeSliderMapper.init(%s,%s,%s,%s)", formGroup.getMarkupId(true),
                valorInicial.getMarkupId(true), valorFinal.getMarkupId(true), disable);

        formGroup.appendLabel(buildLabel(ctx.getModel()));
        formGroup.appendInputHidden(valorInicial);
        formGroup.appendInputHidden(valorFinal);
        formGroup.add(buildIonRangeScriptBehaviour(initScript));

    }

    private Behavior buildIonRangeScriptBehaviour(String initScript){
        return new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                PackageResourceReference prr = new PackageResourceReference(RangeSliderMapper.class, "RangeSliderMapper.js");
                response.render(JavaScriptHeaderItem.forReference(prr));
                response.render(OnDomReadyHeaderItem.forScript(initScript));
            }
        };
    }

    private BSLabel buildLabel(IModel<? extends SInstance> model) {
        final AtributoModel<String> labelModel = new AtributoModel<>(model, SPackageBasic.ATR_LABEL);
        return new BSLabel("label", labelModel);
    }

    private IModel<? extends SInstance> resolveModel(SIComposite mi, String path) {
        final SInstance SInstance = mi.getCampo(path);
        final MInstanceRootModel<?> rootModel = new MInstanceRootModel<>(SInstance);
        return new MInstanciaValorModel<>(rootModel);
    }

    private BSControls createFormGroup(WicketBuildContext ctx) {
        return ctx.getContainer().newFormGroup();
    }
}
