package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;

/**
 * Created by nuk on 21/03/16.
 */
public class CaseInputCoreSelectComboAutoCompletePackage extends SPackage {

    private static final String[] NAMES = new String[]{"Adam Strange","Agent Liberty","Air Wave","Animal Man","Aqualad","Aquaman","Arsenal","Artemis of Bana-Mighdall","The Atom","Atom Smasher","Aurakles","Azrael","Aztec","Bart Allen","Barry Allen","Batgirl","Batman","Beast Boy","Black Canary","Blue Beetle","Booster Gold","Bronze Tiger","Captain Marvel Jr.","Cassandra Cain","Cyborg","Comedian","Captain Metropolis","Captain Marvel (Shazam)","Calendar Man","Catwoman","Dan the Dyna-Mite","Damian Wayne","Dick Grayson","Donna Troy","Dr. Fate","Dr. Manhattan","Elongated Man","Fire","Firestorm","Felicity Smoak","The Flash","Ferro Lad","Gangbuster","Green Arrow (Oliver Queen)","Green Arrow (Connor Hawke)","Guardian","Guy Gardner","Green Lantern","Gypsy","Hal Jordan","Hawkman","Hawkgirl","Hank Hall","Hooded Justice","Hourman","Huntress","Ice","Icemaiden","Impulse","Iris West","Jason Todd","Jericho","Jezebelle","John Stewart","Johnny Quick","Jonah Hex","Judomaster","Kamandi","Karate Kid","Katana","Kate Spencer","Kyle Rayner","Kid Flash","Kid Flash (Iris West)","Kid Flash (Wally West)","Kent Shakespeare","Lagoon Boy","Liberty Belle","Manhunter","Martian Manhunter","Mary Marvel","Mas y Menos","Metamorpho","Misfit","Mister America (Jeffrey Graves)","Mister America (Tex Thompson)","Mr. Terrific","Miss Martian","Nightwing","Nite Owl","Nite Owl II","Night Star","Oracle","Orin","Owlman","Owlwoman","Ozymandias","Pantha","Phantom Lady","Plastic Man","Poison Ivy","Power Girl","The Question","Raven","Ray","Red Arrow","Red Tornado","Robin","Robotman","Rocket","Rocket Red","Rorschach","Roy Harper","Sandy Hawkins","Seven Soldiers of Victory","Shining Knight","Silk Spectre","Solomon Grundy","Spectre","Speedy","Starfire","Stargirl","Starman","Star-Spangled Kid","Static","Stephanie Brown","S.T.R.I.P.E.","Superboy (Kal-El)","Supergirl","Superman","Tiger","Tim Drake","Terry McGinnis","Terra","Tex Thompson","TNT","Vigilante","Vixen","Wally West","Wildcat","Wonder Girl","Wonder Woman"};

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        STypeString tipoContato1 = tipoMyForm.addFieldString("myHero")
                .withSelectionOf(NAMES);


        tipoContato1
                .withView(SViewAutoComplete::new)
                .as(AtrBasic::new).label("Herói Favorito");
    }
}
