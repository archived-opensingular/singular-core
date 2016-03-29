package br.net.mirante.singular.exemplos.notificacaosimpliciada;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificacaoSimplificadaProviderUtils {

    public static List<Pair> linhasProducao() {
        List<Pair> list = new ArrayList<>();
        list.add(Pair.of(1, "Comprimidos"));
        list.add(Pair.of(2, "Comprimidos Revestidos"));
        list.add(Pair.of(3, "Cápsulas"));
        list.add(Pair.of(4, "Cremes e Pomadas"));
        list.add(Pair.of(5, "Suspensões Extemporâneas"));
        return list;
    }


    public static List<Triple> configuracoesLinhaProducao(Integer idLinhaProducao) {
        List<Triple> list = new ArrayList<>();
        list.add(Triple.of(1, 1, "Comprimidos em Camadas"));
        list.add(Triple.of(2, 1, "Comprimidos Placebo"));
        list.add(Triple.of(3, 1, "Comprimidos Simples"));

        list.add(Triple.of(4, 2, "Drágeas Simples"));
        list.add(Triple.of(5, 2, "Drágeas Saborosas"));
        list.add(Triple.of(6, 2, "Drágeas Coloridas"));

        list.add(Triple.of(7, 3, "Cápsula Rídiga"));
        list.add(Triple.of(8, 3, "Cápsula Gelatinosa"));
        list.add(Triple.of(9, 3, "Cápsula de Amido"));

        return list.stream().filter(t -> t.getMiddle().equals(idLinhaProducao)).collect(Collectors.toList());
    }


    public static List<Triple> substancias(Integer idConfiguracaoLinhaProducao) {
        List<Triple> list = new ArrayList<>();
        list.add(Triple.of(1, 1, "Clorpropamida"));
        list.add(Triple.of(2, 1, "Cumarina"));
        list.add(Triple.of(3, 1, "Metilcelulose"));

        list.add(Triple.of(4, 2, "Bumetanida"));
        list.add(Triple.of(5, 2, "Pindolol"));
        list.add(Triple.of(6, 2, "Clopamida"));

        list.add(Triple.of(7, 3, "Clorpropamida"));
        list.add(Triple.of(8, 3, "Noretisterona"));
        list.add(Triple.of(9, 3, "Tiabendazol"));

        list.add(Triple.of(10, 4, "Troxerrutina"));
        list.add(Triple.of(11, 4, "Cumarina"));
        list.add(Triple.of(12, 4, "Silimarina"));

        list.add(Triple.of(13, 5, "Metionina"));
        list.add(Triple.of(14, 5, "Ticlopidina"));
        list.add(Triple.of(15, 5, "Tribenosídeo"));

        list.add(Triple.of(16, 6, "Isometepteno"));
        list.add(Triple.of(17, 6, "Etinilestradiol"));
        list.add(Triple.of(18, 6, "Ciproterona"));

        list.add(Triple.of(19, 7, "Metoclopramida"));
        list.add(Triple.of(20, 7, "Pepsina"));
        list.add(Triple.of(21, 7, "Simeticona"));

        list.add(Triple.of(22, 8, "Condroitina"));
        list.add(Triple.of(23, 8, "Glicosamina"));
        list.add(Triple.of(24, 8, "Dutasterida"));

        list.add(Triple.of(25, 9, "Tansulosina"));
        list.add(Triple.of(26, 9, "Cianocobalamina"));
        list.add(Triple.of(27, 9, "Riboflavina"));


        return list.stream().filter(t -> t.getMiddle().equals(idConfiguracaoLinhaProducao)).collect(Collectors.toList());
    }

    public static List<Pair> formasFarmaceuticas(){
        List<Pair> list = new ArrayList<>();
        list.add(Pair.of(1, "Adesivo"));
        list.add(Pair.of(2, "Anel"));
        list.add(Pair.of(3, "Barra"));
        list.add(Pair.of(4, "Bastão"));
        list.add(Pair.of(5, "Comprimido"));
        list.add(Pair.of(6, "Comprimido de Liberação Prolongada"));
        list.add(Pair.of(7, "Comprimido para Solução"));
        list.add(Pair.of(8, "Granulado"));
        list.add(Pair.of(9, "Implante"));
        list.add(Pair.of(10, "Pó Aerossol"));
        list.add(Pair.of(11, "Tablete"));
        list.add(Pair.of(12, "Espuma"));

        return list;
    }

    public static List<Triple> concentracoes(Integer idSubstancia) {
        List<Triple> list = new ArrayList<>();

        if (idSubstancia == null) {
            return list;
        }

        Integer idSubstanciaFake = idSubstancia % 9;


        list.add(Triple.of(1, 1, "10 mg"));
        list.add(Triple.of(2, 1, "15 mg"));
        list.add(Triple.of(3, 1, "20 mg"));

        list.add(Triple.of(4, 2, "25 mg"));
        list.add(Triple.of(5, 2, "35 mg"));
        list.add(Triple.of(6, 2, "40 mg"));

        list.add(Triple.of(7, 3, "50 mg"));
        list.add(Triple.of(8, 3, "60 mg"));
        list.add(Triple.of(9, 3, "70 mg"));

        list.add(Triple.of(10, 4, "80 mg"));
        list.add(Triple.of(11, 4, "90 mg"));
        list.add(Triple.of(12, 4, "100 mg"));

        list.add(Triple.of(13, 5, "1 g"));
        list.add(Triple.of(14, 5, "2 g"));
        list.add(Triple.of(15, 5, "4 kg"));

        list.add(Triple.of(16, 6, "30 mg + 40 mg"));
        list.add(Triple.of(17, 6, "22 mg"));
        list.add(Triple.of(18, 6, "7 mg"));

        list.add(Triple.of(19, 7, "1 mg"));
        list.add(Triple.of(20, 7, "2 mg"));
        list.add(Triple.of(21, 7, "3 mg"));

        list.add(Triple.of(22, 8, "20 mg + 15 mg"));
        list.add(Triple.of(23, 8, "3 mg"));
        list.add(Triple.of(24, 8, "15 mg + 235 mg"));

        list.add(Triple.of(25, 9, "200 mg"));
        list.add(Triple.of(26, 9, "7 mg"));
        list.add(Triple.of(27, 9, "75 mg"));

        return list.stream().filter(t -> t.getMiddle().equals(idSubstanciaFake)).collect(Collectors.toList());
    }

    public static List<Pair> embalagensPrimarias() {
        List<Pair> list = new ArrayList<>();
        list.add(Pair.of(1, "Ampola"));
        list.add(Pair.of(2, "Bisnaga de plástico opaco"));
        list.add(Pair.of(3, "Blíster de alumínio e plástico opaco"));
        list.add(Pair.of(4, "Carpule"));
        list.add(Pair.of(5, "Envelope"));
        return list;
    }

    public static List<Pair> embalagensSecundarias() {
        List<Pair> list = new ArrayList<>();
        list.add(Pair.of(1, "Caixa"));
        list.add(Pair.of(2, "Cartucho"));
        list.add(Pair.of(3, "Envelope"));
        list.add(Pair.of(4, "Caixa térmica"));
        return list;
    }

    public static List<Pair> unidadesMedida() {
        List<Pair> list = new ArrayList<>();
        list.add(Pair.of(2, "mg"));
        list.add(Pair.of(1, "g"));
        list.add(Pair.of(4, "kg"));
        list.add(Pair.of(3, "ml"));
        list.add(Pair.of(5, "l"));
        return list;
    }

    public static List<Triple> empresaInternacional() {
        List<Triple> list = new ArrayList<>();

        list.add(Triple.of(1, "BAYER", "Munique, Alemanha"));
        list.add(Triple.of(2, "Pfizer", "Nova Iorque, Estados Unidos"));

        return list;
    }

    public static List<Triple> empresaTerceirizada() {
        List<Triple> list = new ArrayList<>();

        list.add(Triple.of(1, "Aché", "Rua presidentre dutra, São Paulo     SP"));
        list.add(Triple.of(2, "Roche", "Anápolis - GO"));

        return list;
    }

    public static List<Triple> outroLocalFabricacao() {
        List<Triple> list = new ArrayList<>();

        list.add(Triple.of(1, "Laboratório EMS", "Moema SP"));
        list.add(Triple.of(2, "Laboratório Fundo de Quintal", "Moema SP"));

        return list;
    }

    public static List<Pair> etapaFabricacao() {
        List<Pair> list = new ArrayList<>();
        list.add(Pair.of(1, "Processo produtivo completo"));
        list.add(Pair.of(2, "Processo de embalagem primária"));
        list.add(Pair.of(3, "Processo de embalagem secundária"));
        list.add(Pair.of(4, "Produção"));
        list.add(Pair.of(5, "Controle de qualidade"));
        return list;
    }
}
