package br.net.mirante.singular.form.mform.io;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.util.xml.MDocument;
import br.net.mirante.singular.form.util.xml.MElement;

public class MformPersistenciaXML {

    public static final String ATRIBUTO_ID = "id";
    public static final String ATRIBUTO_LAST_ID = "lastId";

    public static <T extends MInstancia> T fromXML(MTipo<T> tipo, MElement xml) {
        T novo = tipo.novaInstancia();
        Integer lastId = xml.getInteger("@" + ATRIBUTO_LAST_ID);

        // Colocar em modo de não geraçao de IDs
        novo.getDocument().setLastId(-1);
        fromXML(novo, xml);

        int maxId = verificarIds(novo, new HashSet<>());
        if (lastId == null) {
            novo.getDocument().setLastId(maxId);
        } else {
            novo.getDocument().setLastId(lastId);
        }

        return novo;
    }

    private static int verificarIds(MInstancia instancia, Set<Integer> ids) {
        Integer id = instancia.getId();
        if (ids.contains(id)) {
            throw new SingularFormException(
                    "A instancia " + instancia.getPathFull() + " tem um ID repetido (igual a outra instância) id=" + id);
        }
        if (instancia instanceof ICompositeInstance) {
            int max = id;
            for (MInstancia filho : ((ICompositeInstance) instancia).getChildren()) {
                max = Math.max(max, verificarIds(filho, ids));
            }
            return max;
        }
        return id;
    }

    private static void fromXML(MInstancia instancia, MElement xml) {
        lerAtributos(instancia, xml);
        if (xml == null) {
            // Não precisa fazer nada
        } else if (instancia instanceof MISimples) {
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

    private static void lerAtributos(MInstancia instancia, MElement xml) {
        NamedNodeMap atributos = xml.getAttributes();
        if (atributos != null) {
            for (int i = 0; i < atributos.getLength(); i++) {
                Attr at = (Attr) atributos.item(i);
                if (at.getName().equals(ATRIBUTO_ID)) {
                    instancia.setId(Integer.parseInt(at.getValue()));
                } else if (!at.getName().equals(ATRIBUTO_LAST_ID)) {
                    instancia.setValorAtributo(at.getName(), at.getValue());
                }
            }
        }
    }

    public static MElement toXML(MInstancia instancia) {
        return new PersistenceBuilderXML().withPersistNull(false).toXML(instancia);
    }

    public static MElement toXMLPreservingRuntimeEdition(MInstancia instancia) {
        return new PersistenceBuilderXML().withPersistNull(true).withPersistAttributes(true).toXML(instancia);
    }

    final static MElement toXML(MElement pai, String nomePai, MInstancia instancia, PersistenceBuilderXML builder) {

        MDocument xmlDocument = (pai == null) ? MDocument.newInstance() : pai.getMDocument();
        ConfXMLGeneration conf = new ConfXMLGeneration(builder, xmlDocument);

        MElement xmlResultado = toXML(conf, instancia);
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
        if (builder.isPersistId()) {
            xmlResultado.setAttribute(ATRIBUTO_LAST_ID, Integer.toString(instancia.getDocument().getLastId()));
        }

        return xmlResultado;
    }

    private static MElement toXML(ConfXMLGeneration conf, MInstancia instancia) {
        if (instancia instanceof MISimples<?>) {
            MISimples<?> iSimples = (MISimples<?>) instancia;
            String sPersistencia = iSimples.toStringPersistencia();
            if (sPersistencia != null) {
                return conf.createMElementComValor(instancia, sPersistencia);
            } else if (conf.isPersistirNull()) {
                return conf.createMElement(instancia);
            }
            return null;
        } else if (instancia instanceof MIComposto) {
            MElement registro = null;
            for (MInstancia filho : ((MIComposto) instancia).getCampos()) {
                MElement xmlFilho = toXML(conf, filho);
                if (xmlFilho != null) {
                    if (registro == null) {
                        registro = conf.createMElement(instancia);
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
                MElement xmlFilho = toXML(conf, filho);
                if (xmlFilho != null) {
                    if (xmlLista == null) {
                        xmlLista = conf.createMElement(instancia);
                    }
                    xmlLista.appendChild(xmlFilho);
                }
            }
            return xmlLista;
        } else {
            throw new SingularFormException("Instancia da classe " + instancia.getClass().getName() + " não suportada");
        }
    }

    private static final class ConfXMLGeneration {

        private final MDocument xmlDocument;
        private final PersistenceBuilderXML builder;

        public ConfXMLGeneration(PersistenceBuilderXML builder, MDocument xmlDocument) {
            this.builder = builder;
            this.xmlDocument = xmlDocument;
        }

        public boolean isPersistirNull() {
            return builder.isPersistNull();
        }

        public MElement createMElement(MInstancia instancia) {
            return complement(instancia, xmlDocument.createMElement(instancia.getMTipo().getNomeSimples()));
        }

        public MElement createMElementComValor(MInstancia instancia, String valorPersistencia) {
            return complement(instancia, xmlDocument.createMElementComValor(instancia.getMTipo().getNomeSimples(), valorPersistencia));
        }

        private MElement complement(MInstancia instancia, MElement element) {
            if (builder.isPersistId() && instancia.getId() != null) {
                element.setAttribute(ATRIBUTO_ID, instancia.getId().toString());
            }
            if (builder.isPersistAttributes()) {
                for (Entry<String, MInstancia> atr : instancia.getAtributos().entrySet()) {
                    if (atr.getValue() instanceof MISimples) {
                        String sPersistencia = ((MISimples<?>) atr.getValue()).toStringPersistencia();
                        element.setAttribute(atr.getKey(), sPersistencia);
                    } else {
                        throw new SingularFormException("Não implementada a persitência de atributos compostos: " + atr.getKey() + " em "
                                + instancia.getPathFull());
                    }
                }
            }
            return element;
        }
    }
}
