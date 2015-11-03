package br.net.mirante.singular.form.mform.io;

import br.net.mirante.singular.form.mform.MAtributo;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoCode;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.util.xml.MDocument;
import br.net.mirante.singular.form.util.xml.MElement;

public class MformPersistenciaXML {

    public static <T extends MInstancia> T fromXML(MTipo<T> tipo, MElement xml) {
        T novo = tipo.novaInstancia();
        fromXML(novo, xml);
        return novo;
    }

    // TODO (de Daniel) Verifica se precisa desse método com essa assinatura
    // mesmo. Aparetemente seria necessário apenas o método acima.
    public static void fromXML(MInstancia instancia, MElement xml) {
        if (xml == null)
            return;
        if (instancia instanceof MISimples) {
            MISimples<?> instanciaS = (MISimples<?>) instancia;
            MTipoSimples<?, ?> tipos = instanciaS.getMTipo();
            instancia.setValor(tipos.converter(xml.getTextContent(), tipos.getClasseTipoNativo()));

        } else if (instancia instanceof MIComposto) {
            MIComposto instc = (MIComposto) instancia;
            for (MTipo<?> campo : instc.getMTipo().getFields()) {
                MElement xmlFilho = xml.getElement(campo.getNomeSimples());
                if (xmlFilho != null) {
                    fromXML(instc.getCampo(campo.getNomeSimples()), xmlFilho);
                }
            }
        } else if (instancia instanceof MILista) {
            MILista<?> lista = (MILista<?>) instancia;
            String nomeFilhos = lista.getMTipo().getTipoElementos().getNomeSimples();
            for (MElement xmlFilho : xml.getElements(nomeFilhos)) {
                MInstancia filho = lista.addNovo();
                fromXML(filho, xmlFilho);
            }

        } else {
            throw new UnsupportedOperationException(instancia.getClass().getName());
        }
    }

    // TODO (de Daniel) Retirar as referencias desse método em prol dos acima e
    // depois apagá-lo. A implementação abaixo não trata corretamente todos os
    // casos.
    @Deprecated
    public static void fromXML(MTipo<?> tipo, MInstancia instancia, MElement xml) {
        if (xml == null)
            return;
        if (tipo instanceof MTipoComposto<?>) {
            MTipoComposto<?> tipoc = (MTipoComposto<?>) tipo;
            MIComposto instc = (MIComposto) instancia;
            for (String campo : tipoc.getCampos()) {
                fromXML(
                    tipoc.getCampo(campo),
                    instc.getCampo(campo),
                    xml.getElement(campo));
            }

        } else if (tipo instanceof MTipoSimples) {
            MTipoSimples<?, ?> tipos = (MTipoSimples<?, ?>) tipo;
            instancia.setValor(tipos.converter(xml.getTextContent(), tipos.getClasseTipoNativo()));

        } else if (tipo instanceof MTipoLista<?, ?>) {

        } else if (tipo instanceof MAtributo) {

        } else if (tipo instanceof MTipoCode) {

        } else {
            throw new UnsupportedOperationException(tipo.getClass().getName());
        }
    }

    public static MElement toXML(MInstancia instancia) {
        return toXML(null, null, instancia, false);
    }

    public static MElement toXML(MElement pai, String nomePai, MInstancia instancia, boolean persistirNull) {

        MDocument xmlDocument = (pai == null) ? MDocument.newInstance() : pai.getMDocument();

        MElement xmlResultado = toXML(xmlDocument, instancia, persistirNull);
        if (xmlResultado == null) {
            return pai;
        } else if (nomePai != null) {
            MElement novo = xmlDocument.createMElement(nomePai);
            novo.addElement(xmlResultado);
            xmlResultado = novo;
        }
        if (pai != null) {
            pai.addElement(xmlResultado);
            return pai;
        }
        xmlDocument.setRaiz(xmlResultado);

        return xmlResultado;
    }

    private static MElement toXML(MDocument xmlDocument, MInstancia instancia, boolean persistirNull) {
        if (instancia instanceof MISimples<?>) {
            MISimples<?> iSimples = (MISimples<?>) instancia;
            String sPersistencia = iSimples.toStringPersistencia();
            if (sPersistencia != null) {
                return xmlDocument.createMElementComValor(instancia.getMTipo().getNomeSimples(), sPersistencia);
            } else if (persistirNull) {
                return xmlDocument.createMElement(instancia.getMTipo().getNomeSimples());
            }
            return null;
        } else if (instancia instanceof MIComposto) {
            MElement registro = null;
            for (MInstancia filho : ((MIComposto) instancia).getCampos()) {
                MElement xmlFilho = toXML(xmlDocument, filho, persistirNull);
                if (xmlFilho != null) {
                    if (registro == null) {
                        registro = xmlDocument.createMElement(instancia.getMTipo().getNomeSimples());
                    }
                    registro.appendChild(xmlFilho);
                }
            }
            return registro;
        } else if (instancia instanceof MILista) {
            MILista<?> lista = (MILista<?>) instancia;
            if (lista.isEmpty()) {
                return null;
            }
            MElement xmlLista = null;
            for (MInstancia filho : lista) {
                MElement xmlFilho = toXML(xmlDocument, filho, persistirNull);
                if (xmlFilho != null) {
                    if (xmlLista == null) {
                        xmlLista = xmlDocument.createMElement(instancia.getMTipo().getNomeSimples());
                    }
                    xmlLista.appendChild(xmlFilho);
                }
            }
            return xmlLista;
        } else {
            throw new RuntimeException("Instancia da classe " + instancia.getClass().getName() + " não suportada");
        }
    }
}
