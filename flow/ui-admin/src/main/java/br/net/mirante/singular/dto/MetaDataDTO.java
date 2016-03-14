/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.dto;

import java.util.ArrayList;
import java.util.List;

import br.net.mirante.singular.flow.core.IEntityTaskType;
import br.net.mirante.singular.flow.core.dto.IMetaDataDTO;
import br.net.mirante.singular.flow.core.dto.IParameterDTO;
import br.net.mirante.singular.flow.core.dto.ITransactionDTO;

public class MetaDataDTO implements IMetaDataDTO {
    private static final long serialVersionUID = -6631180471711181801L;

    private Integer id;
    private String task;
    private String type;
    private String executor;
    private List<ITransactionDTO> transactions;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getTask() {
        return task;
    }

    @Override
    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public void setEnumType(IEntityTaskType type) {
        this.type = type.getDescription();
    }

    @Override
    public String getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(String executor) {
        this.executor = executor;
    }

    @Override
    public List<ITransactionDTO> getTransactions() {
        return transactions;
    }

    @Override
    public void setTransactions(List<ITransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public static class TransactionDTO implements ITransactionDTO {
        private static final long serialVersionUID = 8149321077048616674L;

        private String name;
        private String source;
        private String target;
        private List<IParameterDTO> parameters;

        public TransactionDTO() {
            parameters = new ArrayList<>();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getSource() {
            return source;
        }

        @Override
        public void setSource(String source) {
            this.source = source;
        }

        @Override
        public String getTarget() {
            return target;
        }

        @Override
        public void setTarget(String target) {
            this.target = target;
        }

        @Override
        public List<IParameterDTO> getParameters() {
            return parameters;
        }

        @Override
        public void setParameters(List<IParameterDTO> parameters) {
            this.parameters = parameters;
        }
    }

    public static class ParameterDTO implements IParameterDTO {
        private static final long serialVersionUID = -1084541774660192293L;

        private String name;
        private boolean required;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean isRequired() {
            return required;
        }

        @Override
        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}
