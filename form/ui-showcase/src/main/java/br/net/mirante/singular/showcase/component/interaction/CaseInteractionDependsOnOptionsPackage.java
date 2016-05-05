/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.interaction;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.Provider;
import br.net.mirante.singular.form.mform.provider.ProviderContext;
import br.net.mirante.singular.form.mform.provider.SSimpleProvider;
import br.net.mirante.singular.form.mform.provider.SimpleProvider;
import br.net.mirante.singular.form.mform.util.transformer.SCompositeListBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public class CaseInteractionDependsOnOptionsPackage extends SPackage {

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

    public STypeComposite<?> testForm;
    public STypeString letter;
    public STypeString word;

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createCompositeType("testForm");
        letter = testForm.addFieldString("letter");
        word = testForm.addFieldString("word");

        letter.as(SPackageBasic.aspect())
            .label("Letter");
        letter.selectionOf("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(","));

        word.as(SPackageBasic.aspect())
            .label("Word")
            .dependsOn(letter);

        word.selectionOf(String.class).selfIdAndDisplay()
                .simpleProvider((ins)-> {
                    Optional<String> filter = ins.findNearestValue(letter);
                    return filter.map((f) -> {
                        return Stream.of(WORDS)
                                .filter((x) -> x.startsWith(f))
                                .collect(Collectors.toList());
                    }).orElse(newArrayList());

                });

        ;
    }
}
