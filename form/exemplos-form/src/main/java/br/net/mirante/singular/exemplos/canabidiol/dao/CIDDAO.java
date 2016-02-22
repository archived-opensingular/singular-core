package br.net.mirante.singular.exemplos.canabidiol.dao;

import br.net.mirante.singular.exemplos.canabidiol.model.AbstractDadoCID;
import br.net.mirante.singular.exemplos.canabidiol.model.CapituloCID;
import br.net.mirante.singular.exemplos.canabidiol.model.CategoriaCID;
import br.net.mirante.singular.exemplos.canabidiol.model.GrupoCID;
import br.net.mirante.singular.exemplos.canabidiol.model.SubCategoriaCID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class CIDDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(CIDDAO.class);
    private static final List<CapituloCID> capitulosCID;
    private static final List<GrupoCID> grupoCID;
    private static final List<CategoriaCID> categoriaCID;
    private static final List<SubCategoriaCID> subcategoriaCID;

    static {
        try {
            capitulosCID = readFile("CID-10-CAPITULOS.CSV", CapituloCID.class);
            grupoCID = readFile("CID-10-GRUPOS.CSV", GrupoCID.class);
            categoriaCID = readFile("CID-10-CATEGORIAS.CSV", CategoriaCID.class);
            subcategoriaCID = readFile("CID-10-SUBCATEGORIAS.CSV", SubCategoriaCID.class);

            for (CategoriaCID categoria : categoriaCID) {
                relateEachOther(categoria, subcategoriaCID, "setSubCategorias");
            }

            for (GrupoCID grupo : grupoCID) {
                relateEachOther(grupo, categoriaCID, "setCategorias");
            }

            for (CapituloCID capitulo : capitulosCID) {
                relateEachOther(capitulo, grupoCID, "setGrupos");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static void relateEachOther(AbstractDadoCID parent, List<? extends AbstractDadoCID> candidateChilds, String childCollectionName) throws Exception {
        List<AbstractDadoCID> collection = new ArrayList<>();
        for (AbstractDadoCID candidate : candidateChilds) {
            if (parent.getLetraInicial() <= candidate.getLetraInicial()
                    && parent.getNumInicial() <= candidate.getNumInicial()
                    && parent.getLetraFinal() >= candidate.getLetraFinal()
                    && parent.getNumFinal() >= candidate.getNumFinal()) {
                collection.add(candidate);
            }
        }
        Method setCollection = parent.getClass().getMethod(childCollectionName, List.class);
        setCollection.invoke(parent, collection);
    }

    private static <T extends AbstractDadoCID> List<T> readFile(String filename, Class<T> targetClass) throws Exception {
        List<T> cids = new ArrayList<>();
        LineIterator lineIterator = IOUtils.lineIterator(CIDDAO.class.getClassLoader().getResource("data/cid/" + filename).openStream(), "UTF-8");
        lineIterator.next();
        for (; lineIterator.hasNext(); ) {
            cids.add(readCidData(targetClass, lineIterator.next()));
        }
        return cids;
    }

    private static <T extends AbstractDadoCID> T readCidData(Class<T> clazz, String line) throws Exception {
        String[] values = line.split(";");
        T value = clazz.newInstance();
        int index = 0;

        if (clazz.isAssignableFrom(CapituloCID.class)) {
            String cap = StringUtils.trim(values[index++]);
            ((CapituloCID) value).setCapitulo(Integer.parseInt(cap));
        }

        String catinicial = StringUtils.trim(values[index++]);
        value.setLetraInicial(catinicial.charAt(0));
        value.setNumInicial(Integer.parseInt(catinicial.substring(1)));

        String catfinal = null;
        if (clazz.isAssignableFrom(SubCategoriaCID.class)) {
            catfinal = catinicial;
            index += 3;
        } else if (clazz.isAssignableFrom(CategoriaCID.class)) {
            catfinal = catinicial;
            index += 1;
        } else {
            catfinal = StringUtils.trim(values[index++]);
        }

        value.setLetraFinal(catfinal.charAt(0));
        value.setNumFinal(Integer.parseInt(catfinal.substring(1)));

        value.setDescricao(StringUtils.trim(values[index++]));

        value.setDescricaoAbreviada(StringUtils.trim(values[index++]));

        return value;
    }

    public List<CapituloCID> listCapitulos() {
        return capitulosCID;
    }

    public List<GrupoCID> listGrupoByIdCapitulo(String idCapitulo) {
        if (idCapitulo != null) {
            CapituloCID cap = capitulosCID.stream().filter(c -> idCapitulo.equals(c.getId())).findFirst().orElse(null);
            if (cap != null) {
                return cap.getGrupos() == null ? Collections.EMPTY_LIST : cap.getGrupos();
            }
        }
        return Collections.EMPTY_LIST;
    }

    public List<CategoriaCID> listCategoriasByIdGrupo(String idGrupo) {
        if (idGrupo != null) {
            GrupoCID grp = grupoCID.stream().filter(c -> idGrupo.equals(c.getId())).findFirst().orElse(null);
            if (grp != null) {
                return grp.getCategorias() == null ? Collections.EMPTY_LIST : grp.getCategorias();
            }
        }
        return Collections.EMPTY_LIST;

    }

    public List<SubCategoriaCID> listSubCategoriasByIdCategoria(String idCategoria) {
        if (idCategoria != null) {
            CategoriaCID cat = categoriaCID.stream().filter(c -> idCategoria.equals(c.getId())).findFirst().orElse(null);
            if (cat != null) {
                return cat.getSubCategorias() == null ? Collections.EMPTY_LIST : cat.getSubCategorias();
            }
        }
        return Collections.EMPTY_LIST;
    }

}
