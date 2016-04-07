package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.options.SFixedOptionsSimpleProvider;
import br.net.mirante.singular.form.mform.options.SOptionsCompositeProvider;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.mform.util.transformer.SListBuilder;

public class CaseInputCoreSelectComboAutoCompletePackage extends SPackage {

    private static final String[] HEROES = {"Adam Strange","Agent Liberty","Air Wave","Animal Man","Aqualad","Aquaman","Arsenal","Artemis of Bana-Mighdall","The Atom","Atom Smasher","Aurakles","Azrael","Aztec","Bart Allen","Barry Allen","Batgirl","Batman","Beast Boy","Black Canary","Blue Beetle","Booster Gold","Bronze Tiger","Captain Marvel Jr.","Cassandra Cain","Cyborg","Comedian","Captain Metropolis","Captain Marvel (Shazam)","Calendar Man","Catwoman","Dan the Dyna-Mite","Damian Wayne","Dick Grayson","Donna Troy","Dr. Fate","Dr. Manhattan","Elongated Man","Fire","Firestorm","Felicity Smoak","The Flash","Ferro Lad","Gangbuster","Green Arrow (Oliver Queen)","Green Arrow (Connor Hawke)","Guardian","Guy Gardner","Green Lantern","Gypsy","Hal Jordan","Hawkman","Hawkgirl","Hank Hall","Hooded Justice","Hourman","Huntress","Ice","Icemaiden","Impulse","Iris West","Jason Todd","Jericho","Jezebelle","John Stewart","Johnny Quick","Jonah Hex","Judomaster","Kamandi","Karate Kid","Katana","Kate Spencer","Kyle Rayner","Kid Flash","Kid Flash (Iris West)","Kid Flash (Wally West)","Kent Shakespeare","Lagoon Boy","Liberty Belle","Manhunter","Martian Manhunter","Mary Marvel","Mas y Menos","Metamorpho","Misfit","Mister America (Jeffrey Graves)","Mister America (Tex Thompson)","Mr. Terrific","Miss Martian","Nightwing","Nite Owl","Nite Owl II","Night Star","Oracle","Orin","Owlman","Owlwoman","Ozymandias","Pantha","Phantom Lady","Plastic Man","Poison Ivy","Power Girl","The Question","Raven","Ray","Red Arrow","Red Tornado","Robin","Robotman","Rocket","Rocket Red","Rorschach","Roy Harper","Sandy Hawkins","Seven Soldiers of Victory","Shining Knight","Silk Spectre","Solomon Grundy","Spectre","Speedy","Starfire","Stargirl","Starman","Star-Spangled Kid","Static","Stephanie Brown","S.T.R.I.P.E.","Superboy (Kal-El)","Supergirl","Superman","Tiger","Tim Drake","Terry McGinnis","Terra","Tex Thompson","TNT","Vigilante","Vixen","Wally West","Wildcat","Wonder Girl","Wonder Woman"};
    private static final String[] AUTHORS = {"Abgar Renault","Adélia Prado","Adolfo Caminha","Adriana Falcão","Adriana Lisboa","Afonso Arinos","Affonso Romano de Sant'Anna","Afrânio Peixoto","Alaíde Lisboa","Alberto de Oliveira","Alberto Mussa","Alcântara Machado","Alceu Wamosy","Alcides Maia","Alcione Sortica","Aldo Novak","Alphonsus de Guimarães","Aluísio Azevedo","Alvarenga Peixoto","Álvares de Azevedo","Ana Cristina Cesar","Aníbal Beça","Aníbal Machado","Antônio Bezerra","Antônio Callado","Antônio Sales","Araripe Júnior","Artur Azevedo","Antonio Cícero","Arnaldo Antunes","Arnaldo Damasceno Vieira","Arthur Ramos","Augusto de Campos","Augusto dos Anjos","Autran Dourado","Ariano Suassuna","Basílio da Gama","Benjamin Sanches","Bento Teixeira","Bernadette Lyra","Botelho de Oliveira","Bruna Lombardi","Caio Fernando Abreu","Capistrano de Abreu","Carlos de Laet","Carlos Drummond de Andrade","Carlos Heitor Cony","Carlos Henrique Schroeder","Casimiro de Abreu","Cassiano Ricardo","Castro Alves","Catulo da Paixão Cearense","Cecília Meireles","Celso Sisto","César Leal","Chico Buarque","Chico César","Clarice Lispector","Clarice Pacheco","Cléo Martins","Cornélio Pena","Cruz e Sousa","Dalcídio Jurandir","Dalton Trevisan","Darcy Ribeiro","Dau Bastos","Décio Pignatari","Deoscoredes M. dos Santos","Dias Gomes","Dionélio Machado","Domingos Pellegrini","Drauzio Varella","Edison Carneiro","Elisa Lispector","Elisa Lucinda","Elly Herkenhoff","Eneida de Moraes","Érico Veríssimo","Euclides da Cunha","Fagundes Varela","Fernando Bonassi","Fernando Sabino","Fernando Gabeira","Ferreira Gullar","Francisca Júlia","Fran Dotti do Prado","Gabriel Chalita","Gesiel Júnior","Gilberto Freyre","Gilka Machado","Gonçalves de Magalhães","Gonçalves Dias","Graciliano Ramos","Graça Aranha","Gregório de Matos Guerra","Gustavo Reiz","Gustavo Barroso","Haroldo Maranhão","Harry Laus","Hélio Melo","Hélio Pellegrino","Hilda Hilst","Huberto Rohden","Ignácio de Loyola Brandão","Inglês de Sousa","Ivan Sant'anna","Izomar Camargo Guilherme","Josué Guimarães","João Aguiar","João do Rio","João Cabral de Melo Neto","João Gilberto Noll","João Guimarães Rosa","João Paulo Cotrim","João Simões Lopes Neto","João Ubaldo Ribeiro","Jorge Amado","José de Alencar","Jorge Fernando dos Santos","José Leon Machado","José J. Veiga","José Lins do Rego","Julio Cezar Ribeiro Vaugham","Jô Soares","Juvenal Galeno","Leonardo de Moraes","Leonardo Mota","Leo Vaz","Lima Barreto","Livia Garcia-Roza","Lourenço Mutarelli","Luis Eduardo Matta","Luís Fernando Veríssimo","Luiz Bacellar","Luiz Alfredo Garcia Roza","Lygia Fagundes Telles","Lya Luft","Maciel Monteiro","Machado de Assis","Manuel Bandeira","Manuel de Barros","Márcio Souza","Márcio Vassallo","Maria José Dupré","Maria Teresa Elisa Böbel","Mário de Andrade","Mário Faustino","Mário Quintana","Mário Ribeiro da Cruz","Max Martins","Menotti del Picchia","Michel Melamed","Miguel M. Abrahão","Miguel Jorge (escritor)","Miguel Marvilla","Millôr Fernandes","Milton Hatoum","Mino Carta","Moacir Japiassu","Moacyr Scliar","Monteiro Lobato","Moreira Campos","Murilo Mendes","Murilo Rubião","Nelson Hoffmann","Nelson Rodrigues","Nina Rodrigues","Nic Nilson","Otto Lara Resende","Otto Maria Carpeaux","Oswald de Andrade","Paulo Coelho","Paulo Freire","Paulo Leminski","Paulo Lins","Paulo Mendes Campos","Paulo Pontes","Paulo Querido","Pedro Bandeira","Pedro Gil-Pedro","Pedro Nava","Plínio Marcos","Qorpo Santo","Rachel de Queiroz","Raduan Nassar","Raul Bopp","Raul de Leoni","Raul Pompéia","Raul Lody","Regina Echeverria","Reginaldo Prandi","Renard Perez","Renata Palottini","Renato Pacheco","Renato Tapado","Roberto Drummond","Rodolfo Teófilo","Ronaldo Cagiano Barbosa","Rubem Alves","Rubem Braga","Rubem Fonseca","Rui Barbosa","Ruth Rocha","Santiago Nazarian","Sérgio Buarque de Hollanda","Sérgio Jockyman","Sérgio Sant'Anna","Silviano Santiago","Sousândrade","Thales de Andrade","Vicente Cechelero","Victor Louis Stutz","Vinicius de Moraes","Waldo Vieira","Zacarias Martins","Zélia Gattai","Ziraldo"};
    private static final String[][] CHEMICALS = {{"Ac","Actinium"},{"Ag","Silver"},{"Al","Aluminium"},{"Am","Americium"},{"Ar","Argon"},{"As","Arsenic"},{"At","Astatine"},{"Au","Gold"},{"B","Boron"},{"Ba","Barium"},{"Be","Beryllium"},{"Bh","Bhorium"},{"Bi","Bismuth"},{"Bk","Berkelium"},{"Br","Bromine"},{"C","Carbon"},{"Ca","Calcium"},{"Cd","Cadmium"},{"Ce","Cerium"},{"Cf","Californium"},{"Cl","Chlorine"},{"Cm","Curium"},{"Co","Cobalt"},{"Cr","Chromium"},{"Cs","Caesium"},{"Cu","Copper"},{"Ds","Darmstadtium"},{"Db","Dubnium"},{"Dy","Dysprosium"},{"Er","Erbium"},{"Es","Einsteinium"},{"Eu","Europium"},{"F","Fluorine"},{"Fe","Iron"},{"Fm","Fermium"},{"Fr","Francium"},{"Ga","Gallium"},{"Gd","Gadolinium"},{"Ge","Germanium"},{"H","Hydrogen"},{"He","Helium"},{"Hf","Hafnium"},{"Hg","Mercury"},{"Ho","Holmium"},{"Hs","Hassium"},{"I","Iodine"},{"In","Indium"},{"Ir","Iridium"},{"K","Potassium"},{"Kr","Krypton"},{"La","Lanthanum"},{"Li","Lithium"},{"Lr","Lawrencium"},{"Lu","Lutetium"},{"Md","Mendelevium"},{"Mg","Magnesium"},{"Mn","Manganese"},{"Mo","Molybdenum"},{"Mt","Meitnerium"},{"N","Nitrogen"},{"Na","Sodium"},{"Nb","Niobium"},{"Nd","Neodymium"},{"Ne","Neon"},{"Ni","Nickel"},{"No","Nobelium"},{"Np","Neptunium"},{"O","Oxygen"},{"Os","Osmium"},{"P","Phosphorus"},{"Pa","Protactinium"},{"Pb","Lead"},{"Pd","Palladium"},{"Pm","Promethium"},{"Po","Polonium"},{"Pr","Praseodymium"},{"Pt","Platinum"},{"Pu","Plutonium"},{"Ra","Radium"},{"Rb","Rubidium"},{"Re","Rhenium"},{"Rf","Rutherfordium"},{"Rg","Roentgenium"},{"Rh","Rhodium"},{"Rn","Radon"},{"Ru","Ruthenium"},{"S","Sulphur"},{"Sb","Antimony"},{"Sc","Scandium"},{"Se","Selenium"},{"Sg","Seaborgium"},{"Si","Silicon"},{"Sm","Samarium"},{"Sn","Tin"},{"Sr","Strontium"},{"Ta","Tantalum"},{"Tb","Terbium"},{"Tc","Technetium"},{"Te","Tellurium"},{"Th","Thorium"},{"Ti","Titanium"},{"Tl","Thallium"},{"Tm","Thulium"},{"U","Uranium"},{"V","Vanadium"},{"W","Tungsten"},{"Xe","Xenon"},{"Y","Yttrium"},{"Yb","Ytterbium"},{"Zn","Zinc"},{"Zr","Zirconium"}};
    private static final String[] DOMAINS = {"@gmail.com", "@hotmail.com", "@yahoo.com"};
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");


        // Auto Complete padrão com opções pré-definidas.
        STypeString myHero = tipoMyForm.addFieldString("myHero");
        myHero.withSelectionOf(HEROES);
        //@destacar:bloco
        myHero.withView(SViewAutoComplete::new)
                //@destacar:fim
                .asAtrBasic().label("Herói Favorito");

        STypeString myAuthor = tipoMyForm.addFieldString("myAuthor");
        myAuthor.withSelectionOf(AUTHORS);
        myAuthor.withView(SViewAutoComplete::new)
                .asAtrBasic().label("Herói Favorito");


        // Auto Complete padrão com opções pré-definidas chave-valor.
        STypeString mySubstance = tipoMyForm.addFieldString("mySubstance");
        //@destacar:bloco
        SFixedOptionsSimpleProvider provider = mySubstance.withSelection();
        for(String[] chemical : CHEMICALS){ provider.add(chemical[0],chemical[1]);}
        //@destacar:fim
        mySubstance.withView(SViewAutoComplete::new)
                .asAtrBasic().label("Componente Químico");



        // Auto Complete padrão com opções pré-definidas para tipos compostos.
        STypeComposite<SIComposite> myPlanet = tipoMyForm.addFieldComposite("myPlanet");

        STypeString name = myPlanet.addFieldString("name");
        name.asAtrBasic().label("Nome");
        STypeInteger position = myPlanet.addFieldInteger("position");
        position.asAtrBasic().label("Posição");
        STypeDecimal diameter = myPlanet.addFieldDecimal("diameterInKm");
        diameter.asAtrBasic().label("Diâmetro");
        //@destacar:bloco
        myPlanet.withSelectionFromProvider(name, new SOptionsCompositeProvider() {
            @Override
            public void listOptions(SInstance instance, SListBuilder lb) {
                SIComposite value = (SIComposite) SDocumentFactory.empty().createInstance(new RefType() {
                    protected SType<?> retrieve() {
                        return myPlanet;
                    }
                });
                lb.add().set(name,"Mercury").set(position,1).set(diameter,4879);
                lb.add().set(name,"Venus").set(position,2).set(diameter,12104);
                lb.add().set(name,"Earth").set(position,3).set(diameter,12756);
                lb.add().set(name,"Mars").set(position,4).set(diameter,6792);
                lb.add().set(name,"Jupiter").set(position,5).set(diameter,142984);
                lb.add().set(name,"Saturn").set(position,6).set(diameter,120536);
                lb.add().set(name,"Uranus").set(position,7).set(diameter,51118);
                lb.add().set(name,"Neptune").set(position,8).set(diameter,49528);
            }
        });
        //@destacar:fim

        myPlanet.withView(SViewAutoComplete::new)
                .asAtrBasic().label("Planeta de Origem");


        // Auto Complete com opções dinâmicas baseadas nos valores informados.
        STypeString email = tipoMyForm.addFieldString("email");
        email.withSelectionFromProvider(new SOptionsProvider() {
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
        //@destacar:bloco
        email.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC))
                //@destacar:fim
                .asAtrBasic().label("Email");
    }
}
