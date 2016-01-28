package br.net.mirante.singular.service;

import java.util.List;

import br.net.mirante.singular.persistence.entity.Dashboard;

public interface DashboardService {

    List<Dashboard> retrieveCustomDashboards();

    Dashboard retrieveDashboardById(String customDashboardCode);

}
