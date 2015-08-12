package br.net.mirante.singular.form.mform.io;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.util.xml.MDocument;
import br.net.mirante.singular.form.util.xml.MElement;

public class MformPersistenciaXML {

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
