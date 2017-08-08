package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;

@SInfoType(spackage = AntaqPackage.class, newable = false, name = "Porto")
public class STypePorto extends STypeComposite<SIComposite> {

    public STypeString municipio;
    public STypeString pais;
    public STypeString uf;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        pais = this.addFieldString("pais");
        pais.withSelectionFromProvider("paisProvider");
        pais.asAtr().label("País");
        pais.asAtrBootstrap().colPreference(2);

        uf = this.addFieldString("uf");
        uf.asAtr().label("UF");
        uf.withSelectView();
        uf.selectionOf("AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA", "MG", "MS", "MT", "PA", "PB", "PE", "PI", "PR", "RJ", "RN", "RO", "RR", "RS", "SC", "SE", "SP", "TO", "Outros");
        uf.asAtrBootstrap().colPreference(2);
        
        
        municipio = this.addFieldString("municipio");
        municipio.withSelectionFromProvider("municipioDestinoProvider");
        municipio.asAtr().label("Município");
        municipio.asAtrBootstrap().colPreference(2);
        
    }
    
    public void colPreference(int size){
        pais.asAtrBootstrap().colPreference(size);
        uf.asAtrBootstrap().colPreference(size);
        municipio.asAtrBootstrap().colPreference(size);
    }
}
