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

import java.util.List;

/**
 * @author Daniel C. Bordin on 16/04/2017.
 */
public final class OutputTableContext {

    private final TableTool tableTool;

    private final TableOutput tableOutput;

    private int indiceColunaAtual;

    private int indiceLinhaAtual;

    private InfoLinha linha;
    private List<Column> visibleColuns;

    private LineReadContext lineReadContext;

    OutputTableContext(TableTool tableTool, TableOutput tableOutput) {
        this.tableTool = tableTool;
        this.tableOutput = tableOutput;
    }

    public TableTool getTableTool() {
        return tableTool;
    }

    public TableOutput getOutput() {
        return tableOutput;
    }

    public void limpar(int numNivel) {
        linha = null;
        setNivel(numNivel);
    }

    final InfoLinha getLinhaSeExistir() {
        return linha;
    }

    private InfoLinha getLinha() {
        if (linha == null) {
            linha = tableTool.newBlankLine();
        }
        return linha;
    }

    final void setLinha(InfoLinha infoLinha) {
        linha = infoLinha;
    }

    //TODO Verificar se ao final esse método fica
    public String getUrlApp() {
        return tableOutput.getUrlApp();
    }

    public Decorator getDecorador() {
        return getLinha().getDecorador();
    }

    final int getQtdColunaVisiveis() {
        return visibleColuns.size();
    }

    final void setIndiceColunaAtual(int i) {
        indiceColunaAtual = i;
    }

    final int getIndiceColunaAtual() {
        return indiceColunaAtual;
    }

    public final int getIndiceLinhaAtual() {
        return indiceLinhaAtual;
    }

    final void incIndiceLinhaAtual() {
        indiceLinhaAtual++;
    }

    public final void setNivel(int nivel) {
        getLinha().setNivel(nivel);
    }

    public int getNivel() {
        return getLinha().getNivel();
    }

    final boolean isExibirLinha() {
        return linha == null ? true : getLinha().isExibirLinha();
    }

    public void setExibirLinha(boolean exibirLinha) {
        if (linha != null || !exibirLinha) {
            getLinha().setExibirLinha(exibirLinha);
        }
    }

    /**
     * Indica que o conteúdo sendo gerado será estático, ou seja, sera consultado fora do contexto do servidor. Por
     * exemplo, HTML enviado por e-mail, Excel, PDF, etc.
     */
    public boolean isStaticContent() {
        return tableOutput.isStaticConent();
    }

    /** Retorna a lista de coluna a serem exibidas (efetivamente geradas). */
    public List<Column> getVisibleColuns() {
        return visibleColuns;
    }

    /** Define as colunas a serem exibidas no resultado final. */
    final void setVisibleColuns(List<Column> visibleColuns) {
        this.visibleColuns = visibleColuns;
    }

    public LineReadContext getLineReadContext() {
        if (lineReadContext == null) {
            lineReadContext = new LineReadContext(tableTool);
        }
        return lineReadContext;
    }
}
