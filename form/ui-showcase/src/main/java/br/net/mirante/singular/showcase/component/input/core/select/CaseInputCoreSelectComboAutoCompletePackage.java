package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SFixedOptionsSimpleProvider;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;

public class CaseInputCoreSelectComboAutoCompletePackage extends SPackage {

    private static final String[] NAMES = {"Adam Strange","Agent Liberty","Air Wave","Animal Man","Aqualad","Aquaman","Arsenal","Artemis of Bana-Mighdall","The Atom","Atom Smasher","Aurakles","Azrael","Aztec","Bart Allen","Barry Allen","Batgirl","Batman","Beast Boy","Black Canary","Blue Beetle","Booster Gold","Bronze Tiger","Captain Marvel Jr.","Cassandra Cain","Cyborg","Comedian","Captain Metropolis","Captain Marvel (Shazam)","Calendar Man","Catwoman","Dan the Dyna-Mite","Damian Wayne","Dick Grayson","Donna Troy","Dr. Fate","Dr. Manhattan","Elongated Man","Fire","Firestorm","Felicity Smoak","The Flash","Ferro Lad","Gangbuster","Green Arrow (Oliver Queen)","Green Arrow (Connor Hawke)","Guardian","Guy Gardner","Green Lantern","Gypsy","Hal Jordan","Hawkman","Hawkgirl","Hank Hall","Hooded Justice","Hourman","Huntress","Ice","Icemaiden","Impulse","Iris West","Jason Todd","Jericho","Jezebelle","John Stewart","Johnny Quick","Jonah Hex","Judomaster","Kamandi","Karate Kid","Katana","Kate Spencer","Kyle Rayner","Kid Flash","Kid Flash (Iris West)","Kid Flash (Wally West)","Kent Shakespeare","Lagoon Boy","Liberty Belle","Manhunter","Martian Manhunter","Mary Marvel","Mas y Menos","Metamorpho","Misfit","Mister America (Jeffrey Graves)","Mister America (Tex Thompson)","Mr. Terrific","Miss Martian","Nightwing","Nite Owl","Nite Owl II","Night Star","Oracle","Orin","Owlman","Owlwoman","Ozymandias","Pantha","Phantom Lady","Plastic Man","Poison Ivy","Power Girl","The Question","Raven","Ray","Red Arrow","Red Tornado","Robin","Robotman","Rocket","Rocket Red","Rorschach","Roy Harper","Sandy Hawkins","Seven Soldiers of Victory","Shining Knight","Silk Spectre","Solomon Grundy","Spectre","Speedy","Starfire","Stargirl","Starman","Star-Spangled Kid","Static","Stephanie Brown","S.T.R.I.P.E.","Superboy (Kal-El)","Supergirl","Superman","Tiger","Tim Drake","Terry McGinnis","Terra","Tex Thompson","TNT","Vigilante","Vixen","Wally West","Wildcat","Wonder Girl","Wonder Woman"};
    private static final String[][] CHEMICALS = {{"Ac","Actinium"},{"Ag","Silver"},{"Al","Aluminium"},{"Am","Americium"},{"Ar","Argon"},{"As","Arsenic"},{"At","Astatine"},{"Au","Gold"},{"B","Boron"},{"Ba","Barium"},{"Be","Beryllium"},{"Bh","Bhorium"},{"Bi","Bismuth"},{"Bk","Berkelium"},{"Br","Bromine"},{"C","Carbon"},{"Ca","Calcium"},{"Cd","Cadmium"},{"Ce","Cerium"},{"Cf","Californium"},{"Cl","Chlorine"},{"Cm","Curium"},{"Co","Cobalt"},{"Cr","Chromium"},{"Cs","Caesium"},{"Cu","Copper"},{"Ds","Darmstadtium"},{"Db","Dubnium"},{"Dy","Dysprosium"},{"Er","Erbium"},{"Es","Einsteinium"},{"Eu","Europium"},{"F","Fluorine"},{"Fe","Iron"},{"Fm","Fermium"},{"Fr","Francium"},{"Ga","Gallium"},{"Gd","Gadolinium"},{"Ge","Germanium"},{"H","Hydrogen"},{"He","Helium"},{"Hf","Hafnium"},{"Hg","Mercury"},{"Ho","Holmium"},{"Hs","Hassium"},{"I","Iodine"},{"In","Indium"},{"Ir","Iridium"},{"K","Potassium"},{"Kr","Krypton"},{"La","Lanthanum"},{"Li","Lithium"},{"Lr","Lawrencium"},{"Lu","Lutetium"},{"Md","Mendelevium"},{"Mg","Magnesium"},{"Mn","Manganese"},{"Mo","Molybdenum"},{"Mt","Meitnerium"},{"N","Nitrogen"},{"Na","Sodium"},{"Nb","Niobium"},{"Nd","Neodymium"},{"Ne","Neon"},{"Ni","Nickel"},{"No","Nobelium"},{"Np","Neptunium"},{"O","Oxygen"},{"Os","Osmium"},{"P","Phosphorus"},{"Pa","Protactinium"},{"Pb","Lead"},{"Pd","Palladium"},{"Pm","Promethium"},{"Po","Polonium"},{"Pr","Praseodymium"},{"Pt","Platinum"},{"Pu","Plutonium"},{"Ra","Radium"},{"Rb","Rubidium"},{"Re","Rhenium"},{"Rf","Rutherfordium"},{"Rg","Roentgenium"},{"Rh","Rhodium"},{"Rn","Radon"},{"Ru","Ruthenium"},{"S","Sulphur"},{"Sb","Antimony"},{"Sc","Scandium"},{"Se","Selenium"},{"Sg","Seaborgium"},{"Si","Silicon"},{"Sm","Samarium"},{"Sn","Tin"},{"Sr","Strontium"},{"Ta","Tantalum"},{"Tb","Terbium"},{"Tc","Technetium"},{"Te","Tellurium"},{"Th","Thorium"},{"Ti","Titanium"},{"Tl","Thallium"},{"Tm","Thulium"},{"U","Uranium"},{"V","Vanadium"},{"W","Tungsten"},{"Xe","Xenon"},{"Y","Yttrium"},{"Yb","Ytterbium"},{"Zn","Zinc"},{"Zr","Zirconium"}};
    private static final String[] DOMAINS = {"@gmail.com", "@hotmail.com", "@yahoo.com", "@outlook.com","@uol.com.br","@bol.com.br"};
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        //@destacar:bloco
        // Auto Complete padrão com opções pré-definidas.
        //@destacar:fim
        STypeString myHero = tipoMyForm.addFieldString("myHero");
        myHero.withSelectionOf(NAMES);
        myHero.withView(SViewAutoComplete::new)
                .asAtrBasic().label("Herói Favorito");

        //@destacar:bloco
        // Auto Complete padrão com opções pré-definidas chave-valor.
        //@destacar:fim
        STypeString mySubstance = tipoMyForm.addFieldString("mySubstance");
        SFixedOptionsSimpleProvider provider = mySubstance.withSelection();
        for(String[] chemical : CHEMICALS){ provider.add(chemical[0],chemical[1]);}
        mySubstance.withView(SViewAutoComplete::new)
                .asAtrBasic().label("Componente Químico");


        //@destacar:bloco
        // Auto Complete com opções dinâmicas baseadas nos valores informados.
        //@destacar:fim
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
                .asAtrBasic().label("Email");
    }
}
