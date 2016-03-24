package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;

/**
 * Created by nuk on 21/03/16.
 */
public class CaseInputCoreSelectComboAutoCompletePackage extends SPackage {

    private static final String[] NAMES = new String[]{"Adam Strange","Agent Liberty","Air Wave","Animal Man","Aqualad","Aquaman","Arsenal","Artemis of Bana-Mighdall","The Atom","Atom Smasher","Aurakles","Azrael","Aztec","Bart Allen","Barry Allen","Batgirl","Batman","Beast Boy","Black Canary","Blue Beetle","Booster Gold","Bronze Tiger","Captain Marvel Jr.","Cassandra Cain","Cyborg","Comedian","Captain Metropolis","Captain Marvel (Shazam)","Calendar Man","Catwoman","Dan the Dyna-Mite","Damian Wayne","Dick Grayson","Donna Troy","Dr. Fate","Dr. Manhattan","Elongated Man","Fire","Firestorm","Felicity Smoak","The Flash","Ferro Lad","Gangbuster","Green Arrow (Oliver Queen)","Green Arrow (Connor Hawke)","Guardian","Guy Gardner","Green Lantern","Gypsy","Hal Jordan","Hawkman","Hawkgirl","Hank Hall","Hooded Justice","Hourman","Huntress","Ice","Icemaiden","Impulse","Iris West","Jason Todd","Jericho","Jezebelle","John Stewart","Johnny Quick","Jonah Hex","Judomaster","Kamandi","Karate Kid","Katana","Kate Spencer","Kyle Rayner","Kid Flash","Kid Flash (Iris West)","Kid Flash (Wally West)","Kent Shakespeare","Lagoon Boy","Liberty Belle","Manhunter","Martian Manhunter","Mary Marvel","Mas y Menos","Metamorpho","Misfit","Mister America (Jeffrey Graves)","Mister America (Tex Thompson)","Mr. Terrific","Miss Martian","Nightwing","Nite Owl","Nite Owl II","Night Star","Oracle","Orin","Owlman","Owlwoman","Ozymandias","Pantha","Phantom Lady","Plastic Man","Poison Ivy","Power Girl","The Question","Raven","Ray","Red Arrow","Red Tornado","Robin","Robotman","Rocket","Rocket Red","Rorschach","Roy Harper","Sandy Hawkins","Seven Soldiers of Victory","Shining Knight","Silk Spectre","Solomon Grundy","Spectre","Speedy","Starfire","Stargirl","Starman","Star-Spangled Kid","Static","Stephanie Brown","S.T.R.I.P.E.","Superboy (Kal-El)","Supergirl","Superman","Tiger","Tim Drake","Terry McGinnis","Terra","Tex Thompson","TNT","Vigilante","Vixen","Wally West","Wildcat","Wonder Girl","Wonder Woman"};
    private static final String[] DOMAINS = {"@gmail.com", "@hotmail.com", "@yahoo.com", "@outlook.com","@uol.com.br","@bol.com.br"};
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        STypeString myHero = tipoMyForm.addFieldString("myHero");
        myHero.withSelectionOf(NAMES);
        myHero.withView(SViewAutoComplete::new)
                .as(AtrBasic::new).label("Herói Favorito");

        STypeString name = tipoMyForm.addFieldString("email");
        name.withSelectionFromProvider(new SOptionsProvider() {
            @Override
            public SIList<? extends SInstance> listOptions(SInstance instance, String filter) {
                SIList<?> r = instance.getType().newList();
                appendEmailSuggestions(filter, r);
                return r;
            }

            private void appendEmailSuggestions(String filter, SIList<?> r) {
                String prefix = emailPrefix(filter);
                for(String d : DOMAINS){    r.addNew().setValue(prefix+d);  }
            }

            private String emailPrefix(String filter) {
                String prefix = "";
                if(filter != null){ prefix = filter.split("\\@")[0];}
                return prefix;
            }
        });
        name.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC))
                .as(AtrBasic::new).label("Email");
    }
}
