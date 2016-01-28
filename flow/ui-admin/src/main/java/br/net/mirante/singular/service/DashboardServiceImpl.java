package br.net.mirante.singular.service;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.DashboardDAO;
import br.net.mirante.singular.persistence.entity.Dashboard;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Inject
    private DashboardDAO dashboardDAO;

    @Override
    @Transactional
    public List<Dashboard> retrieveCustomDashboards() {
        return dashboardDAO.retrieveCustomDashboards();
    }

    @Override
    @Transactional
    public Dashboard retrieveDashboardById(String customDashboardCode) {
        return dashboardDAO.retrieveDashboardById(Long.valueOf(customDashboardCode));
    }
}
