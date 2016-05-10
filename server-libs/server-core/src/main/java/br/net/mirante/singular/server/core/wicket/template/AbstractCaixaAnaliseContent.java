package br.net.mirante.singular.server.core.wicket.template;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import br.net.mirante.singular.commons.util.Loggable;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.server.commons.config.ServerContext;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.service.AnalisePeticaoService;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.service.dto.ProcessDTO;
import br.net.mirante.singular.server.commons.util.Parameters;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import br.net.mirante.singular.server.commons.ws.ServiceFactoryUtil;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.IBSAction;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.datatable.column.MetronicStatusColumn;
import br.net.mirante.singular.util.wicket.metronic.menu.DropdownMenu;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import br.net.mirante.singular.util.wicket.resource.Icone;

public abstract class AbstractCaixaAnaliseContent<T extends TaskInstanceDTO> extends Content implements Loggable {


    private static final long serialVersionUID = 1767745739019654615L;

    public static final String ID_QUICK_FILTER = "filtroRapido";
    public static final String ID_FORM         = "form";

    protected static final Integer DEFAULT_ROWS_PER_PAGE = 10;

    protected final TextField<String> filtroRapido    = new TextField<>(ID_QUICK_FILTER, new Model<>());

    protected final AjaxButton        pesquisarButton = new AjaxButton("pesquisar") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onSubmit(target, form);
            onFiltroRapido(filtroRapido.getModel(), target);
        }
    };

    protected BSDataTable<T, String> listTable;

    /**
     * Botões globais
     */
    protected RepeatingView botoes = new RepeatingView("_botoes");

    protected DropdownMenu dropdownMenu = new DropdownMenu("_novos");

    private List<ProcessDTO> processes;

    @Inject
    protected AnalisePeticaoService<T> analisePeticaoService;

    @Inject
    protected PetitionService petitionService;

    @Inject
    protected ServiceFactoryUtil serviceFactoryUtil;

    protected abstract BSDataTable<T, String> setupDataTable();

    protected abstract Class<? extends Page> getHistoricoPage();

    public AbstractCaixaAnaliseContent(String id) {
        super(id, false, false);
    }

    public AbstractCaixaAnaliseContent(String id, boolean withInfoLink, boolean withBreadcrumb) {
        super(id, withInfoLink, withBreadcrumb);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(AbstractCaixaAnaliseContent.class, "AbstractCaixaAnaliseContent.js")));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Form(ID_FORM) {
            @Override
            protected void onSubmit() {
                super.onSubmit();
            }
        });
        queue(filtroRapido, pesquisarButton, listTable = setupDataTable(), botoes, dropdownMenu);
    }

    protected void onFiltroRapido(IModel<String> model, AjaxRequestTarget target) {
        target.add(listTable);
    }

    protected String getBaseUrl(String processGroupContext) {
        return  processGroupContext+ ServerContext.WORKLIST.getUrlPath();
    }

    protected BSActionColumn buildActionColumn() {
        final BSActionColumn<T, String> actionColumn = new BSActionColumn<>(getMessage("label.table.column.actions"));
        appendAtribuirAction(actionColumn);
        appendAnalisarAction(actionColumn);

        actionColumn
                .appendStaticAction(getMessage("label.table.column.view"),
                        Icone.EYE, this::criarLinkVisualizar)
                .appendAction(getMessage("label.table.column.history"),
                        Icone.HISTORY, this::criarLinkHistorico);

        return actionColumn;
    }

    protected void appendAnalisarAction(BSActionColumn<T, String> actionColumn) {
        actionColumn
                .appendStaticAction(getMessage("label.table.column.analysis"),
                        Icone.PENCIL, this::criarLinkAnalise);
    }

    protected void appendAtribuirAction(BSActionColumn<T, String> actionColumn) {
        actionColumn
                .appendAction(getMessage("label.botao.atribuir.mim"),
                        Icone.ARROW_DOWN, new IBSAction<T>() {
                            @Override
                            public boolean isVisible(IModel<T> model) {
                                return model.getObject().getTaskType().isPeople()
                                        && isAlocadoParaUsuarioLogado(model.getObject())
                                        && model.getObject().isPossuiPermissao();
                            }

                            @Override
                            public void execute(AjaxRequestTarget target, IModel<T> model) {
                                AbstractCaixaAnaliseContent.this.atribuir(target, model);
                            }
                        });
    }

    protected boolean isAlocadoParaUsuarioLogado(T peticao) {
        return !SingularSession.get().getUsername().equals(
                Optional.ofNullable(peticao)
                        .map(T::getUsuarioAlocado)
                        .map(MUser::getCodUsuario)
                        .orElse(null));
    }

    protected WebMarkupContainer criarLinkVisualizar(T peticao, String id) {
        return criarLink(peticao, id, FormActions.FORM_ANALYSIS_VIEW);
    }

    protected WebMarkupContainer criarLinkAnalise(T peticao, String id) {
        WebMarkupContainer link = criarLink(peticao, id, FormActions.FORM_ANALYSIS);
        link.add($b.visibleIf((IReadOnlyModel<Boolean>) () -> !isAlocadoParaUsuarioLogado(peticao) && peticao.isPossuiPermissao()));
        return link;
    }

    protected WebMarkupContainer criarLink(final T peticao, final String id, FormActions formActions) {
        String href = DispatcherPageUtil
                .baseURL(getBaseUrl(peticao.getProcessGroupContext()) + DispatcherPageUtil.DISPATCHER_PAGE_PATH)
                .formAction(formActions.getId())
                .formId(peticao.getCodPeticao())
                .param(Parameters.SIGLA_FORM_NAME, peticao.getType())
                .build();
        WebMarkupContainer link = new WebMarkupContainer(id);
        link.add($b.attr("href", href));
        link.add($b.attr("target", "_tab" + peticao.getCodPeticao()));
        return link;
    }

    protected void atribuir(AjaxRequestTarget target, IModel<T> model) {
        // TODO parametrizar qual o flow
        try{
            T taskInstanceDTO = model.getObject();
            serviceFactoryUtil.getSingularWS(taskInstanceDTO.getProcessGroupContext()).relocateTask(
                    taskInstanceDTO.getProcessType(),
                    Long.valueOf(taskInstanceDTO.getProcessInstanceId()),
                    SingularSession.get().getUsername(),
                    taskInstanceDTO.getVersionStamp());
            addToastrSuccessMessage("message.allocate.success");
        }catch (Exception e){
            addToastrErrorMessage("global.analise.atribuir.msg.error");
            getLogger().error(e.getMessage(), e);
        }
        target.add(listTable);
    }

    protected void criarLinkHistorico(AjaxRequestTarget target, IModel<T> model) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.add(Parameters.INSTANCE_ID, model.getObject().getProcessInstanceId());
        setResponsePage(getHistoricoPage(), pageParameters);
    }

    protected MetronicStatusColumn.BagdeType badgeMapper(IModel<Object> cellModel, IModel<T> rowModel) {
        if (rowModel.getObject().getTaskName().toLowerCase().contains("gerente")) {
            return MetronicStatusColumn.BagdeType.NONE;
        } else if (rowModel.getObject().getTaskName().toLowerCase().contains("aguardando")) {
            return MetronicStatusColumn.BagdeType.DANGER;
        } else if (rowModel.getObject().getTaskName().toLowerCase().contains("exigência")) {
            return MetronicStatusColumn.BagdeType.WARNING;
        } else {
            return MetronicStatusColumn.BagdeType.INFO;
        }
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("Petições");
    }

    protected Integer getRowsperPage() {
        return DEFAULT_ROWS_PER_PAGE;
    }

    public List<ProcessDTO> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessDTO> processes) {
        this.processes = processes;
    }
}