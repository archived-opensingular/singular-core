package br.net.mirante.singular.showcase.interaction;

import static java.util.stream.Collectors.*;

import java.util.stream.Stream;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInteractionDependsOnOptionsPackage extends MPacote {

    private static final String[] WORDS = (""
        + "consider,minute,accord,evident,practice,intend,concern,commit,issue,approach,establish,utter,conduct,engage,"
        + "obtain,scarce,policy,straight,stock,apparent,property,fancy,concept,court,appoint,passage,vain,instance,coast,"
        + "project,commission,constant,circumstances,constitute,level,affect,institute,render,appeal,generate,theory,"
        + "range,campaign,league,labor,confer,grant,dwell,entertain,contract,earnest,yield,wander,insist,knight,convince,"
        + "inspire,convention,skill,harry,financial,reflect,novel,furnish,compel,venture,territory,temper,bent,intimate,"
        + "undertake,majority,assert,crew,chamber,humble,scheme,keen,liberal,despair,tide,attitude,justify,flag,merit,"
        + "manifest,notion,scale,formal,resource,persist,contempt,tour,plead,weigh,mode,distinction,inclined,attribute,"
        + "exert,oppress,contend,stake,toil,perish,disposition,rail,cardinal,boast,advocate,bestow,allege,"
        + "notwithstanding,lofty,multitude,steep,heed,modest,partial,apt,esteem,credible,provoke,tread,ascertain,fare,"
        + "cede,perpetual,decree,contrive,derived,elaborate,substantial,frontier,facile,cite,warrant,sob,rider,dense,"
        + "afflict,flourish,ordain,pious,vex,gravity,suspended,conspicuous,retort,jet,bolt,assent,purse,plus,sanction,"
        + "proceeding,exalt,siege,malice,extravagant,wax,throng,venerate,assail,sublime,exploit,exertion,kindle,endow,"
        + "imposed,humiliate,suffrage,ensue,brook,gale,muse,satire,intrigue,indication,dispatch,cower,wont,tract,canon,"
        + "impel,latitude,vacate,undertaking,slay,predecessor,delicacy,forsake,beseech,philosophical,grove,frustrate,"
        + "illustrious,device,pomp,entreat,impart,propriety,consecrate,proceeds,fathom,objective,clad,partisan,faction,"
        + "contrived,venerable,restrained,besiege,manifestation,rebuke,insurgent,rhetoric,scrupulous,ratify,stump,"
        + "discreet,imposing,wistful,mortify,ripple,premise,subside,adverse,caprice,muster,comprehensive,accede,fervent,"
        + "cohere,tribunal,austere,recovering,stratum,conscientious,arbitrary,exasperate,conjure,ominous,edifice,elude,"
        + "pervade,foster,admonish,repeal,retiring,incidental,acquiesce,slew,usurp,sentinel,precision,depose,wanton,"
        + "odium,precept,deference,fray,candid,enduring,impertinent,bland,insinuate,nominal,suppliant,languid,rave,"
        + "monetary,headlong,infallible,coax,explicate,gaunt,morbid,ranging,pacify,pastoral,dogged,ebb,aide,appease,"
        + "stipulate,recourse,constrained,bate,aversion,conceit,loath,rampart,extort,tarry,perpetrate,decorum,"
        + "luxuriant,cant,enjoin,avarice,edict,disconcert,symmetry,capitulate,arbitrate,cleave,append,visage,horde,"
        + "parable,chastise,foil,veritable,grapple,gentry,pall,maxim,projection,prowess,dingy,semblance,tout,fortitude,"
        + "asunder,rout,staid,beguile,purport,deprave,bequeath,enigma,assiduous,vassal,quail,outskirts,bulwark,swerve,"
        + "gird,betrothed,prospective,advert,peremptory,rudiment,deduce,halting,ignominy,ideology,pallid,chagrin,obtrude")
            .split(",");

    public MTipoComposto<?> testForm;
    public MTipoString      letter;
    public MTipoString      word;

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createTipoComposto("testForm");
        letter = testForm.addCampoString("letter");
        word = testForm.addCampoString("word");

        letter.as(MPacoteBasic.aspect())
            .label("Letter");
        //TODO: Fabs : I'm commenting since this is causing some compilation errros, and I must revisit later.
        letter.withSelectionOf("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(","));

        word.as(MPacoteBasic.aspect())
            .label("Word")
            .dependsOn(letter);
//        word.withSelectionFromProvider(ins -> {
//            String prefix = ins.findNearest(letter).get().getValor();
//            return (prefix == null)
//                ? ins.getMTipo().novaLista()
//                : ins.getMTipo().novaLista()
//                    .addValores(Stream.of(WORDS)
//                        .filter(s -> s.startsWith(prefix))
//                        .collect(toList()));
//        });
    }
}
