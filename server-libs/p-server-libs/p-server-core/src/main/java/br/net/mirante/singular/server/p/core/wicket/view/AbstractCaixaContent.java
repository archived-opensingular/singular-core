package br.net.mirante.singular.server.p.core.wicket.view;

import static br.net.mirante.singular.server.commons.util.Parameters.SIGLA_FORM_NAME;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.server.commons.exception.SingularServerException;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.service.dto.ProcessDTO;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.metronic.menu.DropdownMenu;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.resource.Icone;

/**
 * Classe base para construição de caixas do servidor de petições
 */
public abstract class AbstractCaixaContent<T extends PeticaoDTO> extends Content {

    private static final long serialVersionUID = -3611649597709058163L;

    public static final int DEFAULT_ROWS_PER_PAGE = 15;

    private String processGroupCod;

    private String menu;

    private List<ProcessDTO> processes;

    @Inject
    private PetitionService petitionService;

    /**
     * Form padrão
     */
    private Form<?> form = new Form<>("form");

    /**
     * Filtro Rapido
     */
    private TextField<String> filtroRapido = new TextField<>("filtroRapido", new Model<>());

    /**
     * Botões globais
     */
    protected RepeatingView botoes = new RepeatingView("_botoes");

    protected DropdownMenu dropdownMenu = new DropdownMenu("_novos");

    /**
     * Tabela de registros
     */
    private BSDataTable<T, String> tabela = construirTabela(new BSDataTableBuilder<>(criarDataProvider()));

    /**
     * Botão de pesquisa do filtro rapido
     */
    private AjaxButton pesquisarButton = new AjaxButton("pesquisar") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onSubmit(target, form);
            onFiltroRapido(filtroRapido.getModel(), target);
        }
    };

    /**
     * Delete Form
     */
    private Form<?> deleteForm = new Form<>("deleteForm");

    private IModel<T> currentModel;

    /**
     * Modal de deleção
     */
    private final BSModalBorder deleteModal = construirModalBorder();

    public AbstractCaixaContent(String id, String processGroupCod, String menu) {
        super(id);
        this.processGroupCod = processGroupCod;
        this.menu = menu;
    }


    protected abstract String getBaseUrl();

    protected String getProcessGroupCod() {
        return processGroupCod;
    }

    protected abstract void appendPropertyColumns(BSDataTableBuilder<T, String, IColumn<T, String>> builder);

    /**
     * @return Um par String e Boolean representando a propriedade de ordenação e se deve ser ascendente respectivamente.
     */
    protected abstract Pair<String, SortOrder> getSortProperty();

    protected abstract void onDelete(T peticao);

    protected void appendActionColumns(BSDataTableBuilder<T, String, IColumn<T, String>> builder) {
        BSActionColumn<T, String> actionColumn = new BSActionColumn<>(getMessage("label.table.column.actions"));
        appendEditAction(actionColumn);
        appendViewAction(actionColumn);
        appendDeleteAction(actionColumn);
        builder.appendColumn(actionColumn);
    }

    protected void appendEditAction(BSActionColumn<T, String> actionColumn) {
        actionColumn.appendStaticAction(getMessage("label.table.column.edit"), Icone.PENCIL_SQUARE, this::criarLinkEdicao);
    }

    protected void appendViewAction(BSActionColumn<T, String> actionColumn) {
        actionColumn.appendStaticAction(getMessage("label.table.column.view"), Icone.EYE, this::criarLinkVisualizacao);
    }

    protected void appendDeleteAction(BSActionColumn<T, String> actionColumn) {
        actionColumn.appendAction(getMessage("label.table.column.delete"), Icone.MINUS, this::deleteSelected);
    }

    protected BSDataTable<T, String> construirTabela(BSDataTableBuilder<T, String, IColumn<T, String>> builder) {
        appendPropertyColumns(builder);
        appendActionColumns(builder);
        builder.setRowsPerPage(getRowsPerPage());
        return builder.setRowsPerPage(getRowsPerPage()).build("tabela");
    }

    protected WebMarkupContainer criarLinkEdicao(T peticao, String id) {
        return criarLink(peticao, id, FormActions.FORM_FILL);
    }

    protected WebMarkupContainer criarLinkVisualizacao(T peticao, String id) {
        return criarLink(peticao, id, FormActions.FORM_VIEW);
    }

    protected WebMarkupContainer criarLink(T peticao, String id, FormActions formActions) {
        String href = DispatcherPageUtil
                .baseURL(getBaseUrl())
                .formAction(formActions.getId())
                .formId(peticao.getCod())
                .params(getcriarLinkParameters(peticao))
                .build();

        WebMarkupContainer link = new WebMarkupContainer(id);
        link.add($b.attr("target", String.format("_%s", peticao.getCod())));
        link.add($b.attr("href", href));
        return link;
    }

    protected Map<String, String> getcriarLinkParameters(T peticao){
        Map<String, String> params = new HashMap<>();
        params.put(SIGLA_FORM_NAME, peticao.getType());
        return params;
    }

    protected BSModalBorder construirModalBorder() {
        BSModalBorder deleteModal = new BSModalBorder("deleteModal", getMessage("label.title.delete.draft"));
        deleteModal.addButton(BSModalBorder.ButtonStyle.EMPTY, "label.button.cancel", new AjaxButton("cancel-delete-btn", deleteForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                currentModel = null;
                deleteModal.hide(target);
            }
        });
        deleteModal.addButton(BSModalBorder.ButtonStyle.DANGER, "label.button.delete", new AjaxButton("delete-btn", deleteForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                onDelete(currentModel.getObject());
                currentModel = null;
                target.add(tabela);
                deleteModal.hide(target);
            }
        });

        return deleteModal;
    }

    private void deleteSelected(AjaxRequestTarget target, IModel<T> model) {
        currentModel = model;
        deleteModal.show(target);
    }

    public <X> void adicionarBotaoGlobal(IFunction<String, Link<X>> funcaoConstrutora) {
        botoes.add(funcaoConstrutora.apply(botoes.newChildId()));
    }

    public String getFiltroRapido() {
        return filtroRapido.getModelObject();
    }

    protected void onFiltroRapido(IModel<String> model, AjaxRequestTarget target) {
        target.add(tabela);
    }

    protected Integer getRowsPerPage() {
        return DEFAULT_ROWS_PER_PAGE;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(form.add(filtroRapido, pesquisarButton, botoes, dropdownMenu));
        add(tabela);
        add(deleteForm.add(deleteModal));
        if (getMenu() != null) {
            setProcesses(SingularSession.get().getMenuPorLabel(getMenu()).getProcesses());
        }
    }

    protected BaseDataProvider<T, String> criarDataProvider() {
        BaseDataProvider<T, String> dataProvider = new BaseDataProvider<T, String>() {
            @Override
            public long size() {
                if (getProcessesNames().isEmpty()) {
                    return 0;
                }
                return countQuickSearch(novoFiltro(), getProcessesNames());
            }

            @Override
            public Iterator<? extends T> iterator(int first, int count, String sortProperty,
                                                  boolean ascending) {
                QuickFilter filtroRapido = novoFiltro()
                        .withFirst(first)
                        .withCount(count)
                        .withSortProperty(sortProperty)
                        .withAscending(ascending);

                return quickSearch(filtroRapido, getProcessesNames()).iterator();
            }

            private List<String> getProcessesNames() {
                if (getProcesses() == null) {
                    return Collections.emptyList();
                } else {
                    return getProcesses()
                            .stream()
                            .map(ProcessDTO::getAbbreviation)
                            .collect(Collectors.toList());
                }
            }
        };
        Pair<String, SortOrder> sort = getSortProperty();
        if (sort != null) {
            dataProvider.setSort(sort.getLeft(), sort.getRight());
        }
        return dataProvider;
    }


    public String getModuleContext() {
        final String groupConnectionURL = petitionService.findByProcessGroupCod(getProcessGroupCod()).getConnectionURL();
        try {
            final String path = new URL(groupConnectionURL).getPath();
            return path.substring(0, path.indexOf("/", 1));
        } catch (Exception e) {
            throw new SingularServerException(String.format("Erro ao tentar fazer o parse da URL: %s", groupConnectionURL), e);
        }
    }

    protected QuickFilter novoFiltro() {
        return montarFiltroBasico();
    }

    protected abstract QuickFilter montarFiltroBasico();

    protected abstract List<T> quickSearch(QuickFilter filtro, List<String> siglasProcesso);

    protected abstract long countQuickSearch(QuickFilter filter, List<String> processesNames);

    public List<ProcessDTO> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessDTO> processes) {
        this.processes = processes;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }
}
