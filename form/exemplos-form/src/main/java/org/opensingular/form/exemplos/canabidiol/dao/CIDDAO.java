/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.form.exemplos.canabidiol.dao;

import org.opensingular.form.SingularFormException;
import org.opensingular.form.exemplos.canabidiol.model.AbstractDadoCID;
import org.opensingular.form.exemplos.canabidiol.model.CategoriaCID;
import org.opensingular.form.exemplos.canabidiol.model.SubCategoriaCID;
import org.opensingular.form.exemplos.canabidiol.model.CapituloCID;
import org.opensingular.form.exemplos.canabidiol.model.GrupoCID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class CIDDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(CIDDAO.class);
    private static final List<CapituloCID> capitulosCID;
    private static final List<GrupoCID> grupoCID;
    private static final List<CategoriaCID>    categoriaCID;
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
            throw SingularFormException.rethrow(e.getMessage(), e);
        }
    }

    private static void relateEachOther(AbstractDadoCID parent, List<? extends AbstractDadoCID> candidateChilds, String childCollectionName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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

    private static <T extends AbstractDadoCID> List<T> readFile(String filename, Class<T> targetClass) throws IOException, InstantiationException, IllegalAccessException {
        List<T> cids = new ArrayList<>();
        LineIterator lineIterator = IOUtils.lineIterator(CIDDAO.class.getClassLoader().getResource("data/cid/" + filename).openStream(), StandardCharsets.UTF_8.name());
        lineIterator.next();
        for (; lineIterator.hasNext(); ) {
            cids.add(readCidData(targetClass, lineIterator.next()));
        }
        return cids;
    }

    private static <T extends AbstractDadoCID> T readCidData(Class<T> clazz, String line) throws IllegalAccessException, InstantiationException {
        String[] values = line.split(";");
        T value = clazz.newInstance();
        int index = 0;

        if (clazz.isAssignableFrom(CapituloCID.class)) {
            String cap = StringUtils.trim(values[index++]);
            ((CapituloCID) value).setCapitulo(Integer.valueOf(cap));
        }

        String catinicial = StringUtils.trim(values[index++]);
        value.setLetraInicial(catinicial.charAt(0));
        value.setNumInicial(Integer.valueOf(catinicial.substring(1)));

        String catfinal;
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
        value.setNumFinal(Integer.valueOf(catfinal.substring(1)));

        value.setDescricao(StringUtils.trim(values[index++]));

        value.setDescricaoAbreviada(StringUtils.trim(values[index]));

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
