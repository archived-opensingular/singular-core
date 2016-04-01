package br.net.mirante.singular.exemplos.notificacaosimplificada.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.*;
import br.net.mirante.singular.form.mform.util.transformer.SListBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.exemplos.notificacaosimplificada.dao.EnderecoEmpresaInternacionalDAO;
import br.net.mirante.singular.exemplos.notificacaosimplificada.dao.GenericDAO;
import br.net.mirante.singular.exemplos.notificacaosimplificada.dao.VocabularioControladoDAO;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.corporativo.PessoaJuridica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.geral.EnderecoEmpresaInternacional;

@Service
@Transactional(readOnly = true)
public class DominioService {

    @Inject
    private VocabularioControladoDAO vocabularioControladoDAO;

    @Inject
    private GenericDAO genericDAO;

    @Inject
    private EnderecoEmpresaInternacionalDAO enderecoEmpresaInternacionalDAO;

    public List<LinhaCbpf> linhasProducao(String filtro) {
        return vocabularioControladoDAO.findByDescricao(LinhaCbpf.class, filtro);
    }


    public List<Triple> configuracoesLinhaProducao(Integer idLinhaProducao) {
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

        return list.stream().filter(t -> t.getMiddle().equals(idLinhaProducao % 3)).collect(Collectors.toList());
    }


    public List<Substancia> substancias(Integer idConfiguracaoLinhaProducao, String filter) {
        return vocabularioControladoDAO.findByDescricao(Substancia.class, filter);
    }

    public List<FormaFarmaceuticaBasica> formasFarmaceuticas(String filtro){
        return vocabularioControladoDAO.findByDescricao(FormaFarmaceuticaBasica.class, filtro);
    }

    public List<Triple> concentracoes(Integer idSubstancia) {

        List<Triple> list = new ArrayList<>();

        if (idSubstancia == null) {
            return list;
        }

        Integer idSubstanciaFake = idSubstancia % 2;

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

    @Transactional
    public List<EmbalagemPrimariaBasica> findEmbalagensBasicas(String filtro) {
        return vocabularioControladoDAO.findByDescricao(EmbalagemPrimariaBasica.class, filtro);
    }

    public List<EmbalagemSecundaria> embalagensSecundarias(String filtro) {
        return vocabularioControladoDAO.findByDescricao(EmbalagemSecundaria.class, filtro);
    }

    public List<UnidadeMedida> unidadesMedida(String filtro) {
        return vocabularioControladoDAO.findByDescricao(UnidadeMedida.class, filtro);
    }

    public List<EnderecoEmpresaInternacional> empresaInternacional(String filtro) {
        return enderecoEmpresaInternacionalDAO.buscarEnderecos(filtro, 5);
    }

    public List<PessoaJuridica> empresaTerceirizada(String filtro) {
        return genericDAO.findByProperty(PessoaJuridica.class, "razaoSocial", filtro, 5);
    }

    public List<PessoaJuridica> outroLocalFabricacao(String filtro) {
        return genericDAO.findByProperty(PessoaJuridica.class, "razaoSocial", filtro, 5);
    }

    public List<EtapaFabricacao> etapaFabricacao(String filtro) {
        return vocabularioControladoDAO.findByDescricao(EtapaFabricacao.class, filtro);
    }

    public List<CategoriaRegulatoriaMedicamento> listCategoriasRegulatorias() {
        return vocabularioControladoDAO.listAll(CategoriaRegulatoriaMedicamento.class);
    }
}
