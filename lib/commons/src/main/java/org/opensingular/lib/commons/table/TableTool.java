/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.commons.table;

import com.google.common.base.Predicates;
import org.jetbrains.annotations.NotNull;
import org.opensingular.lib.commons.views.ViewGeneratorProvider;
import org.opensingular.lib.commons.views.ViewMultiGenerator;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class TableTool implements ViewMultiGenerator<TableTool>, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Column> columns = new ArrayList<>();

    private LeitorArvore leitor;

    private boolean tabelaPorNivel;

    private boolean simpleTable;

    private boolean hasSuperTitles = false;

    private int levelLimit;

    private String align;

    private String width;

    private boolean showTitles = true;

    private int nivelInicial;

    private int colunaIndentada;

    private boolean corLinhaAlternada = true;

    private List<ModificadorGerador> modificadores = new ArrayList<>();

    /**
     * configura��o a respeito de gerar ou n�o uma linha com o total das colunas
     * no final da tabela
     */
    private boolean totalizar_;

    /**
     * Indica qual nivel deve ser utilizado para totalizar
     */
    private Integer nivelTotalizar = 0;

    private InfoLinha totalLine;

    private String id;

    private Decorator decorator = new Decorator();

    public TableTool() {

    }

    public TableTool(String id) {
        this.id = id;
    }

    /**
     * Adiciona uma nova coluna no relat�rio segundo o t�tulo e tipo informado
     * (opcional). Considera a primeira coluna como sendo a respons�vel pela
     * identa��o.
     *
     * @param tipo   Tipo da classe a ser condiserada ao formatar a coluna.
     * @param title Texto para aparecer na primeira linha.
     * @return Coluna criada, para eventual modifica��o de formata��o.
     */
    public Column addColumn(Column.TipoColuna tipo, String title) {
        Column column = new Column(tipo);
        column.setTitle(title);
        column.setIndex(columns.size());
        columns.add(column);
        return column;
    }

    public Column addColumn(Column.TipoColuna tipo) {
        return addColumn(tipo, null);
    }

    /**
     * Adiciona um linha superior ao t�tulo normais. Com isso � poss�vel ter
     * duas um t�tulo superior sobre v�rias colunas.
     *
     * @param colunaInicio Primeira coluna a qual se aplica o super t�tulo
     * @param colunaFim    �ltima coluna a qual se aplica o super t�tulo
     * @param superTitulo  Texto para fica centralizado sobre todas as colunas
     */
    public void addSuperTitulo(int colunaInicio, int colunaFim, String superTitulo) {
        addSuperTitulo(colunaInicio, colunaFim, superTitulo, true);
    }

    public void addSuperTitulo(int colunaInicio, int colunaFim, String superTitulo, boolean separador) {
        hasSuperTitles = true;
        for (int i = colunaInicio; i <= colunaFim; i++) {
            Column c = columns.get(i);
            c.setSuperTitulo(superTitulo);
            c.setPossuiSeparador(separador);
        }
    }

    public TableTool addAgrupamentoPor(String tituloColuna) {
        addOrdenamentoPor(tituloColuna);
        Column c = getColumn(tituloColuna);
        addModificador(new ModificadorGeradorAgrupar(this, c));
        return this;
    }

    public TableTool addOrdenamentoPor(String tituloColuna) {
        return addOrdenamentoPor(tituloColuna, false);
    }

    public TableTool addOrdenamentoPor(String tituloColuna, boolean descending) {
        return addOrdenamentoPor(getColumn(tituloColuna), descending);
    }

    public TableTool addOrdenamentoPor(int indiceColuna) {
        return addOrdenamentoPor(indiceColuna, false);
    }

    public TableTool addOrdenamentoPor(int indiceColuna, boolean descending) {
        return addOrdenamentoPor(getColumn(indiceColuna), descending);
    }

    public TableTool addOrdenamentoPor(Column column, boolean descending) {
        Optional<ModificadorGerador> ordenar = modificadores.stream().filter(
                Predicates.instanceOf(ModificadorGeradorOrdenar.class)).findFirst();
        if (ordenar.isPresent()) {
            ((ModificadorGeradorOrdenar) ordenar.get()).addColuna(column);
        } else {
            addModificador(new ModificadorGeradorOrdenar(this, column, descending));
        }
        return this;
    }

    public TableTool addAgregacao(Column column, TipoAgregacaoCampo tipoAgregacaoCampo) {
        achaOuAdicionaModificador(ModificadorGeradorAgregar.class, () -> new ModificadorGeradorAgregar(this)).addColuna(
                column, tipoAgregacaoCampo);
        return this;
    }

    public TableTool addAgregacao(Column column, Object valor) {
        achaOuAdicionaModificador(ModificadorGeradorAgregar.class, () -> new ModificadorGeradorAgregar(this))
                .setValorCalculoExterno(column, valor);
        return this;
    }

    public TableTool addFiltro(Column column, Predicate<InfoCelula> filtro) {
        achaOuAdicionaModificador(ModificadorGeradorFiltrar.class, () -> new ModificadorGeradorFiltrar(this)).addColuna(
                column, filtro);
        return this;
    }

    public TableTool configuraAgrupamentoComAgregacao(Map<Column, TipoAgregacaoCampo> configuracaoAgregacao) {
        achaOuAdicionaModificador(ModificadorGeradorAgruparComAgregacao.class,
                () -> new ModificadorGeradorAgruparComAgregacao(this, configuracaoAgregacao));
        return this;
    }

    public TableTool addAgrupamentoComAgregacao(Column column) {
        achaOuAdicionaModificador(ModificadorGeradorAgruparComAgregacao.class,
                () -> new ModificadorGeradorAgruparComAgregacao(this)).addColuna(column);
        return this;
    }

    public Decorator getDecorator() {
        return decorator;
    }

    public <T extends ModificadorGerador> T achaOuAdicionaModificador(Class<T> modificador,
            Supplier<T> novoModificador) {
        @SuppressWarnings("unchecked") Optional<T> find = (Optional<T>) modificadores.stream().filter(
                Predicates.instanceOf(modificador)).findFirst();
        return find.orElseGet(() -> addModificador(novoModificador.get()));
    }

    public <T extends ModificadorGerador> T addModificador(T m) {
        if (modificadores.size() > 0) {
            modificadores.get(0).addFimCadeia(m);
        }
        modificadores.add(m);
        return m;
    }

    public void setLevelLimit(int levelLimit) {
        this.levelLimit = levelLimit;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public boolean isShowTitles() {
        return showTitles;
    }

    boolean isSimpleTable() {
        return simpleTable;
    }

    public void setShowTitles(boolean showTitles) {
        this.showTitles = showTitles;
    }

    /**
     * Indica se deve ser gerada uma linha com a soma total de cada coluna no
     * final da tabela
     */
    public void setTotalizar(boolean totalizar) {
        totalizar_ = totalizar;
    }

    public boolean isTotalizar() {
        return totalizar_;
    }

    /**
     * Indica qual nivel deve ser utilizado para totalizar
     */
    public void setTotalizarNivel(Integer nivelTotalizar) {
        this.nivelTotalizar = nivelTotalizar;
    }

    public boolean isCorLinhaAlternada() {
        return corLinhaAlternada;
    }

    public void setCorLinhaAlternada(boolean corLinhaAlternada) {
        this.corLinhaAlternada = corLinhaAlternada;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Column getColumn(int index) {
        return columns.get(index);
    }

    public Column getColumn(String titulo) {
        return columns.stream().filter(c -> titulo.equalsIgnoreCase(c.getTitle())).findFirst().orElseGet(
                () -> columns.stream().filter(c -> titulo.equalsIgnoreCase(c.getId())).findFirst().orElse(null));
    }

    public void setNivelInicial(int nivelInicial) {
        this.nivelInicial = nivelInicial;
    }

    private List<Column> calculataVisibleColuns(OutputTableContext ctx) {
        List<Column> visiveis = new ArrayList<>(columns.size());
        String superTitulo = null;
        boolean adicionouSeparadorGrupoSuperTitulo = true;
        for (int i = 0; i < columns.size(); i++) {
            Column c = columns.get(i);
            if (c.isVisivel() && (c.getProcessor().shouldBeGeneretedOnStaticContent() || !ctx.isStaticContent())) {
                if (!Objects.equals(superTitulo, c.getSuperTitle())) {
                    if (!c.isPossuiSeparador() && c.getSuperTitle() != null) {
                        adicionouSeparadorGrupoSuperTitulo = true;
                    } else {
                        adicionouSeparadorGrupoSuperTitulo = false;
                    }
                    superTitulo = c.isPossuiSeparador() ? c.getSuperTitle() : null;
                }
                if ((c.isPossuiSeparador() || c.getSuperTitle() == null) && !adicionouSeparadorGrupoSuperTitulo) {
                    if (i > 0) {
                        visiveis.add(null);
                    }
                    adicionouSeparadorGrupoSuperTitulo = true;
                }
                visiveis.add(c);
            }
        }
        return visiveis;
    }

    public PopulatorTable createSimpleTablePopulator() {
        PopulatorTable preenchedor = new PopulatorTable(this);
        leitor = preenchedor.asLeitorArvore();
        simpleTable = true;
        return preenchedor;
    }

    @SuppressWarnings("unchecked")
    public <T> void setLeitorTabelaSimples(Iterable<? extends T> lista, LeitorLinha<T> leitor) {
        this.leitor = GeradorUtil.toLeitorArvore((Object) lista, (LeitorLinha<Object>) leitor);
        simpleTable = true;
    }

    public void setLeitorArvore(LeitorArvore leitor) {
        this.leitor = leitor;
        simpleTable = false;
    }

    public void setLeitorPorNivel(LeitorArvore leitor) {
        this.leitor = leitor;
        tabelaPorNivel = true;
    }

    public void setColunaIndentada(int colunaIndentada) {
        this.colunaIndentada = colunaIndentada;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void generate(@Nonnull TableOutput tableOutput) {
        if (getColumns().isEmpty()) {
            return;
        }
        totalLine = newBlankLine();

        DadoLeitor dadoLeitor = new DadoLeitorPorInterador(leitor, leitor.getRaizes(), 0);
        if (!isShowTitles() && dadoLeitor.isEmpty()) {
            return;
        }

        OutputTableContext ctx = new OutputTableContext(this, tableOutput);
        ctx.setVisibleColuns(calculataVisibleColuns(ctx));

        ctx.getOutput().generateTableStart(ctx, this);

        if (modificadores.size() > 0) {
            ctx.setVisibleColuns(modificadores.get(0).adjustTitles(ctx.getVisibleColuns()));
            generateTitles(ctx);
            dadoLeitor = modificadores.get(0).aplicar(dadoLeitor);
        } else {
            generateTitles(ctx);
        }
        gerarFilhos(dadoLeitor, ctx, nivelInicial);
        if (totalizar_) {
            generateTotalLine(ctx);
        }

        ctx.getOutput().generateTableEnd(ctx, this);
    }

    private void generateTitles(OutputTableContext ctx) {
        if (!isShowTitles() && !hasSuperTitles) {
            return;
        }
        List<Column> visibles = ctx.getVisibleColuns();
        ctx.getOutput().generateTitleBlockStart(ctx);
        if (hasSuperTitles) {
            ctx.getOutput().generateTitleLineStart(ctx, true);
            boolean nextColumnWithSeparator = false;
            for (int i = 0; i < visibles.size(); i++) {
                Column c = visibles.get(i);
                if (c == null) {
                    nextColumnWithSeparator = true;
                } else if (c.getSuperTitle() == null) {
                    ctx.getOutput().generateTiltleCell(ctx, c, 2, false, nextColumnWithSeparator);
                    nextColumnWithSeparator = false;
                } else {
                    int ult = i;
                    while ((ult + 1 < visibles.size()) && (visibles.get(ult + 1) != null) && (Objects.equals(
                            visibles.get(ult + 1).getSuperTitle(), c.getSuperTitle()))) {
                        ult++;
                    }
                    ctx.getOutput().generateTitleCellSuper(ctx, c, ult - i + 1, nextColumnWithSeparator);
                    nextColumnWithSeparator = false;
                    i = ult;
                }
            }
            ctx.getOutput().generateTitleLineEnd(ctx, true);
        }
        if (isShowTitles()) {
            ctx.getOutput().generateTitleLineStart(ctx, false);
            boolean nextColumnWithSeparator = false;
            for (Column column : visibles) {
                if (column == null) {
                    nextColumnWithSeparator = true;
                } else if ((column.getSuperTitle() != null) || !hasSuperTitles) {
                    ctx.getOutput().generateTiltleCell(ctx, column, 1, hasSuperTitles, nextColumnWithSeparator);
                    nextColumnWithSeparator = false;
                }
            }
            ctx.getOutput().generateTitleLineEnd(ctx, false);
        }
        ctx.getOutput().generateTitleBlockEnd(ctx);
    }

    final InfoLinha newBlankLine() {
        return new InfoLinha(this);
    }

    private void gerarFilhos(DadoLeitor filhos, OutputTableContext ctx, int nivel) {
        if (filhos != null && !filhos.isEmpty()) {
            ctx.getOutput().generateBodyBlockStart(ctx);
            if (tabelaPorNivel || columns.stream().anyMatch(c -> c.getNivelDados() > 0)) {
                int qtdNiveis = columns.stream().mapToInt(c -> c.getNivelDados()).max().getAsInt() + 1;
                int[] contadorLinha = new int[qtdNiveis];
                for (DadoLinha dado : filhos) {
                    for (DadoLinha[] linha : dado.normalizarNiveis(qtdNiveis)) {
                        gerarTabelaPorNivel(linha, ctx, contadorLinha);
                    }
                }
            } else {
                for (DadoLinha dado : filhos) {
                    if (simpleTable) {
                        gerarTabelaSimples(dado, ctx);
                    } else {
                        gerarLinhaArvore(dado, ctx, nivel);
                    }
                }
            }
            ctx.getOutput().generateBodyBlockEnd(ctx);
        }
    }

    private void gerarTabelaPorNivel(DadoLinha[] linha, OutputTableContext ctx, int[] contadorLinha) {
        int nivelLinha = -1;
        int linhaCor = 0;
        InfoLinha line = null;
        for (int i = 0; i < linha.length; i++) {
            if (linha[i] != null) {
                if (nivelLinha == -1) {
                    nivelLinha = i;
                    contadorLinha[i]++;
                    for (int j = i + 1; j < contadorLinha.length; j++) {
                        contadorLinha[j] = contadorLinha[i];
                    }
                    linhaCor = contadorLinha[i];
                }
                ctx.getLineReadContext().setLevel(nivelLinha);
                line = linha[i].recuperarValores(ctx.getLineReadContext(), i, nivelLinha == i, false);
            }
        }
        if (line == null) {
            throw new RuntimeException("Invalid State");
        }
        if (ctx.isExibirLinha()) {
            int lineAlternation = isCorLinhaAlternada() ? linhaCor : -1;
            ctx.getOutput().generateLineSimpleStart(ctx, line, lineAlternation);

            int idxColuna = 0;
            boolean nextColumnWithSeparator = false;
            while (idxColuna < ctx.getVisibleColuns().size()) {
                Column c = ctx.getVisibleColuns().get(idxColuna);
                ctx.setIndiceColunaAtual(idxColuna);
                int qtdSpan = 1;
                if (c == null) {
                    if (linha[0] != null) {
                        nextColumnWithSeparator = true;
                    }
                } else {
                    int nivel = c.getNivelDados();
                    InfoCelula cell = line.get(c);
                    if (nivel >= nivelLinha) {
                        OutputCellContext ctxCell = createCellContext(ctx, cell, nextColumnWithSeparator);
                        nextColumnWithSeparator = false;
                        if (linha[nivel] != null) {
                            int rowSpan = linha[nivel].getLinhas();
                            ctxCell.getTempDecorator().setRowSpan(rowSpan);
                        }
                        if (ctxCell.getTempDecorator().getRowSpan() != 0) {
                            ctxCell.setLevel(colunaIndentada == idxColuna ? nivel : -1);
                            ctx.getOutput().generateCell(ctxCell);
                        }

                        addToTotal(c, cell, nivel);
                        qtdSpan = ctxCell.getTempDecorator().getColSpan();
                    }
                }
                idxColuna += qtdSpan;
            }
            ctx.getOutput().generateLineSimpleEnd(ctx);

            ctx.incIndiceLinhaAtual();
        }
    }

    private Decorator resolverDecorator(OutputTableContext ctx, InfoLinha line) {
        return line.createTempDecorator();
    }

    private void gerarLinhaArvore(DadoLinha dado, OutputTableContext ctx, int nivel) {
        if ((levelLimit > 0) && (nivel + 1 > levelLimit)) {
            return;
        }
        ctx.getLineReadContext().setLevel(nivel);
        InfoLinha line = dado.recuperarValores(ctx.getLineReadContext(), nivel, true, false);

        if (ctx.isExibirLinha()) {
            //nivel = ctx.getNivel();
            ctx.getOutput().generateLineTreeStart(ctx, line, nivel);

            int idxColuna = 0;
            boolean nextColumnWithSeparator = false;
            while (idxColuna < ctx.getVisibleColuns().size()) {
                Column c = ctx.getVisibleColuns().get(idxColuna);
                ctx.setIndiceColunaAtual(idxColuna);
                if (c == null) {
                    nextColumnWithSeparator = true;
                    idxColuna++;
                } else {
                    InfoCelula cell = line.get(c);
                    if ((nivel == 0) && c.isCalcularPercentualPai()) {
                        c.setValorReferenciaPercentual(cell.getValueAsNumberOrNull());
                    }
                    OutputCellContext ctxCell = createCellContext(ctx, cell, nextColumnWithSeparator);
                    nextColumnWithSeparator = false;
                    ctxCell.setLevel(colunaIndentada == idxColuna ? nivel : -1);
                    if (ctxCell.getTempDecorator().getRowSpan() != 0) {
                        ctx.getOutput().generateCell(ctxCell);
                    }

                    addToTotal(c, cell, nivel);
                    idxColuna += ctxCell.getTempDecorator().getColSpan();
                }
            }
            ctx.getOutput().generateLineTreeEnd(ctx);

            ctx.incIndiceLinhaAtual();

            gerarFilhos(dado.getLeitorFilhos(), ctx, nivel + 1);
        } else {
            gerarFilhos(dado.getLeitorFilhos(), ctx, nivel);
        }
    }

    private void gerarTabelaSimples(DadoLinha dado, OutputTableContext ctx) {

        InfoLinha line = dado.recuperarValores(ctx.getLineReadContext(), 0, true, false);

        if (ctx.isExibirLinha()) {

            int lineAlternation = isCorLinhaAlternada() ? ctx.getIndiceLinhaAtual() % 2 : -1;
            ctx.getOutput().generateLineSimpleStart(ctx, line, lineAlternation);

            int idxColuna = 0;
            boolean nextColumnWithSeparator = false;
            while (idxColuna < ctx.getVisibleColuns().size()) {
                Column c = ctx.getVisibleColuns().get(idxColuna);
                ctx.setIndiceColunaAtual(idxColuna);
                if (c == null) {
                    nextColumnWithSeparator = true;
                    idxColuna++;
                } else {
                    InfoCelula cell = line.get(c);
                    if (c.isCalcularPercentualPai()) {
                        c.setValorReferenciaPercentual(cell.getValueAsNumberOrNull());
                    }
                    OutputCellContext ctxCell = createCellContext(ctx, cell, nextColumnWithSeparator);
                    nextColumnWithSeparator = false;
                    ctxCell.setLevel(-1);
                    if (ctxCell.getTempDecorator().getRowSpan() != 0) {
                        ctx.getOutput().generateCell(ctxCell);
                    }

                    addToTotal(c, cell, 0);
                    idxColuna += ctxCell.getTempDecorator().getColSpan();
                }
            }
            ctx.getOutput().generateLineSimpleEnd(ctx);

            ctx.incIndiceLinhaAtual();
        }
    }

    @NotNull
    private OutputCellContext createCellContext(OutputTableContext ctx, InfoCelula cell, boolean columnWithSeparator) {
        DecoratorCell decorator = cell.createTempDecorator();
        if (decorator.isColSpanAll()) {
            decorator.setColSpan(ctx.getQtdColunaVisiveis() - ctx.getIndiceColunaAtual());
        }
        OutputCellContext ctxCell = new OutputCellContext(ctx, cell, decorator);

        // Trata a exibição do valor como percentual do valor pai
        if (ctxCell.getColumn().isCalcularPercentualPai()) {
            ctxCell.setColumnProcessor(Column.TipoColuna.tpPercent.getProcessor());
            if (ctxCell.getValue() instanceof Number) {
                Number value = (Number) ctxCell.getValue();
                value = AlocproToolkit.divide(value, ctxCell.getColumn().getValorReferenciaPercentual());
                ctxCell.setValue(value);
            }
        }
        ctxCell.setColumnWithSeparator(columnWithSeparator);
        return ctxCell;
    }

    private void addToTotal(Column c, InfoCelula cell, int nivel) {
        if (totalizar_ && c.isTotalizar() && (nivelTotalizar == null || nivel == nivelTotalizar)) {
            InfoCelula total = totalLine.get(c);
            Number value = null;
            if (cell.getValue() instanceof Number) {
                value = (Number) cell.getValue();
            } else if (cell.getValorReal() instanceof Number) {
                value = (Number) cell.getValorReal();
            }
            total.setValor(AlocproToolkit.add(total.getValueAsNumberOrNull(), value));
        }
    }


    private void generateTotalLine(OutputTableContext ctx) {
        Decorator tmpDecorator = resolverDecorator(ctx, totalLine);

        if (ctx.getTableTool().isSimpleTable()) {
            ctx.getOutput().generateTotalLineStart(ctx, totalLine, tmpDecorator, -1);
        } else {
            ctx.getOutput().generateTotalLineStart(ctx, totalLine, tmpDecorator, nivelInicial);
        }

        int indiceColuna = 0;
        boolean nextColumnWithSeparator = false;
        for (Column c : ctx.getVisibleColuns()) {
            if (c == null) {
                nextColumnWithSeparator = true;
            } else if (!c.isTotalizar()) {
                ctx.getOutput().generateTotalCellSkip(ctx, c, nextColumnWithSeparator);
                nextColumnWithSeparator = false;
            } else if (indiceColuna == 0) {
                DecoratorCell tmpDecoratorCell = new DecoratorCell(c.getDecoratorValues());
                ctx.getOutput().generateTotalLabel(ctx, c, "Total", tmpDecoratorCell, nivelInicial);
            } else {
                InfoCelula cell = totalLine.get(c);
                OutputCellContext ctxCell = new OutputCellContext(ctx, cell, cell.createTempDecorator()).setLevel(-1);
                ctxCell.setColumnWithSeparator(nextColumnWithSeparator);
                Number value = cell.getValueAsNumberOrNull();
                ctx.getOutput().generateTotalCell(ctxCell, value);
                nextColumnWithSeparator = false;
            }
            indiceColuna++;
        }

        ctx.getOutput().generateTotalLineEnd(ctx);
    }

    @Nonnull
    @Override
    public Collection<ViewGeneratorProvider<TableTool>> getGenerators() {
        return TableToolUtil.getGenerators();
    }

}
