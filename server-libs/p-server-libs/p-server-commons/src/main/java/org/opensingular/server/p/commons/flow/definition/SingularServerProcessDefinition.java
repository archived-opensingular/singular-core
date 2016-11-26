package org.opensingular.server.p.commons.flow.definition;

import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.builder.BTransition;
import org.opensingular.flow.core.variable.VarService;
import org.opensingular.server.commons.flow.metadata.ServerContextMetaData;
import org.opensingular.server.commons.flow.rest.ActionConfig;
import org.opensingular.server.commons.flow.rest.actions.DefaultAssignController;
import org.opensingular.server.p.commons.config.PServerContext;

import static org.opensingular.server.commons.flow.action.DefaultActions.ACTION_ANALYSE;
import static org.opensingular.server.commons.flow.action.DefaultActions.ACTION_ASSIGN;
import static org.opensingular.server.commons.flow.action.DefaultActions.ACTION_DELETE;
import static org.opensingular.server.commons.flow.action.DefaultActions.ACTION_EDIT;
import static org.opensingular.server.commons.flow.action.DefaultActions.ACTION_VIEW;

public abstract class SingularServerProcessDefinition<I extends ProcessInstance> extends ProcessDefinition<I> {


    protected SingularServerProcessDefinition(Class<I> instanceClass) {
        super(instanceClass);
    }

    protected SingularServerProcessDefinition(Class<I> processInstanceClass, VarService varService) {
        super(processInstanceClass, varService);
    }

    protected BTransition worklist(BTransition t) {
        t.getTransition()
                .setMetaDataValue(ServerContextMetaData.KEY,
                        ServerContextMetaData
                                .enable()
                                .enableOn(PServerContext.WORKLIST));
        return t;
    }


    protected BTransition petition(BTransition t) {
        t.getTransition()
                .setMetaDataValue(ServerContextMetaData.KEY,
                        ServerContextMetaData
                                .enable()
                                .enableOn(PServerContext.PETITION));
        return t;
    }


    @Override
    protected void configureActions(FlowMap flowMap) {
        final ActionConfig actionConfig = new ActionConfig();
        actionConfig
                .addDefaultAction(ACTION_EDIT)
                .addDefaultAction(ACTION_DELETE)
                .addDefaultAction(ACTION_VIEW)
                .addDefaultAction(ACTION_ANALYSE)
                .addAction(ACTION_ASSIGN, DefaultAssignController.class);
        flowMap.setMetaDataValue(ActionConfig.KEY, actionConfig);
    }
}
