/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.view;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class WebRefImpl implements WebRef {

    public static final WebRef VOLTAR = WebRefImpl.ofJs("Voltar", "history.back();");

    private String nome;

    private String nomeCurto;

    private Lnk path;

    private String pathIcone;

    private String pathIconePequeno;

    private boolean seAplicaAoContexto = true;

    private String confirmacao;

    private boolean possuiDireitoAcesso = true;

    private boolean js = false;

    private boolean abrirEmNovaJanela = false;

    private Map<String, String> atributosExtras;

    private ModalViewDef modalViewDef;

    protected WebRefImpl() {
    }

    public static WebRefImpl of(String nome) {
        return of(nome, (Lnk) null);
    }

    public static WebRefImpl of(String nome, Lnk path) {
        WebRefImpl impl = new WebRefImpl();
        impl.setNome(nome);
        impl.setPath(path);
        return impl;
    }

    public static WebRefImpl ofJs(String nome, String js) {
        WebRefImpl impl = new WebRefImpl();
        impl.setNome(nome);
        impl.setJs(js);
        return impl;
    }

    public static WebRefImpl of(String nome, String path) {
        return of(nome, Lnk.of(path));
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public String getNomeCurto() {
        return nomeCurto;
    }

    @Override
    public WebRefImpl addParam(String nome, Object valor) {
        if (valor != null) {
            path = path.and(nome, valor.toString());
        }
        return this;
    }

    @Override
    public String getJs() {
        return path == null ? null : path.toString();
    }

    @Override
    public Lnk getPath() {
        return path;
    }

    @Override
    public String getPathIcone() {
        return pathIcone;
    }

    @Override
    public String getPathIconePequeno() {
        return pathIconePequeno;
    }

    @Override
    public boolean isSeAplicaAoContexto() {
        return seAplicaAoContexto;
    }

    @Override
    public String getConfirmacao() {
        return confirmacao;
    }

    @Override
    public boolean isPossuiDireitoAcesso() {
        return possuiDireitoAcesso;
    }

    @Override
    public boolean isAbrirEmNovaJanela() {
        return abrirEmNovaJanela;
    }

    public WebRefImpl setNome(String nome) {
        this.nome = nome;
        if (this.nomeCurto == null)
            this.nomeCurto = nome;
        return this;
    }

    public WebRefImpl setNomeCurto(String nomeCurto) {
        this.nomeCurto = nomeCurto;
        if (this.nome == null)
            this.nome = nomeCurto;
        return this;
    }

    public WebRefImpl setPath(String path) {
        return setPath(Lnk.of(path));
    }

    public WebRefImpl setPath(Lnk path) {
        this.path = path;
        return this;
    }

    public WebRefImpl setAbrirEmNovaJanela(boolean abrirEmNovaJanela) {
        this.abrirEmNovaJanela = abrirEmNovaJanela;
        return this;
    }

    public WebRefImpl setPathIcone(String pathIcone) {
        this.pathIcone = pathIcone;
        return this;
    }

    public WebRefImpl setPathIconePequeno(String pathIconePequeno) {
        this.pathIconePequeno = pathIconePequeno;
        return this;
    }

    public WebRefImpl setSeAplicaAoContexto(boolean seAplicaAoContexto) {
        this.seAplicaAoContexto = seAplicaAoContexto;
        return this;
    }

    public WebRefImpl setAtributo(String nome, String valor) {
        Objects.requireNonNull(nome);
        if (valor == null) {
            if (atributosExtras != null) {
                atributosExtras.remove(nome);
            }
        } else {
            if (atributosExtras == null) {
                atributosExtras = Maps.newHashMap();
            }
            atributosExtras.put(nome, valor);
        }
        return this;
    }

    public Map<String, String> getAtributos() {
        return atributosExtras;
    }

    @Override
    public boolean isJs() {
        return js;
    }

    public WebRefImpl setJs(String js) {
        this.js = true;
        setPath(js);
        return this;
    }

    public WebRefImpl setConfirmacao(String confirmacao) {
        this.confirmacao = confirmacao;
        return this;
    }

    public WebRefImpl setPossuiDireitoAcesso(boolean possuiDireitoAcesso) {
        this.possuiDireitoAcesso = possuiDireitoAcesso;
        return this;
    }

    @Override
    public ModalViewDef getModalViewDef() {
        return this.modalViewDef;
    }

    public WebRefImpl setModalViewDef(ModalViewDef modalViewDef) {
        this.modalViewDef = modalViewDef;
        return this;
    }

    @Override
    public String gerarHtml(String urlApp) {
        return gerarHtmlIntero(urlApp, this);
    }

    private static String gerarHtmlIntero(String pathApp, WebRef ref) {
        if (!ref.isPossuiDireitoAcesso()) {
            return "";
        }
        StringBuilder href = new StringBuilder("<a ");
        print(href, "value", "");
        if (!StringUtils.isEmpty(ref.getPathIconePequeno())) {
            print(href, "style", "cursor:pointer;margin-right:2px;border:0;width:16px;padding: 0;");
        } else {
            print(href, "value", "");
            print(href, "style", "cursor:pointer;margin-right:2px;background:none;text-decoration: underline;border:0;");
        }
        if (ref.getPath() != null) {
            StringBuilder onclick = new StringBuilder();
            if (!StringUtils.isEmpty(ref.getConfirmacao())) {
                onclick.append("if(!confirm('").append(ref.getConfirmacao()).append("'))return false;");
            }
            if (ref.getModalViewDef() != null) {
                onclick.append("var janela = new JanelaModal(window);janela.setOnClose(function(){window.location.reload()});janela.show('")
                        .append(ref.getPath().addUrlApp(pathApp).getUrl())
                        .append("','").append(ref.getNome())
                        .append("', 'status:no; scroll:yes; resizable:yes; dialogWidth:")
                        .append(ref.getModalViewDef().getWidth()).append("px;")
                        .append("dialogHeight:").append(ref.getModalViewDef().getHeight()).append("px;")
                        .append("center:yes; help:no');return false;");

                print(href, "href", "#");
            } else if (!ref.isJs()) {
                onclick.append("if(window.parent && window.parent.exibirLoading)window.parent.exibirLoading();");
                print(href, "href", ref.getPath().addUrlApp(pathApp).getUrl());

            } else {
                onclick.append(ref.getPath()).append("return false;");
                print(href, "href", "#");
            }
            print(href, "onclick", onclick.toString(), pathApp);
        }
        if (ref.isAbrirEmNovaJanela()) {
            print(href, "target", "_blank");
        }
        print(href, "title", ref.getNome());
        if (ref instanceof WebRefImpl) {
            WebRefImpl ref2 = (WebRefImpl) ref;
            if (ref2.atributosExtras != null) {
                for (Map.Entry<String, String> e : ref2.atributosExtras.entrySet()) {
                    print(href, e.getKey(), e.getValue(), pathApp);
                }
            }
        }
        href.append(">");
        if (StringUtils.isEmpty(ref.getPathIconePequeno())) {
            href.append(ref.getNomeCurto());
        } else {
            href.append("<img ");
            print(href, "style", "width:16px;padding: 0;");
            print(href, "src", Lnk.of(pathApp, ref.getPathIconePequeno()).getUrl());
            href.append(">");
        }
        href.append("</a>");
        return href.toString();
    }

    private static void print(StringBuilder builder, String nome, String valor, String pathApp) {
        if (valor != null) {
            if (valor.indexOf("${path}") != -1) {
                valor = valor.replace("${path}", pathApp);
            }
            print(builder, nome, valor);
        }
    }

    private static void print(StringBuilder builder, String nome, String valor) {
        if (valor != null) {
            builder.append(' ').append(nome).append("=\"").append(valor).append('"');
        }
    }
}
