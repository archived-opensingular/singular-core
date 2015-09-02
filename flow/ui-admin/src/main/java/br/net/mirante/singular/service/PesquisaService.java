package br.net.mirante.singular.service;

import java.time.Period;
import java.util.List;
import java.util.Map;

public interface PesquisaService {

    List<Map<String, String>> retrieveMeanTimeByProcess(Period period);

    List<Map<String, String>> retrieveNewInstancesQuantityLastYear();

    List<Map<String, String>> retrieveStatusQuantityByPeriod(Period period);

    List<Map<String, String>> retrieveMeanTimeByTask(Long processId);
}
