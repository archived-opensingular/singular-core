/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.util.diff;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SMultiSelectionBySelectView;
import org.opensingular.internal.lib.commons.xml.MElement;

public class TestDocumentDiffCheckboxes {


    public static final String PASSAGEIRO_CAMAROTE1 = "Camarote 1";
    public static final String PASSAGEIRO_CAMAROTE2 = "Camarote 2";
    public static final String PASSAGEIRO_CRIANCA = "Criança";
    public static final String PASSAGEIRO_COM_DESCONTO = "com Desconto";
    public static final String PASSAGEIRO_ESTUDANTE = "Estudante";
    public static final String PASSAGEIRO_GRATUITO = "Gratuito";
    public static final String PASSAGEIRO_IDOSO = "Idoso";
    public static final String PASSAGEIRO_PNE = "PNE";
    public static final String PASSAGEIRO_REDE1 = "Rede 1";
    public static final String PASSAGEIRO_REDE2 = "Rede 2";
    private STypeComposite<SIComposite> teste;
    private STypeList<STypeString, SIString> passageiro;

    @Before
    public void setup(){
        SDictionary                 dictionary     = SDictionary.create();
        PackageBuilder              packageBuilder = dictionary.createNewPackage("teste");
        teste = packageBuilder.createCompositeType("teste");

        passageiro = teste.addFieldListOf("passageiro", STypeString.class);
        passageiro.selectionOf(String.class,new SMultiSelectionBySelectView())
                .selfIdAndDisplay()
                .simpleProviderOf(PASSAGEIRO_CAMAROTE1,PASSAGEIRO_CAMAROTE2, PASSAGEIRO_CRIANCA, PASSAGEIRO_COM_DESCONTO, PASSAGEIRO_ESTUDANTE, PASSAGEIRO_GRATUITO, PASSAGEIRO_IDOSO,PASSAGEIRO_PNE, PASSAGEIRO_REDE1,PASSAGEIRO_REDE2);

    }


    @Test
    public void tesDiffRemovingListElement() {
        SIComposite testeInstance1 = teste.newInstance();

        SIList<SIString> passageiroInstance1 = testeInstance1.getDescendant(passageiro);
        passageiroInstance1.addNew().setValue(PASSAGEIRO_CAMAROTE1);
        passageiroInstance1.addNew().setValue(PASSAGEIRO_CAMAROTE2);
        passageiroInstance1.addNew().setValue(PASSAGEIRO_ESTUDANTE);
        passageiroInstance1.addNew().setValue(PASSAGEIRO_CRIANCA);


        MElement el = SFormXMLUtil.toXML(testeInstance1).get();
        SIComposite testeInstance2 = SFormXMLUtil.fromXML(teste, el);
        testeInstance2.getDescendant(passageiro).remove(2);

        DocumentDiff diff          = DocumentDiffUtil.calculateDiff(testeInstance1, testeInstance2);
        for (DiffInfo diffInfo : diff.getDiffRoot().getChildren()) {
            System.out.println(diffInfo.getOriginal().toStringDisplay());
            System.out.println(diffInfo.getNewer().toStringDisplay());
        }

        SFormXMLUtil.toXML(testeInstance1).get().printTabulado();
        SFormXMLUtil.toXML(testeInstance2).get().printTabulado();

        Assert.assertEquals(1, diff.getQtdChanges());


    }
}
