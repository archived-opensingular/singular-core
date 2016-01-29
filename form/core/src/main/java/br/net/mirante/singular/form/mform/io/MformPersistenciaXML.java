package br.net.mirante.singular.form.mform.io;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.util.xml.MDocument;
import br.net.mirante.singular.form.util.xml.MElement;

public class MformPersistenciaXML {

    public static final String ATRIBUTO_ID = "id";
    public static final String ATRIBUTO_LAST_ID = "lastId";

    public static <T extends SInstance> T fromXML(SType<T> tipo, MElement xml) {
        T novo = tipo.novaInstancia();
        Integer lastId = 0;
        if(xml !=  null) {  lastId = xml.getInteger("@" + ATRIBUTO_LAST_ID); }

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

    private static int verificarIds(SInstance instancia, Set<Integer> ids) {
        Integer id = instancia.getId();
        if (ids.contains(id)) {
            throw new SingularFormException(
                    "A instancia " + instancia.getPathFull() + " tem um ID repetido (igual a outra instância) id=" + id);
        }
        if (instancia instanceof ICompositeInstance) {
            int max = id;
            for (SInstance filho : ((ICompositeInstance) instancia).getChildren()) {
                max = Math.max(max, verificarIds(filho, ids));
            }
            return max;
        }
        return id;
    }

    private static void fromXML(SInstance instancia, MElement xml) {
        if (xml == null)
            return; // Não precisa fazer nada
        lerAtributos(instancia, xml);
        if (instancia instanceof SISimple) {
            SISimple<?> instanciaS = (SISimple<?>) instancia;
            STypeSimple<?, ?> tipos = instanciaS.getMTipo();
            instancia.setValor(tipos.fromStringPersistencia(xml.getTextContent()));
        } else if (instancia instanceof SIComposite) {
            SIComposite instc = (SIComposite) instancia;
            for (SType<?> campo : instc.getMTipo().getFields()) {
                MElement xmlFilho = xml.getElement(campo.getNomeSimples());
                if (xmlFilho != null) {
                    fromXML(instc.getCampo(campo.getNomeSimples()), xmlFilho);
                }
            }
        } else if (instancia instanceof SList) {
            SList<?> lista = (SList<?>) instancia;
            String nomeFilhos = lista.getMTipo().getTipoElementos().getNomeSimples();
            for (MElement xmlFilho : xml.getElements(nomeFilhos)) {
                SInstance filho = lista.addNovo();
                fromXML(filho, xmlFilho);
            }
        } else {
            throw new UnsupportedOperationException(instancia.getClass().getName());
        }
    }

    private static void lerAtributos(SInstance instancia, MElement xml) {
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

    /**
     * Gera uma string XML representando a instância de forma apropriada para
     * persitência. Já trata escapes de caracteres especiais dentro dos valores.
     */
    public static Optional<String> toStringXML(SInstance instancia) {
        MElement xml = toXML(instancia);
        return xml == null ? Optional.empty() : Optional.of(xml.toStringExato());
    }

    public static MElement toXML(SInstance instancia) {
        return new PersistenceBuilderXML().withPersistNull(false).toXML(instancia);
    }

    public static MElement toXMLPreservingRuntimeEdition(SInstance instancia) {
        return new PersistenceBuilderXML().withPersistNull(true).withPersistAttributes(true).toXML(instancia);
    }

    final static MElement toXML(MElement pai, String nomePai, SInstance instancia, PersistenceBuilderXML builder) {

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

    private static MElement toXML(ConfXMLGeneration conf, SInstance instancia) {
        if (instancia instanceof SISimple<?>) {
            SISimple<?> iSimples = (SISimple<?>) instancia;
            String sPersistencia = iSimples.toStringPersistencia();
            if (sPersistencia != null) {
                return conf.createMElementComValor(instancia, sPersistencia);
            } else if (conf.isPersistirNull()) {
                return conf.createMElement(instancia);
            }
            return null;
        } else if (instancia instanceof SIComposite) {
            MElement registro = null;
            for (SInstance filho : ((SIComposite) instancia).getCampos()) {
                MElement xmlFilho = toXML(conf, filho);
                if (xmlFilho != null) {
                    if (registro == null) {
                        registro = conf.createMElement(instancia);
                    }
                    registro.appendChild(xmlFilho);
                }
            }
            return registro;
        } else if (instancia instanceof SList) {
            SList<?> lista = (SList<?>) instancia;
            if (lista.isEmpty()) {
                return null;
            }
            MElement xmlLista = null;
            for (SInstance filho : lista) {
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

        public MElement createMElement(SInstance instancia) {
            return complement(instancia, xmlDocument.createMElement(instancia.getMTipo().getNomeSimples()));
        }

        public MElement createMElementComValor(SInstance instancia, String valorPersistencia) {
            return complement(instancia, xmlDocument.createMElementComValor(instancia.getMTipo().getNomeSimples(), valorPersistencia));
        }

        private MElement complement(SInstance instancia, MElement element) {
            if (builder.isPersistId() && instancia.getId() != null) {
                element.setAttribute(ATRIBUTO_ID, instancia.getId().toString());
            }
            if (builder.isPersistAttributes()) {
                for (Entry<String, SInstance> atr : instancia.getAtributos().entrySet()) {
                    if (atr.getValue() instanceof SISimple) {
                        String sPersistencia = ((SISimple<?>) atr.getValue()).toStringPersistencia();
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
