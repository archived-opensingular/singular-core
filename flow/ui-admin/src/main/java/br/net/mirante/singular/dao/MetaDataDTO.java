package br.net.mirante.singular.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetaDataDTO implements Serializable {
    private static final long serialVersionUID = -6631180471711181801L;

    private Long id;
    private String task;
    private String type;
    private String executor;
    private List<TransactionDTO> transactions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public class TransactionDTO implements Serializable {
        private static final long serialVersionUID = 8149321077048616674L;

        private String name;
        private String source;
        private String target;
        private List<ParameterDTO> parameters;

        public TransactionDTO() {
            parameters = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public List<ParameterDTO> getParameters() {
            return parameters;
        }

        public void setParameters(List<ParameterDTO> parameters) {
            this.parameters = parameters;
        }
    }

    public class ParameterDTO implements Serializable {
        private static final long serialVersionUID = -1084541774660192293L;

        private String name;
        private boolean required;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}
